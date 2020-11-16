package github.leavesc.monitor.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import github.leavesc.monitor.R
import github.leavesc.monitor.adapter.MonitorFragmentAdapter
import github.leavesc.monitor.utils.FormatUtils
import github.leavesc.monitor.viewmodel.MonitorDetailViewModel
import kotlinx.android.synthetic.main.activity_monitor_details.*

/**
 * 作者：leavesC
 * 时间：2020/11/8 17:04
 * 描述：
 * GitHub：https://github.com/leavesC
 */
class MonitorDetailsActivity : AppCompatActivity() {

    companion object {

        private const val KEY_ID = "keyId"

        fun navTo(context: Context, id: Long) {
            val intent = Intent(context, MonitorDetailsActivity::class.java)
            intent.putExtra(KEY_ID, id)
            context.startActivity(intent)
        }

    }

    private val monitorDetailViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MonitorDetailViewModel(intent.getLongExtra(KEY_ID, 0)) as T
            }
        }).get(MonitorDetailViewModel::class.java).apply {
            recordLiveData.observe(this@MonitorDetailsActivity, Observer { httpInformation ->
                tvToolbarTitle.text =
                    String.format("%s  %s", httpInformation.method, httpInformation.path)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitor_details)
        initView()
        monitorDetailViewModel.queryRecordById()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val monitorFragmentAdapter = MonitorFragmentAdapter(this)
        viewPager.adapter = monitorFragmentAdapter
        viewPager.offscreenPageLimit = monitorFragmentAdapter.itemCount
        val tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = monitorFragmentAdapter.getTitle(position)
        }
        tabLayoutMediator.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_monitor_share, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share -> {
                val httpInformation = monitorDetailViewModel.recordLiveData.value
                if (httpInformation != null) {
                    share(FormatUtils.getShareText(httpInformation))
                }
            }
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

    private fun share(content: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, content)
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, null))
    }

}