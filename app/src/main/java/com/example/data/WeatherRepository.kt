package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherRepository {

    suspend fun fetchWeatherByCity(cityQuery: String): WeatherInfo {
        val rawCity = cityQuery.trim().ifEmpty { "Floriano - PI" }
        // Extrai o nome principal da cidade antes de "-" ou "," para melhorar precisão no Open-Meteo Geocoding
        val cleanName = rawCity.substringBefore("-").substringBefore(",").trim()
        val encodedName = URLEncoder.encode(cleanName, "UTF-8")
        val geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=$encodedName&count=5&language=pt&format=json"

        val geoJsonStr = httpGet(geoUrl)
        val geoRoot = JSONObject(geoJsonStr)
        val results = geoRoot.optJSONArray("results")
            ?: throw IllegalStateException("Cidade \"$rawCity\" não encontrada no Open-Meteo.")
        if (results.length() == 0) {
            throw IllegalStateException("Cidade \"$rawCity\" não encontrada. Tente informar apenas o nome da cidade.")
        }

        // Prioriza resultado no Brasil (BR) ou estado compatível se houver sufixo
        val stateHint = rawCity.substringAfter("-", "").substringAfter(",", "").trim().lowercase(Locale("pt", "BR"))
        var chosen = results.getJSONObject(0)
        for (i in 0 until results.length()) {
            val candidate = results.getJSONObject(i)
            val countryCode = candidate.optString("country_code", "")
            val admin1 = candidate.optString("admin1", "").lowercase(Locale("pt", "BR"))
            if (stateHint.isNotEmpty() && admin1.contains(stateHint)) {
                chosen = candidate
                break
            }
            if (countryCode.equals("BR", ignoreCase = true) && chosen.optString("country_code") != "BR") {
                chosen = candidate
            }
        }

        val lat = chosen.getDouble("latitude")
        val lon = chosen.getDouble("longitude")
        val resolvedName = chosen.optString("name", cleanName)
        val admin1 = chosen.optString("admin1", "")
        val label = if (admin1.isNotBlank()) "$resolvedName - $admin1" else resolvedName

        return fetchWeatherByCoordinates(lat, lon, label)
    }

    suspend fun fetchWeatherByCoordinates(
        latitude: Double,
        longitude: Double,
        resolvedCityLabel: String? = null
    ): WeatherInfo {
        val forecastUrl =
            "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current=temperature_2m,relative_humidity_2m,apparent_temperature,weather_code&timezone=auto"

        val jsonStr = httpGet(forecastUrl)
        val root = JSONObject(jsonStr)
        val current = root.getJSONObject("current")

        val temp = current.optDouble("temperature_2m", 0.0)
        val feelsLike = current.optDouble("apparent_temperature", temp)
        val humidity = current.optInt("relative_humidity_2m", 0)
        val weatherCode = current.optInt("weather_code", 0)
        val condition = mapWmoCodeToPortuguese(weatherCode)
        val timeText = SimpleDateFormat("HH:mm", Locale("pt", "BR")).format(Date())

        return WeatherInfo(
            locationLabel = resolvedCityLabel ?: "GPS (${String.format(Locale.US, "%.2f", latitude)}, ${String.format(Locale.US, "%.2f", longitude)})",
            temperatureCelsius = temp,
            feelsLikeCelsius = feelsLike,
            humidityPercent = humidity,
            conditionLabel = condition,
            weatherCode = weatherCode,
            updatedAtText = timeText
        )
    }

    private suspend fun httpGet(endpoint: String): String {
        return withTimeout(20_000L) {
            withContext(Dispatchers.IO) {
                val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 12_000
                    readTimeout = 15_000
                    setRequestProperty("Accept", "application/json")
                }
                try {
                    val code = connection.responseCode
                    val stream = if (code in 200..299) connection.inputStream else connection.errorStream
                    val body = stream?.let { input ->
                        BufferedReader(InputStreamReader(input, Charsets.UTF_8)).use { it.readText() }
                    }.orEmpty()
                    if (code !in 200..299) {
                        throw IllegalStateException("Erro ao consultar Open-Meteo (HTTP $code).")
                    }
                    body
                } finally {
                    connection.disconnect()
                }
            }
        }
    }

    private fun mapWmoCodeToPortuguese(code: Int): String = when (code) {
        0 -> "Céu limpo"
        1 -> "Predominantemente limpo"
        2 -> "Parcialmente nublado"
        3 -> "Nublado"
        45, 48 -> "Neblina"
        51, 53, 55 -> "Garoa"
        56, 57 -> "Garoa congelante"
        61 -> "Chuva leve"
        63 -> "Chuva moderada"
        65 -> "Chuva forte"
        66, 67 -> "Chuva congelante"
        71, 73, 75, 77 -> "Neve"
        80, 81, 82 -> "Pancadas de chuva"
        85, 86 -> "Pancadas de neve"
        95 -> "Tempestade"
        96, 99 -> "Tempestade com granizo"
        else -> "Condição variável"
    }
}
