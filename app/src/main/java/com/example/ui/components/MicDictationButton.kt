package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.theme.NexusNeonBlue

@Composable
fun MicDictationButton(
    contentDescription: String = "Ditar por voz",
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tag: String = "mic_dictation_button"
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .testTag(tag)
    ) {
        Icon(
            imageVector = Icons.Outlined.Mic,
            contentDescription = contentDescription,
            tint = NexusNeonBlue
        )
    }
}
