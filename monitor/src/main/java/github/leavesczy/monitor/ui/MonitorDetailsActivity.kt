package github.leavesczy.monitor.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import github.leavesczy.monitor.R
import github.leavesczy.monitor.adapter.MonitorFragmentAdapter
import github.leavesczy.monitor.utils.FormatUtils
import github.leavesczy.monitor.viewmodel.MonitorDetailViewModel

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 17:04
 * @Desc:
 * @Github：https://github.com/leavesCZY
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

    private val tvToolbarTitle by lazy {
        findViewById<TextView>(R.id.tvToolbarTitle)
    }

    private val toolbar by lazy {
        findViewById<Toolbar>(R.id.toolbar)
    }

    private val tabLayout by lazy {
        findViewById<TabLayout>(R.id.tabLayout)
    }

    private val viewPager by lazy {
        findViewById<ViewPager2>(R.id.viewPager)
    }

    private val monitorDetailViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MonitorDetailViewModel(intent.getLongExtra(KEY_ID, 0)) as T
            }
        }).get(MonitorDetailViewModel::class.java).apply {
            recordLiveData.observe(this@MonitorDetailsActivity, { httpInformation ->
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