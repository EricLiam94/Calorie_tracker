package com.example.calorie_tracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.calorie_tracker.Models.Foods;
import com.example.calorie_tracker.Models.Users;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.example.calorie_tracker.SHA.SHA256;
public class MainActivity extends AppCompatActivity {
    EditText userText ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView sign = findViewById(R.id.sign_up);
        final Button login =  findViewById(R.id.sign_in_button);
        userText = findViewById(R.id.username);
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,sign_up_credential.class);
                startActivityForResult(intent,1);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText q1 = findViewById(R.id.username);
                    boolean check = true;
                    EditText q2 =findViewById(R.id.password);
                    if (q1.getText().toString().length()==0)
                    {
                        check=false;
                        q1.setError("Empty");}
                    if (q2.getText().toString().length()==0){
                        q2.setError("Empty");
                    check=false;}

                    if (check==true){
                    loginCheck temp = new loginCheck();
                    temp.execute();}
                }
            });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                showUser su = new showUser();
                su.execute(data.getStringExtra("username"));
            }
        }
    }
    private class showUser extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            return params[0];
        }
        protected void onPostExecute(String result) {
            userText.setText(result);
        }
    }

    private class loginCheck extends  AsyncTask<Void,Void, Users>{
        @Override
        protected Users doInBackground(Void... voids) {
            EditText unameet = findViewById(R.id.username);
            String username = unameet.getText().toString().trim();
            EditText passet = findViewById(R.id.password);
            String password = passet.getText().toString().trim();
            password = SHA256.getSHA(password);
            String input = username+"/"+password;
            Users u = null;
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            try {
                String  mid = RestClient.getRest("credential/usernamePassword/",input);
                 u = gson.fromJson(mid,Users.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return u;
        }

        @Override
        protected void onPostExecute(Users s) {
            if(s ==null){
                TextView error = findViewById(R.id.errorShow);
                error.setText("Wrong username or password");
        }
        else{
                Intent intent = new Intent(MainActivity.this,MainScreen.class);
                intent.putExtra("user",s);
                startActivity(intent);
                finish();

            }

        }

    }

}
