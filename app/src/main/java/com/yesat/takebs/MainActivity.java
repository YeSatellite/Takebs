package com.yesat.takebs;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.yesat.yesat.takebs.Fragment.AddRouteFragment;
import com.yesat.yesat.takebs.Fragment.ChatFragment;
import com.yesat.yesat.takebs.Fragment.FavouritesFragment;
import com.yesat.yesat.takebs.Fragment.ProfileFragment;
import com.yesat.yesat.takebs.Fragment.RoutesFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "yernar";
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapeter;
    private BottomNavigationView bottom;
    private MenuItem prevMenuItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        bottom = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        disableShiftMode(bottom);
        setupViewPager(viewPager);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottom.getMenu().getItem(0).setChecked(false);
                }

                bottom.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottom.getMenu().getItem(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RoutesFragment(), R.id.bnv_i1);
        adapter.addFragment(new FavouritesFragment(), R.id.bnv_i2);
        adapter.addFragment(new AddRouteFragment(), R.id.bnv_i3);
        adapter.addFragment(new ChatFragment(), R.id.bnv_i4);
        adapter.addFragment(new ProfileFragment(), R.id.bnv_i5);
        viewPager.setAdapter(adapter);
        bottom.setOnNavigationItemSelectedListener(adapter);
    }
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Unable to change value of shift mode", e);
        }
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter implements BottomNavigationView.OnNavigationItemSelectedListener {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final HashMap<Integer,Integer> mFragmentHash = new HashMap<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, int res) {
            mFragmentList.add(fragment);
            mFragmentHash.put(res,mFragmentHash.size());
        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            viewPager.setCurrentItem(mFragmentHash.get(item.getItemId()));
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        super.onBackPressed();
    }
    View getContentView(){
        return findViewById(R.id.root);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getContentView().getViewTreeObserver()
                .addOnGlobalLayoutListener(mLayoutKeyboardVisibilityListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getContentView().getViewTreeObserver()
                .removeOnGlobalLayoutListener(mLayoutKeyboardVisibilityListener);
    }

    private boolean mKeyboardVisible = true;

    private final ViewTreeObserver.OnGlobalLayoutListener mLayoutKeyboardVisibilityListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            final Rect rectangle = new Rect();
            final View contentView = getContentView();
            contentView.getWindowVisibleDisplayFrame(rectangle);
            int screenHeight = contentView.getRootView().getHeight();

            // r.bottom is the position above soft keypad or device button.
            // If keypad is shown, the rectangle.bottom is smaller than that before.
            int keypadHeight = screenHeight - rectangle.bottom;
            // 0.15 ratio is perhaps enough to determine keypad height.
            boolean isKeyboardNowVisible = keypadHeight > screenHeight * 0.15;

            if (mKeyboardVisible != isKeyboardNowVisible) {
                if (isKeyboardNowVisible) {
                    onKeyboardShown();
                } else {
                    onKeyboardHidden();
                }
            }

            mKeyboardVisible = isKeyboardNowVisible;
        }
    };

    private void onKeyboardShown() {
        bottom.setVisibility(View.GONE);
    }

    private void onKeyboardHidden() {
        bottom.setVisibility(View.VISIBLE);
    }


}
