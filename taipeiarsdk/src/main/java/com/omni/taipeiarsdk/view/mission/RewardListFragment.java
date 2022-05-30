package com.omni.taipeiarsdk.view.mission;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.omni.taipeiarsdk.R;
import com.omni.taipeiarsdk.manager.AnimationFragmentManager;
import com.omni.taipeiarsdk.model.OmniEvent;
import com.omni.taipeiarsdk.model.mission.RewardData;
import com.omni.taipeiarsdk.model.mission.RewardFeedback;
import com.omni.taipeiarsdk.network.NetworkManager;
import com.omni.taipeiarsdk.network.TpeArApi;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import static com.omni.taipeiarsdk.TaipeiArSDKActivity.userId;

public class RewardListFragment extends Fragment {

    private View mView;
    private RecyclerView mRecyclerView;
    public static String missionId = "";
    public static String title = "";
    public static String describe = "";
    public static int reward_num = 0;
    public static RewardFeedback mRewardFeedback;
    private LinearLayout emptyLayout;
    private ArrayList<RewardData> mData = new ArrayList();
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private EventBus mEventBus;

    public static RewardListFragment newInstance() {
        RewardListFragment fragment = new RewardListFragment();

        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(OmniEvent event) {
        switch (event.getType()) {
            case OmniEvent.TYPE_REWARD_COMPLETE:
                TpeArApi.getInstance().getReward(this.getActivity(),
                        "reward",
                        userId,
                        new NetworkManager.NetworkManagerListener<RewardFeedback>() {
                            @Override
                            public void onSucceed(RewardFeedback feedback) {
                                mRewardFeedback = feedback;
                                reward_num = feedback.getData().length;
                                mData.clear();
                                for (int i = 0; i < reward_num; i++) {
                                    if (feedback.getData()[i].getIs_finish()) {
                                        mData.add(feedback.getData()[i]);
                                    }
                                }
                                mRecyclerViewAdapter.notifyDataSetChanged();
                                if (reward_num != 0) {
                                    emptyLayout.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onFail(VolleyError error, boolean shouldRetry) {
                            }
                        });
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mEventBus == null) {
            mEventBus = EventBus.getDefault();
        }
        mEventBus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mEventBus != null) {
            mEventBus.unregister(this);
        }
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

            TpeArApi.getInstance().getReward(this.getActivity(),
                    "reward",
                    userId,
                    new NetworkManager.NetworkManagerListener<RewardFeedback>() {
                        @Override
                        public void onSucceed(RewardFeedback feedback) {
                            mRewardFeedback = feedback;
                            reward_num = feedback.getData().length;
                            for (int i = 0; i < reward_num; i++) {
                                if (feedback.getData()[i].getIs_finish()) {
                                    mData.add(feedback.getData()[i]);
                                }
                            }
                            mRecyclerViewAdapter = new RecyclerViewAdapter();
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
            public TextView status;

            public ViewHolder(View v) {
                super(v);
                this.title = v.findViewById(R.id.reward_title);
                this.date = v.findViewById(R.id.reward_date);
                this.img = v.findViewById(R.id.reward_img);
                this.status = v.findViewById(R.id.reward_state);
            }
        }

        public RecyclerViewAdapter() {
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

            if (mData.get(position).getRws_enabled().equals("Done")) {
                holder.status.setText(R.string.received);
                holder.status.setTextColor(getResources().getColor(R.color.gray_6d));
            }
            NetworkManager.getInstance().setNetworkImage(getContext(),
                    holder.img, mData.get(position).getRW_img());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFragmentPage(RewardFragment.Companion.newInstance(mData.get(position)), RewardFragment.TAG);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    private void openFragmentPage(Fragment fragment, String tag) {
        AnimationFragmentManager.getInstance().addFragmentPage(getActivity(),
                R.id.activity_main_fl, fragment, tag);
    }
}
