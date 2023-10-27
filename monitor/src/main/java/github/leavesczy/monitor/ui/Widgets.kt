package github.leavesczy.monitor.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.monitor.R
import github.leavesczy.monitor.db.MonitorHeader

/**
 * @Author: leavesCZY
 * @Date: 2023/10/27 16:21
 * @Desc:
 */
internal val LazyColumnPadding = PaddingValues(
    start = 20.dp,
    top = 15.dp,
    end = 20.dp,
    bottom = 40.dp
)

@Composable
internal fun MonitorDetailsPage(
    headers: List<MonitorHeader>,
    bodyFormat: String
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        state = rememberLazyListState(),
        contentPadding = LazyColumnPadding
    ) {
        items(
            items = headers,
            contentType = {
                "headers"
            }
        ) {
            MonitorPairItem(key = it.name, value = it.value)
        }
        if (bodyFormat.isNotBlank()) {
            item(contentType = "bodyFormat") {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    text = bodyFormat,
                    style = TextStyle(
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        color = colorResource(id = R.color.monitor_http_status_successful)
                    )
                )
            }
        }
    }
}

@Composable
internal fun MonitorPairItem(key: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            modifier = Modifier
                .weight(weight = 3.2f),
            text = key,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = colorResource(id = R.color.monitor_http_status_successful)
            )
        )
        Text(
            modifier = Modifier
                .weight(weight = 5f)
                .padding(start = 10.dp),
            text = value,
            style = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = colorResource(id = R.color.monitor_http_status_successful)
            )
        )
    }
}