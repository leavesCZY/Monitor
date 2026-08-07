package github.leavesczy.monitor.internal.ui

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import github.leavesczy.monitor.internal.ui.screen.MonitorDetailScreen
import github.leavesczy.monitor.internal.ui.screen.MonitorListScreen
import github.leavesczy.monitor.internal.ui.theme.MonitorTheme
import github.leavesczy.monitor.internal.ui.viewmodel.MonitorViewModel

internal class MonitorActivity : AppCompatActivity() {

    private val monitorViewModel by viewModels<MonitorViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {
            MonitorTheme {
                MonitorPage(viewModel = monitorViewModel)
            }
        }
    }

}

@Composable
private fun MonitorPage(viewModel: MonitorViewModel) {
    val showDetail = viewModel.selectedRecordId != null
    val detailViewState = viewModel.detailViewState
    BackHandler(enabled = showDetail) {
        viewModel.closeDetail()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        MonitorListScreen(
            pagingDataFlow = viewModel.pagingDataFlow,
            onClickClear = viewModel::onClickClear,
            onClickRecord = { record ->
                viewModel.openDetail(recordId = record.id)
            }
        )
        AnimatedVisibility(
            modifier = Modifier
                .fillMaxSize(),
            visible = showDetail,
            enter = slideInHorizontally(
                animationSpec = tween(
                    durationMillis = 370,
                    easing = FastOutSlowInEasing
                ),
                initialOffsetX = { it }
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 370,
                    easing = FastOutSlowInEasing
                )
            ),
            exit = slideOutHorizontally(
                animationSpec = tween(
                    durationMillis = 370,
                    easing = FastOutSlowInEasing
                ),
                targetOffsetX = { it }
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = 370,
                    easing = FastOutSlowInEasing
                )
            )
        ) {
            MonitorDetailScreen(
                modifier = Modifier
                    .fillMaxSize(),
                detailViewState = detailViewState,
                onBack = viewModel::closeDetail,
                onCopyText = viewModel::copyText,
                onShareAsText = viewModel::shareAsText,
                onShareAsFile = viewModel::shareAsFile
            )
        }
    }
}