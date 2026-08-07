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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.monitor.R
import github.leavesczy.monitor.internal.db.MonitorHttpHeader
import github.leavesczy.monitor.internal.ui.MonitorJsonHighlighter
import github.leavesczy.monitor.internal.ui.component.MonitorPairItem
import github.leavesczy.monitor.internal.ui.theme.MonitorTheme
import github.leavesczy.monitor.internal.ui.viewmodel.MonitorDetailViewState
import kotlinx.coroutines.launch

@Composable
internal fun MonitorDetailScreen(
    modifier: Modifier,
    detailViewState: MonitorDetailViewState,
    onBack: () -> Unit,
    onCopyText: () -> Unit,
    onShareAsText: () -> Unit,
    onShareAsFile: () -> Unit
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        containerColor = MonitorTheme.colorScheme.background.color,
        contentColor = Color.Transparent,
        contentWindowInsets = WindowInsets.navigationBars,
        topBar = {
            MonitorDetailTopBar(
                title = detailViewState.title,
                onBack = onBack,
                onCopyText = onCopyText,
                onShareAsText = onShareAsText,
                onShareAsFile = onShareAsFile
            )
        }
    ) { innerPadding ->
        MonitorDetailContent(
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .fillMaxSize(),
            detailViewState = detailViewState
        )
    }
}

@Composable
private fun MonitorDetailContent(
    modifier: Modifier,
    detailViewState: MonitorDetailViewState
) {
    val tabs = detailViewState.tabs
    if (tabs.isEmpty()) {
        return
    }
    val pagerState = rememberPagerState(pageCount = {
        tabs.size
    })
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = modifier) {
        PrimaryTabRow(
            modifier = Modifier
                .fillMaxWidth(),
            selectedTabIndex = pagerState.currentPage,
            containerColor = MonitorTheme.colorScheme.topBar.color,
            contentColor = MonitorTheme.colorScheme.onTopBar.color,
            indicator = {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(
                            selectedTabIndex = pagerState.currentPage,
                            matchContentSize = true
                        ),
                    color = MonitorTheme.colorScheme.tabIndicator.color
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page = index)
                        }
                    },
                    text = {
                        Text(
                            modifier = Modifier,
                            text = title,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                )
            }
        }
        HorizontalPager(
            modifier = Modifier
                .weight(weight = 1f)
                .fillMaxWidth(),
            state = pagerState,
            key = {
                tabs[it]
            }
        ) { page ->
            SelectionContainer {
                when (page) {
                    0 -> {
                        MonitorDetailOverviewPage(overview = detailViewState.overview)
                    }

                    1 -> {
                        MonitorDetailBodyPage(
                            headers = detailViewState.requestHeaders,
                            bodyFormatted = detailViewState.requestBodyFormatted
                        )
                    }

                    else -> {
                        MonitorDetailBodyPage(
                            headers = detailViewState.responseHeaders,
                            bodyFormatted = detailViewState.responseBodyFormatted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonitorDetailTopBar(
    title: String,
    onBack: () -> Unit,
    onCopyText: () -> Unit,
    onShareAsText: () -> Unit,
    onShareAsFile: () -> Unit
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
                .background(color = MonitorTheme.colorScheme.topBar.color)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(size = 24.dp)
                    .clickable(onClick = onBack),
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                tint = MonitorTheme.colorScheme.onTopBar.color,
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .weight(weight = 1f)
                    .padding(horizontal = 16.dp),
                text = title,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium,
                color = MonitorTheme.colorScheme.onTopBar.color,
                style = LocalTextStyle.current.copy(lineBreak = LineBreak.Paragraph)
            )
            Icon(
                modifier = Modifier
                    .size(size = 24.dp)
                    .clickable(onClick = {
                        menuExpanded = true
                    }),
                imageVector = Icons.Filled.Share,
                tint = MonitorTheme.colorScheme.onTopBar.color,
                contentDescription = null
            )
        }
        Box(
            modifier = Modifier
                .align(alignment = Alignment.TopEnd)
                .padding(end = 10.dp)
        ) {
            MonitorShareMenu(
                expanded = menuExpanded,
                onDismissRequest = {
                    menuExpanded = false
                },
                onCopyText = onCopyText,
                onShareAsText = onShareAsText,
                onShareAsFile = onShareAsFile
            )
        }
    }
}

@Composable
private fun MonitorShareMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onCopyText: () -> Unit,
    onShareAsText: () -> Unit,
    onShareAsFile: () -> Unit
) {
    DropdownMenu(
        modifier = Modifier
            .background(color = MonitorTheme.colorScheme.background.color),
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuItem(
            text = {
                MonitorShareMenuText(text = stringResource(id = R.string.monitor_copy))
            },
            onClick = {
                onDismissRequest()
                onCopyText()
            }
        )
        DropdownMenuItem(
            text = {
                MonitorShareMenuText(text = stringResource(id = R.string.monitor_share_as_text))
            },
            onClick = {
                onDismissRequest()
                onShareAsText()
            }
        )
        DropdownMenuItem(
            text = {
                MonitorShareMenuText(text = stringResource(id = R.string.monitor_share_as_file))
            },
            onClick = {
                onDismissRequest()
                onShareAsFile()
            }
        )
    }
}

@Composable
private fun MonitorShareMenuText(text: String) {
    Text(
        modifier = Modifier,
        text = text,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        color = MonitorTheme.colorScheme.primaryText.color
    )
}

@Composable
private fun MonitorDetailOverviewPage(overview: List<MonitorHttpHeader>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 14.dp, end = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(space = 7.dp)
    ) {
        itemsIndexed(
            items = overview,
            key = { index, header ->
                "${header.name}\u0000$index"
            }
        ) { _, header ->
            MonitorPairItem(header = header)
        }
    }
}

@Composable
private fun MonitorDetailBodyPage(
    headers: List<MonitorHttpHeader>,
    bodyFormatted: String
) {
    val keyColor = MonitorTheme.colorScheme.jsonKey.color
    val valueColor = MonitorTheme.colorScheme.jsonValue.color
    val literalColor = MonitorTheme.colorScheme.jsonLiteral.color
    val braceColor = MonitorTheme.colorScheme.jsonBrace.color
    val punctuationColor = MonitorTheme.colorScheme.jsonPunctuation.color
    val highlightedBody = remember(
        bodyFormatted,
        keyColor,
        valueColor,
        literalColor,
        braceColor,
        punctuationColor
    ) {
        MonitorJsonHighlighter.highlight(
            json = bodyFormatted,
            keyColor = keyColor,
            valueColor = valueColor,
            literalColor = literalColor,
            braceColor = braceColor,
            punctuationColor = punctuationColor
        )
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 14.dp, end = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(space = 7.dp)
    ) {
        itemsIndexed(
            items = headers,
            key = { index, header ->
                "${header.name}\u0000$index"
            }
        ) { _, header ->
            MonitorPairItem(header = header)
        }
        if (bodyFormatted.isNotBlank()) {
            item(key = "bodyFormatted") {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = if (headers.isEmpty()) {
                                0.dp
                            } else {
                                8.dp
                            }
                        ),
                    text = highlightedBody,
                    fontSize = 12.5.sp,
                    lineHeight = 19.sp,
                    fontFamily = FontFamily.Monospace,
                    color = punctuationColor
                )
            }
        }
    }
}