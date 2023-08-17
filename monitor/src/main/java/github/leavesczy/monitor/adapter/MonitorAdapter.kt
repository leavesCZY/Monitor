package github.leavesczy.monitor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
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
internal class MonitorAdapter(private val context: Context) :
    RecyclerView.Adapter<MonitorViewHolder>() {

    private val colorSuccess = getColor(R.color.monitor_status_success)

    private val colorRequested = getColor(R.color.monitor_status_requested)

    private val colorError = getColor(R.color.monitor_status_error)

    private val color300 = getColor(R.color.monitor_status_300)

    private val color400 = getColor(R.color.monitor_status_400)

    private val color500 = getColor(R.color.monitor_status_500)

    private val asyncListDiffer = AsyncListDiffer(this, MonitorDiffUtilItemCallback())

    var clickListener: MonitorItemClickListener? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MonitorViewHolder {
        return MonitorViewHolder(viewGroup)
    }

    override fun onBindViewHolder(holder: MonitorViewHolder, position: Int) {
        val httpInformation = asyncListDiffer.currentList[position]
        holder.tvId.text = httpInformation.id.toString()
        holder.tvPath.text = String.format("%s  %s", httpInformation.method, httpInformation.path)
        holder.tvHost.text = httpInformation.host
        holder.tvRequestDate.text = httpInformation.requestDateFormatShort
        holder.ivSsl.visibility = if (httpInformation.isSsl) View.VISIBLE else View.GONE
        holder.tvCode.text = httpInformation.responseCodeFormat
        holder.tvDuration.text = httpInformation.durationFormat
        holder.tvSize.text = httpInformation.totalSizeFormat
        setStatusColor(holder, httpInformation)
        holder.view.setOnClickListener {
            clickListener?.onClick(holder.adapterPosition, httpInformation)
        }
    }

    private fun setStatusColor(holder: MonitorViewHolder, monitorHttp: MonitorHttp) {
        val color = when {
            monitorHttp.httpStatus == MonitorHttpStatus.Failed -> {
                colorError
            }

            monitorHttp.httpStatus == MonitorHttpStatus.Requested -> {
                colorRequested
            }

            monitorHttp.responseCode >= 500 -> {
                color500
            }

            monitorHttp.responseCode >= 400 -> {
                color400
            }

            monitorHttp.responseCode >= 300 -> {
                color300
            }

            else -> {
                colorSuccess
            }
        }
        holder.tvCode.setTextColor(color)
        holder.tvPath.setTextColor(color)
        holder.tvId.setTextColor(color)
        holder.tvHost.setTextColor(color)
        holder.tvRequestDate.setTextColor(color)
        holder.tvDuration.setTextColor(color)
        holder.tvSize.setTextColor(color)
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    fun setData(dataList: List<MonitorHttp>) {
        asyncListDiffer.submitList(dataList)
    }

    private fun getColor(@ColorRes id: Int): Int {
        return ContextCompat.getColor(context, id)
    }

}

internal interface MonitorItemClickListener {

    fun onClick(position: Int, model: MonitorHttp)

}

private class MonitorDiffUtilItemCallback : DiffUtil.ItemCallback<MonitorHttp>() {

    override fun areItemsTheSame(oldItem: MonitorHttp, newItem: MonitorHttp): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: MonitorHttp,
        newItem: MonitorHttp
    ): Boolean {
        return oldItem == newItem
    }

}

internal class MonitorViewHolder(viewGroup: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(viewGroup.context).inflate(R.layout.item_monitor, viewGroup, false)
) {

    val view: View = itemView
    val tvId: TextView = view.findViewById(R.id.tvId)
    val tvCode: TextView = view.findViewById(R.id.tvCode)
    val tvPath: TextView = view.findViewById(R.id.tvPath)
    val tvHost: TextView = view.findViewById(R.id.tvHost)
    val ivSsl: ImageView = view.findViewById(R.id.ivSsl)
    val tvRequestDate: TextView = view.findViewById(R.id.tvRequestDate)
    val tvDuration: TextView = view.findViewById(R.id.tvDuration)
    val tvSize: TextView = view.findViewById(R.id.tvSize)

}