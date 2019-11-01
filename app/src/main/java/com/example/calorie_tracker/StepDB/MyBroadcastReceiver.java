package com.example.calorie_tracker.StepDB;

import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.calorie_tracker.Models.Reports;
import com.example.calorie_tracker.Models.ReportsPK;
import com.example.calorie_tracker.Models.Users;
import com.example.calorie_tracker.RestClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyBroadcastReceiver extends BroadcastReceiver {
    int id ;
    Users u ;
    Context c ;
    @Override
    public void onReceive(Context context, Intent intent) {
        c=context;
        //u= intent.getParcelableExtra("user");
        id = intent.getExtras().getInt("id",0);
        Log.e("id in Receiver",String.valueOf(id));
        Daily d = new Daily();
        d.execute(c);
    }


    private class Daily extends AsyncTask<Context,Void,Void> {
        @Override
        protected Void doInBackground(Context... contexts) {
            String result = RestClient.getRest("users/",String.valueOf(id));
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            u = gson.fromJson(result,Users.class);
            StepsDataBase stepDB = Room.databaseBuilder(contexts[0],
                    StepsDataBase.class, "CustomerDatabase")
                    .fallbackToDestructiveMigration()
                    .build();
            int steps = stepDB.stepDAO().sumSteps();
            double goal = 0;
            double totalBurned = 0;
            double totalConsumed = 0;
            SharedPreferences sp =
                    contexts[0].getSharedPreferences("test",Context.MODE_PRIVATE);
            result= sp.getString("goal",null);
            Log.e("goal",result + "/" +String.valueOf(steps));
            if(result !=null)
                 goal = Double.parseDouble(result);
            steps = ((StepsDataBase)stepDB).stepDAO().sumSteps();

            try {
                double cPerStep = Double.parseDouble(RestClient.scrape("users/Calorie_Step/"+id));
                double restCal = Double.parseDouble(RestClient.scrape("users/Calorie_Activity/"+id));
                totalBurned = Math.round(cPerStep*restCal);
            } catch (Exception e) {
                e.printStackTrace();
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateInput = simpleDateFormat.format(new Date());
            try {
                double calConsumed = Double.parseDouble(RestClient.scrape("consumption/Calorie/" + id + "/" + dateInput));
                totalConsumed = calConsumed;
            }catch (Exception e){e.printStackTrace();}
            Log.e("consumed",String.valueOf(totalConsumed));
            Date today =new Date();

            ReportsPK pk = new ReportsPK(id,today);
            Reports r = new Reports(pk,totalConsumed,totalBurned,steps,goal,u);
            RestClient.createRest("reports",r);
            stepDB.stepDAO().deleteAll();
            stepDB.close();
            return null;
        }
    }
}
