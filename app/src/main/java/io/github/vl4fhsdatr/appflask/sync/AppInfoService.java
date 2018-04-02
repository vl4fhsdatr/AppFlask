package io.github.vl4fhsdatr.appflask.sync;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.vl4fhsdatr.appflask.AppFlask;
import io.github.vl4fhsdatr.appflask.persistence.AppDatabase;
import io.github.vl4fhsdatr.appflask.core.AppInfo;

public class AppInfoService extends IntentService {
    private static final String TAG = "AppInfoService";

    private static final String ACTION_SET_ENABLED = "io.github.vl4fhsdatr.appflask.action.SET_ENABLED";
    private static final String ACTION_ADD_APP = "io.github.vl4fhsdatr.appflask.action.ADD_APP";
    private static final String ACTION_REMOVE_APP = "io.github.vl4fhsdatr.appflask.action.REMOVE_APP";
    private static final String ACTION_INITIALIZE = "io.github.vl4fhsdatr.appflask.action.INITIALIZE";

    private static final String EXTRA_APP_LIST = "io.github.vl4fhsdatr.appflask.extra.APP_LIST";

    public AppInfoService() {
        super("AppInfoService");
    }

    @Inject
    AppDatabase mDatabase;

    @Override
    public void onCreate() {
        super.onCreate();

        ((AppFlask)getApplication()).persistenceComponent().inject(this);

    }

    public static void startActionSetEnabled(Context context, List<String> appList) {
        Intent intent = new Intent(context, AppInfoService.class);
        intent.setAction(ACTION_SET_ENABLED);
        ArrayList<String> stringArrayList = new ArrayList<>(appList);
        intent.putStringArrayListExtra(EXTRA_APP_LIST, stringArrayList);
        context.startService(intent);
    }

    public static void startActionAddApp(Context context, List<String> appList) {
        Intent intent = new Intent(context, AppInfoService.class);
        intent.setAction(ACTION_ADD_APP);
        ArrayList<String> stringArrayList = new ArrayList<>(appList);
        intent.putStringArrayListExtra(EXTRA_APP_LIST, stringArrayList);
        context.startService(intent);
    }

    public static void startActionRemoveApp(Context context, List<Integer> appList) {
        Intent intent = new Intent(context, AppInfoService.class);
        intent.setAction(ACTION_REMOVE_APP);
        ArrayList<Integer> integerArrayList = new ArrayList<>(appList);
        intent.putIntegerArrayListExtra(EXTRA_APP_LIST, integerArrayList);
        context.startService(intent);
    }

    public static void startActionInit(Context context) {
        Intent intent = new Intent(context, AppInfoService.class);
        intent.setAction(ACTION_INITIALIZE);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SET_ENABLED.equals(action)) {
                List<String> appList = intent.getStringArrayListExtra(EXTRA_APP_LIST);
                handleActionSetEnabled(appList);
            } else if (ACTION_ADD_APP.equals(action)) {
                List<String> appList = intent.getStringArrayListExtra(EXTRA_APP_LIST);
                handleActionAddApp(appList);
            } else if (ACTION_REMOVE_APP.equals(action)) {
                List<Integer> appList = intent.getIntegerArrayListExtra(EXTRA_APP_LIST);
                handleActionRemoveApp(appList);
            } else if (ACTION_INITIALIZE.equals(action)) {
                handleActionInit();
            }
        }
    }

    private void handleActionSetEnabled(List<String> appList) {
        PackageManager packageManager = getPackageManager();
            for (String p: appList) {
                try {
                    boolean enabled = packageManager.getApplicationInfo(p, 0).enabled;
                    mDatabase.getAppInfoDao().setEnabled(p, enabled);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, "error retrieve package info", e);
                }
            }


    }

    private void handleActionAddApp(List<String> appList) {
        AppInfo[] appInfos = new AppInfo[appList.size()];
        PackageManager packageManager = getPackageManager();
        for (int i = 0; i < appInfos.length; i++) {
            String p = appList.get(i);
            AppInfo appInfo = new AppInfo();
            appInfo.setName(p);
            appInfo.setInFlask(false);

            appInfo.setInProcessing(false);
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(p, 0);
                appInfo.setEnabled(applicationInfo.enabled);
                appInfo.setUid(applicationInfo.uid);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "error retrieve package info", e);
            }
            appInfos[i] = appInfo;
        }
        mDatabase.getAppInfoDao().insert(appInfos);
    }

    private void handleActionRemoveApp(List<Integer> appList) {
        for (int p: appList) {
            mDatabase.getAppInfoDao().deleteIfUidEquals(p);
        }
    }

    private void handleActionInit() {
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
        List<AppInfo> appInfoList = new ArrayList<>();
        List<AppInfo> oldAppInfoList = mDatabase.getAppInfoDao().list();
        mDatabase.getAppInfoDao().deleteAll();
        for (PackageInfo packageInfo: packageInfoList) {
            boolean isUpdated = false;
            for (AppInfo appInfo: oldAppInfoList) {
                if (appInfo.getName().equals(packageInfo.packageName)) {
                    appInfo.setEnabled(packageInfo.applicationInfo.enabled);
                    appInfo.setInProcessing(false);
                    appInfoList.add(appInfo);
                    isUpdated = true;
                    break;
                }
            }
            if (!isUpdated) {
                AppInfo appInfo = new AppInfo();
                appInfo.setName(packageInfo.packageName);
                appInfo.setEnabled(packageInfo.applicationInfo.enabled);
                appInfo.setInFlask(false);
                appInfo.setUid(packageInfo.applicationInfo.uid);
                appInfo.setInProcessing(false);
                appInfoList.add(appInfo);
            }

        }
        mDatabase.getAppInfoDao().insert(appInfoList.toArray(new AppInfo[0]));
    }

}
