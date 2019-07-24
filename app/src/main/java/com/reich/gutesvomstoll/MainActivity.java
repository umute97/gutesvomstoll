package com.reich.gutesvomstoll;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.google.android.material.tabs.TabLayout;
import com.reich.gutesvomstoll.ui.main.FavsFragment;
import com.reich.gutesvomstoll.ui.main.SoundsFragment;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private ArrayAdapter mAdapter;
    private Toolbar mToolbar;
    private TabLayout mTabs;
    private String[] mSoundArray; //TODO: Get list of sounds from directory. Add sounds.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new ArrayAdapter(MainActivity.this,
                android.R.layout.simple_list_item_1, mSoundArray);

        // Viewpager
        mViewPager = findViewById(R.id.view_pager);
        setupViewPager(mViewPager);

        // Toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Tabs
        mTabs = findViewById(R.id.tabs);
        mTabs.setupWithViewPager(mViewPager);


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SoundsFragment(), getString(R.string.sound_frag_tabtitle));
        adapter.addFragment(new FavsFragment(), getString(R.string.favs_frag_tabtitle));
        viewPager.setAdapter(adapter);
    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {

            // Get the searchview that's activated, when the search icon is selected
            SearchView searchView = (SearchView) item.getActionView();

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    mAdapter.getFilter().filter(newText);
                    return true;
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

}