package github.leavesczy.monitor.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import github.leavesczy.monitor.R
import github.leavesczy.monitor.adapter.MonitorDetailAdapter
import github.leavesczy.monitor.db.MonitorHttpDetail
import github.leavesczy.monitor.logic.MonitorDetailViewModel
import github.leavesczy.monitor.utils.FormatUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 14:44
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal class MonitorOverviewFragment : Fragment() {

    companion object {

        fun newInstance(): MonitorOverviewFragment {
            return MonitorOverviewFragment()
        }
    }

    private lateinit var rvMonitorDetailList: RecyclerView

    private val monitorDetailViewModel by lazy(mode = LazyThreadSafetyMode.NONE) {
        ViewModelProvider(requireActivity())[MonitorDetailViewModel::class.java]
    }

    private val monitorHttpDetailList = mutableListOf<MonitorHttpDetail>()

    private val monitorDetailAdapter = MonitorDetailAdapter(dataList = monitorHttpDetailList)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.monitor_fragment_monitor_overview, container, false)
        rvMonitorDetailList = view.findViewById(R.id.rvMonitorDetailList)
        rvMonitorDetailList.layoutManager = LinearLayoutManager(requireActivity())
        rvMonitorDetailList.adapter = monitorDetailAdapter
        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.RESUMED) {
                monitorDetailViewModel.httpRecordFlow
                    .distinctUntilChanged()
                    .map {
                        FormatUtils.buildMonitorHttpOverview(
                            monitorHttp = it
                        )
                    }.distinctUntilChanged().collectLatest {
                        monitorHttpDetailList.clear()
                        monitorHttpDetailList.addAll(elements = it)
                        monitorDetailAdapter.notifyDataSetChanged()
                    }
            }
        }
    }

}