package github.leavesczy.monitor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import github.leavesczy.monitor.R
import github.leavesczy.monitor.db.MonitorHttpDetail

/**
 * @Author: leavesCZY
 * @Date: 2023/08/20 18:13
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal class MonitorDetailAdapter(private val dataList: List<MonitorHttpDetail>) :
    RecyclerView.Adapter<MonitorDetailViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): MonitorDetailViewHolder {
        return MonitorDetailViewHolder(viewGroup)
    }

    override fun onBindViewHolder(holder: MonitorDetailViewHolder, position: Int) {
        val data = dataList[position]
        holder.tvHeader.text = data.header
        holder.tvValue.text = data.value
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}

internal class MonitorDetailViewHolder(viewGroup: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(viewGroup.context)
        .inflate(R.layout.monitor_item_monitor_detail, viewGroup, false)
) {

    val tvHeader: TextView = itemView.findViewById(R.id.tvHeader)
    val tvValue: TextView = itemView.findViewById(R.id.tvValue)

}