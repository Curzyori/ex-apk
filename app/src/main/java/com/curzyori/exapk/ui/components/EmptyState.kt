package com.curzyori.exapk.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.curzyori.exapk.R

enum class EmptyStateType {
    NO_FILTER,
    NO_SEARCH,
    ERROR
}

@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier,
    type: EmptyStateType = EmptyStateType.NO_FILTER,
    showRetry: Boolean = false,
    onRetry: (() -> Unit)? = null
) {
    val (icon, iconDesc, description) = when (type) {
        EmptyStateType.NO_FILTER -> Triple(
            Icons.Default.FilterListOff,
            stringResource(R.string.empty_filter),
            stringResource(R.string.empty_filter_hint)
        )
        EmptyStateType.NO_SEARCH -> Triple(
            Icons.Default.SearchOff,
            stringResource(R.string.empty_search),
            stringResource(R.string.empty_search_hint)
        )
        EmptyStateType.ERROR -> Triple(
            Icons.Default.ErrorOutline,
            stringResource(R.string.error_occurred),
            stringResource(R.string.error_hint)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = iconDesc,
                modifier = Modifier.size(64.dp),
                tint = when (type) {
                    EmptyStateType.ERROR -> MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            if (showRetry && onRetry != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetry) {
                    Text(stringResource(R.string.retry))
                }
            }
        }
    }
}
