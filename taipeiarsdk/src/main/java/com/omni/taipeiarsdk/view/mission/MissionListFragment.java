package com.omni.taipeiarsdk.view.mission;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.omni.taipeiarsdk.R;
import com.omni.taipeiarsdk.TaipeiArSDKActivity;
import com.omni.taipeiarsdk.manager.AnimationFragmentManager;
import com.omni.taipeiarsdk.model.mission.MissionData;
import com.omni.taipeiarsdk.model.mission.MissionFeedback;
import com.omni.taipeiarsdk.network.NetworkManager;
import com.omni.taipeiarsdk.network.TpeArApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MissionListFragment extends Fragment {

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
    private View mView;
    private RecyclerView mRecyclerView;
    public static String userId = "";
    private String missionId = "";
    private String title = "";
    private String describe = "";
    private String verify_code = "";
    private int mission_num = 0;
    private MissionFeedback mMissionFeedback;
    private ArrayList<MissionData> allData;
    private ArrayList<MissionData> mData;
    private Date startDate;
    private Date endDate;
    private RecyclerViewAdapter mRecyclerViewAdapter;

    public static MissionListFragment newInstance() {
        MissionListFragment fragment = new MissionListFragment();

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
            mView = inflater.inflate(R.layout.mission_list_recycler_view, null, false);
            mRecyclerView = mView.findViewById(R.id.mission_list_recycler_view);
            final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
            mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);

            userId = TaipeiArSDKActivity.userId;
            if (userId == null || userId.length() == 0) {
                userId = "Hf1242aaa6"; // not login
            }

            TpeArApi.getInstance().getMission(this.getActivity(),
                    "mission",
                    userId,
                    new NetworkManager.NetworkManagerListener<MissionFeedback>() {
                        @Override
                        public void onSucceed(MissionFeedback feedback) {
                            mMissionFeedback = feedback;
                            mission_num = feedback.getData().length;
                            allData = new ArrayList<>();
                            for (int i = 0; i < mission_num; i++) {
                                allData.add(feedback.getData()[i]);
                            }
                            mRecyclerViewAdapter = new RecyclerViewAdapter(allData);
                            mRecyclerView.setAdapter(mRecyclerViewAdapter);
                        }

                        @Override
                        public void onFail(VolleyError error, boolean shouldRetry) {
                        }
                    });
        }

        mView.findViewById(R.id.startDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String datetime = String.valueOf(year) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(day);
                        ((TextView) mView.findViewById(R.id.startDate)).setText(datetime);

                        try {
                            startDate = formatter.parse(datetime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, year, month, day).show();
            }
        });
        mView.findViewById(R.id.endDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String datetime = String.valueOf(year) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(day);
                        ((TextView) mView.findViewById(R.id.endDate)).setText(datetime);

                        try {
                            endDate = formatter.parse(datetime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, year, month, day).show();
            }
        });
        mView.findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TextView) mView.findViewById(R.id.startDate)).setText(R.string.start_date);
                ((TextView) mView.findViewById(R.id.endDate)).setText(R.string.end_date);
                mData.clear();
                for (MissionData data : allData) {
                    mData.add(data);
                }
                mRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
        mView.findViewById(R.id.date_search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startDate != null && endDate != null) {
                    Date missionStartDate, missionEndDate;
                    mData.clear();
                    for (MissionData data : allData) {
                        try {
                            missionStartDate = formatter.parse(
                                    data.getM_start_time().split(" ")[0].replace("-", "/"));
                            missionEndDate = formatter.parse(
                                    data.getM_end_time().split(" ")[0].replace("-", "/"));

                            if (!(missionStartDate.after(endDate) || missionEndDate.before(startDate))) {
                                mData.add(data);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    mRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });
        return mView;
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public TextView date;
            public NetworkImageView img;

            public ViewHolder(View v) {
                super(v);
                this.title = v.findViewById(R.id.mission_title);
                this.date = v.findViewById(R.id.mission_date);
                this.img = v.findViewById(R.id.mission_img);
            }
        }

        public RecyclerViewAdapter(ArrayList<MissionData> data) {
            mData = new ArrayList<>();
            mData.addAll(data);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.mission_list_card_view, parent, false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.title.setText(mData.get(position).getM_title());
            holder.date.setText(mData.get(position).getM_start_time().split(" ")[0].replace("-", "/")
                    + " ~ "
                    + mData.get(position).getM_end_time().split(" ")[0].replace("-", "/"));
            final int pos = position;
            NetworkManager.getInstance().setNetworkImage(getContext(),
                    holder.img, mData.get(position).getM_img());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    missionId = mMissionFeedback.getData()[pos].getM_id();
                    title = mMissionFeedback.getData()[pos].getM_title();
                    describe = mMissionFeedback.getData()[pos].getM_describe();
                    verify_code = mMissionFeedback.getData()[pos].getVerify_code();

                    openFragmentPage(NineGridFragment.Companion.newInstance(missionId,
                            title, describe, verify_code), NineGridFragment.TAG);
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
