package com.example.calorie_tracker;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import com.example.calorie_tracker.Models.*;
import com.google.gson.JsonArray;

public class RestClient {
    private static final String BASE_URL = "http://10.0.2.2:8080/Calorie_Tracker/webresources/res.";
    private static final String NND_URL = "https://api.nal.usda.gov/ndb/search/?format=json&";
    private static final String NDD_DETIAL = "https://api.nal.usda.gov/ndb/V2/reports?ndbno=";
    private static final String NDD_PART =   "&type=b&format=json";
    private static final String API_KEY = "&api_key=MVtNhB9JwFaiOhHpyRxMZJbyzlAcYHAmZNLmrcJX";
    public static String scrape(String urlString) throws Exception {
        URL url = new URL(BASE_URL+urlString);
        URLConnection connection = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        String line = null, data = "";

        while ((line = reader.readLine()) != null) {
            data += line + "\n";
        }

        return data;
    }
    public static String getRest(String src,String value) {
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
//Making HTTP request
        try {
            url = new URL((BASE_URL+src+value).trim()); //changed
//open the connection
            conn = (HttpURLConnection) url.openConnection();
//set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
//set the connection method to GET
            conn.setRequestMethod("GET");
//add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
//Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
//read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           // conn.disconnect();
        }
        return textResult;
    }




    public static void createRest(String src,Object value){
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        final String methodPath=src;
        try {
            Gson gson =new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").create();
            String stringCourseJson=gson.toJson(value);
            Log.e("check",stringCourseJson);
            url = new URL(BASE_URL + methodPath);
//open the connection
            conn = (HttpURLConnection) url.openConnection();
//set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
//set the connection method to POST
            conn.setRequestMethod("POST");
            //set the output to true
            conn.setDoOutput(true);
//set length of the data you want to send
            conn.setFixedLengthStreamingMode(stringCourseJson.getBytes().length);
//add HTTP headers
            conn.setRequestProperty("Content-Type", "application/json");
//Send the POST out
            PrintWriter out= new PrintWriter(conn.getOutputStream());
            out.print(stringCourseJson);
            out.close();
            Log.i("error",new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
    }
        public static String NNDClient(String value ){
            final String methodPath = "q="+value+"&sort=n&max=25&offset=0"+API_KEY;
            //initialise
            URL url = null;
            HttpURLConnection conn = null;
            String textResult = "";
//Making HTTP request
            try {
                url = new URL(NND_URL + methodPath);
//open the connection
                conn = (HttpURLConnection) url.openConnection();
//set the timeout
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                Scanner inStream = new Scanner(conn.getInputStream());
//read the input steream and store it as string
                while (inStream.hasNextLine()) {
                    textResult += inStream.nextLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
         textResult = getAttributes(textResult,"ndbno");
            return textResult;
        }
    public static String getAttributes(String result,String attribute){
        String snippet = null;
        try{
            JSONObject jsonObject = new JSONObject(result);
            jsonObject= jsonObject.getJSONObject("list");
            JSONArray jsonArray = jsonObject.getJSONArray("item");
            if(jsonArray != null && jsonArray.length() > 0) {
                snippet =jsonArray.getJSONObject(0).getString(attribute);

            }
        }catch (Exception e){
            e.printStackTrace();
            snippet = "NO INFO FOUND gg";
        }
        return snippet;
    }
    public  static  String findDetail(String input){
        Log.e("id",input);
        HttpURLConnection conn = null;
        String textResult = "";
        try {
            URL url = new URL(NDD_DETIAL+input+NDD_PART+API_KEY);
            Log.e("url",NDD_DETIAL+input+NDD_PART+API_KEY);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            Scanner inStream = new Scanner(conn.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("result",textResult);
        String type = "None";
        String amount="";
        String unit="";
        String calorie="";
        String fat = "";
        String sd = "";
        try {
            JSONObject tempjb = new JSONObject(textResult);
            JSONArray jsarray = tempjb.getJSONArray("foods");
            Log.e("test",jsarray.toString());
            sd = (String) jsarray.getJSONObject(0).getJSONObject("food").getJSONObject("ing").get("desc");
            jsarray=jsarray.getJSONObject(0).getJSONObject("food").getJSONArray("nutrients");
            JSONObject temp = jsarray.getJSONObject(0).getJSONArray("measures").getJSONObject(0);
             fat = "";
             calorie = "";
             unit = (String)temp.get("eunit");
             amount = String.valueOf(temp.get("qty"));
            textResult="";
            for(int i =0; i<jsarray.length();i++){
                JSONObject j = (JSONObject) jsarray.get(i);
                if (j.get("nutrient_id").equals("208"))
                    calorie=(String) j.get("value");
                if (j.get("nutrient_id").equals("204"))
                    fat = (String) j.get("value");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
                textResult =unit+","+amount+","+calorie+","+fat+","+sd;
        Log.e("msg",textResult);
            return textResult;
    }

}


