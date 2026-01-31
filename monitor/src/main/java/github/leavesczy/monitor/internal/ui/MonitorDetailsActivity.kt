package github.leavesczy.monitor.internal.ui

import android.os.Bundle
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import github.leavesczy.monitor.R
import github.leavesczy.monitor.internal.db.MonitorPair
import github.leavesczy.monitor.internal.ui.logic.MonitorDetailOverviewPageViewState
import github.leavesczy.monitor.internal.ui.logic.MonitorDetailPageViewState
import github.leavesczy.monitor.internal.ui.logic.MonitorDetailRequestPageViewState
import github.leavesczy.monitor.internal.ui.logic.MonitorDetailResponsePageViewState
import github.leavesczy.monitor.internal.ui.logic.MonitorDetailsViewModel
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 17:04
 * @Desc:
 */
internal class MonitorDetailsActivity : AppCompatActivity() {

    companion object {

        const val KEY_MONITOR_ID = "monitorId"

    }

    private val monitorId by lazy(mode = LazyThreadSafetyMode.NONE) {
        intent.getLongExtra(KEY_MONITOR_ID, 0)
    }

    private val monitorDetailsViewModel by viewModels<MonitorDetailsViewModel> {
        object : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MonitorDetailsViewModel(
                    monitorId = monitorId,
                    application = application
                ) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {
            MonitorTheme {
                MonitorDetailsPage(
                    mainPageViewState = monitorDetailsViewModel.mainPageViewState,
                    overviewPageViewState = monitorDetailsViewModel.overviewPageViewState,
                    requestPageViewState = monitorDetailsViewModel.requestPageViewState,
                    responsePageViewState = monitorDetailsViewModel.responsePageViewState,
                    copyText = monitorDetailsViewModel::copyText,
                    shareAsText = monitorDetailsViewModel::shareAsText,
                    shareAsFile = monitorDetailsViewModel::shareAsFile
                )
            }
        }
    }

}

@Composable
private fun MonitorDetailsPage(
    mainPageViewState: MonitorDetailPageViewState,
    overviewPageViewState: MonitorDetailOverviewPageViewState,
    requestPageViewState: MonitorDetailRequestPageViewState,
    responsePageViewState: MonitorDetailResponsePageViewState,
    copyText: () -> Unit,
    shareAsText: () -> Unit,
    shareAsFile: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MonitorTheme.colorScheme.c_FFFFFFFF_FF101010.color,
        contentColor = Color.Transparent,
        contentWindowInsets = WindowInsets.navigationBars,
        topBar = {
            MonitorDetailsTopBar(
                title = mainPageViewState.title,
                copyText = copyText,
                shareAsText = shareAsText,
                shareAsFile = shareAsFile
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .fillMaxSize()
        ) {
            val coroutineScope = rememberCoroutineScope()
            val pagerState = rememberPagerState {
                mainPageViewState.tabTagList.size
            }
            ScrollableTabRow(
                tagList = mainPageViewState.tabTagList,
                selectedTabIndex = pagerState.currentPage,
                scrollToPage = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(page = it)
                    }
                }
            )
            HorizontalPager(
                modifier = Modifier
                    .weight(weight = 1f)
                    .fillMaxWidth(),
                state = pagerState,
                key = {
                    mainPageViewState.tabTagList[it]
                }
            ) {
                when (it) {
                    0 -> {
                        SelectionContainer {
                            MonitorDetailsOverviewPage(
                                pageViewState = overviewPageViewState
                            )
                        }
                    }

                    1 -> {
                        SelectionContainer {
                            MonitorDetailsPage(
                                headers = requestPageViewState.headers,
                                bodyFormatted = requestPageViewState.bodyFormatted
                            )
                        }
                    }

                    2 -> {
                        SelectionContainer {
                            MonitorDetailsPage(
                                headers = responsePageViewState.headers,
                                bodyFormatted = responsePageViewState.bodyFormatted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonitorDetailsTopBar(
    title: String,
    copyText: () -> Unit,
    shareAsText: () -> Unit,
    shareAsFile: () -> Unit
) {
    var menuExpanded by remember {
        mutableStateOf(value = false)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .background(color = MonitorTheme.colorScheme.c_FF2196F3_FF2E3036.color)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val onBackPressedDispatcher =
                LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
            Icon(
                modifier = Modifier
                    .size(size = 24.dp)
                    .clickable {
                        onBackPressedDispatcher?.onBackPressed()
                    },
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                tint = MonitorTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color,
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .weight(weight = 1f)
                    .padding(horizontal = 16.dp),
                text = title,
                fontSize = 17.sp,
                lineHeight = 18.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Medium,
                color = MonitorTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color,
                style = LocalTextStyle.current.copy(lineBreak = LineBreak.Paragraph)
            )
            Icon(
                modifier = Modifier
                    .size(size = 24.dp)
                    .clickable(onClick = {
                        menuExpanded = true
                    }),
                imageVector = Icons.Filled.Share,
                tint = MonitorTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color,
                contentDescription = null
            )
        }
        Box(
            modifier = Modifier
                .align(alignment = Alignment.TopEnd)
                .padding(end = 10.dp)
        ) {
            TopBarDropdownMenu(
                modifier = Modifier,
                expanded = menuExpanded,
                onDismissRequest = {
                    menuExpanded = false
                },
                copyText = copyText,
                shareAsText = shareAsText,
                shareAsFile = shareAsFile
            )
        }
    }
}

@Composable
private fun TopBarDropdownMenu(
    modifier: Modifier,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    copyText: () -> Unit,
    shareAsText: () -> Unit,
    shareAsFile: () -> Unit
) {
    DropdownMenu(
        modifier = modifier
            .background(color = MonitorTheme.colorScheme.c_FFFFFFFF_FF101010.color),
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(id = R.string.monitor_copy),
                    fontSize = 18.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = MonitorTheme.colorScheme.c_FF001018_DEFFFFFF.color
                )
            },
            onClick = {
                onDismissRequest()
                copyText()
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(id = R.string.monitor_share_as_text),
                    fontSize = 18.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = MonitorTheme.colorScheme.c_FF001018_DEFFFFFF.color
                )
            },
            onClick = {
                onDismissRequest()
                shareAsText()
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(id = R.string.monitor_share_as_file),
                    fontSize = 18.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = MonitorTheme.colorScheme.c_FF001018_DEFFFFFF.color
                )
            },
            onClick = {
                onDismissRequest()
                shareAsFile()
            }
        )
    }
}

@Composable
private fun ScrollableTabRow(
    tagList: List<String>,
    selectedTabIndex: Int,
    scrollToPage: (Int) -> Unit
) {
    SecondaryTabRow(
        modifier = Modifier
            .fillMaxWidth(),
        containerColor = MonitorTheme.colorScheme.c_FF2196F3_FF2E3036.color,
        contentColor = MonitorTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color,
        selectedTabIndex = selectedTabIndex,
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(
                    selectedTabIndex = selectedTabIndex,
                    matchContentSize = false
                ),
                color = MonitorTheme.colorScheme.c_FF8EBBEA_660085EB.color
            )
        },
        divider = {

        }
    ) {
        tagList.forEachIndexed { index, tag ->
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        scrollToPage(index)
                    }
                    .padding(vertical = 12.dp),
                text = tag,
                fontSize = 15.sp,
                lineHeight = 16.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                color = if (index == selectedTabIndex) {
                    MonitorTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color
                } else {
                    MonitorTheme.colorScheme.c_DEFFFFFF_DEFFFFFF.color
                }
            )
        }
    }
}

@Composable
private fun MonitorDetailsOverviewPage(pageViewState: MonitorDetailOverviewPageViewState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(
            start = 14.dp,
            top = 14.dp,
            end = 14.dp,
            bottom = 30.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 4.dp,
            alignment = Alignment.Top
        )
    ) {
        items(
            items = pageViewState.overview,
            key = {
                it.name
            },
            contentType = {
                "MonitorPairItem"
            }
        ) {
            MonitorPairItem(pair = it)
        }
    }
}

@Composable
private fun MonitorDetailsPage(
    headers: List<MonitorPair>,
    bodyFormatted: String
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(
            start = 14.dp,
            top = 14.dp,
            end = 14.dp,
            bottom = 30.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 4.dp,
            alignment = Alignment.Top
        )
    ) {
        items(
            items = headers,
            key = {
                it.name
            },
            contentType = {
                "MonitorPairItem"
            }
        ) {
            MonitorPairItem(pair = it)
        }
        if (bodyFormatted.isNotBlank()) {
            item(
                key = "bodyFormatted",
                contentType = "bodyFormatted"
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = if (headers.isEmpty()) {
                                0.dp
                            } else {
                                17.dp
                            }
                        ),
                    text = bodyFormatted,
                    fontSize = 15.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Normal,
                    color = MonitorTheme.colorScheme.c_B3001018_B3FFFFFF.color
                )
            }
        }
    }
}

@Composable
private fun MonitorPairItem(pair: MonitorPair) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(space = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            modifier = Modifier
                .weight(weight = 3.4f),
            text = pair.name,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MonitorTheme.colorScheme.c_FF001018_DEFFFFFF.color
        )
        Text(
            modifier = Modifier
                .weight(weight = 5f),
            text = pair.value,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Normal,
            color = MonitorTheme.colorScheme.c_B3001018_B3FFFFFF.color
        )
    }
}