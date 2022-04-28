package com.omni.taipeiarsdk.view.theme

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.omni.taipeiarsdk.R
import com.omni.taipeiarsdk.adapter.ThemeGuideAdapter
import com.omni.taipeiarsdk.model.tpe_location.ThemeFeedback
import com.omni.taipeiarsdk.network.NetworkManager.NetworkManagerListener
import com.omni.taipeiarsdk.network.TpeArApi

class ThemeGuideFragment : Fragment() {
    private var mContext: Context? = null
    private var themeGuideRV: RecyclerView? = null
    private var mAdapter: ThemeGuideAdapter? = null

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
        val view: View = inflater.inflate(R.layout.fragment_theme_guide, container, false)
        view.findViewById<View>(R.id.back_fl)
            .setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }

        TpeArApi.getInstance()
            .getTheme(requireActivity(), object : NetworkManagerListener<ThemeFeedback?> {
                override fun onSucceed(themeFeedback: ThemeFeedback?) {
                    themeGuideRV = view.findViewById(R.id.theme_guide_recycler_view)
                    themeGuideRV!!.layoutManager = LinearLayoutManager(mContext)
                    if (mAdapter == null) {
                        mAdapter = ThemeGuideAdapter(
                            mContext,
                            themeFeedback!!.data.toMutableList()
                        )
                    }
                    themeGuideRV!!.adapter = mAdapter
                }

                override fun onFail(error: VolleyError?, shouldRetry: Boolean) {
                }
            })

        return view
    }

    companion object {
        const val TAG = "fragment_tag_theme_guide_main"
        fun newInstance(): ThemeGuideFragment {
            return ThemeGuideFragment()
        }
    }
}