package io.github.vl4fhsdatr.appflask.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import java.util.List;

import io.github.vl4fhsdatr.appflask.core.AppInfo;

@Dao
public interface AppInfoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(AppInfo... apps);

    @Query("SELECT * FROM app_info")
    List<AppInfo> list();

    @Query("SELECT * FROM app_info")
    @Transaction
    LiveData<List<AppInfo>> listAllApps();

    @Query("DELETE FROM app_info WHERE uid = :uid")
    void deleteIfUidEquals(int uid);

    @Query("DELETE FROM app_info")
    void deleteAll();

    @Query("UPDATE app_info SET enabled = :enabled WHERE name = :name")
    void setEnabled(String name, boolean enabled);

    @Query("UPDATE app_info SET in_flask = :in_flask WHERE name = :name")
    void setInFlask(String name, boolean in_flask);

    @Query("UPDATE app_info SET in_processing = :in_processing WHERE name = :name")
    void setInProcessing(String name, boolean in_processing);

}
