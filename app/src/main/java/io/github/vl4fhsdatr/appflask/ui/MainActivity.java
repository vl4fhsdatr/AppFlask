package io.github.vl4fhsdatr.appflask.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.lang.reflect.InvocationTargetException;

import io.github.vl4fhsdatr.appflask.R;
import io.github.vl4fhsdatr.appflask.ui.readme.ReadmeFragment;
import io.github.vl4fhsdatr.appflask.ui.home.HomeFragment;
import io.github.vl4fhsdatr.appflask.ui.setting.SettingFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        TabLayoutSupport, ActionModeSupport {

    @SuppressWarnings("unused")
    private static final String TAG = "MainActivity";

    private static final Class<?>[] FRAGMENT_CLASSES = {
            HomeFragment.class, SettingFragment.class, ReadmeFragment.class };
    private static final int[] FRAGMENT_TITLE_RESOURCES = {
            R.string.title_fragment_home, R.string.title_fragment_settings, R.string.title_fragment_about };
    private static final int[] FRAGMENT_MENU_IDS = {
            R.id.nav_home, R.id.nav_settings, R.id.nav_about };

    /**
     * current drawer fragment index
     */
    private int mCurrentDrawerFragmentIndex;

    /**
     * global ActionMode reference
     */
    private ActionMode mActionMode;

    // cached views

    private DrawerLayout mDrawerLayout;
    private AppBarLayout mAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mAppBar = findViewById(R.id.app_bar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        try {
            Fragment targetFragment = (Fragment)FRAGMENT_CLASSES[0]
                    .getMethod("newInstance")
                    .invoke(null);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_main, targetFragment)
                    .commit();
            setTitle(FRAGMENT_TITLE_RESOURCES[0]);
            mCurrentDrawerFragmentIndex = 0;
        } catch ( InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        int targetDrawerFragmentIndex = 0;
        for ( int i = 0; i < FRAGMENT_MENU_IDS.length; i++) {
            if (FRAGMENT_MENU_IDS[i] == id) {
                targetDrawerFragmentIndex = i;
                break;
            }
        }

        if (targetDrawerFragmentIndex != mCurrentDrawerFragmentIndex) {
            try {
                Fragment targetFragment = (Fragment) FRAGMENT_CLASSES[targetDrawerFragmentIndex]
                        .getMethod("newInstance")
                        .invoke(null);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_main, targetFragment)
                        .commit();
                setTitle(FRAGMENT_TITLE_RESOURCES[targetDrawerFragmentIndex]);
                mCurrentDrawerFragmentIndex = targetDrawerFragmentIndex;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void enterTabLayout() {
        // View#setElevation is available until api level 21
        ViewCompat.setElevation(mAppBar, 0);
    }

    @Override
    public void exitTabLayout() {
        float appBarElevation = getResources().getDimension(R.dimen.app_bar_elevation);
        ViewCompat.setElevation(mAppBar, appBarElevation);
    }

    @Override
    public void onSupportActionModeStarted(@NonNull ActionMode mode) {
        super.onSupportActionModeStarted(mode);
        mActionMode = mode;
    }

    @Override
    public void onSupportActionModeFinished(@NonNull ActionMode mode) {
        super.onSupportActionModeFinished(mode);
        mActionMode = null;
    }

    @Override
    public ActionMode getSupportActionMode() {
        return mActionMode;
    }


}
