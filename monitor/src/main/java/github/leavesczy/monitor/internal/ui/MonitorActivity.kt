package github.leavesczy.monitor.internal.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import github.leavesczy.monitor.R
import github.leavesczy.monitor.internal.db.Monitor
import github.leavesczy.monitor.internal.db.MonitorHttpState
import github.leavesczy.monitor.internal.ui.logic.MonitorViewModel

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 15:58
 * @Desc:
 */
internal class MonitorActivity : AppCompatActivity() {

    private val monitorViewModel by viewModels<MonitorViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {
            MonitorTheme {
                val pagingItems = monitorViewModel.pagingDataFlow.collectAsLazyPagingItems()
                MonitorPage(
                    monitorLazyPagingItems = pagingItems,
                    onClickClear = monitorViewModel::onClickClear,
                    onClickMonitorItem = ::onClickMonitorItem
                )
            }
        }
    }

    private fun onClickMonitorItem(monitor: Monitor) {
        val intent = Intent(this, MonitorDetailsActivity::class.java)
        intent.putExtra(MonitorDetailsActivity.KEY_MONITOR_ID, monitor.id)
        startActivity(intent)
    }

}

@Composable
private fun MonitorPage(
    monitorLazyPagingItems: LazyPagingItems<Monitor>,
    onClickClear: () -> Unit,
    onClickMonitorItem: (Monitor) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MonitorTheme.colorScheme.c_FFFFFFFF_FF101010.color,
        contentColor = Color.Transparent,
        contentWindowInsets = WindowInsets.navigationBars,
        topBar = {
            MonitorTopBar(onClickClear = onClickClear)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .fillMaxSize(),
            state = rememberLazyListState(),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(
                count = monitorLazyPagingItems.itemCount,
                key = monitorLazyPagingItems.itemKey {
                    it.id
                },
                contentType = monitorLazyPagingItems.itemContentType {
                    "MonitorItem"
                }
            ) { index ->
                val monitor = monitorLazyPagingItems[index]
                if (monitor != null) {
                    MonitorItem(
                        monitor = monitor,
                        onClick = onClickMonitorItem
                    )
                }
            }
        }
    }
}

@Composable
private fun MonitorTopBar(onClickClear: () -> Unit) {
    Row(
        modifier = Modifier
            .background(color = MonitorTheme.colorScheme.c_FF2196F3_FF2E3036.color)
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier,
            text = stringResource(id = R.string.monitor_monitor),
            fontSize = 19.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            color = MonitorTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color
        )
        Icon(
            modifier = Modifier
                .size(size = 24.dp)
                .clickable(onClick = onClickClear),
            imageVector = Icons.Filled.DeleteOutline,
            tint = MonitorTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color,
            contentDescription = null
        )
    }
}

@Composable
private fun MonitorItem(
    monitor: Monitor,
    onClick: (Monitor) -> Unit
) {
    val titleColor = when (monitor.httpState) {
        MonitorHttpState.Requesting -> {
            MonitorTheme.colorScheme.c_B3001018_B3FFFFFF.color
        }

        MonitorHttpState.Complete -> {
            if (monitor.responseCode == 200) {
                MonitorTheme.colorScheme.c_FF001018_DEFFFFFF.color
            } else {
                MonitorTheme.colorScheme.c_FFFF545C_FFFA525A.color
            }
        }

        MonitorHttpState.Failed -> {
            MonitorTheme.colorScheme.c_FFFF545C_FFFA525A.color
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(monitor)
            }
    ) {
        Row(
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = 6.dp),
            verticalAlignment = Alignment.Top
        ) {
            ItemTitle(
                modifier = Modifier
                    .widthIn(min = 30.dp),
                text = monitor.responseCodeFormatted,
                color = titleColor
            )
            Column(
                modifier = Modifier
                    .weight(weight = 1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(
                    space = 3.dp,
                    alignment = Alignment.Top
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ItemTitle(
                        modifier = Modifier
                            .weight(weight = 1f),
                        text = monitor.pathWithQuery,
                        color = titleColor
                    )
                    ItemTitle(
                        modifier = Modifier,
                        text = monitor.id.toString(),
                        color = titleColor
                    )
                }
                ItemSubtitle(
                    modifier = Modifier,
                    text = monitor.host
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ItemSubtitle(
                        modifier = Modifier,
                        text = monitor.requestTimeFormatted
                    )
                    ItemSubtitle(
                        modifier = Modifier,
                        text = monitor.requestDurationFormatted
                    )
                    ItemSubtitle(
                        modifier = Modifier,
                        text = monitor.totalSizeFormatted
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .fillMaxWidth()
                .height(height = 0.8.dp)
                .background(color = MonitorTheme.colorScheme.c_FFEFF1F3_FF333333.color)
        )
    }
}

@Composable
private fun ItemTitle(
    modifier: Modifier,
    text: String,
    color: Color
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 16.sp,
        lineHeight = 18.sp,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Start,
        fontWeight = FontWeight.Bold,
        color = color,
        style = LocalTextStyle.current.copy(lineBreak = LineBreak.Paragraph)
    )
}

@Composable
private fun ItemSubtitle(
    modifier: Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        textAlign = TextAlign.Start,
        fontSize = 14.sp,
        lineHeight = 15.sp,
        fontWeight = FontWeight.Normal,
        color = MonitorTheme.colorScheme.c_B3001018_B3FFFFFF.color
    )
}