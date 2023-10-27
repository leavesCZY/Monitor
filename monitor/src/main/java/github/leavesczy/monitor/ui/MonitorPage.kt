package github.leavesczy.monitor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.monitor.R
import github.leavesczy.monitor.db.Monitor
import github.leavesczy.monitor.db.MonitorStatus

/**
 * @Author: leavesCZY
 * @Date: 2023/10/27 16:18
 * @Desc:
 */
@Composable
internal fun MonitorPage(
    onClickBack: () -> Unit,
    onClickClear: () -> Unit,
    dataList: List<Monitor>,
    onClickMonitorItem: (Monitor) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(connection = scrollBehavior.nestedScrollConnection),
        containerColor = Color.White,
        topBar = {
            MonitorTopBar(
                scrollBehavior = scrollBehavior,
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
                items = dataList,
                key = {
                    it.id
                },
                contentType = {
                    "MonitorHttp"
                }
            ) {
                MonitorItem(monitor = it, onClick = onClickMonitorItem)
            }
        }
    }
}

@Composable
private fun MonitorItem(monitor: Monitor, onClick: (Monitor) -> Unit) {
    val color = when (monitor.httpStatus) {
        MonitorStatus.Requesting -> {
            R.color.monitor_http_status_requesting
        }

        MonitorStatus.Complete -> {
            if (monitor.responseCode == 200) {
                R.color.monitor_http_status_successful
            } else {
                R.color.monitor_http_status_unsuccessful
            }
        }

        MonitorStatus.Failed -> {
            R.color.monitor_http_status_unsuccessful
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(id = R.color.monitor_background))
            .clickable(
                onClickLabel = null,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick(monitor)
            }
            .padding(start = 12.dp, end = 12.dp, top = 10.dp)
    ) {
        val responseCodeWidth = 40.dp
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val textStyle = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                color = colorResource(id = color)
            )
            Text(
                modifier = Modifier.width(width = responseCodeWidth),
                text = monitor.responseCodeFormat,
                style = textStyle
            )
            Text(
                modifier = Modifier.weight(weight = 1f),
                text = monitor.pathWithQuery,
                style = textStyle
            )
            Text(
                modifier = Modifier,
                text = monitor.id.toString(),
                style = textStyle
            )
        }
        val textStyle = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = colorResource(id = color).copy(alpha = 0.8f)
        )
        Text(
            modifier = Modifier
                .padding(
                    start = responseCodeWidth,
                    top = 2.dp,
                    bottom = 2.dp
                ),
            text = String.format("%s://%s", monitor.scheme, monitor.host),
            style = textStyle
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = responseCodeWidth),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier,
                text = monitor.requestDateMDHMS,
                style = textStyle
            )
            Text(
                modifier = Modifier,
                text = monitor.requestDurationFormat,
                style = textStyle
            )
            Text(
                modifier = Modifier,
                text = monitor.totalSizeFormat,
                style = textStyle
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .height(height = 4.dp)
                .background(color = colorResource(id = R.color.monitor_separator))
        )
    }
}

@Composable
private fun MonitorTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onClickBack: () -> Unit,
    onClickClear: () -> Unit
) {
    TopAppBar(
        modifier = Modifier,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = colorResource(id = R.color.monitor_top_bar),
            navigationIconContentColor = Color.White,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        title = {
            Text(
                modifier = Modifier,
                text = stringResource(id = R.string.monitor_library_name),
                fontSize = 20.sp
            )
        },
        navigationIcon = {
            IconButton(
                content = {
                    Icon(
                        modifier = Modifier
                            .size(size = 26.dp),
                        imageVector = Icons.Default.ArrowBack,
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
                        modifier = Modifier.size(size = 26.dp),
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = null
                    )
                },
                onClick = onClickClear
            )
        }
    )
}