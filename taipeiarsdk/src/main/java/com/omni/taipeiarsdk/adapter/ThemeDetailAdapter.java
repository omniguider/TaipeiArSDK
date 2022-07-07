package com.omni.taipeiarsdk.adapter;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.omni.taipeiarsdk.R;
import com.omni.taipeiarsdk.model.OmniEvent;
import com.omni.taipeiarsdk.model.tpe_location.IndexPoi;
import com.omni.taipeiarsdk.model.tpe_location.ThemeData;
import com.omni.taipeiarsdk.network.NetworkManager;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.omni.taipeiarsdk.TaipeiArSDKActivity.themeTitle;

public class ThemeDetailAdapter extends RecyclerView.Adapter<ThemeDetailAdapter.ViewHolder> {

    private final Context mContext;
    private List<IndexPoi> mIndexPoi;

    public ThemeDetailAdapter(Context context, List<IndexPoi> data) {
        mContext = context;
        mIndexPoi = data;
    }

    public void updateAdapter(List<IndexPoi> data) {
        mIndexPoi = data;
        notifyDataSetChanged();
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.theme_detail_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mIndexPoi.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final IndexPoi data = mIndexPoi.get(position);

        holder.title.setText(data.getName());
        NetworkManager.getInstance().setNetworkImage(mContext, holder.img, data.getImage());

        holder.info.setOnClickListener(view -> {

        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        protected NetworkImageView img;
        protected TextView title;
        protected TextView info;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            title = itemView.findViewById(R.id.title);
            info = itemView.findViewById(R.id.info);
        }
    }
}
