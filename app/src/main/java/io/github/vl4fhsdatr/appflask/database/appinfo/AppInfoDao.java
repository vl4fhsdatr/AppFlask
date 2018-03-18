package io.github.vl4fhsdatr.appflask.database.appinfo;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface AppInfoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(AppInfo... apps);

    @Query("SELECT * FROM app_info")
    List<AppInfo> list();

    @Query("DELETE FROM app_info WHERE app_name = :app_name")
    void deleteIfAppNameEquals(String app_name);

    @Query("DELETE FROM app_info")
    void deleteAll();

}
