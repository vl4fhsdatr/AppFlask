package io.github.vl4fhsdatr.appflask;

import android.app.Application;

import io.github.vl4fhsdatr.appflask.sync.AppInfoService;

public class AppFlask extends Application {

    private PersistenceComponent mPersistenceComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mPersistenceComponent = DaggerPersistenceComponent.builder()
                .appFlaskModule(new AppFlaskModule(this))
                .persistenceModule(new PersistenceModule())
                .build();

        AppInfoService.startActionInit(this);

    }

    public PersistenceComponent persistenceComponent() {
        return mPersistenceComponent;
    }

}
