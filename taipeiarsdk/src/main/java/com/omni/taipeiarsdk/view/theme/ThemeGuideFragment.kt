package com.omni.taipeiarsdk.view.theme

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
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
    private var mThemeSearchData: ArrayList<ThemeData>? = null
    private var searchEdt: EditText? = null
    private var mSearchText: String? = ""
    private var filterCount: TextView? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: OmniEvent) {
        when (event.type) {
            OmniEvent.TYPE_SEARCH_FILTER -> {
                if (filterKeyword.isEmpty()) {
                    filterCount!!.visibility = View.GONE
                    mThemeFilterData = ArrayList()
                    mThemeFilterData!!.addAll(mThemeData!!)
                    doSearchPOI(mSearchText)
                } else {
                    filterCount!!.visibility = View.VISIBLE
                    filterCount!!.text = filterKeyword.size.toString()
                    mThemeFilterData = ArrayList()
                    for (item in mThemeData!!) {
                        if (filterKeyword.contains(item.category.title)) {
                            mThemeFilterData!!.add(item)
                        }
                    }
                    doSearchPOI(mSearchText)
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

                    mThemeFilterData = ArrayList()
                    mThemeFilterData!!.addAll(mThemeData!!)
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

        searchEdt = view.findViewById(R.id.keyword_search)
        val searchBtn = view.findViewById<FrameLayout>(R.id.keyword_search_btn)
        searchBtn.setOnClickListener {
            val searchText = searchEdt!!.text.toString()
            if (searchText.isNotEmpty()) {
                mSearchText = searchText
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
                doSearchPOI(searchText)
            }
        }
        searchEdt!!.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchText = searchEdt!!.text.toString()
                mSearchText = searchText
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
                doSearchPOI(searchText)
                return@OnEditorActionListener true
            }
            false
        })
        searchEdt!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                doSearchPOI(s.toString())
            }
        })

        filterCount = view.findViewById(R.id.filter_cnt)

        return view
    }

    private fun doSearchPOI(keyword: String?) {
        mThemeSearchData = ArrayList()
        for (item in mThemeFilterData!!) {
            if (item.name.contains(keyword!!)) {
                mThemeSearchData!!.add(item)
            }
        }
        mAdapter!!.updateAdapter(mThemeSearchData)
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