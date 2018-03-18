package io.github.vl4fhsdatr.appflask.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

import io.github.vl4fhsdatr.appflask.database.appinfo.AppInfoDao;
import io.github.vl4fhsdatr.appflask.database.appinfo.AppInfo;

@Database(entities =  {AppInfo.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DB_NAME = "app_db";

    public abstract AppInfoDao getAppInfoDao();

    // https://androidstudy.com/2017/11/22/android-architecture-components-room%e2%80%8amigration/
    public static final Migration FROM_1_TO_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE IF EXISTS `package`");
            database.execSQL("DROP TABLE IF EXISTS `app_info`");
            database.execSQL("CREATE TABLE IF NOT EXISTS `app_info` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `app_name` TEXT)");
            database.execSQL("CREATE UNIQUE INDEX `index_app_info_app_name` ON `app_info` (`app_name`)");
        }
    };

}
