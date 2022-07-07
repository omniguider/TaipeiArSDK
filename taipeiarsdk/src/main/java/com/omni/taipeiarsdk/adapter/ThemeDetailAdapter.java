package com.omni.taipeiarsdk.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import java.text.DecimalFormat;
import java.util.List;

import static com.omni.taipeiarsdk.TaipeiArSDKActivity.isMission;
import static com.omni.taipeiarsdk.TaipeiArSDKActivity.themeTitle;

public class ThemeDetailAdapter extends RecyclerView.Adapter<ThemeDetailAdapter.ViewHolder> {

    private final Context mContext;
    private List<IndexPoi> mIndexPoi;
    private AlertDialog poiInfoDialog;
    private DecimalFormat df = new DecimalFormat("##0.00");

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
        holder.distance.setText(df.format(data.getDistance()) + "公尺");
        NetworkManager.getInstance().setNetworkImage(mContext, holder.img, data.getImage());

        holder.info.setOnClickListener(view -> {
            View dialogPoiInfo = LayoutInflater.from(mContext).inflate(R.layout.dialog_poi_info, null, false);
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext).setView(dialogPoiInfo);
            poiInfoDialog = builder.create();
            poiInfoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            poiInfoDialog.show();

            String url = data.getImage();
            NetworkManager.getInstance().setNetworkImage(mContext,
                    dialogPoiInfo.findViewById(R.id.dialog_poi_info_img),
                    url);
            ((TextView) dialogPoiInfo.findViewById(R.id.dialog_poi_info_title)).setText(data.getName());
            ((TextView) dialogPoiInfo.findViewById(R.id.dialog_poi_info_desc)).setText(data.getDesc());
            dialogPoiInfo.findViewById(R.id.dialog_poi_info_close).setOnClickListener(v -> poiInfoDialog.dismiss());
            dialogPoiInfo.findViewById(R.id.dialog_poi_info_btn_ll).setVisibility(View.GONE);
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        protected NetworkImageView img;
        protected TextView title;
        protected TextView distance;
        protected TextView info;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            title = itemView.findViewById(R.id.title);
            distance = itemView.findViewById(R.id.distance);
            info = itemView.findViewById(R.id.info);
        }
    }
}
