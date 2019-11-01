package com.example.calorie_tracker.Navi_Control;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calorie_tracker.CustomizedSearch;
import com.example.calorie_tracker.Models.Foods;
import com.example.calorie_tracker.R;
import com.example.calorie_tracker.RestClient;

import java.io.InputStream;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLDisplay;

public class AddFoodFragment extends Fragment implements View.OnClickListener {
    View vDisplayUnit;
    Bitmap bm;
    String[] apiResult=null;
    int id;
    Foods f = new Foods();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vDisplayUnit =  inflater.inflate(R.layout.fragment_foodadd, container, false);
        SharedPreferences sp =
                getActivity().getSharedPreferences("test", Context.MODE_PRIVATE);
        String result= sp.getString("category",null);
        if (result==null)
        {
            EditText searchet = vDisplayUnit.findViewById(R.id.addfoodsearchET);
            searchet.setHint("Please go DailyDiet to check if food exist");
        }
        else
            {
                Spinner cateSp = vDisplayUnit.findViewById(R.id.addcategorySp);
                String[] cate = result.split(",");
                final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, Arrays.asList(cate));
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                cateSp.setAdapter(spinnerAdapter);
            }
        Button searchbt = vDisplayUnit.findViewById(R.id.foodSearch);
        searchbt.setOnClickListener(this);
        Button confirm = vDisplayUnit.findViewById(R.id.addFood);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(apiResult==null)
                {
                    EditText temp = vDisplayUnit.findViewById(R.id.addfoodsearchET);
                    temp.setError("Empty");
                }
                else
                {
                    searchId s = new searchId();
                    s.execute();
                }
            }
        });
        return vDisplayUnit;

    }

    @Override
    public void onClick(View v) {
        EditText et = vDisplayUnit.findViewById(R.id.addfoodsearchET);
        String temp = et.getText().toString().trim();
        Log.e("msg",temp);
        if (temp.length()==0) et.setError("Empty");
        else
        {
            SearchAsyncTask f = new SearchAsyncTask();
            f.execute(temp);
        }
    }
    private class searchId extends  AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... voids) {
            try {
                id = Integer.parseInt(RestClient.scrape("foods/maxId").trim());
                Spinner temp = vDisplayUnit.findViewById(R.id.addcategorySp);
                String cate = temp.getSelectedItem().toString().trim();
                Log.e("cate",cate);
                EditText et= vDisplayUnit.findViewById(R.id.addfoodsearchET);
                String name = et.getText().toString();
                f = new Foods(id,name,cate,Double.parseDouble(apiResult[2]),
                        apiResult[0],Double.parseDouble(apiResult[1]), Double.parseDouble(apiResult[3]));
                Log.e("id",f.getFoodId().toString());
                RestClient.createRest("foods",f);
            }catch (Exception e){e.printStackTrace();}
            return null;
            }

        @Override
        protected void onPostExecute(String s) {
            Toast toast = Toast.makeText(getActivity(), "Add successfully",  Toast.LENGTH_SHORT);
            toast.show();

        }
    }

    private class SearchAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            ProgressBar pd = vDisplayUnit.findViewById(R.id.addsearch_process);
            pd.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = RestClient.NNDClient(params[0]);
            String temp =RestClient.NNDClient(params[0]);
            apiResult = RestClient.findDetail(temp).split(",",5);
            String res = CustomizedSearch.search(params[0], new String[]{"num"}, new
                    String[]{"1"});
            String link = CustomizedSearch.getSnippet(res, "link");
            bm = getBitmapFromUrl(link);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if(apiResult.length!=0)
            {
                TextView unit = vDisplayUnit.findViewById(R.id.addAmount);
                unit.setText(apiResult[1] +" "+ apiResult[0]);
                TextView cal = vDisplayUnit.findViewById(R.id.addcalorieView);
                cal.setText(apiResult[2]);
                TextView fat = vDisplayUnit.findViewById(R.id.addfatView);
                fat.setText(apiResult[3]);
                TextView desc = vDisplayUnit.findViewById(R.id.desc);
                desc.setText(apiResult[4]);
            }
            else {
                Toast toast = Toast.makeText(getActivity(), "No result",  Toast.LENGTH_SHORT);
                toast.show();
            }
            ProgressBar pd = vDisplayUnit.findViewById(R.id.addsearch_process);
            pd.setVisibility(View.GONE);
            ImageView iv = vDisplayUnit.findViewById(R.id.foodaddimg);
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

}
