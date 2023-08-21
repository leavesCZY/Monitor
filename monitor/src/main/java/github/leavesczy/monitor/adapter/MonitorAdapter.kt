package github.leavesczy.monitor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import github.leavesczy.monitor.R
import github.leavesczy.monitor.db.MonitorHttp
import github.leavesczy.monitor.db.MonitorHttpStatus

/**
 * @Author: leavesCZY
 * @Date: 2020/10/20 18:26
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal class MonitorAdapter : RecyclerView.Adapter<MonitorViewHolder>() {

    private val asyncListDiffer = AsyncListDiffer(this, MonitorDiffUtilItemCallback())

    var clickListener: MonitorItemClickListener? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): MonitorViewHolder {
        return MonitorViewHolder(viewGroup)
    }

    override fun onBindViewHolder(holder: MonitorViewHolder, position: Int) {
        val monitorHttp = asyncListDiffer.currentList[position]
        holder.tvHttpCode.text = monitorHttp.responseCodeFormat
        holder.tvHttpPath.text = monitorHttp.pathWithQuery
        holder.tvMonitorId.text = monitorHttp.id.toString()
        holder.tvUrl.text = String.format("%s://%s", monitorHttp.scheme, monitorHttp.host)
        holder.tvRequestTime.text = monitorHttp.requestDateMDHMS
        holder.tvRequestDuration.text = monitorHttp.requestDurationFormat
        holder.tvTotalSize.text = monitorHttp.totalSizeFormat
        setStatusColor(holder, monitorHttp)
        holder.itemView.setOnClickListener {
            clickListener?.onClick(holder.adapterPosition, monitorHttp)
        }
    }

    private fun setStatusColor(holder: MonitorViewHolder, monitorHttp: MonitorHttp) {
        val context = holder.itemView.context
        val color = when (monitorHttp.httpStatus) {
            MonitorHttpStatus.Requesting -> {
                ContextCompat.getColor(context, R.color.monitor_http_status_requesting)
            }

            MonitorHttpStatus.Complete -> {
                if (monitorHttp.responseCode == 200) {
                    ContextCompat.getColor(context, R.color.monitor_http_status_successful)
                } else {
                    ContextCompat.getColor(context, R.color.monitor_http_status_unsuccessful)
                }
            }

            MonitorHttpStatus.Failed -> {
                ContextCompat.getColor(context, R.color.monitor_http_status_unsuccessful)
            }
        }
        holder.tvHttpCode.setTextColor(color)
        holder.tvHttpPath.setTextColor(color)
        holder.tvMonitorId.setTextColor(color)
        holder.tvUrl.setTextColor(color)
        holder.tvRequestTime.setTextColor(color)
        holder.tvRequestDuration.setTextColor(color)
        holder.tvTotalSize.setTextColor(color)
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    fun setData(dataList: List<MonitorHttp>) {
        asyncListDiffer.submitList(dataList)
    }

}

internal interface MonitorItemClickListener {

    fun onClick(position: Int, model: MonitorHttp)

}

private class MonitorDiffUtilItemCallback : DiffUtil.ItemCallback<MonitorHttp>() {

    override fun areItemsTheSame(oldItem: MonitorHttp, newItem: MonitorHttp): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MonitorHttp, newItem: MonitorHttp): Boolean {
        return oldItem == newItem
    }

}

internal class MonitorViewHolder(viewGroup: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(viewGroup.context).inflate(R.layout.monitor_item_monitor, viewGroup, false)
) {

    val tvMonitorId: TextView = itemView.findViewById(R.id.tvMonitorId)
    val tvHttpCode: TextView = itemView.findViewById(R.id.tvHttpCode)
    val tvHttpPath: TextView = itemView.findViewById(R.id.tvHttpPath)
    val tvUrl: TextView = itemView.findViewById(R.id.tvUrl)
    val tvRequestTime: TextView = itemView.findViewById(R.id.tvRequestTime)
    val tvRequestDuration: TextView = itemView.findViewById(R.id.tvRequestDuration)
    val tvTotalSize: TextView = itemView.findViewById(R.id.tvRequestSize)

}