package com.omni.taipeiarsdk.view.theme

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.omni.taipeiarsdk.R
import com.omni.taipeiarsdk.TaipeiArSDKActivity.Companion.filterKeyword
import com.omni.taipeiarsdk.adapter.ThemeGuideAdapter
import com.omni.taipeiarsdk.manager.AnimationFragmentManager
import com.omni.taipeiarsdk.model.OmniEvent
import com.omni.taipeiarsdk.model.tpe_location.*
import com.omni.taipeiarsdk.network.NetworkManager.NetworkManagerListener
import com.omni.taipeiarsdk.network.TpeArApi
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ThemeGuideFragment : Fragment() {
    private var mContext: Context? = null
    private var themeGuideRV: RecyclerView? = null
    private var mAdapter: ThemeGuideAdapter? = null
    private var mEventBus: EventBus? = null
    private var mThemeData: ArrayList<ThemeData>? = null
    private var mThemeFilterData: ArrayList<ThemeData>? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: OmniEvent) {
        when (event.type) {
            OmniEvent.TYPE_SEARCH_FILTER -> {
                if (filterKeyword.isEmpty()) {
                    mAdapter!!.updateAdapter(mThemeData)
                } else {
                    mThemeFilterData = ArrayList()
                    for (item in mThemeData!!) {
                        if (filterKeyword.contains(item.category.title)) {
                            mThemeFilterData!!.add(item)
                        }
                    }
                    mAdapter!!.updateAdapter(mThemeFilterData)
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mEventBus == null) {
            mEventBus = EventBus.getDefault()
        }
        mEventBus!!.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (mEventBus != null) {
            mEventBus!!.unregister(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_theme_guide, container, false)
        view.findViewById<View>(R.id.back_fl)
            .setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }

        TpeArApi.getInstance()
            .getTheme(requireActivity(), object : NetworkManagerListener<ThemeFeedback?> {
                override fun onSucceed(themeFeedback: ThemeFeedback?) {
                    mThemeData = themeFeedback!!.data.toCollection(ArrayList())
                    themeGuideRV = view.findViewById(R.id.theme_guide_recycler_view)
                    themeGuideRV!!.layoutManager = LinearLayoutManager(mContext)
                    if (mAdapter == null) {
                        mAdapter = ThemeGuideAdapter(
                            mContext,
                            mThemeData!!.toMutableList()
                        )
                    }
                    themeGuideRV!!.adapter = mAdapter
                }

                override fun onFail(error: VolleyError?, shouldRetry: Boolean) {
                }
            })

        view.findViewById<View>(R.id.filter)
            .setOnClickListener {
                openFragmentPage(
                    ThemeFilterFragment.newInstance(),
                    ThemeFilterFragment.TAG
                )
            }

        return view
    }

    companion object {
        const val TAG = "fragment_tag_theme_guide_main"
        fun newInstance(): ThemeGuideFragment {
            return ThemeGuideFragment()
        }
    }

    private fun openFragmentPage(fragment: Fragment, tag: String) {
        AnimationFragmentManager.getInstance().addFragmentPage(
            activity,
            R.id.activity_main_fl, fragment, tag
        )
    }
}