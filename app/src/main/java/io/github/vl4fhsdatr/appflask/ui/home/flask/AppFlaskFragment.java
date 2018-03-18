package io.github.vl4fhsdatr.appflask.ui.home.flask;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import eu.chainfire.libsuperuser.Shell;
import io.github.vl4fhsdatr.appflask.database.appinfo.AppInfo;
import io.github.vl4fhsdatr.appflask.database.appinfo.AppInfoDao;
import io.github.vl4fhsdatr.appflask.R;
import io.github.vl4fhsdatr.appflask.ui.DatabaseSupport;
import io.github.vl4fhsdatr.appflask.ui.home.applist.AbstractAppListFragment;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class AppFlaskFragment extends AbstractAppListFragment {

    @SuppressWarnings("unused")
    private static final String TAG = "AppFlaskFragment";

    // https://stackoverflow.com/questions/1714297/android-view-setidint-id-programmatically-how-to-avoid-id-conflicts#15442898
    private static final int ID_FAB_DISABLE = ViewCompat.generateViewId();

    public AppFlaskFragment() {
    }

    @SuppressWarnings("unused")
    public static AppFlaskFragment newInstance() {
        return new AppFlaskFragment();
    }

    @Override
    protected void onCreateFabOptionsMenu() {
        addFabOptionsMenuItem(ID_FAB_DISABLE, R.string.label_fab_disable, R.drawable.ic_menu_disable);
    }

    @Override
    protected void onFabOptionsItemSelected(int id) {
        if (id == ID_FAB_DISABLE) {
            triggerDisableFlaskApps();
        }
    }

    @Override
    protected Observable<List<PackageInfo>> onCreatePackageInfoListObservable() {
        final AppInfoDao database = ((DatabaseSupport)getActivity()).getDatabase().getAppInfoDao();
        final PackageManager packageManager = getActivity().getPackageManager();
        return Observable.defer(new Callable<ObservableSource<? extends List<PackageInfo>>>() {
            @Override
            public ObservableSource<? extends List<PackageInfo>> call() throws Exception {
                List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
                List<AppInfo> packages = database.list();
                List<PackageInfo> result = new ArrayList<>();
                for (PackageInfo pi: packageInfoList) {
                    for (AppInfo p: packages) {
                        if (p.getAppName().equals(pi.packageName)) {
                            result.add(pi);
                            break;
                        }
                    }
                }
                return Observable.just(result);
            }
        });
    }

    @Override
    protected void onCreateContextualOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.app_flask, menu);
    }

    @Override
    protected boolean onContextualOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                triggerRemoveSelectionsFromFlask();
                return true;
        }
        return false;
    }

    private Observable<Void> createRemoveSelectionsFromFlaskObservable() {
        final AppInfoDao database = ((DatabaseSupport)getActivity()).getDatabase().getAppInfoDao();
        final List<String> packageList = getCheckedApplications();
        return Observable.defer(new Callable<ObservableSource<? extends Void>>() {
            @Override
            public ObservableSource<? extends Void> call() throws Exception {
                for (String p: packageList) {
                    database.deleteIfAppNameEquals(p);
                }

                return Observable.empty();
            }
        });
    }

    private Observable<Void> createDisableFlaskAppsObservable() {
        final List<String> packages = getAllApplications();
        return Observable.defer(new Callable<ObservableSource<? extends Void>>() {
            @Override
            public ObservableSource<? extends Void> call() throws Exception {
                String[] commandLines = new String[1];
                for (String p: packages) {
                    if (Shell.SU.available()) {
                        commandLines[0] = "pm disable " + p;
                        Shell.run("su", commandLines, null, true);
                    }
                }
                return Observable.empty();
            }
        });
    }

    private void triggerDisableFlaskApps() {
        beginAsyncTask("triggerDisableFlaskApps");
        mDisposables.add(createDisableFlaskAppsObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Void>() {
                    @Override
                    public void onNext(Void aVoid) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "triggerDisableFlaskApps=>onError", e);
                        endAsyncTask("triggerDisableFlaskApps");
                    }

                    @Override
                    public void onComplete() {
                        triggerLoadPackageInfoList();
                        endAsyncTask("triggerDisableFlaskApps");
                    }
                })
        );
    }

    private void triggerRemoveSelectionsFromFlask() {
        beginAsyncTask("triggerRemoveSelectionsFromFlask");
        mDisposables.add(createRemoveSelectionsFromFlaskObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Void>() {
                    @Override
                    public void onNext(Void aVoid) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "triggerRemoveSelectionsFromFlask=>onError", e);
                        endAsyncTask("triggerRemoveSelectionsFromFlask");
                    }

                    @Override
                    public void onComplete() {
                        triggerLoadPackageInfoList();
                        endAsyncTask("triggerRemoveSelectionsFromFlask");
                    }
                })
        );
    }

}
