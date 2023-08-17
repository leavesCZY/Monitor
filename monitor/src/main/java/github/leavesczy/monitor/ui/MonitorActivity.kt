package github.leavesczy.monitor.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import github.leavesczy.monitor.R
import github.leavesczy.monitor.adapter.MonitorAdapter
import github.leavesczy.monitor.adapter.MonitorItemClickListener
import github.leavesczy.monitor.db.MonitorDatabase
import github.leavesczy.monitor.db.MonitorHttp
import github.leavesczy.monitor.provider.NotificationProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 15:58
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal class MonitorActivity : AppCompatActivity() {

    private val httpRecordFlow by lazy(mode = LazyThreadSafetyMode.NONE) {
        MonitorDatabase.instance.monitorDao.queryRecord(limit = 400)
    }

    private val monitorAdapter by lazy(mode = LazyThreadSafetyMode.NONE) {
        MonitorAdapter(context = this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitor)
        initView()
        initObserver()
    }

    private fun initView() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.monitor_lib_name)
        }
        monitorAdapter.clickListener = object : MonitorItemClickListener {
            override fun onClick(position: Int, model: MonitorHttp) {
                MonitorDetailsActivity.navTo(this@MonitorActivity, model.id)
            }
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = monitorAdapter
    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.RESUMED) {
                httpRecordFlow.collectLatest {
                    monitorAdapter.setData(it)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_monitor_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear -> {
                lifecycleScope.launch {
                    MonitorDatabase.instance.monitorDao.deleteAll()
                    NotificationProvider.clearBuffer()
                    NotificationProvider.dismiss()
                }
            }

            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

}