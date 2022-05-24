package com.omni.taipeiarsdk.view.mission

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.android.volley.VolleyError
import com.omni.taipeiarsdk.R
import com.omni.taipeiarsdk.manager.AnimationFragmentManager
import com.omni.taipeiarsdk.model.mission.GridData
import com.omni.taipeiarsdk.model.mission.MissionGridData
import com.omni.taipeiarsdk.model.mission.MissionGridFeedback
import com.omni.taipeiarsdk.network.NetworkManager
import com.omni.taipeiarsdk.network.TpeArApi
import kotlin.math.roundToInt

class NineGridFragment : Fragment() {
    private var mContext: Context? = null
    private var missionId: String? = null
    private var title: String? = null
    private var describe: String? = null
    private var verify_code: String? = null

    private var userId = ""
    private var myNineGridData: MissionGridData? = null
    private var getRewardBtn: TextView? = null
    private var getRewardAlreadyBtn: TextView? = null
    private var mList: ArrayList<GridData>? = null
    private var mAdapter: NineGridAdapter? = null
    private var recyclerView: RecyclerView? = null

    companion object {
        const val TAG = "fragment_tag_nine_grid"
        private const val ARG_KEY_TITLE = "arg_key_title"
        private const val ARG_KEY_DESC = "arg_key_desc"
        private const val ARG_KEY_ID = "arg_key_id"
        private const val ARG_KEY_CODE = "arg_key_code"

        fun newInstance(
            missionId: String?,
            title: String?,
            describe: String?,
            verify_code: String?
        ): NineGridFragment {
            val fragment = NineGridFragment()
            val arg = Bundle()
            arg.putString(ARG_KEY_TITLE, title)
            arg.putString(ARG_KEY_DESC, describe)
            arg.putString(ARG_KEY_ID, missionId)
            arg.putString(ARG_KEY_CODE, verify_code)
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
        missionId = requireArguments().getString(ARG_KEY_ID)
        title = requireArguments().getString(ARG_KEY_TITLE)
        describe = requireArguments().getString(ARG_KEY_DESC)
        verify_code = requireArguments().getString(ARG_KEY_CODE)

        userId = MissionListFragment.userId
        if (userId.isEmpty()) {
            userId = "Hf1242aaa6" // not login
        }
        makePageDataReq()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mView: View = inflater.inflate(R.layout.fragment_nine_grid, container, false)
        mView.findViewById<View>(R.id.back_fl)
            .setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }

        val titleTv: TextView = mView.findViewById(R.id.mission_title_tv)
        val descTv: TextView = mView.findViewById(R.id.mission_desc_tv)
        titleTv.text = title
        descTv.text = describe

        getRewardAlreadyBtn = mView.findViewById<TextView>(R.id.already_collect_rewards_btn)
        getRewardBtn = mView.findViewById<TextView>(R.id.collect_rewards_btn)

        mList = ArrayList<GridData>()
        mAdapter = NineGridAdapter(requireActivity(), mList!!)
        recyclerView = mView.findViewById(R.id.grid_rv)
        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireActivity(), 3)
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.addItemDecoration(
            GridSpacingItemDecoration(
                3,
                dpToPx(12),
                true
            )
        )
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.adapter = mAdapter

        return mView
    }

    private fun makePageDataReq() {
        TpeArApi.getInstance().getMissionGrid(
            requireActivity(),
            missionId,
            userId,
            object : NetworkManager.NetworkManagerListener<MissionGridFeedback?> {
                override fun onSucceed(feedback: MissionGridFeedback?) {
                    myNineGridData = feedback!!.data
                    prepareData()
                }

                override fun onFail(error: VolleyError?, shouldRetry: Boolean) {}
            })
    }

    private fun prepareData() {
        Log.e("LOG", "myNineGridData" + myNineGridData!!.nine_grid.size)
        if (myNineGridData!!.is_finish) {
            if (myNineGridData!!.rws_enabled.equals("Done")) {
                getRewardAlreadyBtn!!.visibility = View.VISIBLE
                getRewardBtn!!.visibility = View.GONE
                getRewardAlreadyBtn!!.text = resources.getString(R.string.receive_reward)
            } else if (myNineGridData!!.rws_enabled.equals("None")) {
                getRewardAlreadyBtn!!.visibility = View.GONE
                getRewardBtn!!.visibility = View.VISIBLE
            }
        }
        for (i in RewardListFragment.mRewardFeedback.data.indices) {
            if (RewardListFragment.mRewardFeedback.data[i].m_id.equals(missionId)) {
                if (RewardListFragment.mRewardFeedback.data[i].rws_enabled.equals("Done")) {
                    getRewardAlreadyBtn!!.visibility = View.VISIBLE
                    getRewardBtn!!.visibility = View.GONE
                    getRewardAlreadyBtn!!.setText(R.string.received)
                }
            }
        }
        mList!!.clear()
        val idList: MutableList<String> = ArrayList()
        for (i in myNineGridData!!.nine_grid.indices) {
            if (myNineGridData!!.nine_grid[i].id != null) {
                idList.add(myNineGridData!!.nine_grid[i].id)
                mList!!.add(myNineGridData!!.nine_grid[i])
                if (myNineGridData!!.nine_grid[i].is_complete) {
                }
            } else if (myNineGridData!!.nine_grid[i].id == null) {
                mList!!.add(GridData())
            }
        }
        mAdapter!!.notifyDataSetChanged()
    }

    class NineGridAdapter(
        private val mContext: Context,
        private val worksList: ArrayList<GridData>
    ) :
        RecyclerView.Adapter<NineGridAdapter.MyViewHolder>() {
        inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.title)
            val thumbnail: ImageView = view.findViewById(R.id.thumbnail)
            val finish: ImageView = view.findViewById(R.id.finish)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.nine_grid_card, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val data = worksList[position]
            holder.title.text = (position + 1).toString()
            if (data.is_complete) {
                holder.finish.visibility = View.VISIBLE
            }
            holder.thumbnail.setOnClickListener {
                openFragmentPage(GridDescFragment.newInstance(data), GridDescFragment.TAG)
            }
        }

        override fun getItemCount(): Int {
            return worksList.size
        }

        private fun openFragmentPage(fragment: Fragment, tag: String) {
            AnimationFragmentManager.getInstance().addFragmentPage(
                mContext as FragmentActivity?,
                R.id.activity_main_fl, fragment, tag
            )
        }
    }

    class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val spacing: Int,
        private val includeEdge: Boolean
    ) :
        ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // item position
            val column = position % spanCount // item column
            if (includeEdge) {
                outRect.left =
                    spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
                outRect.right =
                    (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)
                if (position < spanCount) { // top edge
                    outRect.top = spacing
                }
                outRect.bottom = spacing // item bottom
            } else {
                outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
                outRect.right =
                    spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing // item top
                }
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        val r = resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            r.displayMetrics
        ).roundToInt()
    }
}