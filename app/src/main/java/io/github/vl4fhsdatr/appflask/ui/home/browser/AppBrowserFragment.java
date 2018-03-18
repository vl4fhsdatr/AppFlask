package io.github.vl4fhsdatr.appflask.ui.home.browser;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.github.vl4fhsdatr.appflask.database.AppDatabase;
import io.github.vl4fhsdatr.appflask.database.appinfo.AppInfo;
import io.github.vl4fhsdatr.appflask.R;
import io.github.vl4fhsdatr.appflask.ui.DatabaseSupport;
import io.github.vl4fhsdatr.appflask.ui.home.applist.AbstractAppListFragment;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
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
    protected void onCreateFabOptionsMenu() {
    }

    @Override
    protected void onFabOptionsItemSelected(int id) {
    }

    @Override
    protected Observable<List<PackageInfo>> onCreatePackageInfoListObservable() {
        List<PackageInfo> mutablePackageInfoList = getActivity().getPackageManager().getInstalledPackages(0);
        if (!shouldShowSystemApp()) {
            List<PackageInfo> newResult = new ArrayList<>();
            for (PackageInfo packageInfo : mutablePackageInfoList) {
                // https://stackoverflow.com/questions/8784505/how-do-i-check-if-an-app-is-a-non-system-app-in-android#8784719
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    newResult.add(packageInfo);
                }
            }
            mutablePackageInfoList = newResult;
        }
        final List<PackageInfo> packageInfoList = mutablePackageInfoList;
        return Observable.defer(new Callable<ObservableSource<? extends List<PackageInfo>>>() {
            @Override
            public ObservableSource<? extends List<PackageInfo>> call() throws Exception {
                return Observable.just(packageInfoList);
            }
        });
    }

    @Override
    protected void onCreateContextualOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.app_browser, menu);
    }

    private Observable<Void> createPutSelectionsIntoObservable() {
        final AppDatabase database = ((DatabaseSupport)getActivity()).getDatabase();
        List<String> packageList = getCheckedApplications();
        final AppInfo[] packages = new AppInfo[packageList.size()];
        for ( int i = 0; i < packageList.size(); i++ ) {
            packages[i] = new AppInfo();
            packages[i].setAppName(packageList.get(i));
        }
        return Observable.defer(new Callable<ObservableSource<? extends Void>>() {
            @Override
            public ObservableSource<? extends Void> call() throws Exception {
                database.getAppInfoDao().insert(packages);
                return Observable.empty();
            }
        });
    }


    @Override
    protected boolean onContextualOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_freeze:
                triggerPutSelectionsIntoFlask();
                return true;
        }
        return false;
    }

    private void triggerPutSelectionsIntoFlask() {
        beginAsyncTask("triggerPutSelectionsIntoFlask");
        mDisposables.add(createPutSelectionsIntoObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Void>() {
                    @Override
                    public void onNext(Void aVoid) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "triggerPutSelectionsIntoFlask=>onError", e);
                        endAsyncTask("triggerPutSelectionsIntoFlask");
                    }

                    @Override
                    public void onComplete() {
                        triggerLoadPackageInfoList();
                        endAsyncTask("triggerPutSelectionsIntoFlask");
                    }
                })
        );
    }

}

