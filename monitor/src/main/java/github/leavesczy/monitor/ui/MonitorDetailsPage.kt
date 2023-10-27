package github.leavesczy.monitor.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.monitor.R
import github.leavesczy.monitor.logic.MonitorDetailOverviewPageViewState
import github.leavesczy.monitor.logic.MonitorDetailPageViewState
import github.leavesczy.monitor.logic.MonitorDetailRequestPageViewState
import github.leavesczy.monitor.logic.MonitorDetailResponsePageViewState
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2023/10/27 16:12
 * @Desc:
 */
@Composable
internal fun MonitorDetailsPage(
    mainPageViewState: MonitorDetailPageViewState,
    overviewPageViewState: MonitorDetailOverviewPageViewState,
    requestPageViewState: MonitorDetailRequestPageViewState,
    responsePageViewState: MonitorDetailResponsePageViewState,
    onClickBack: () -> Unit,
    onClickShare: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(connection = scrollBehavior.nestedScrollConnection),
        containerColor = Color.White,
        topBar = {
            MonitorDetailsTopBar(
                scrollBehavior = scrollBehavior,
                title = mainPageViewState.title,
                onClickBack = onClickBack,
                onClickShare = onClickShare
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
        ) {
            val coroutineScope = rememberCoroutineScope()
            val pagerState = rememberPagerState(
                initialPage = 0,
                initialPageOffsetFraction = 0f
            ) {
                mainPageViewState.tabTagList.size
            }
            ScrollableTabRow(
                tagList = mainPageViewState.tabTagList,
                selectedTabIndex = pagerState.currentPage,
                scrollToPage = {
                    if (pagerState.currentPage != it) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page = it)
                        }
                    }
                }
            )
            SelectionContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 1f)
            ) {
                HorizontalPager(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = pagerState
                ) {
                    when (it) {
                        0 -> {
                            MonitorDetailsOverviewPage(pageViewState = overviewPageViewState)
                        }

                        1 -> {
                            MonitorDetailsRequestPage(pageViewState = requestPageViewState)
                        }

                        2 -> {
                            MonitorDetailsResponsePage(pageViewState = responsePageViewState)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonitorDetailsTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    title: String,
    onClickBack: () -> Unit,
    onClickShare: () -> Unit
) {
    CenterAlignedTopAppBar(
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
                modifier = Modifier.fillMaxWidth(),
                text = title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 19.sp
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
                        imageVector = Icons.Filled.Share,
                        contentDescription = null
                    )
                },
                onClick = onClickShare
            )
        }
    )
}

@Composable
private fun ScrollableTabRow(
    tagList: List<String>,
    selectedTabIndex: Int,
    scrollToPage: (Int) -> Unit
) {
    TabRow(
        modifier = Modifier.fillMaxWidth(),
        selectedTabIndex = selectedTabIndex,
        containerColor = colorResource(id = R.color.monitor_top_bar),
        indicator = @Composable { tabPositions ->
            if (selectedTabIndex < tabPositions.size) {
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = Color.White
                )
            }
        },
        divider = @Composable {
            Divider()
        }
    ) {
        tagList.forEachIndexed { index, item ->
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        scrollToPage(index)
                    }
                    .padding(vertical = 12.dp),
                text = item,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = if (index == selectedTabIndex) {
                    Color.White
                } else {
                    Color(0xB3FFFFFF)
                }
            )
        }
    }
}