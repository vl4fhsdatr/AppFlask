package io.github.vl4fhsdatr.appflask;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.vl4fhsdatr.appflask.persistence.AppDatabase;
import io.github.vl4fhsdatr.appflask.core.AppInfo;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class RoomTest {

    @Before
    public void roomDaoTestSetup() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDatabase = Room.databaseBuilder(context, AppDatabase.class, TEST_DB_NAME)
                .addMigrations(AppDatabase.FROM_1_TO_2)
                .build();
        mDatabase.getAppInfoDao().deleteAll();
    }

    @After
    public void roomDaoTestTearDown() {
        mDatabase.close();
    }

    private AppDatabase mDatabase;

    private static final String TEST_DB_NAME = "app_db_test";

    @Test
    public void roomDaoTest() {

        final String testAppName = "app_name_test";

        AppInfo appInfo1 = new AppInfo();
        appInfo1.setName(testAppName);
        mDatabase.getAppInfoDao().insert(appInfo1);
        AppInfo appInfo2 = new AppInfo();
        appInfo2.setName(testAppName);
        mDatabase.getAppInfoDao().insert(appInfo2);
        assertEquals(1, mDatabase.getAppInfoDao().list().size());

    }

}
