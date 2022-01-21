package github.leavesczy.monitor.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import github.leavesczy.monitor.Monitor
import github.leavesczy.monitor.R
import github.leavesczy.monitor.adapter.MonitorAdapter
import github.leavesczy.monitor.db.HttpInformation
import github.leavesczy.monitor.viewmodel.MonitorViewModel

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 15:58
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class MonitorActivity : AppCompatActivity() {

    private val monitorViewModel by lazy {
        ViewModelProvider(this).get(MonitorViewModel::class.java).apply {
            allRecordLiveData.observe(this@MonitorActivity, Observer {
                monitorAdapter.setData(it)
            })
        }
    }

    private val monitorAdapter = MonitorAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitor)
        initView()
        monitorViewModel.init()
    }

    private fun initView() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val tvToolbarTitle = findViewById<TextView>(R.id.tvToolbarTitle)
        tvToolbarTitle.text = getString(R.string.monitor)
        monitorAdapter.clickListener = object : MonitorAdapter.OnClickListener {
            override fun onClick(position: Int, model: HttpInformation) {
                MonitorDetailsActivity.navTo(this@MonitorActivity, model.id)
            }
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = monitorAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_monitor_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear -> {
                Monitor.clearCache()
                Monitor.clearNotification()
            }
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

}