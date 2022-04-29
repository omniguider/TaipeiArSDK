package com.omni.taipeiarsdk.view.theme

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.omni.taipeiarsdk.R
import com.omni.taipeiarsdk.TaipeiArSDKActivity
import com.omni.taipeiarsdk.TaipeiArSDKActivity.Companion.filterKeyword
import com.omni.taipeiarsdk.TaipeiArSDKActivity.Companion.filterKeywordCopy
import com.omni.taipeiarsdk.adapter.ThemeFilterAdapter
import com.omni.taipeiarsdk.model.OmniEvent
import com.omni.taipeiarsdk.model.tpe_location.CategoryFeedback
import com.omni.taipeiarsdk.network.NetworkManager.NetworkManagerListener
import com.omni.taipeiarsdk.network.TpeArApi
import org.greenrobot.eventbus.EventBus

class ThemeFilterFragment : Fragment() {
    private var mContext: Context? = null
    private var themeFilterRV: RecyclerView? = null
    private var mAdapter: ThemeFilterAdapter? = null

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
        val view: View = inflater.inflate(R.layout.fragment_theme_filter, container, false)
        view.findViewById<View>(R.id.back_fl)
            .setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }

        TpeArApi.getInstance()
            .getThemeCategory(
                requireActivity(),
                object : NetworkManagerListener<CategoryFeedback?> {
                    override fun onSucceed(categoryFeedback: CategoryFeedback?) {
                        themeFilterRV = view.findViewById(R.id.theme_filter_recycler_view)
                        val mLayoutManager: RecyclerView.LayoutManager =
                            GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
                        themeFilterRV!!.layoutManager = mLayoutManager

                        filterKeywordCopy.clear()
                        filterKeywordCopy.addAll(filterKeyword)

                        if (mAdapter == null) {
                            mAdapter = ThemeFilterAdapter(
                                mContext,
                                categoryFeedback!!.data.toMutableList()
                            )
                        }
                        themeFilterRV!!.adapter = mAdapter
                    }

                    override fun onFail(error: VolleyError?, shouldRetry: Boolean) {
                    }
                })

        view.findViewById<View>(R.id.clear)
            .setOnClickListener {
                filterKeyword.clear()
                themeFilterRV!!.adapter!!.notifyDataSetChanged()
                EventBus.getDefault().post(OmniEvent(OmniEvent.TYPE_SEARCH_FILTER, ""))
            }

        view.findViewById<View>(R.id.search)
            .setOnClickListener {
                filterKeyword.clear()
                filterKeyword.addAll(filterKeywordCopy)
                EventBus.getDefault().post(OmniEvent(OmniEvent.TYPE_SEARCH_FILTER, ""))
                requireActivity().supportFragmentManager.popBackStack()
            }

        return view
    }

    companion object {
        const val TAG = "fragment_tag_theme_filter"
        fun newInstance(): ThemeFilterFragment {
            return ThemeFilterFragment()
        }
    }
}