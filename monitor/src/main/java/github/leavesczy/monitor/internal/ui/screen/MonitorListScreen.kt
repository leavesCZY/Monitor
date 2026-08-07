package github.leavesczy.monitor.internal.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import github.leavesczy.monitor.R
import github.leavesczy.monitor.internal.db.MonitorRecord
import github.leavesczy.monitor.internal.ui.component.MonitorSubtitleText
import github.leavesczy.monitor.internal.ui.component.MonitorTitleText
import github.leavesczy.monitor.internal.ui.component.monitorStatusColor
import github.leavesczy.monitor.internal.ui.model.httpState
import github.leavesczy.monitor.internal.ui.model.pathWithQuery
import github.leavesczy.monitor.internal.ui.model.requestDurationFormatted
import github.leavesczy.monitor.internal.ui.model.requestTimeFormatted
import github.leavesczy.monitor.internal.ui.model.responseCodeFormatted
import github.leavesczy.monitor.internal.ui.model.totalSizeFormatted
import github.leavesczy.monitor.internal.ui.theme.MonitorTheme
import kotlinx.coroutines.flow.Flow

@Composable
internal fun MonitorListScreen(
    pagingDataFlow: Flow<PagingData<MonitorRecord>>,
    onClickClear: () -> Unit,
    onClickRecord: (MonitorRecord) -> Unit
) {
    val pagingItems = pagingDataFlow.collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MonitorTheme.colorScheme.background.color,
        contentColor = Color.Transparent,
        contentWindowInsets = WindowInsets.navigationBars,
        topBar = {
            MonitorListTopBar(onClickClear = onClickClear)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(
                count = pagingItems.itemCount,
                key = pagingItems.itemKey { record ->
                    record.id
                },
                contentType = pagingItems.itemContentType {
                    "MonitorRecordItem"
                }
            ) { index ->
                val record = pagingItems[index]
                if (record != null) {
                    MonitorListItem(
                        record = record,
                        onClick = onClickRecord
                    )
                }
            }
        }
    }
}

@Composable
private fun MonitorListTopBar(onClickClear: () -> Unit) {
    Row(
        modifier = Modifier
            .background(color = MonitorTheme.colorScheme.topBar.color)
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.monitor_title),
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = MonitorTheme.colorScheme.onTopBar.color
        )
        Icon(
            modifier = Modifier
                .size(size = 24.dp)
                .clickable(onClick = onClickClear),
            imageVector = Icons.Filled.DeleteOutline,
            tint = MonitorTheme.colorScheme.onTopBar.color,
            contentDescription = null
        )
    }
}

@Composable
private fun MonitorListItem(
    record: MonitorRecord,
    onClick: (MonitorRecord) -> Unit
) {
    val titleColor = monitorStatusColor(
        httpState = record.httpState,
        responseCode = record.responseCode
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                onClick(record)
            })
    ) {
        Row(
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = 6.dp),
            verticalAlignment = Alignment.Top
        ) {
            MonitorTitleText(
                modifier = Modifier.widthIn(min = 30.dp),
                text = record.responseCodeFormatted,
                color = titleColor
            )
            Column(
                modifier = Modifier.weight(weight = 1f),
                verticalArrangement = Arrangement.spacedBy(space = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MonitorTitleText(
                        modifier = Modifier.weight(weight = 1f),
                        text = record.pathWithQuery,
                        color = titleColor
                    )
                    MonitorTitleText(
                        text = record.id.toString(),
                        color = titleColor
                    )
                }
                MonitorSubtitleText(text = record.host)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = 15.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MonitorSubtitleText(text = record.requestTimeFormatted)
                    MonitorSubtitleText(text = record.requestDurationFormatted)
                    MonitorSubtitleText(text = record.totalSizeFormatted)
                }
            }
        }
        Box(
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .fillMaxWidth()
                .height(height = 0.8.dp)
                .background(color = MonitorTheme.colorScheme.divider.color)
        )
    }
}