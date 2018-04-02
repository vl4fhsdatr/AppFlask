package io.github.vl4fhsdatr.appflask.ui.home.flask;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppFlask)getActivity().getApplication()).persistenceComponent().inject(this);
    }

    @Inject
    AppDatabase mAppDatabase;


    @Override
    protected AppDatabase getDatabase() {
        return mAppDatabase;
    }

    @Override
    protected void onCreateFabOptionsMenu() {
        addFabOptionsMenuItem(ID_FAB_DISABLE, R.string.label_fab_disable, R.drawable.ic_menu_disable);
    }

    @Override
    protected void onFabOptionsItemSelected(int id) {
        if (id == ID_FAB_DISABLE) {
            doDisableFlaskApps();
        }
    }

    @Override
    protected boolean filterAppInfo(AppInfo info) {
        return info.isInFlask();
    }


    @Override
    protected void onCreateContextualOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.app_flask, menu);
    }

    @Override
    protected boolean onContextualOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                doRemoveSelectionsFromFlask();
                return true;
        }
        return false;
    }


    private void doDisableFlaskApps() {
        mDisposables.add(RxUtils.disableApp(getAllApplications(), getDatabase())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        );
    }

    private void doRemoveSelectionsFromFlask() {
        mDisposables.add(RxUtils.removeAppFromFlask(getCheckedApplications(), getDatabase())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        );
    }

}
