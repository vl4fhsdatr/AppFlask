package io.github.vl4fhsdatr.appflask;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppFlaskModule {

    private Application mApplication;

    public AppFlaskModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return mApplication;
    }

}
