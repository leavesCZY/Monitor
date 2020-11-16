package github.leavesc.monitor.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import github.leavesc.monitor.ui.MonitorOverviewFragment
import github.leavesc.monitor.ui.MonitorRequestFragment
import github.leavesc.monitor.ui.MonitorResponseFragment

/**
 * 作者：leavesC
 * 时间：2020/11/8 14:56
 * 描述：
 * GitHub：https://github.com/leavesC
 */
internal class MonitorFragmentAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                MonitorOverviewFragment.newInstance()
            }
            1 -> {
                MonitorRequestFragment.newInstance()
            }
            2 -> {
                MonitorResponseFragment.newInstance()
            }
            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    fun getTitle(position: Int): String {
        return when (position) {
            0 -> {
                "Overview"
            }
            1 -> {
                "Request"
            }
            2 -> {
                "Response"
            }
            else -> {
                throw IllegalArgumentException()
            }
        }
    }

}