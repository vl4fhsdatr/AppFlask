package io.github.vl4fhsdatr.appflask.ui.home.applist;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.github.vl4fhsdatr.appflask.R;
import io.github.vl4fhsdatr.appflask.core.AppInfo;

public class AppInfoAdapter extends RecyclerView.Adapter<AppInfoAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mLabel;
        private ImageView mIcon;
        private ImageView mChecked;
        private ImageView mDisabled;

        ViewHolder(View itemView) {
            super(itemView);
            mLabel = itemView.findViewById(R.id.text_app_label);
            mIcon = itemView.findViewById(R.id.image_app_icon);
            mChecked = itemView.findViewById(R.id.image_app_checked);
            mDisabled = itemView.findViewById(R.id.image_app_disabled);
        }

    }

    private List<AppInfo> mAppInfoList;
    private PackageManager mPackageManager;
    private SparseBooleanArray mCheckedItems = new SparseBooleanArray();

    /**
     *
     * @param appInfoList initial data
     * @param packageManager PackageManager
     */
    AppInfoAdapter(List<AppInfo> appInfoList, PackageManager packageManager) {
        this.mAppInfoList = appInfoList;
        this.mPackageManager = packageManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_app_cell, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppInfo info = mAppInfoList.get(position);
        try {
            ApplicationInfo applicationInfo = mPackageManager.getApplicationInfo(info.getName(), 0);
            holder.mIcon.setImageDrawable(applicationInfo.loadIcon(mPackageManager));
            if (info.isInProcessing()) {
                setLocked(holder.mIcon);
            } else {
                setUnlocked(holder.mIcon);
            }
            holder.mLabel.setText(applicationInfo.loadLabel(mPackageManager));
            holder.mDisabled.setVisibility(applicationInfo.enabled ? View.GONE : View.VISIBLE);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        holder.mChecked.setVisibility(mCheckedItems.get(position, false) ? View.VISIBLE: View.GONE);
    }

    // https://stackoverflow.com/questions/28308325/androidset-gray-scale-filter-to-imageview#28312202

    public static void  setLocked(ImageView v)
    {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);  //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        v.setColorFilter(cf);
        v.setImageAlpha(128);   // 128 = 0.5
    }

    public static void  setUnlocked(ImageView v)
    {
        v.setColorFilter(null);
        v.setImageAlpha(255);
    }

    @Override
    public int getItemCount() {
        return mAppInfoList.size();
    }

    /**
     * Get data at position.
     * @param position position
     * @return current position data
     */
    @SuppressWarnings("WeakerAccess")
    public AppInfo getItemData(int position) {
        return mAppInfoList.get(position);
    }

    /**
     * Reset data.
     * @param appInfoList new data
     */
    @SuppressWarnings("WeakerAccess")
    public void resetDataSet(List<AppInfo> appInfoList) {
        this.mAppInfoList = appInfoList;
        notifyDataSetChanged();
    }


    /**
     * Set item to isChecked
     * @param position position
     * @param isChecked isChecked
     */
    @SuppressWarnings("WeakerAccess")
    public void setItemChecked(int position, boolean isChecked) {
        if (isChecked) {
            mCheckedItems.put(position, true);
        } else {
            mCheckedItems.delete(position);
        }
        notifyItemChanged(position);
    }

    /**
     * Get isChecked at position.
     * @param position position
     * @return isChecked
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isItemChecked(int position) {
        return mCheckedItems.get(position, false);
    }

    /**
     * Get checked item positions.
     * @return checked item position represent by a SparseBooleanArray
     */
    @SuppressWarnings("WeakerAccess")
    public SparseBooleanArray getCheckedItemPositions() {
        return mCheckedItems;
    }

    /**
     * Get checked item number.
     * @return checked item number
     */
    @SuppressWarnings("unused")
    public int getCheckedItemsCount() {
        return mCheckedItems.size();
    }

    /**
     * Set all item to isChecked.
     * @param isChecked isChecked
     */
    @SuppressWarnings("WeakerAccess")
    public void setAllItemChecked(boolean isChecked) {
        if (isChecked) {
            for (int i = 0; i < mAppInfoList.size(); i++) {
                mCheckedItems.put(i, true);
            }
        } else {
            mCheckedItems.clear();
        }
        notifyItemRangeChanged(0, mAppInfoList.size());
    }

}
