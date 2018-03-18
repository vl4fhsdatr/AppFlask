package io.github.vl4fhsdatr.appflask.ui.home.applist;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import io.github.vl4fhsdatr.appflask.R;
import io.github.vl4fhsdatr.appflask.ui.ActionModeSupport;
import io.reactivex.Observable;
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

    private PackageInfoAdapter mRecyclerViewAdapter;
    private int mNumberOfWipOperations;

    protected final CompositeDisposable mDisposables = new CompositeDisposable();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_abstract_app_list, container, false);
        mIndicatorView = view.findViewById(R.id.li_app);
        mRecyclerView = view.findViewById(R.id.recycler_view_app);
        mFabMenu = view.findViewById(R.id.fab_menu);

        setupRecyclerView();
        setupFabMenu();

        triggerLoadPackageInfoListInternal();

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mNumberOfWipOperations > 0) {
            if (!mIndicatorView.isShown()) {
                mIndicatorView.setVisibility(View.VISIBLE);
                mIndicatorView.show();
            }
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mIndicatorView.isShown()) {
            mIndicatorView.hide();
        }
        EventBus.getDefault().unregister(this);
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
            case R.id.fab_menu_refresh:
                triggerLoadPackageInfoList();
                break;
            default:
                onFabOptionsItemSelected(view.getId());
        }
    }

    protected abstract boolean onContextualOptionsItemSelected(MenuItem item);

    protected abstract void onCreateFabOptionsMenu();

    protected abstract void onFabOptionsItemSelected(int id);

    protected abstract void onCreateContextualOptionsMenu(Menu menu);

    protected abstract Observable<List<PackageInfo>> onCreatePackageInfoListObservable();

    protected List<String> getCheckedApplications() {
        List<String> result = new ArrayList<>();
        SparseBooleanArray array = mRecyclerViewAdapter.getCheckedItemPositions();
        int itemCount = mRecyclerViewAdapter.getItemCount();
        for ( int i = 0; i < itemCount; i++) {
            if (array.get(i, false)) {
                result.add(mRecyclerViewAdapter.getItemData(i).packageName);
            }
        }
        return result;
    }

    protected List<String> getAllApplications() {
        List<String> result = new ArrayList<>();
        int itemCount = mRecyclerViewAdapter.getItemCount();
        for ( int i = 0; i < itemCount; i++) {
            result.add(mRecyclerViewAdapter.getItemData(i).packageName);
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

    protected void beginAsyncTask(String tag) {
        Log.d(TAG, String.format("(%d)%s::beginAsyncTask => %s", mNumberOfWipOperations, getClass().getName(),tag));
        mNumberOfWipOperations += 1;
        if (mNumberOfWipOperations == 1) {
            if (!mIndicatorView.isShown()) {
                // !important
                mIndicatorView.setVisibility(View.VISIBLE);
                mIndicatorView.show();
            }
            mRecyclerView.setVisibility(View.GONE);
            mFabMenu.setVisibility(View.GONE);
        }
    }

    protected void endAsyncTask(String tag) {
        mNumberOfWipOperations -= 1;
        if (mNumberOfWipOperations == 0) {
            if (mIndicatorView.isShown()) {
                mIndicatorView.hide();
            }
            mRecyclerView.setVisibility(View.VISIBLE);
            mFabMenu.setVisibility(View.VISIBLE);
        }
        Log.d(TAG, String.format("(%d)%s::endAsyncTask => %s", mNumberOfWipOperations, getClass().getName(),tag));
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRefresh(RefreshEvent ev) {
        triggerLoadPackageInfoListInternal();
    }

    // TODO find a better way to refresh data
    protected void triggerLoadPackageInfoList() {
        EventBus.getDefault().post(new RefreshEvent());
    }

    private void triggerLoadPackageInfoListInternal() {
        beginAsyncTask("triggerLoadPackageInfoListInternal");
        mDisposables.add(onCreatePackageInfoListObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<PackageInfo>>() {

                    @Override
                    public void onNext(List<PackageInfo> packageInfoList) {
                        onPackageInfoListLoaded(packageInfoList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "triggerLoadPackageInfoListInternal=>onError", e);
                        endAsyncTask("triggerLoadPackageInfoListInternal");
                    }

                    @Override
                    public void onComplete() {
                        endAsyncTask("triggerLoadPackageInfoListInternal");
                    }
                })
        );
    }

    private void setupFabMenu() {
        onCreateFabOptionsMenu();
        mFabMenu.findViewById(R.id.fab_menu_edit).setOnClickListener(this);
        mFabMenu.findViewById(R.id.fab_menu_refresh).setOnClickListener(this);
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
        mRecyclerViewAdapter = new PackageInfoAdapter(new ArrayList<PackageInfo>(0), getActivity().getPackageManager());
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

    }

    private void onPackageItemClick(int position) {
        PackageInfo packageInfo = mRecyclerViewAdapter.getItemData(position);
        if (!packageInfo.applicationInfo.enabled) {
            if (Shell.SU.available()) {
                Shell.run("su", new String[] {"pm enable " + packageInfo.packageName}, null, true);
            }
            //noinspection ConstantConditions
            Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(packageInfo.packageName);
            if (launchIntent != null) {
                startActivity(launchIntent);
                return;
            }
        }
        // https://stackoverflow.com/questions/31127116/open-app-permission-settings
        Intent detailIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + packageInfo.packageName));
        detailIntent.addCategory(Intent.CATEGORY_DEFAULT);
        detailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(detailIntent);

    }

    private void onPackageInfoListLoaded(List<PackageInfo> packageInfoList) {
        mRecyclerViewAdapter.resetDataSet(packageInfoList);
    }

    private boolean actionModeIsPresent() {
        Activity activity = getActivity();
        if (!(activity instanceof ActionModeSupport)) {
            return false;
        }
        ActionModeSupport actionModeSupport = (ActionModeSupport) activity;
        return actionModeSupport.getSupportActionMode() != null;
    }

    private static class RefreshEvent {
    }

}
