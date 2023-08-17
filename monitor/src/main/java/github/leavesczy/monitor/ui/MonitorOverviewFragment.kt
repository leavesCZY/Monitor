package github.leavesczy.monitor.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import github.leavesczy.monitor.R
import github.leavesczy.monitor.logic.MonitorDetailViewModel
import github.leavesczy.monitor.utils.FormatUtils
import kotlinx.coroutines.flow.collectLatest
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

    private lateinit var tvUrl: TextView
    private lateinit var tvMethod: TextView
    private lateinit var tvProtocol: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvResponse: TextView
    private lateinit var tvSsl: TextView
    private lateinit var tvTlsVersion: TextView
    private lateinit var tvCipherSuite: TextView
    private lateinit var tvRequestTime: TextView
    private lateinit var tvResponseTime: TextView
    private lateinit var tvDuration: TextView
    private lateinit var tvRequestSize: TextView
    private lateinit var tvResponseSize: TextView
    private lateinit var tvTotalSize: TextView

    private val monitorDetailViewModel by lazy(mode = LazyThreadSafetyMode.NONE) {
        ViewModelProvider(requireActivity())[MonitorDetailViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_monitor_overview, container, false)
        tvUrl = view.findViewById(R.id.tv_url)
        tvMethod = view.findViewById(R.id.tv_method)
        tvProtocol = view.findViewById(R.id.tv_protocol)
        tvStatus = view.findViewById(R.id.tv_status)
        tvResponse = view.findViewById(R.id.tv_response)
        tvSsl = view.findViewById(R.id.tv_ssl)
        tvTlsVersion = view.findViewById(R.id.tv_tlsVersion)
        tvCipherSuite = view.findViewById(R.id.tv_cipherSuite)
        tvRequestTime = view.findViewById(R.id.tv_request_time)
        tvResponseTime = view.findViewById(R.id.tv_response_time)
        tvDuration = view.findViewById(R.id.tvDuration)
        tvRequestSize = view.findViewById(R.id.tv_request_size)
        tvResponseSize = view.findViewById(R.id.tv_response_size)
        tvTotalSize = view.findViewById(R.id.tv_total_size)
        tvRequestTime = view.findViewById(R.id.tv_request_time)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.RESUMED) {
                monitorDetailViewModel.httpRecordFlow.collectLatest {
                    tvUrl.text = it.url
                    tvMethod.text = it.method
                    tvProtocol.text = it.protocol
                    tvStatus.text = it.httpStatus.toString()
                    tvResponse.text = it.responseSummaryText
                    tvSsl.text = if (it.isSsl) {
                        "Yes"
                    } else {
                        "No"
                    }
                    tvTlsVersion.text = it.responseTlsVersion
                    tvCipherSuite.text = it.responseCipherSuite
                    tvRequestTime.text = it.requestDateFormatLong
                    tvResponseTime.text = it.responseDateFormatLong
                    tvDuration.text = it.durationFormat
                    tvRequestSize.text = FormatUtils.formatBytes(it.requestContentLength)
                    tvResponseSize.text = FormatUtils.formatBytes(it.responseContentLength)
                    tvTotalSize.text = it.totalSizeFormat
                }
            }
        }
    }

}