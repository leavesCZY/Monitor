package github.leavesczy.monitor.internal.ui.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import github.leavesczy.monitor.R
import github.leavesczy.monitor.internal.db.MonitorDatabase
import github.leavesczy.monitor.internal.ui.export.MonitorOverviewBuilder
import github.leavesczy.monitor.internal.ui.export.MonitorShareExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class MonitorViewModel(application: Application) :
    AndroidViewModel(application = application) {

    val pagingDataFlow = Pager(
        config = PagingConfig(
            pageSize = 20,
            initialLoadSize = 30,
            prefetchDistance = 10,
            enablePlaceholders = true
        ),
        pagingSourceFactory = {
            MonitorDatabase.instance.monitorDao.queryRecords()
        },
        remoteMediator = null
    ).flow.cachedIn(scope = viewModelScope)

    var selectedRecordId by mutableStateOf<Long?>(value = null)
        private set

    var detailViewState by mutableStateOf(value = MonitorDetailViewState())
        private set

    private var detailObserverJob: Job? = null

    fun openDetail(recordId: Long) {
        if (selectedRecordId == recordId) {
            return
        }
        selectedRecordId = recordId
        observeDetail(recordId = recordId)
    }

    fun closeDetail() {
        detailObserverJob?.cancel()
        detailObserverJob = null
        // Keep detailViewState so the exit transition still shows the current page.
        selectedRecordId = null
    }

    fun onClickClear() {
        closeDetail()
        viewModelScope.launch {
            MonitorDatabase.instance.monitorDao.deleteAll()
        }
    }

    fun copyText() {
        val recordId = selectedRecordId ?: return
        viewModelScope.launch(context = Dispatchers.Default) {
            MonitorShareExporter.copyText(
                application = getApplication(),
                recordId = recordId,
                showToast = ::showToast
            )
        }
    }

    fun shareAsText() {
        val recordId = selectedRecordId ?: return
        viewModelScope.launch(context = Dispatchers.Default) {
            MonitorShareExporter.shareAsText(
                application = getApplication(),
                recordId = recordId,
                showToast = ::showToast
            )
        }
    }

    fun shareAsFile() {
        val recordId = selectedRecordId ?: return
        viewModelScope.launch(context = Dispatchers.Default) {
            MonitorShareExporter.shareAsFile(
                application = getApplication(),
                recordId = recordId,
                showToast = ::showToast
            )
        }
    }

    override fun onCleared() {
        detailObserverJob?.cancel()
        super.onCleared()
    }

    private fun observeDetail(recordId: Long) {
        detailObserverJob?.cancel()
        detailObserverJob = viewModelScope.launch {
            MonitorDatabase.instance.monitorDao.queryRecordAsFlow(id = recordId)
                .distinctUntilChanged()
                .collectLatest { record ->
                    detailViewState = withContext(context = Dispatchers.Default) {
                        MonitorOverviewBuilder.buildDetailViewState(
                            record = record,
                            overviewLabel = getString(resId = R.string.monitor_overview),
                            requestLabel = getString(resId = R.string.monitor_request),
                            responseLabel = getString(resId = R.string.monitor_response)
                        )
                    }
                }
        }
    }

    private suspend fun showToast(message: String) {
        withContext(context = Dispatchers.Main.immediate) {
            Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getString(resId: Int): String {
        return getApplication<Application>().getString(resId)
    }

}