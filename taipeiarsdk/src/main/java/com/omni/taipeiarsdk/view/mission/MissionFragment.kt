package com.omni.taipeiarsdk.view.mission

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.omni.taipeiarsdk.R
import com.omni.taipeiarsdk.manager.AnimationFragmentManager
import org.greenrobot.eventbus.EventBus

class MissionFragment : Fragment() {
    private var mContext: Context? = null
    private var mEventBus: EventBus? = null
    private var missionTabLayout: TabLayout? = null
    private var missionViewPager: ViewPager? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mView: View = inflater.inflate(R.layout.fragment_mission, container, false)
        mView.findViewById<View>(R.id.back_fl)
            .setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }

        missionViewPager = mView.findViewById(R.id.mission_fragment_view_ovp)
        missionViewPager!!.adapter = MissionPagerAdapter(
            requireActivity().supportFragmentManager,
            activity
        )

        missionTabLayout = mView.findViewById(R.id.mission_fragment_view_tl)
        missionTabLayout!!.setupWithViewPager(missionViewPager, true)

        return mView
    }

    companion object {
        const val TAG = "fragment_tag_mission"
        fun newInstance(): MissionFragment {
            return MissionFragment()
        }
    }

    private fun openFragmentPage(fragment: Fragment, tag: String) {
        AnimationFragmentManager.getInstance().addFragmentPage(
            activity,
            R.id.activity_main_fl, fragment, tag
        )
    }
}