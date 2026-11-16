package com.example.data

enum class ToolCategory(val label: String) {
    TODAS("Todas"),
    TEXTO("Texto"),
    IMAGEM("Imagem"),
    VIDEO("Vídeo"),
    CODIGO("Código"),
    NEGOCIOS("Negócios")
}

enum class CommercialChannel(val label: String) {
    WHATSAPP("WhatsApp"),
    EMAIL("E-mail"),
    ANUNCIOS("Anúncios")
}

data class WebsiteFormInput(
    val companyName: String = "",
    val niche: String = "",
    val whatsapp: String = "",
    val cityState: String = "",
    val services: String = ""
)

data class GeneratedWebsite(
    val companyName: String,
    val htmlCode: String,
    val summary: String
)

data class SavedSiteEntry(
    val id: String,
    val companyName: String,
    val niche: String,
    val whatsapp: String,
    val cityState: String,
    val services: String,
    val htmlCode: String,
    val createdAtMillis: Long
)

data class UrlAnalysisResult(
    val normalizedUrl: String,
    val isSafe: Boolean,
    val riskScore: Int,
    val badgeTitle: String,
    val detailedExplanation: String,
    val indicators: List<String>
)

data class AiTool(
    val id: String,
    val rank: Int,
    val name: String,
    val category: ToolCategory,
    val description: String,
    val quickTip: String,
    val officialUrl: String,
    val tag: String
)

data class ToolAssistantResponse(
    val toolName: String,
    val guidance: String,
    val readyPrompt: String
)

data class CommercialCopyResult(
    val channel: CommercialChannel,
    val title: String,
    val body: String
)

data class WeatherInfo(
    val locationLabel: String,
    val temperatureCelsius: Double,
    val feelsLikeCelsius: Double,
    val humidityPercent: Int,
    val conditionLabel: String,
    val weatherCode: Int,
    val updatedAtText: String
)
