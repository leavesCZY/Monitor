package github.leavesczy.monitor.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import github.leavesczy.monitor.R
import github.leavesczy.monitor.adapter.MonitorFragmentAdapter
import github.leavesczy.monitor.logic.MonitorDetailViewModel
import github.leavesczy.monitor.utils.FormatUtils

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 17:04
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal class MonitorDetailsActivity : AppCompatActivity() {

    companion object {

        private const val KEY_ID = "keyId"

        fun navTo(context: Context, id: Long) {
            val intent = Intent(context, MonitorDetailsActivity::class.java)
            intent.putExtra(KEY_ID, id)
            context.startActivity(intent)
        }

    }

    private val monitorDetailViewModel by lazy(mode = LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val id = intent.getLongExtra(KEY_ID, 0)
                return MonitorDetailViewModel(id = id) as T
            }
        })[MonitorDetailViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitor_details)
        initView()
        initObserver()
    }

    private fun initView() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val monitorFragmentAdapter = MonitorFragmentAdapter(this)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = monitorFragmentAdapter
        viewPager.offscreenPageLimit = monitorFragmentAdapter.itemCount
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = monitorFragmentAdapter.getTitle(position = position)
        }
        tabLayoutMediator.attach()
    }

    private fun initObserver() {
        monitorDetailViewModel.recordLiveData.observe(this) {
            supportActionBar?.title = String.format("%s  %s", it.method, it.path)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_monitor_share, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share -> {
                val monitorHttp = monitorDetailViewModel.recordLiveData.value
                if (monitorHttp != null) {
                    share(FormatUtils.getShareText(monitorHttp))
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
        sendIntent.putExtra(Intent.EXTRA_TEXT, content)
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, getString(R.string.monitor_lib_name)))
    }

}