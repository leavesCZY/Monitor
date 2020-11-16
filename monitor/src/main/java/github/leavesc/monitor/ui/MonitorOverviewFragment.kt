package github.leavesc.monitor.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import github.leavesc.monitor.R
import github.leavesc.monitor.utils.FormatUtils
import github.leavesc.monitor.viewmodel.MonitorDetailViewModel
import kotlinx.android.synthetic.main.fragment_monitor_overview.*

/**
 * 作者：leavesC
 * 时间：2020/11/8 14:44
 * 描述：
 * GitHub：https://github.com/leavesC
 */
class MonitorOverviewFragment : Fragment() {

    companion object {

        fun newInstance(): MonitorOverviewFragment {
            return MonitorOverviewFragment()
        }
    }

    private val monitorDetailViewModel by lazy {
        ViewModelProvider(activity!!).get(MonitorDetailViewModel::class.java).apply {
            recordLiveData.observe(this@MonitorOverviewFragment, Observer { information ->
                information?.apply {
                    tv_url.text = url
                    tv_method.text = method
                    tv_protocol.text = protocol
                    tv_status.text = status.toString()
                    tv_response.text = responseSummaryText
                    tv_ssl.text = if (isSsl) "Yes" else "No"
                    tv_tlsVersion.text = responseTlsVersion
                    tv_cipherSuite.text = responseCipherSuite
                    tv_request_time.text = requestDateFormatLong
                    tv_response_time.text = responseDateFormatLong
                    tv_duration.text = durationFormat
                    tv_request_size.text = FormatUtils.formatBytes(requestContentLength)
                    tv_response_size.text = FormatUtils.formatBytes(responseContentLength)
                    tv_total_size.text = totalSizeFormat
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_monitor_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        monitorDetailViewModel.init()
    }

}