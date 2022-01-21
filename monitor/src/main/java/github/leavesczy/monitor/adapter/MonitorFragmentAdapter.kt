package github.leavesczy.monitor.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import github.leavesczy.monitor.ui.MonitorOverviewFragment
import github.leavesczy.monitor.ui.MonitorRequestFragment
import github.leavesczy.monitor.ui.MonitorResponseFragment

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 14:56
 * @Desc:
 * @Github：https://github.com/leavesCZY
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