package com.example.calorie_tracker.Navi_Control;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.calorie_tracker.List.CustomAdapter;
import com.example.calorie_tracker.R;
import com.example.calorie_tracker.StepDB.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class StepFragment extends Fragment {
    View vEnterUnit;
    RoomDatabase stepDB = null;
    EditText et;
    List<HashMap<String, String>> unitListArray;
    CustomAdapter myListAdapter;
    ListView unitList;
    HashMap<String,String> map = new HashMap<String,String>();
    String[] colHEAD = new String[] {"Time","Steps"};
    int[] dataCell = new int[] {R.id.time,R.id.Steps};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vEnterUnit = inflater.inflate(R.layout.fragment_step, container, false);
        stepDB = Room.databaseBuilder(getActivity().getApplicationContext(),
                StepsDataBase.class, "CustomerDatabase")
                .fallbackToDestructiveMigration()
                .build();
        Button bt = vEnterUnit.findViewById(R.id.stepbt);
        et = vEnterUnit.findViewById(R.id.stepInput);
        unitList = vEnterUnit.findViewById(R.id.steplist);
        unitListArray = new ArrayList<HashMap<String, String>>();
        ListSteps as = new ListSteps();
        as.execute();
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = et.getText().toString();
                int stepInput=0;
                if (input.length()>0) {
                    stepInput = Integer.parseInt(input);
                    insertDB id = new insertDB();
                    id.execute(String.valueOf(stepInput));
                }
                     else
                    et.setError("Empty");
            }
        });
        return vEnterUnit;
    }



    private class insertDB extends AsyncTask<String, Void, Steps> {
        @Override
        protected Steps doInBackground(String... strings) {
            Steps s = new Steps(new Date().toString(),Integer.parseInt(strings[0]));
            ((StepsDataBase)stepDB).stepDAO().insert(s);
            return s;
        }

        @Override
        protected void onPostExecute(Steps s) {
            Toast t = Toast.makeText(vEnterUnit.getContext(),"Insert successfully! Steps:"+s.getSteps(),Toast.LENGTH_SHORT);
            t.show();
            myListAdapter.InsertRecord(s);
        }
    }

    private class ListSteps extends AsyncTask<String, Void, List<Steps>> {
        @Override
        protected List<Steps> doInBackground(String... strings) {

            List<Steps> li =((StepsDataBase)stepDB).stepDAO().getAll();
            return li;
        }

        @Override
        protected void onPostExecute(List<Steps> s) {
            myListAdapter = new
                    CustomAdapter(s,getActivity().getApplicationContext());
            unitList.setAdapter(myListAdapter);
        }
    }
}

