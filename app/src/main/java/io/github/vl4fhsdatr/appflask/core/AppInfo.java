package io.github.vl4fhsdatr.appflask.core;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(
        tableName = "app_info",
        indices = {
                @Index(unique = true, value = "name") // add unique constraint
        }
)
public class AppInfo {

    @PrimaryKey(autoGenerate = true)  @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "enabled")
    private boolean enabled;

    @ColumnInfo(name = "in_flask")
    private boolean inFlask;

    @ColumnInfo(name = "in_processing")
    private boolean inProcessing;

    @ColumnInfo(name = "uid")
    private int uid;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isInFlask() {
        return inFlask;
    }

    public void setInFlask(boolean inFlask) {
        this.inFlask = inFlask;
    }

    public boolean isInProcessing() {
        return inProcessing;
    }

    public void setInProcessing(boolean inProcessing) {
        this.inProcessing = inProcessing;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

}
