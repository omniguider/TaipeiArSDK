package com.omni.taipeiarsdk.view.mission

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.toolbox.NetworkImageView
import com.omni.taipeiarsdk.R
import com.omni.taipeiarsdk.TaipeiArSDKActivity
import com.omni.taipeiarsdk.TaipeiArSDKActivity.Companion.currentNineGridData
import com.omni.taipeiarsdk.manager.AnimationFragmentManager
import com.omni.taipeiarsdk.model.OmniEvent
import com.omni.taipeiarsdk.model.mission.GridData
import com.omni.taipeiarsdk.model.tpe_location.IndexPoi
import com.omni.taipeiarsdk.network.NetworkManager
import org.greenrobot.eventbus.EventBus

class GridDescFragment : Fragment() {
    private var mContext: Context? = null
    private var gridData: GridData? = null
    private var startMissionBtn: TextView? = null

    companion object {
        const val TAG = "fragment_tag_grid_desc"
        private const val ARG_KEY_DATA = "arg_key_data"

        fun newInstance(
            data: GridData?
        ): GridDescFragment {
            val fragment = GridDescFragment()
            val arg = Bundle()
            arg.putSerializable(ARG_KEY_DATA, data)
            fragment.arguments = arg
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gridData = requireArguments().getSerializable(ARG_KEY_DATA) as GridData?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mView: View = inflater.inflate(R.layout.fragment_grid_desc, container, false)
        mView.findViewById<View>(R.id.back_fl)
            .setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }

        val titleTv: TextView = mView.findViewById(R.id.grid_title_tv)
        val descTv: TextView = mView.findViewById(R.id.grid_desc_tv)
        val hintTv: TextView = mView.findViewById(R.id.grid_hint_tv)
        val pic: NetworkImageView = mView.findViewById(R.id.grid_pic)
        titleTv.text = gridData!!.title
        descTv.text = gridData!!.description
        hintTv.text = gridData!!.notice
        NetworkManager.getInstance().setNetworkImage(mContext, pic, gridData!!.poi.image)

        startMissionBtn = mView.findViewById(R.id.start_mission_btn)
        startMissionBtn!!.setOnClickListener {
            TaipeiArSDKActivity.missionTitle = currentNineGridData.mission_title
            TaipeiArSDKActivity.ng_id = gridData!!.id
            var indexPoi: ArrayList<IndexPoi> = ArrayList()
            for (item in currentNineGridData.nine_grid) {
                indexPoi.add(item.poi)
            }
            EventBus.getDefault().post(OmniEvent(OmniEvent.TYPE_OPEN_AR_MISSION, indexPoi.toTypedArray()))
        }

        return mView
    }

    private fun openFragmentPage(fragment: Fragment, tag: String) {
        AnimationFragmentManager.getInstance().addFragmentPage(
            activity,
            R.id.activity_main_fl, fragment, tag
        )
    }
}