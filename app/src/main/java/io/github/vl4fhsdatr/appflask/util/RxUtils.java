package io.github.vl4fhsdatr.appflask.util;

import java.util.List;
import java.util.concurrent.Callable;

import eu.chainfire.libsuperuser.Shell;
import io.github.vl4fhsdatr.appflask.persistence.AppDatabase;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;

public class RxUtils {

    public static Observable<Void> removeAppFromFlask(final List<String> appNameList, final AppDatabase database) {
        return Observable.defer(new Callable<ObservableSource<? extends Void>>() {
            @Override
            public ObservableSource<? extends Void> call() throws Exception {
                for (String p: appNameList) {
                    database.getAppInfoDao().setInFlask(p, false);
                }
                return Observable.empty();
            }
        });
    }

    public static Observable<Void> addAppToFlask(final List<String> appNameList, final AppDatabase database) {
        return Observable.defer(new Callable<ObservableSource<? extends Void>>() {
            @Override
            public ObservableSource<? extends Void> call() throws Exception {
                for (String p: appNameList) {
                    database.getAppInfoDao().setInFlask(p, true);
                }
                return Observable.empty();
            }
        });
    }

    public static Observable<Void> disableApp(final List<String> appNameList, final AppDatabase database) {
        return Observable.defer(new Callable<ObservableSource<? extends Void>>() {
            @Override
            public ObservableSource<? extends Void> call() throws Exception {
                String[] commandLines = new String[1];
                for (String p: appNameList) {
                    if (Shell.SU.available()) {
                        database.getAppInfoDao().setInProcessing(p, true);

                        commandLines[0] = "pm disable " + p;
                        Shell.run("su", commandLines, null, true);

                        database.getAppInfoDao().setInProcessing(p, false);
                    }
                }
                return Observable.empty();
            }
        });
    }

    public static Observable<Void> enableApp(final String packageName, final AppDatabase database) {
        return Observable.defer(new Callable<ObservableSource<? extends Void>>() {
            @Override
            public ObservableSource<? extends Void> call() throws Exception {
                database.getAppInfoDao().setInProcessing(packageName, true);
                if (Shell.SU.available()) {
                    Shell.run("su", new String[] {"pm enable " + packageName}, null, true);
                }
                database.getAppInfoDao().setInProcessing(packageName, false);
                return Observable.empty();
            }
        });
    }

}
