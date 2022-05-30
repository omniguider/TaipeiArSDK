package com.omni.taipeiarsdk.view.mission

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.VolleyError
import com.android.volley.toolbox.NetworkImageView
import com.omni.taipeiarsdk.R
import com.omni.taipeiarsdk.TaipeiArSDKActivity.Companion.userId
import com.omni.taipeiarsdk.manager.AnimationFragmentManager
import com.omni.taipeiarsdk.model.OmniEvent
import com.omni.taipeiarsdk.model.mission.MissionRewardFeedback
import com.omni.taipeiarsdk.model.mission.RewardData
import com.omni.taipeiarsdk.network.NetworkManager
import com.omni.taipeiarsdk.network.NetworkManager.NetworkManagerListener
import com.omni.taipeiarsdk.network.TpeArApi
import org.greenrobot.eventbus.EventBus

class RewardFragment : Fragment() {
    private var mContext: Context? = null
    private var rewardData: RewardData? = null
    private var receiveBtn: TextView? = null
    private var getRewardBtn: TextView? = null
    private var getRewardAlreadyBtn: TextView? = null

    companion object {
        const val TAG = "fragment_tag_reward"
        private const val ARG_KEY_DATA = "arg_key_data"

        fun newInstance(
            data: RewardData?
        ): RewardFragment {
            val fragment = RewardFragment()
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
        rewardData = requireArguments().getSerializable(ARG_KEY_DATA) as RewardData?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mView: View = inflater.inflate(R.layout.fragment_reward, container, false)
        mView.findViewById<View>(R.id.back_fl)
            .setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }

        val titleTv: TextView = mView.findViewById(R.id.fragment_reward_title_tv)
        val descTv: TextView = mView.findViewById(R.id.fragment_reward_desc_tv)
        val dateTv: TextView = mView.findViewById(R.id.fragment_reward_date_tv)
        val pic: NetworkImageView = mView.findViewById(R.id.fragment_reward_pic)
        titleTv.text = rewardData!!.rW_title
        descTv.text = rewardData!!.rW_describe
        NetworkManager.getInstance().setNetworkImage(mContext, pic, rewardData!!.rW_img)

        getRewardAlreadyBtn = mView.findViewById<TextView>(R.id.already_collect_rewards_btn)
        getRewardBtn = mView.findViewById<TextView>(R.id.collect_rewards_btn)
        if (rewardData!!.is_finish) {
            if (rewardData!!.rws_enabled.equals("Done")) {
                getRewardAlreadyBtn!!.visibility = View.VISIBLE
                getRewardBtn!!.visibility = View.GONE
            } else if (rewardData!!.rws_enabled.equals("None")) {
                getRewardAlreadyBtn!!.visibility = View.GONE
                getRewardBtn!!.visibility = View.VISIBLE
            }
        }
        getRewardBtn!!.setOnClickListener {
            showHintMessage()
        }

        return mView
    }

    @SuppressLint("HardwareIds")
    fun showHintMessage() {
        val view =
            LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_message, null, false)
        val builder = AlertDialog.Builder(requireActivity()).setView(view)
        val messageDialog = builder.create()
        messageDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        view.findViewById<View>(R.id.dialog_message_neg).visibility = View.VISIBLE
        view.findViewById<View>(R.id.dialog_message_divider).visibility = View.VISIBLE
        (view.findViewById<View>(R.id.dialog_message_title) as TextView).setText(R.string.receive_reward)
        (view.findViewById<View>(R.id.dialog_message_desc) as TextView).setText(R.string.receive_reward_hint)
        (view.findViewById<View>(R.id.dialog_message_neg) as TextView).setText(R.string.reward_postpone)

        view.findViewById<View>(R.id.dialog_message_pos)
            .setOnClickListener {
                TpeArApi.getInstance().getMissionReward(requireActivity(),
                    rewardData!!.m_id,
                    userId,
                    Settings.Secure.getString(
                        mContext!!.contentResolver,
                        Settings.Secure.ANDROID_ID
                    ),
                    object : NetworkManagerListener<MissionRewardFeedback?> {
                        override fun onSucceed(feedback: MissionRewardFeedback?) {
                            getRewardAlreadyBtn!!.visibility = View.VISIBLE
                            getRewardBtn!!.visibility = View.GONE
                            messageDialog.dismiss()
                            EventBus.getDefault()
                                .post(OmniEvent(OmniEvent.TYPE_REWARD_COMPLETE, ""))
                        }

                        override fun onFail(error: VolleyError, shouldRetry: Boolean) {}
                    })
            }
        view.findViewById<View>(R.id.dialog_message_neg)
            .setOnClickListener { messageDialog.dismiss() }
        messageDialog.show()
    }

    private fun openFragmentPage(fragment: Fragment, tag: String) {
        AnimationFragmentManager.getInstance().addFragmentPage(
            activity,
            R.id.activity_main_fl, fragment, tag
        )
    }
}