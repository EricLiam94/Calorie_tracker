package com.example.calorie_tracker;

import android.app.AlarmManager;
import android.support.v4.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DigitalClock;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.calorie_tracker.Navi_Control.MapFragment;

import com.example.calorie_tracker.Models.Users;
import com.example.calorie_tracker.Navi_Control.*;
import com.example.calorie_tracker.StepDB.MyBroadcastReceiver;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Users u = null;
    Intent intent;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    NavigationView navigationView;
    Fragment lastFragment;
    Intent receive;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DigitalClock dc = (DigitalClock) findViewById(R.id.digitalClock);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setTitle("Calorie Tracker");
        Intent i = getIntent();
        u = i.getParcelableExtra("user");
        Log.e("wo de id", String.valueOf(u.getId()));
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new
                MainFragment()).commit();
        TextView t = findViewById(R.id.dateView);
        Calendar calendar=Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        t.setText(year+"/"+(month+1)+"/"+day);
        setHeader();
        startAlertAtParticularTime();
    }
    private void setHeader()
    {
        View headerLayout =
                navigationView.getHeaderView(0);
        TextView email = headerLayout.findViewById(R.id.textView);
        ImageView imageView = headerLayout.findViewById(R.id.imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        email.setText(u.getEmail());
        Button bt = headerLayout.findViewById(R.id.sendReport);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(intent);
            }
        });
        if(u.getGender().equals("f"))
                imageView.setImageResource(R.drawable.female);
        else
            imageView.setImageResource(R.drawable.male);
        TextView t = headerLayout.findViewById(R.id.name);
        t.setText(u.getFirstname() + " " + u.getSurname());
    }
    public void startAlertAtParticularTime() {

        // alarm first vibrate at 14 hrs and 40 min and repeat itself at ONE_HOUR interval

        intent = new Intent(this, MyBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
        intent.putExtra("id",u.getId());
        int interval = 1000 * 60 * 60 * 24;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0 );
        calendar.set(Calendar.MILLISECOND,0);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                interval, pendingIntent);

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_screen, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment nextFragment = null;
        switch (id) {
            case R.id.nav_welcome:
                getSupportActionBar().setTitle("Calorie Tracker");
                nextFragment = new MainFragment();
                break;
            case R.id.nav_diet:
                getSupportActionBar().setTitle("My Daily Diet");
                nextFragment = new DailyDiet();
                break;
            case R.id.nav_steps:
                getSupportActionBar().setTitle("Steps");
                nextFragment = new StepFragment();
                break;
            case R.id.nav_foodadd:
                getSupportActionBar().setTitle("Food add");
                nextFragment = new AddFoodFragment();
                break;
            case R.id.nav_tracker:
                getSupportActionBar().setTitle("Tracker");
                nextFragment = new TrackerFragment();

                break;
            case R.id.nav_chart:
                getSupportActionBar().setTitle("Report");
                nextFragment = new ChartFragment();
                break;
            case R.id.nav_map:
                getSupportActionBar().setTitle("Map");
                nextFragment = new MapFragment();
                break;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame,
                nextFragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }


    public Users getU() {
        return u;
    }
}
