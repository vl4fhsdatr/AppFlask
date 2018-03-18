package io.github.vl4fhsdatr.appflask.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.InvocationTargetException;

import io.github.vl4fhsdatr.appflask.R;
import io.github.vl4fhsdatr.appflask.ui.ActionModeSupport;
import io.github.vl4fhsdatr.appflask.ui.TabLayoutSupport;
import io.github.vl4fhsdatr.appflask.ui.home.browser.AppBrowserFragment;
import io.github.vl4fhsdatr.appflask.ui.home.flask.AppFlaskFragment;

public class HomeFragment extends Fragment
        implements ViewPager.OnPageChangeListener {

    @SuppressWarnings("unused")
    private static final String TAG = "HomeFragment";

    private static final int[] FRAGMENT_TITLE_RESOURCES = {
            R.string.title_fragment_app_flask, R.string.title_fragment_app_browser
    };
    private static final Class<?>[] FRAGMENT_CLASSES = {
            AppFlaskFragment.class, AppBrowserFragment.class
    };

    public HomeFragment() {
    }

    @SuppressWarnings("unused")
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ViewPager pager = view.findViewById(R.id.pager_home);
        // while using nested fragment, you must use getChildFragmentManager
        // reference https://stackoverflow.com/questions/19073541/how-set-viewpager-inside-a-fragment
        pager.setAdapter(new HomePagerAdapter(getChildFragmentManager()));
        TabLayout tabs = view.findViewById(R.id.tabs_home);
        tabs.setupWithViewPager(pager);
        pager.addOnPageChangeListener(this);

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        if (activity instanceof TabLayoutSupport) {
            ((TabLayoutSupport)getActivity()).enterTabLayout();
        } else {
            Log.w(TAG, "using HomeFragment within activity which does not implement TabLayoutSupport.");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Activity activity = getActivity();
        if (activity instanceof TabLayoutSupport) {
            TabLayoutSupport tabLayoutSupport = (TabLayoutSupport)getActivity();
            tabLayoutSupport.exitTabLayout();

        } else {
            Log.w(TAG, "using HomeFragment within activity which does not implement TabLayoutSupport.");
        }
        if (activity instanceof ActionModeSupport) {
            ActionModeSupport actionModeSupport = (ActionModeSupport)activity;
            if (actionModeSupport.getSupportActionMode() != null) {
                actionModeSupport.getSupportActionMode().finish();
            }
        } else {
            Log.w(TAG, "using HomeFragment within activity which does not implement ActionModeSupport.");
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Activity activity = getActivity();
        if (activity instanceof ActionModeSupport) {
            ActionModeSupport actionModeSupport = (ActionModeSupport)activity;
            if (actionModeSupport.getSupportActionMode() != null) {
                actionModeSupport.getSupportActionMode().finish();
            }
        } else {
            Log.w(TAG, "using HomeFragment within activity which does not implement ActionModeSupport.");
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class HomePagerAdapter extends FragmentPagerAdapter {

        HomePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getString(FRAGMENT_TITLE_RESOURCES[position]);
        }

        @Override
        public int getCount() {
            return FRAGMENT_CLASSES.length;
        }

        @Override
        public Fragment getItem(int position) {
            try {
                return (Fragment)FRAGMENT_CLASSES[position].getMethod("newInstance").invoke(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("error invoke newInstance");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("error invoke newInstance");
            } catch (InvocationTargetException e) {
                throw new RuntimeException("error invoke newInstance");
            }
        }

    }

}
