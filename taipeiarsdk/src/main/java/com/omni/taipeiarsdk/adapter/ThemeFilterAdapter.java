package com.omni.taipeiarsdk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.omni.taipeiarsdk.R;
import com.omni.taipeiarsdk.TaipeiArSDKActivity;
import com.omni.taipeiarsdk.model.OmniEvent;
import com.omni.taipeiarsdk.model.tpe_location.Category;
import com.omni.taipeiarsdk.model.tpe_location.ThemeData;
import com.omni.taipeiarsdk.network.NetworkManager;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.omni.taipeiarsdk.TaipeiArSDKActivity.filterKeyword;
import static com.omni.taipeiarsdk.TaipeiArSDKActivity.filterKeywordCopy;

public class ThemeFilterAdapter extends RecyclerView.Adapter<ThemeFilterAdapter.ViewHolder> {

    private final Context mContext;
    private List<Category> mCategoryData;

    public ThemeFilterAdapter(Context context, List<Category> data) {
        mContext = context;
        mCategoryData = data;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.theme_category_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mCategoryData.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Category data = mCategoryData.get(position);

        holder.title.setText(data.getTitle());

        if (filterKeyword.contains(data.getTitle())) {
            holder.layout.setBackgroundResource(R.drawable.solid_round_rectangle_gray_green_stroke);
            holder.title.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.layout.setBackgroundResource(R.drawable.solid_round_rectangle_gray);
            holder.title.setTextColor(mContext.getResources().getColor(R.color.gray_33));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filterKeywordCopy.contains(data.getTitle())) {
                    holder.layout.setBackgroundResource(R.drawable.solid_round_rectangle_gray);
                    holder.title.setTextColor(mContext.getResources().getColor(R.color.gray_33));
                    filterKeywordCopy.remove(data.getTitle());
                } else {
                    holder.layout.setBackgroundResource(R.drawable.solid_round_rectangle_gray_green_stroke);
                    holder.title.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                    filterKeywordCopy.add(data.getTitle());
                }
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView title;
        protected FrameLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            layout = itemView.findViewById(R.id.layout);
        }
    }
}
