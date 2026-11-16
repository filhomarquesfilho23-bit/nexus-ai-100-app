package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.Thunderstorm
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.WeatherUiState
import com.example.ui.theme.NexusBgDeep
import com.example.ui.theme.NexusBorder
import com.example.ui.theme.NexusDangerRed
import com.example.ui.theme.NexusEmerald
import com.example.ui.theme.NexusNeonBlue
import com.example.ui.theme.NexusSurface
import com.example.ui.theme.NexusTextPrimary
import com.example.ui.theme.NexusTextSecondary
import kotlin.math.roundToInt

@Composable
fun WeatherWidgetCard(
    cityInput: String,
    weatherUiState: WeatherUiState,
    onCityChange: (String) -> Unit,
    onRefreshCity: () -> Unit,
    onUseGps: () -> Unit,
    onMicClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NexusSurface),
        border = BorderStroke(1.dp, NexusNeonBlue.copy(alpha = 0.45f)),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("weather_widget_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            NexusNeonBlue.copy(alpha = 0.12f),
                            NexusEmerald.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (weatherUiState) {
                is WeatherUiState.Loading -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp,
                            color = NexusNeonBlue
                        )
                        Text(
                            text = "Buscando previsão ao vivo (Open-Meteo)...",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTextSecondary
                        )
                    }
                }

                is WeatherUiState.Error -> {
                    Text(
                        text = weatherUiState.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusDangerRed
                    )
                }

                is WeatherUiState.Success -> {
                    val w = weatherUiState.weather
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(NexusNeonBlue.copy(alpha = 0.14f))
                                    .border(1.dp, NexusNeonBlue.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = weatherIconForCode(w.weatherCode),
                                    contentDescription = w.conditionLabel,
                                    tint = NexusNeonBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = w.locationLabel,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = NexusTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${w.conditionLabel} • Atualizado ${w.updatedAtText}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexusEmerald,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Text(
                            text = "${w.temperatureCelsius.roundToInt()}°C",
                            style = MaterialTheme.typography.displaySmall.copy(fontSize = 26.sp),
                            color = NexusNeonBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        WeatherMetricChip(
                            icon = Icons.Outlined.Thermostat,
                            label = "Sensação ${w.feelsLikeCelsius.roundToInt()}°C",
                            tint = NexusNeonBlue,
                            modifier = Modifier.weight(1f)
                        )
                        WeatherMetricChip(
                            icon = Icons.Outlined.WaterDrop,
                            label = "Umidade ${w.humidityPercent}%",
                            tint = NexusEmerald,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Compact City Input + Mic + GPS + Refresh
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedTextField(
                    value = cityInput,
                    onValueChange = onCityChange,
                    placeholder = { Text("Cidade (ex.: Floriano - PI)") },
                    singleLine = true,
                    trailingIcon = {
                        MicDictationButton(
                            contentDescription = "Ditar cidade do clima",
                            onClick = onMicClick,
                            tag = "mic_weather_city"
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onRefreshCity() }),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NexusNeonBlue,
                        unfocusedBorderColor = NexusBorder,
                        focusedContainerColor = NexusBgDeep,
                        unfocusedContainerColor = NexusBgDeep,
                        focusedTextColor = NexusTextPrimary,
                        unfocusedTextColor = NexusTextPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("weather_city_input")
                )

                IconButton(
                    onClick = onUseGps,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NexusBgDeep)
                        .border(1.dp, NexusEmerald.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .testTag("weather_gps_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MyLocation,
                        contentDescription = "Usar localização GPS",
                        tint = NexusEmerald
                    )
                }

                IconButton(
                    onClick = onRefreshCity,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NexusNeonBlue.copy(alpha = 0.16f))
                        .border(1.dp, NexusNeonBlue.copy(alpha = 0.55f), RoundedCornerShape(12.dp))
                        .testTag("weather_refresh_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Atualizar previsão do tempo",
                        tint = NexusNeonBlue
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherMetricChip(
    icon: ImageVector,
    label: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(NexusBgDeep)
            .border(1.dp, NexusBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = NexusTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun weatherIconForCode(code: Int): ImageVector = when (code) {
    0, 1 -> Icons.Outlined.WbSunny
    2, 3, 45, 48 -> Icons.Outlined.Cloud
    71, 73, 75, 77, 85, 86 -> Icons.Outlined.AcUnit
    else -> Icons.Outlined.Thunderstorm
}
