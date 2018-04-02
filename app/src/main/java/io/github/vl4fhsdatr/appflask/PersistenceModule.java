package io.github.vl4fhsdatr.appflask;

import android.app.Application;
import android.arch.persistence.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.vl4fhsdatr.appflask.persistence.AppDatabase;

@Module
public class PersistenceModule {

    @Provides
    @Singleton
    public AppDatabase providesDatabase(Application application) {
        return Room.databaseBuilder(application, AppDatabase.class, AppDatabase.DB_NAME)
                .addMigrations(AppDatabase.FROM_1_TO_2, AppDatabase.FROM_2_TO_3, AppDatabase.FROM_3_TO_4, AppDatabase.FROM_4_TO_5)
                .build();
    }

}
