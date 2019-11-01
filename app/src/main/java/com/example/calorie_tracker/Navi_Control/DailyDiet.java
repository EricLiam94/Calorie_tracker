package com.example.calorie_tracker.Navi_Control;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calorie_tracker.CustomizedSearch;
import com.example.calorie_tracker.R;
import com.example.calorie_tracker.RestClient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import com.example.calorie_tracker.Models.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;


public class DailyDiet extends Fragment implements  AdapterView.OnItemSelectedListener {
    View vDisplayUnit;
    String [] category;
    Bitmap bm;
    String link ;
    int id = 0;
    Spinner cateSp;
    Foods f = null;
    String temp="fruit,meat";
    List<Foods> foodList = new ArrayList<>();
    List<String> name= new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vDisplayUnit = inflater.inflate(R.layout.fragment_diet, container, false);
        cateSp = vDisplayUnit.findViewById(R.id.categorySp);
        final TextView calorieView = vDisplayUnit.findViewById(R.id.calorieView);
        final TextView fatView = vDisplayUnit.findViewById(R.id.fatView);
        SharedPreferences sp =
                getActivity().getSharedPreferences("test", Context.MODE_PRIVATE);
        String result= sp.getString("category",null);
        if (result ==null){
        CategoryCheck catecheck = new CategoryCheck();
        catecheck.execute();}
        else{
            Spinner cateSp = vDisplayUnit.findViewById(R.id.categorySp);
            String[] cate = result.split(",");
            final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, Arrays.asList(cate));
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cateSp.setAdapter(spinnerAdapter);

        }
        cateSp.setOnItemSelectedListener(this);
        Spinner foodsp = vDisplayUnit.findViewById(R.id.foodSp);
        Button addbt = vDisplayUnit.findViewById(R.id.addRecord);
        EditText jump = vDisplayUnit.findViewById(R.id.foodsearchET);
        jump.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AddFoodFragment NAME = new AddFoodFragment();
                fragmentTransaction.replace(R.id.content_frame, NAME);
                fragmentTransaction.commit();
                return false;
            }
        }
        );
        addbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText amountet = vDisplayUnit.findViewById(R.id.quantityet);
                String amount = amountet.getText().toString().trim();
                if(amount.length()==0)
                    amountet.setError("Empty");
                else
                {
                    ConsumptionPK pk = null;
                    Intent i = getActivity().getIntent();
                    Users u = i.getParcelableExtra("user");
                    if (f==null){}
                    else{
                        pk = new ConsumptionPK(f.getFoodId(),u.getId(),new Date());
                    }
                    EditText et = vDisplayUnit.findViewById(R.id.quantityet);
                    if(et.getText().toString().trim().length()==0)
                        et.setError("Empty");
                    else
                        {
                            int quantity = Integer.parseInt(et.getText().toString().trim());
                            Consumption consumption = new Consumption(pk,quantity,f,u);
                            PostRecord p = new PostRecord();
                            p.execute(consumption);
                        }
                    et.setText("");
                    et.clearAnimation();

                }
            }
        });
        foodsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String foodSelected =
                        parent.getItemAtPosition(position).toString();
                f= foodList.get(position);
                id = f.getFoodId();
                double fat = f.getFat();
                double calorie = f.getCalorie();
                String unit = f.getAmount()+ " " + f.getUnit();
                TextView unitView = vDisplayUnit.findViewById(R.id.Amount);
                unitView.setText(unit);
                fatView.setText(String.valueOf(fat));
                calorieView.setText(String.valueOf(calorie));
                SearchAsyncTask imageTask = new SearchAsyncTask();
                imageTask.execute(foodSelected);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        return vDisplayUnit;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        foodList.clear();
        String categorySeleted =
                parent.getItemAtPosition(position).toString();
        FoodFinder foodfinder = new FoodFinder();
        foodfinder.execute(categorySeleted.trim());
        if(categorySeleted != null){
            Toast.makeText(parent.getContext(), "Selected Category is " +
                    categorySeleted,Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private class FoodFinder extends AsyncTask<String,Void,String>{


        @Override
        protected String doInBackground(String... strings) {
            String t =RestClient.getRest("foods/type/",strings[0]);
            Log.e("food",t);
            try {
                JSONArray jarray =new JSONArray(t);
                Gson gson = new Gson();
                for(int i = 0;i<jarray.length();i++)
                {
                   Foods f = gson.fromJson(jarray.get(i).toString(), Foods.class);
                    foodList.add(f);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            Spinner foodsp = vDisplayUnit.findViewById(R.id.foodSp);
            name.clear();
            for(Foods f :foodList)
            {
                name.add(f.getName());
            }
            final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, name);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            foodsp.setAdapter(spinnerAdapter);
        }
    }


    private class CategoryCheck extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            try {
                temp = RestClient.scrape("foods/category/").trim();
                temp = temp.substring(1,temp.length()-1);
                Log.e("msg",temp);
            }catch (Exception e){Log.e("xxx","error");}
        return temp;
        }

        @Override
        protected void onPostExecute(String s) {
            category = s.split(",");
            SharedPreferences sp = getActivity().getSharedPreferences("test",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sp.edit();
            ed.putString("category", s);
            ed.apply();
            final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, Arrays.asList(category));
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cateSp.setAdapter(spinnerAdapter);
        }
    }
    private class SearchAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            ProgressBar pd = vDisplayUnit.findViewById(R.id.search_process);
            pd.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = RestClient.NNDClient(params[0]);
            String temp =RestClient.NNDClient(params[0]);
            RestClient.findDetail(temp);
            String res = CustomizedSearch.search(params[0], new String[]{"num"}, new
                    String[]{"1"});
            String link = CustomizedSearch.getSnippet(res, "link");
            bm = getBitmapFromUrl(link);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            ProgressBar pd = vDisplayUnit.findViewById(R.id.search_process);
            pd.setVisibility(View.GONE);
            ImageView iv = vDisplayUnit.findViewById(R.id.foodimg);
            iv.setImageBitmap(bm);
        }
    }

    public Bitmap getBitmapFromUrl(String src) {
        try {
            InputStream in = new java.net.URL(src).openStream();
            Bitmap bm = BitmapFactory.decodeStream(in);
            return bm;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }
    private  class PostRecord extends AsyncTask<Consumption,Void,Void>{
        @Override
        protected Void doInBackground(Consumption... consumptions) {
            RestClient.createRest("consumption",consumptions[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Context context = getActivity().getApplicationContext();
            CharSequence text = "Add successfully";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }
}
