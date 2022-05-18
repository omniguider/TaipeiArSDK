package com.omni.taipeiarsdk.view.mission;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.omni.taipeiarsdk.R;
import com.omni.taipeiarsdk.TaipeiArSDKActivity;
import com.omni.taipeiarsdk.model.mission.MissionData;
import com.omni.taipeiarsdk.model.mission.RewardData;
import com.omni.taipeiarsdk.model.mission.RewardFeedback;
import com.omni.taipeiarsdk.network.NetworkManager;
import com.omni.taipeiarsdk.network.TpeArApi;
import com.omni.taipeiarsdk.tool.DialogTools;

import java.util.ArrayList;
import java.util.List;

public class RewardListFragment extends Fragment {

    private View mView;
    private RecyclerView mRecyclerView;
    public static String userId = "";
    public static String missionId = "";
    public static String title = "";
    public static String describe = "";
    public static int reward_num = 0;
    public static RewardFeedback mRewardFeedback;
    private LinearLayout emptyLayout;
    private ArrayList<RewardData> mData;

    public static RewardListFragment newInstance() {
        RewardListFragment fragment = new RewardListFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.reward_list_recycler_view, null, false);
            mRecyclerView = mView.findViewById(R.id.reward_list_recycler_view);
            final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
            mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);

            emptyLayout = mView.findViewById(R.id.empty_layout);

            userId = TaipeiArSDKActivity.userId;
            if (userId == null || userId.length() == 0) {
                userId = "Hf1242aaa6"; // not login
            }

            TpeArApi.getInstance().getReward(this.getActivity(),
                    "reward",
                    userId,
                    new NetworkManager.NetworkManagerListener<RewardFeedback>() {
                        @Override
                        public void onSucceed(RewardFeedback feedback) {
                            mRewardFeedback = feedback;
                            reward_num = feedback.getData().length;
                            ArrayList<RewardData> myData = new ArrayList<>();
                            for (int i = 0; i < reward_num; i++) {
                                if (feedback.getData()[i].getIs_finish()) {
                                    myData.add(feedback.getData()[i]);
                                }
                            }
                            RecyclerViewAdapter mRecyclerViewAdapter = new RecyclerViewAdapter(myData);
                            mRecyclerView.setAdapter(mRecyclerViewAdapter);
                            if (reward_num != 0) {
                                emptyLayout.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFail(VolleyError error, boolean shouldRetry) {
                        }
                    });
        }
        return mView;
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public TextView date;
            public NetworkImageView img;

            public ViewHolder(View v) {
                super(v);
                this.title = v.findViewById(R.id.reward_title);
                this.date = v.findViewById(R.id.reward_date);
                this.img = v.findViewById(R.id.reward_img);
            }
        }

        public RecyclerViewAdapter(ArrayList<RewardData> data) {
            mData = new ArrayList<>();
            mData.addAll(data);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.reward_list_card_view, parent, false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.title.setText(mData.get(position).getRW_title());

            final int pos = position;
            NetworkManager.getInstance().setNetworkImage(getContext(),
                    holder.img, mData.get(position).getM_img());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mTitle = mData.get(position).getM_title();
                    for (int i = 0; i < reward_num; i++) {
                        if (mRewardFeedback.getData()[i].getM_title().equals(mTitle)) {
                            missionId = mRewardFeedback.getData()[i].getM_id();
                            title = mRewardFeedback.getData()[pos].getRW_title();
                            describe = mRewardFeedback.getData()[pos].getRW_describe();
                            break;
                        }
                    }
//                        Intent toMissionActivity = new Intent(getActivity(), MissionActivity.class);
//                        toMissionActivity.putExtra("missionID",missionId);
//                        toMissionActivity.putExtra("title",title);
//                        toMissionActivity.putExtra("describe",describe);
//                        startActivity(toMissionActivity);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }
}
