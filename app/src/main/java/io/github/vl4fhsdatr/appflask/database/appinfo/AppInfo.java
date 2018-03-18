package io.github.vl4fhsdatr.appflask.database.appinfo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(
        tableName = "app_info",
        indices = {
                @Index(unique = true, value = "app_name") // add unique constraint
        }
)
public class AppInfo {

    @PrimaryKey(autoGenerate = true)  @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "app_name")
    private String appName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

}
