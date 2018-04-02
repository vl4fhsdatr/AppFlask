package io.github.vl4fhsdatr.appflask.ui.home.applist;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;
import io.github.vl4fhsdatr.appflask.R;
import io.github.vl4fhsdatr.appflask.persistence.AppDatabase;
import io.github.vl4fhsdatr.appflask.core.AppInfo;
import io.github.vl4fhsdatr.appflask.ui.ActionModeSupport;
import io.github.vl4fhsdatr.appflask.util.RxUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public abstract class AbstractAppListFragment extends Fragment
        implements ActionMode.Callback, View.OnClickListener {

    @SuppressWarnings("unused")
    private static final String TAG = "AbstractAppListFragment";

    private static final int ID_MENU_SELECT_ALL = ViewCompat.generateViewId();

    private AVLoadingIndicatorView mIndicatorView;
    private RecyclerView mRecyclerView;
    private FloatingActionMenu mFabMenu;

    private AppInfoAdapter mRecyclerViewAdapter;

    protected final CompositeDisposable mDisposables = new CompositeDisposable();

    private boolean mDataLoaded;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_abstract_app_list, container, false);
        mIndicatorView = view.findViewById(R.id.li_app);
        mRecyclerView = view.findViewById(R.id.recycler_view_app);
        mFabMenu = view.findViewById(R.id.fab_menu);

        setupRecyclerView();
        setupFabMenu();

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mDataLoaded && !mIndicatorView.isShown()) {
            mIndicatorView.setVisibility(View.VISIBLE);
            mIndicatorView.show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mIndicatorView.isShown()) {
            mIndicatorView.hide();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIndicatorView = null;
        mRecyclerView = null;
        mFabMenu = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposables.clear();
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        actionMode.setTitle("Edit");
        onCreateContextualOptionsMenu(menu);
        MenuItem item = menu.add(Menu.NONE, ID_MENU_SELECT_ALL, Menu.NONE, "SelectAll");
        item.setIcon(R.drawable.ic_menu_select_all);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        if (menuItem.getItemId() == ID_MENU_SELECT_ALL) {
            mRecyclerViewAdapter.setAllItemChecked(true);
            return true;
        }
        boolean result = onContextualOptionsItemSelected(menuItem);
        actionMode.finish();
        return result;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        mRecyclerViewAdapter.setAllItemChecked(false);
        mFabMenu.setEnabled(true);
    }

    @Override
    public void onClick(View view) {
        if (!mFabMenu.isEnabled()) {
            return;
        }
        switch (view.getId()) {
            case R.id.fab_menu_edit: {
                Activity activity = getActivity();
                if (activity instanceof AppCompatActivity) {
                    AppCompatActivity appCompatActivity = (AppCompatActivity)activity;
                    if (appCompatActivity.startSupportActionMode(this) != null) {
                        mFabMenu.close(false);
                        mFabMenu.setEnabled(false);
                    }
                } else {
                    Log.w(TAG, "using AbstractAppListFragment within Activity which does not extend AppCompatActivity");
                }
            }
                break;

            default:
                onFabOptionsItemSelected(view.getId());
        }
    }

    protected abstract void onCreateContextualOptionsMenu(Menu menu);

    protected abstract boolean onContextualOptionsItemSelected(MenuItem item);

    protected abstract void onCreateFabOptionsMenu();

    protected abstract void onFabOptionsItemSelected(int id);

    protected abstract AppDatabase getDatabase();

    protected List<String> getCheckedApplications() {
        List<String> result = new ArrayList<>();
        SparseBooleanArray array = mRecyclerViewAdapter.getCheckedItemPositions();
        int itemCount = mRecyclerViewAdapter.getItemCount();
        for ( int i = 0; i < itemCount; i++) {
            if (array.get(i, false)) {
                result.add(mRecyclerViewAdapter.getItemData(i).getName());
            }
        }
        return result;
    }

    protected List<String> getAllApplications() {
        List<String> result = new ArrayList<>();
        int itemCount = mRecyclerViewAdapter.getItemCount();
        for ( int i = 0; i < itemCount; i++) {
            result.add(mRecyclerViewAdapter.getItemData(i).getName());
        }
        return result;
    }

    protected void addFabOptionsMenuItem(@IdRes int id, @StringRes int labelRes, @DrawableRes int iconRes) {
        //noinspection ConstantConditions
        FloatingActionButton button = new FloatingActionButton(getActivity());
        button.setLayoutParams(new FloatingActionMenu.LayoutParams(
                FloatingActionMenu.LayoutParams.WRAP_CONTENT,
                FloatingActionMenu.LayoutParams.WRAP_CONTENT
        ));
        button.setId(id);
        button.setLabelText(getString(labelRes));
        button.setImageResource(iconRes);
        button.setOnClickListener(this);
        button.setColorDisabledResId(R.color.colorFabDisabled);
        button.setColorNormalResId(R.color.colorFabNormal);
        button.setColorRippleResId(R.color.colorFabRipple);
        button.setColorPressedResId(R.color.colorFabPressed);
        mFabMenu.addMenuButton(button);
    }

    protected boolean shouldShowSystemApp() {
        return PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(getString(R.string.pref_show_system_app), false);
    }

    private void setupFabMenu() {
        onCreateFabOptionsMenu();
        mFabMenu.findViewById(R.id.fab_menu_edit).setOnClickListener(this);
    }

    private void setupRecyclerView() {

        DisplayMetrics metrics = new DisplayMetrics();
        //noinspection ConstantConditions
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int spanCount = (int)(metrics.widthPixels / getResources().getDimension(R.dimen.app_cell_width));
        spanCount = spanCount < 1 ? 1 : spanCount;
        RecyclerView.LayoutManager layoutManager =new GridLayoutManager(getContext(), spanCount);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (actionModeIsPresent()) {
                    mRecyclerViewAdapter.setItemChecked(position, !mRecyclerViewAdapter.isItemChecked(position));
                } else {
                    onPackageItemClick(position);
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        //noinspection ConstantConditions
        mRecyclerViewAdapter = new AppInfoAdapter(new ArrayList<AppInfo>(0), getActivity().getPackageManager());
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        getDatabase().getAppInfoDao().listAllApps().observe(this, new Observer<List<AppInfo>>() {
            @Override
            public void onChanged(@Nullable List<AppInfo> appInfos) {
                mDataLoaded = true;
                mIndicatorView.hide();
                List<AppInfo> results = new ArrayList<>();
                if (appInfos != null) {
                    for (AppInfo info: appInfos) {
                        if (filterAppInfo(info)) {
                            results.add(info);
                        }
                    }
                }
                mRecyclerViewAdapter.resetDataSet(results);
                mRecyclerView.setVisibility(View.VISIBLE);
                mFabMenu.setVisibility(View.VISIBLE);
            }
        });

    }

    protected abstract boolean filterAppInfo(AppInfo info);

    private void onPackageItemClick(int position) {
        final AppInfo info = mRecyclerViewAdapter.getItemData(position);
        //  TODO
        mDisposables.add(RxUtils.enableApp(info.getName(), getDatabase())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Void>() {

                    @Override
                    public void onNext(Void aVoid) {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        startOrViewApp(info.getName());
                    }
                })
        );
    }

    private void startOrViewApp(String packageName) {
        //noinspection ConstantConditions
        Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            startActivity(launchIntent);
            return;
        }
        // https://stackoverflow.com/questions/31127116/open-app-permission-settings
        Intent detailIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + packageName));
        detailIntent.addCategory(Intent.CATEGORY_DEFAULT);
        detailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(detailIntent);
    }


    private boolean actionModeIsPresent() {
        Activity activity = getActivity();
        if (!(activity instanceof ActionModeSupport)) {
            return false;
        }
        ActionModeSupport actionModeSupport = (ActionModeSupport) activity;
        return actionModeSupport.getSupportActionMode() != null;
    }

}
