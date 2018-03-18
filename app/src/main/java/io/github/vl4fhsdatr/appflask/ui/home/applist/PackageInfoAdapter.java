package io.github.vl4fhsdatr.appflask.ui.home.applist;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

public class PackageInfoAdapter extends RecyclerView.Adapter<PackageInfoAdapter.ViewHolder> {

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

    private List<PackageInfo> mPackageInfoList;
    private PackageManager mPackageManager;
    private SparseBooleanArray mCheckedItems = new SparseBooleanArray();

    /**
     *
     * @param packageInfoList initial data
     * @param packageManager PackageManager
     */
    PackageInfoAdapter(List<PackageInfo> packageInfoList, PackageManager packageManager) {
        this.mPackageInfoList = packageInfoList;
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
        PackageInfo packageInfo = mPackageInfoList.get(position);
        holder.mIcon.setImageDrawable(packageInfo.applicationInfo.loadIcon(mPackageManager));
        holder.mLabel.setText(packageInfo.applicationInfo.loadLabel(mPackageManager));
        holder.mChecked.setVisibility(mCheckedItems.get(position, false) ? View.VISIBLE: View.GONE);
        holder.mDisabled.setVisibility(packageInfo.applicationInfo.enabled ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mPackageInfoList.size();
    }

    /**
     * Get data at position.
     * @param position position
     * @return current position data
     */
    @SuppressWarnings("WeakerAccess")
    public PackageInfo getItemData(int position) {
        return mPackageInfoList.get(position);
    }

    /**
     * Reset data.
     * @param packageInfoList new data
     */
    @SuppressWarnings("WeakerAccess")
    public void resetDataSet(List<PackageInfo> packageInfoList) {
        this.mPackageInfoList = packageInfoList;
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
            for (int i = 0; i < mPackageInfoList.size(); i++) {
                mCheckedItems.put(i, true);
            }
        } else {
            mCheckedItems.clear();
        }
        notifyItemRangeChanged(0, mPackageInfoList.size());
    }

}
