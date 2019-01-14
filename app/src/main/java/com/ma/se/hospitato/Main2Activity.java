package com.ma.se.hospitato;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class Main2Activity extends AppCompatActivity {

    MainList mainList;
    filterView filterView;
    FloatingActionButton fab;
    NestedScrollView scrollView;
    boolean skipped = false;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle b = getIntent().getExtras();
        try {
            if (b.getString("firstAction").equals("skipped"))
                skipped = true;
        }catch (Exception e) {
            e.printStackTrace();
            skipped = false;
       }
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewPagercontainer);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        fab = (FloatingActionButton) findViewById(R.id.mapButton);
        //fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this, MapView.class);
                intent.putExtra("FromMain", 2); //2 - For obtaining hospital positions
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int i = item.getItemId();
        if (i== R.id.profileButton){
            Log.d("Profile Button", "Clicked");
            Intent toProfile = new Intent(Main2Activity.this, MedicalView.class);
            startActivity(toProfile);
        }
        if(i==R.id.registerButton){
            Intent toSignUp = new Intent(Main2Activity.this, SignUpView.class);
            startActivity(toSignUp);
        }
        if(i==R.id.loginButton){
            startActivity(new Intent(Main2Activity.this, LogInView.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0:
                    mainList = new MainList();
                    //fab.setVisibility(View.VISIBLE);
                    return mainList;
                case 1:
                    filterView = new filterView();
                    //fab.setVisibility(View.INVISIBLE);
                    return  filterView;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "MainList";
                case 1:
                    return "FilterView";
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
       System.out.println("Stack: " + getSupportFragmentManager().getBackStackEntryCount());
       if(getSupportFragmentManager().getBackStackEntryCount() > 0 && (!mainList.isAdded() || !filterView.isAdded()))
           getSupportFragmentManager().popBackStackImmediate();
       else
           super.onBackPressed();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d("PrepareMenu", "preparing");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        MenuItem register = menu.findItem(R.id.registerButton);
        MenuItem profile = menu.findItem(R.id.profileButton);
        MenuItem login = menu.findItem(R.id.loginButton);
        if(skipped || user == null) {
            //not registered or logged
            register.setVisible(true);
            profile.setVisible(false);
            login.setVisible(true);
        }else if(user!= null){
            //registered
            register.setVisible(false);
            profile.setVisible(true);
        }
        return true;

    }








}
