package com.curzyori.exapk.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.curzyori.exapk.R
import com.curzyori.exapk.data.model.FilterType
import com.curzyori.exapk.data.model.SortType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(
    selectedFilter: FilterType,
    onFilterChange: (FilterType) -> Unit,
    selectedSort: SortType,
    onSortChange: (SortType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Filter group
        FilterGroup(
            label = stringResource(R.string.filter_label),
            chips = {
                FilterChip(
                    selected = selectedFilter == FilterType.USER_APPS,
                    onClick = { onFilterChange(FilterType.USER_APPS) },
                    label = { Text(stringResource(R.string.filter_user)) }
                )
                FilterChip(
                    selected = selectedFilter == FilterType.SYSTEM_APPS,
                    onClick = { onFilterChange(FilterType.SYSTEM_APPS) },
                    label = { Text(stringResource(R.string.filter_system)) }
                )
                FilterChip(
                    selected = selectedFilter == FilterType.ALL,
                    onClick = { onFilterChange(FilterType.ALL) },
                    label = { Text(stringResource(R.string.filter_all)) }
                )
            }
        )

        // Sort group
        FilterGroup(
            label = stringResource(R.string.sort_label),
            chips = {
                FilterChip(
                    selected = selectedSort == SortType.NAME,
                    onClick = { onSortChange(SortType.NAME) },
                    label = { Text(stringResource(R.string.sort_name)) }
                )
                FilterChip(
                    selected = selectedSort == SortType.SIZE,
                    onClick = { onSortChange(SortType.SIZE) },
                    label = { Text(stringResource(R.string.sort_size)) }
                )
                FilterChip(
                    selected = selectedSort == SortType.DATE,
                    onClick = { onSortChange(SortType.DATE) },
                    label = { Text(stringResource(R.string.sort_date)) }
                )
            }
        )
    }
}

@Composable
private fun FilterGroup(
    label: String,
    chips: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                chips()
            }
        }
    }
}
