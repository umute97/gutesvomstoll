package com.reich.gutesvomstoll;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.reich.gutesvomstoll.ui.frag.FavsFragment;
import com.reich.gutesvomstoll.ui.frag.SoundsFragment;
import com.reich.gutesvomstoll.util.SoundDBHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "com.reich.gutesvomstoll";

    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private TabLayout mTabs;
    private SoundsFragment mSoundsFragment;
    private FavsFragment mFavsFragment;
    private SoundDBHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize DBHelper that handles all the DB communication
        mDBHelper = new SoundDBHelper(this);

        //Check if this is an update to the database and repopulate if necessary
        if(appUpdate())  {

            mDBHelper.populateDB();
        }

        // Viewpager that hosts and switches fragments
        mViewPager = findViewById(R.id.view_pager);
        setupViewPager(mViewPager);

        // Toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Tabs that hold the fragments
        TabLayout.OnTabSelectedListener sync = new SyncFavesListener();
        mTabs = findViewById(R.id.tabs);
        mTabs.setupWithViewPager(mViewPager);
        mTabs.addOnTabSelectedListener(sync);
    }

    /*  Adds and initializes all necessary fragments and adds them to the viewpager
        TODO: Remove hardcoded tab headers.
     */
    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        mSoundsFragment = new SoundsFragment(mDBHelper);
        mFavsFragment = new FavsFragment(mDBHelper);

        adapter.addFragment(mSoundsFragment, getString(R.string.sound_frag_tabtitle));
        adapter.addFragment(mFavsFragment, getString(R.string.favs_frag_tabtitle));
        viewPager.setAdapter(adapter);
    }

    // Inner class that handles fragment-viewpager interaction
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Two actions supported: Search a sound and stop a currently playing sound
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {

            // Get the SearchView that's activated, when the search icon is selected
            SearchView searchView = (SearchView) item.getActionView();

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    mSoundsFragment.getmAdapter().getFilter().filter(newText);
                    mFavsFragment.getmAdapter().getFilter().filter(newText);
                    return true;
                }
            });
        }

        else if (id == R.id.action_stop_mp)  {

            try {

                if (mSoundsFragment.mMP != null && mSoundsFragment.mMP.isPlaying()) {

                    mSoundsFragment.mMP.stop();
                    mSoundsFragment.mMP.release();
                }
            } catch (java.lang.IllegalStateException ex)  {

                Log.i(TAG, "Stop button pressed with no instance of MediaPlayer.");
            }

            try {

                if (mFavsFragment.mMP != null && mFavsFragment.mMP.isPlaying()) {

                    mFavsFragment.mMP.stop();
                    mFavsFragment.mMP.release();
                }
            } catch (java.lang.IllegalStateException ex)  {

                Log.i(TAG, "Stop button pressed with no instance of MediaPlayer.");
            }

        }

        return super.onOptionsItemSelected(item);
    }

    /*  Actual (ugly) code that handles updates to the database. Works by manually incrementing the
        version code with each addition/change of sounds in the db.
     */
    private boolean appUpdate()  {

        // Define a name for the preference file and a key name to save the version code to
        final String PREFS_NAME = "version";
        final String PREF_VERSION_CODE_KEY = "version_code";

        // Define a value that is set if the key does not exist
        final int DOESNT_EXIST = -1;

        // Get the current version code from the package
        int currentVersionCode = 0;

        try{

            currentVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;

        } catch (PackageManager.NameNotFoundException e)  {

            Log.e(TAG, e.getMessage());
        }

        // Get the SharedPreferences from the preference file
        // Creates the preference file if it does not exist
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Get the saved version code or set it if it does not exist
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Create an editor to edit the shared preferences on app update
        SharedPreferences.Editor edit = prefs.edit();

        //Check for updates
        if (savedVersionCode == DOESNT_EXIST)  {

            mDBHelper.appUpdate();

            // First run of the app
            // Set the saved version code to the current version code
            edit.putInt(PREF_VERSION_CODE_KEY, currentVersionCode);
            edit.commit();
            return true;
        }
        else if (currentVersionCode > savedVersionCode)  {

            // App update
            mDBHelper.appUpdate();
            edit.putInt(PREF_VERSION_CODE_KEY, currentVersionCode);
            edit.commit();
            return true;
        }

        return false;
    }

    private class SyncFavesListener implements TabLayout.OnTabSelectedListener  {

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

            int pastPos = tab.getPosition();

            Log.i(TAG, "Unselected pos " + pastPos);
            switch (pastPos)  {

                case 0:
                    mFavsFragment.updateView(mDBHelper.getFavesFromDB());
                    break;

                case 1:
                    mSoundsFragment.updateView(mDBHelper.getSoundsFromDB());
                    break;
            }
        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {}
        public void onTabReselected(TabLayout.Tab tab) {}

    }

}