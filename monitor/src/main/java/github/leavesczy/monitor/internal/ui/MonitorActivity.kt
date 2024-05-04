package github.leavesczy.monitor.internal.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import github.leavesczy.monitor.R
import github.leavesczy.monitor.internal.db.Monitor
import github.leavesczy.monitor.internal.db.MonitorStatus
import github.leavesczy.monitor.internal.ui.logic.MonitorViewModel

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 15:58
 * @Desc:
 */
internal class MonitorActivity : AppCompatActivity() {

    private val monitorViewModel by viewModels<MonitorViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MonitorTheme {
                val pagingItems = monitorViewModel.getMonitors().collectAsLazyPagingItems()
                MonitorPage(
                    monitorLazyPagingItems = pagingItems,
                    onClickBack = ::onClickBack,
                    onClickClear = monitorViewModel::onClickClear,
                    onClickMonitorItem = ::onClickMonitorItem
                )
            }
        }
    }

    private fun onClickBack() {
        finish()
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
    onClickBack: () -> Unit,
    onClickClear: () -> Unit,
    onClickMonitorItem: (Monitor) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            MonitorTopBar(
                onClickBack = onClickBack,
                onClickClear = onClickClear
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding),
            state = rememberLazyListState(),
            contentPadding = PaddingValues(bottom = 60.dp)
        ) {
            items(
                count = monitorLazyPagingItems.itemCount,
                key = monitorLazyPagingItems.itemKey {
                    it.id
                },
                contentType = monitorLazyPagingItems.itemContentType {
                    "monitor"
                }
            ) { index ->
                monitorLazyPagingItems[index]?.let {
                    MonitorItem(monitor = it, onClick = onClickMonitorItem)
                }
            }
        }
    }
}

@Composable
private fun MonitorItem(monitor: Monitor, onClick: (Monitor) -> Unit) {
    val titleColor: Int
    val subtitleColor: Int
    when (monitor.httpStatus) {
        MonitorStatus.Requesting -> {
            titleColor = R.color.monitor_http_status_requesting_title
            subtitleColor = R.color.monitor_http_status_requesting_subtitle
        }

        MonitorStatus.Complete -> {
            if (monitor.responseCode == 200) {
                titleColor = R.color.monitor_http_status_successful_title
                subtitleColor = R.color.monitor_http_status_successful_subtitle
            } else {
                titleColor = R.color.monitor_http_status_unsuccessful_title
                subtitleColor = R.color.monitor_http_status_unsuccessful_subtitle
            }
        }

        MonitorStatus.Failed -> {
            titleColor = R.color.monitor_http_status_unsuccessful_title
            subtitleColor = R.color.monitor_http_status_unsuccessful_subtitle
        }
    }
    val titleTextStyle = TextStyle(
        fontSize = 17.sp,
        lineHeight = 19.sp,
        color = colorResource(id = titleColor),
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium
    )
    val subtitleTextStyle = TextStyle(
        fontSize = 14.sp,
        lineHeight = 16.sp,
        color = colorResource(id = subtitleColor),
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(monitor)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 14.dp, top = 10.dp)
        ) {
            Text(
                modifier = Modifier
                    .width(width = 40.dp),
                text = monitor.responseCodeFormat,
                style = titleTextStyle
            )
            Column(
                modifier = Modifier
                    .weight(weight = 1f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .weight(weight = 1f),
                        text = monitor.pathWithQuery,
                        style = titleTextStyle
                    )
                    Text(
                        modifier = Modifier,
                        text = monitor.id.toString(),
                        style = titleTextStyle
                    )
                }
                Text(
                    modifier = Modifier
                        .padding(vertical = 3.dp),
                    text = monitor.host,
                    style = subtitleTextStyle
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier,
                        text = monitor.requestDateMDHMS,
                        style = subtitleTextStyle
                    )
                    Text(
                        modifier = Modifier,
                        text = monitor.requestDurationFormat,
                        style = subtitleTextStyle
                    )
                    Text(
                        modifier = Modifier,
                        text = monitor.totalSizeFormat,
                        style = subtitleTextStyle
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )
    }
}

@Composable
private fun MonitorTopBar(
    onClickBack: () -> Unit,
    onClickClear: () -> Unit
) {
    TopAppBar(
        modifier = Modifier,
        title = {
            Text(
                modifier = Modifier,
                fontSize = 20.sp,
                text = stringResource(id = R.string.monitor_monitor)
            )
        },
        navigationIcon = {
            IconButton(
                content = {
                    Icon(
                        modifier = Modifier
                            .size(size = 24.dp),
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                },
                onClick = onClickBack
            )
        },
        actions = {
            IconButton(
                content = {
                    Icon(
                        modifier = Modifier
                            .size(size = 24.dp),
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = null
                    )
                },
                onClick = onClickClear
            )
        }
    )
}