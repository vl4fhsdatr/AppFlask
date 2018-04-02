package io.github.vl4fhsdatr.appflask.ui.home.browser;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import io.github.vl4fhsdatr.appflask.AppFlask;
import io.github.vl4fhsdatr.appflask.persistence.AppDatabase;
import io.github.vl4fhsdatr.appflask.core.AppInfo;
import io.github.vl4fhsdatr.appflask.R;
import io.github.vl4fhsdatr.appflask.ui.home.applist.AbstractAppListFragment;
import io.github.vl4fhsdatr.appflask.util.RxUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AppBrowserFragment extends AbstractAppListFragment {

    @SuppressWarnings("unused")
    private static final String TAG = "AppBrowserFragment";

    public AppBrowserFragment() {
    }

    @SuppressWarnings("unused")
    public static AppBrowserFragment newInstance() {
        return new AppBrowserFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppFlask)getActivity().getApplication()).persistenceComponent().inject(this);
    }

    @Override
    protected void onCreateFabOptionsMenu() {
    }

    @Override
    protected void onFabOptionsItemSelected(int id) {
    }

    @Inject
     AppDatabase mAppDatabase;


    @Override
    protected AppDatabase getDatabase() {
        return mAppDatabase;
    }

    @Override
    protected boolean filterAppInfo(AppInfo info) {
        try {
            return shouldShowSystemApp() || (getActivity().getPackageManager().getApplicationInfo(info.getName(), 0).flags & ApplicationInfo.FLAG_SYSTEM) == 0;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    protected void onCreateContextualOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.app_browser, menu);
    }


    @Override
    protected boolean onContextualOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_freeze:
                doPutSelectionsIntoFlask();
                return true;
        }
        return false;
    }

    private void doPutSelectionsIntoFlask() {
        mDisposables.add(RxUtils.addAppToFlask(getCheckedApplications(), getDatabase())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        );
    }

}

