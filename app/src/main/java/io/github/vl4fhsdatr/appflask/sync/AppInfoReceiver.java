package io.github.vl4fhsdatr.appflask.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AppInfoReceiver extends BroadcastReceiver {
    private static final String TAG = "AppInfoReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
            handlePackageChanged(context, intent);
        } else if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            handlePackageAdded(context, intent);
        } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
            handlePackageRemoved(context, intent);
        }

    }

    private void handlePackageChanged(Context context, Intent intent) {

        int packageUid = intent.getIntExtra(Intent.EXTRA_UID, -1);
        if (packageUid == -1) {
            Log.w(TAG, "bad package uid");
            return;
        }

        String[] changedComponentNames = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_COMPONENT_NAME_LIST);
        PackageManager packageManager = context.getPackageManager();
        String[] packageNames = packageManager.getPackagesForUid(packageUid);
        if (packageNames != null && packageNames.length >= 1) {
            if (changedComponentNames != null
                    && changedComponentNames.length >= 1
                    && !changedComponentNames[0].equals(packageNames[0])) {
                return;
            }
            List<String> appList = new ArrayList<>();
            appList.add(packageNames[0]);
            AppInfoService.startActionSetEnabled(context, appList);
        }

    }

    private void handlePackageAdded(Context context, Intent intent) {

        int packageUid = intent.getIntExtra(Intent.EXTRA_UID, -1);
        if (packageUid == -1) {
            Log.w(TAG, "bad package uid");
            return;
        }

        PackageManager packageManager = context.getPackageManager();
        String[] packageNames = packageManager.getPackagesForUid(packageUid);
        if (packageNames != null && packageNames.length >= 1) {
                List<String> appList = new ArrayList<>();
                appList.add(packageNames[0]);
                AppInfoService.startActionAddApp(context, appList);
        }

    }

    private void handlePackageRemoved(Context context, Intent intent) {

        int packageUid = intent.getIntExtra(Intent.EXTRA_UID, -1);
        if (packageUid == -1) {
            Log.w(TAG, "bad package uid");
            return;
        }

        List<Integer> appList = new ArrayList<>();
        appList.add(packageUid);
        AppInfoService.startActionRemoveApp(context, appList);

    }

}
