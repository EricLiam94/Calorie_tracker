package com.example.calorie_tracker.Navi_Control;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calorie_tracker.Models.Users;
import com.example.calorie_tracker.R;

public class MainFragment extends Fragment implements View.OnClickListener {
    View vMain;
    EditText goal;
    Button bt;
    TextView welcome;
    TextView display;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vMain = inflater.inflate(R.layout.fragment_main, container, false);
        bt = (Button)vMain.findViewById(R.id.goalbt);
        goal = (EditText) vMain.findViewById(R.id.goalet);
        welcome = (TextView) vMain.findViewById(R.id.welcome);
        display = (TextView) vMain.findViewById(R.id.goaldisplay);
        SharedPreferences sp =
                getActivity().getSharedPreferences("test",Context.MODE_PRIVATE);
        String result= sp.getString("goal",null);
        TextView welcome = vMain.findViewById(R.id.welcome);
        Users u = getActivity().getIntent().getParcelableExtra("user");
        welcome.setText("Welcome "+u.getFirstname());
        if(result!=null)
            display.setText("Goal Today is "+ result);
        bt.setOnClickListener(this);
        return vMain;
    }


    @Override
    public void onClick(View v) {
        String s = goal.getText().toString().trim();
        if (s.length()==0)
            goal.setError("Empty");
        else
        {
            display.setText("Goal Today is "+s);
            Toast toast = Toast.makeText(getActivity(), "Gaol set",  Toast.LENGTH_SHORT);
            toast.show();
            SharedPreferences sp = getActivity().getSharedPreferences("test",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sp.edit();
            ed.putString("goal", s);
            ed.apply();
      }
    }
}
