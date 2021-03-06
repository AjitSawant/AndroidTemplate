package com.palash.sampleapp.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.buzzbox.mob.android.scheduler.SchedulerManager;
import com.palash.sampleapp.R;
import com.palash.sampleapp.database.DatabaseAdapter;
import com.palash.sampleapp.database.DatabaseContract;
import com.palash.sampleapp.entiry.ELLogin;
import com.palash.sampleapp.fragment.PatientListFragment;
import com.palash.sampleapp.task.SynchronizationTask;
import com.palash.sampleapp.utilities.Constants;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private DatabaseContract databaseContract;
    private DatabaseAdapter databaseAdapter;
    private DatabaseAdapter.LoginAdapter loginAdapter;

    private ArrayList<ELLogin> listLogin;

    private TextView nav_header_username;

    private View header;
    private Fragment fragment = null;
    private Class fragmentClass = null;
    private NavigationView navigationView;
    private FragmentManager fragmentManager = null;
    private FrameLayout home_fl_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
        InitView();
    }

    private void Init() {
        try {
            context = this;
            databaseContract = new DatabaseContract(context);
            databaseAdapter = new DatabaseAdapter(databaseContract);
            loginAdapter = databaseAdapter.new LoginAdapter();
            listLogin = loginAdapter.listAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void InitView() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            header = navigationView.getHeaderView(0);
            home_fl_content = (FrameLayout) findViewById(R.id.home_fl_content);
            fragmentManager = getSupportFragmentManager();
            ShowAddmittedPatientList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        ShowUser();
        RunScheduler();
        super.onResume();
    }

    private void RunScheduler() {
        try {
            SchedulerManager.getInstance().runNow(context, SynchronizationTask.class, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ShowAddmittedPatientList() {
        try {
            fragmentClass = PatientListFragment.class;
            if (fragmentClass != null) {
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                fragmentManager.beginTransaction().replace(R.id.home_fl_content, fragment).addToBackStack(null).commit();
                setTitle("Admitted Patients");
                navigationView.getMenu().getItem(1).setChecked(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ShowUser() {
        try {
            if (listLogin != null && listLogin.size() > 0) {
                nav_header_username = (TextView) header.findViewById(R.id.nav_header_username);
                nav_header_username.setText("Dr. " + listLogin.get(0).getFullName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog
                    .Builder(context)
                    .setTitle(getResources().getString(R.string.app_name))
                    .setMessage("Do you really want to exit application?")
                    .setCancelable(true)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent startMain = new Intent(Intent.ACTION_MAIN);
                            startMain.addCategory(Intent.CATEGORY_HOME);
                            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(startMain);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(R.mipmap.ic_launcher)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        fragment = null;
        fragmentClass = null;

        if (id == R.id.nav_patients) {
            fragmentClass = PatientListFragment.class;
        } else if (id == R.id.nav_logout) {
            Logout();
        }

        if (fragmentClass != null) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            fragmentManager.beginTransaction().replace(R.id.home_fl_content, fragment).commit();
        }
        if (!item.getTitle().equals("Logout")) {
            setTitle(item.getTitle());
        }
        RunScheduler();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void Logout() {
        listLogin = loginAdapter.listAll();
        if (listLogin != null && listLogin.size() > 0) {
            new AlertDialog
                    .Builder(new ContextThemeWrapper(context, R.style.alertDialog))
                    .setTitle(getResources().getString(R.string.app_name))
                    .setMessage("Do you really want to logout?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SchedulerManager.getInstance().stopAll(context);
                            listLogin.get(0).setLoginStatus(Constants.STATUS_LOG_OUT);
                            loginAdapter.UpdateStatus(listLogin.get(0));
                            startActivity(new Intent(context, LoginActivity.class));
                        }
                    })
                    .setIcon(R.mipmap.ic_launcher)
                    .show();
        }
    }
}
