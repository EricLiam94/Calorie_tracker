package com.example.calorie_tracker.Navi_Control;

import android.support.v4.app.Fragment;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.calorie_tracker.Models.Users;
import com.example.calorie_tracker.R;
import com.example.calorie_tracker.RestClient;
import com.example.calorie_tracker.StepDB.StepsDataBase;

import org.w3c.dom.Text;

import java.util.Date;
import java.text.SimpleDateFormat;

public class TrackerFragment extends Fragment {
    View vEnterUnit;
    Users u = null;
    RoomDatabase stepDB;
    double totalBurned = 0;
    double totalConsumed = 0;
    int steps =0 ;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vEnterUnit = inflater.inflate(R.layout.fragment_tracker, container, false);
        Intent i = getActivity().getIntent();
        stepDB = Room.databaseBuilder(getActivity().getApplicationContext(),
                StepsDataBase.class, "CustomerDatabase")
                .fallbackToDestructiveMigration()
                .build();
        u = i.getParcelableExtra("user");
        GettingResult gr = new GettingResult();
        gr.execute();
        return  vEnterUnit;

}


private class GettingResult extends AsyncTask<Void,Void,Void>{
    @Override
    protected Void doInBackground(Void... voids) {
        steps = ((StepsDataBase)stepDB).stepDAO().sumSteps();

        try {
            double cPerStep = Double.parseDouble(RestClient.scrape("users/Calorie_Step/"+u.getId()));
            double restCal = Double.parseDouble(RestClient.scrape("users/Calorie_Activity/"+u.getId()));
            totalBurned = Math.round(cPerStep*restCal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
        String dateInput = sp.format(new Date());
        try {
            double calConsumed = Double.parseDouble(RestClient.scrape("consumption/Calorie/" + u.getId() + "/" + dateInput));
            totalConsumed = calConsumed;
        }catch (Exception e){e.printStackTrace();}
        return  null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        TextView goal = vEnterUnit.findViewById(R.id.yourgoal);
        TextView Step = vEnterUnit.findViewById(R.id.yourSteps);
        TextView consume = vEnterUnit.findViewById(R.id.yourConsumed);
        TextView burn = vEnterUnit.findViewById(R.id.yourBurned);
        SharedPreferences sp =
                getActivity().getSharedPreferences("test", Context.MODE_PRIVATE);
        String result= sp.getString("goal",null);
        goal.setText("Your Goal today : "+result);
        Step.setText("Steps you have taken "+ steps);
        consume.setText("Total Calorie Consumed "+String.valueOf(totalConsumed));
        burn.setText(String.valueOf("Total Calorie Burned "+totalBurned));

    }
}

}
