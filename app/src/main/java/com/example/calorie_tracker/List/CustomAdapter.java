package com.example.calorie_tracker.List;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.calorie_tracker.R;
import com.example.calorie_tracker.StepDB.Steps;
import com.example.calorie_tracker.StepDB.StepsDataBase;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends BaseAdapter {
    List<Steps> stepList ;
    private Context context;
    private StepsDataBase stepDB = null;

    public CustomAdapter(List<Steps> stepList, Context context) {
        this.stepList = stepList;
        this.context = context;
        stepDB=stepDB = Room.databaseBuilder(context,
                StepsDataBase.class, "CustomerDatabase")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Override
    public int getCount() {
        return stepList.size();
    }

    @Override
    public Object getItem(int position) {
        return stepList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        final ListHolder listViewHolder;
        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.steplist,parent,false);
            listViewHolder = new ListHolder();
            listViewHolder.timeView = row.findViewById(R.id.time);
            listViewHolder.stepView = row.findViewById(R.id.Steps);
            row.setTag(listViewHolder);
        }
        else
        {
            row=convertView;
            listViewHolder= (ListHolder) row.getTag();
        }
        final Steps step = (Steps) getItem(position);
        final String time = step.getDate().split(" ")[3];
        listViewHolder.timeView.setText(time);
        listViewHolder.stepView.setText(String.valueOf(step.getSteps()));
//        listViewHolder.stepView.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                final Steps newStep = new Steps(step.getDate(),Integer.parseInt(s.toString()));
//                        update u = new update();
//                        u.execute(newStep);
//            }
//        });

        return row;
    }

    private  class update extends AsyncTask<Steps,Void,String> {
        @Override
        protected String doInBackground(Steps... steps) {
            ((StepsDataBase)stepDB).stepDAO().updateUsers(steps[0]);
            return "Update Successfully";
        }

        @Override
        protected void onPostExecute(String s) {
           Log.e("test","check successfully");
        }
    }

    public void InsertRecord(Steps item)
    {
        stepList.add(item);

    }
}
