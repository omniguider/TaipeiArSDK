package com.omni.taipeiarsdk.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.omni.taipeiarsdk.R;
import com.omni.taipeiarsdk.manager.AnimationFragmentManager;
import com.omni.taipeiarsdk.model.OmniEvent;
import com.omni.taipeiarsdk.model.tpe_location.ThemeData;
import com.omni.taipeiarsdk.network.NetworkManager;
import com.omni.taipeiarsdk.view.theme.ThemeDetailFragment;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.omni.taipeiarsdk.TaipeiArSDKActivity.themeTitle;

public class ThemeGuideAdapter extends RecyclerView.Adapter<ThemeGuideAdapter.ViewHolder> {

    private final Context mContext;
    private List<ThemeData> mThemeData;

    public ThemeGuideAdapter(Context context, List<ThemeData> data) {
        mContext = context;
        mThemeData = data;
    }

    public void updateAdapter(List<ThemeData> data) {
        mThemeData = data;
        notifyDataSetChanged();
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.theme_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mThemeData.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ThemeData data = mThemeData.get(position);

        holder.title.setText(data.getName());
        NetworkManager.getInstance().setNetworkImage(mContext, holder.img, data.getImage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                themeTitle = data.getName();

//                EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_OPEN_AR_GUIDE, data.getPoi()));
                openFragmentPage(ThemeDetailFragment.Companion.newInstance(data), ThemeDetailFragment.TAG);
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        protected NetworkImageView img;
        protected TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            title = itemView.findViewById(R.id.title);
        }
    }

    private void openFragmentPage(Fragment fragment, String tag) {
        AnimationFragmentManager.getInstance().addFragmentPage((FragmentActivity) mContext,
                R.id.activity_main_fl, fragment, tag);
    }
}
