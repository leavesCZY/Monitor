package github.leavesczy.monitor.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import github.leavesczy.monitor.R
import github.leavesczy.monitor.utils.FormatUtils
import github.leavesczy.monitor.viewmodel.MonitorDetailViewModel

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 14:44
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class MonitorOverviewFragment : Fragment() {

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

    private val monitorDetailViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MonitorDetailViewModel::class.java).apply {
            recordLiveData.observe(viewLifecycleOwner, { information ->
                information?.apply {
                    tvUrl.text = url
                    tvMethod.text = method
                    tvProtocol.text = protocol
                    tvStatus.text = status.toString()
                    tvResponse.text = responseSummaryText
                    tvSsl.text = if (isSsl) "Yes" else "No"
                    tvTlsVersion.text = responseTlsVersion
                    tvCipherSuite.text = responseCipherSuite
                    tvRequestTime.text = requestDateFormatLong
                    tvResponseTime.text = responseDateFormatLong
                    tvDuration.text = durationFormat
                    tvRequestSize.text = FormatUtils.formatBytes(requestContentLength)
                    tvResponseSize.text = FormatUtils.formatBytes(responseContentLength)
                    tvTotalSize.text = totalSizeFormat
                }
            })
        }
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
        monitorDetailViewModel.init()
    }

}