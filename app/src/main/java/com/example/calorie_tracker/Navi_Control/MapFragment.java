package com.example.calorie_tracker.Navi_Control;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.calorie_tracker.Models.Users;
import com.example.calorie_tracker.R;
import com.github.mikephil.charting.components.MarkerImage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    View parent;
    GoogleMap gmap;
    Geocoder geocoder;
    Address  local  = new Address(Locale.CHINA);
    Users u ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        parent = inflater.inflate(R.layout.fragment_map, container, false);
        Intent i = getActivity().getIntent();
        u = i.getParcelableExtra("user");
        geocoder = new Geocoder(parent.getContext(), new Locale("AU"));
        local.setLatitude(20);
        local.setLongitude(20);
        try {
            local = geocoder.getFromLocationName(u.getAddress()+u.getPostcode(),1).get(0);
        }catch (Exception e){}
        SupportMapFragment smf = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        smf.getMapAsync(this);

        return parent;
}
    private  void showPark(){
            StringBuilder sb  = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            sb.append("location="+local.getLatitude()+","+local.getLongitude());
            sb.append("&radius=5000");
            sb.append("&keyword=park");
            sb.append("&key="+getResources().getString(R.string.api_key));
            String url = sb.toString();
            Log.e("xx",url);
            GetNearByPark gnp = new GetNearByPark();
            gnp.execute(url);




            }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        double lat = local.getLatitude();
        double lon = local.getLongitude();
        showPark();
        gmap = googleMap;
        LatLng home = new LatLng(lat,lon);
        gmap.clear();
        // Animating to the touched position
        MarkerOptions mo = new MarkerOptions();
        mo.title("home");
        mo.position(home);
        mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        List<Address> newLocal = new ArrayList<>();
        try{
         newLocal = geocoder.getFromLocationName("park",10);}
        catch (Exception e){e.printStackTrace();
        Log.e("error","fail");}
        for(Address item:newLocal)
        {
            MarkerOptions marker = new MarkerOptions();
            marker.title(item.getFeatureName());
            marker.position(new LatLng(item.getLatitude(),item.getLongitude()));
            gmap.addMarker(marker);
        }

        // Placing a marker on the touched position
        gmap.addMarker(mo);
        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(local.getLatitude(), local.getLongitude()), 12.0f));
    }

    private class GetNearByPark extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... strings) {
            String data ="";
            String url = strings[0];
            try {
                URL myUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) myUrl.openConnection();
                conn.connect();
                InputStream is  = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String result = "";
                StringBuffer sb = new StringBuffer();
                while ((result=br.readLine())!=null)
                {
                    sb.append(result);
                }
                data = sb.toString();
            }catch (Exception e){}


            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject j = new JSONObject(s);
                JSONArray jarry = j.getJSONArray("results");
                for(int i = 0 ;i<jarry.length();i++)
                {
                    JSONObject ob = jarry.getJSONObject(i);
                    JSONObject geom = ob.getJSONObject("geometry").getJSONObject("location");
                    String lat = geom.getString("lat");
                    String lon = geom.getString("lng");
                    String name = ob.getString("name");
                    LatLng newPlace = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(newPlace);
                    markerOptions.title(name);
                    gmap.addMarker(markerOptions);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
