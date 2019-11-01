package com.example.calorie_tracker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.calorie_tracker.Models.Credential;
import com.example.calorie_tracker.Models.Users;
import com.example.calorie_tracker.SHA.SHA256;

import java.util.Date;

public class sign_up_credential extends AppCompatActivity {
    boolean hide = true;
    EditText un;
    EditText password;
    EditText password2;
    TextView error;
    boolean valid;
    String errors;
    Credential c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_credential);
        un = findViewById(R.id.usernameet);
        password = findViewById(R.id.passwordet);
        password2 = findViewById(R.id.confirmpassword);
        error = findViewById(R.id.error);
        Switch sw = findViewById(R.id.switch1);
        Button next = findViewById(R.id.confirmbutton);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valid = true;
                if (password.getText().toString().trim().length() > 0 && password.getText().toString().trim().length() > 0 &&
                        un.getText().toString().trim().length() > 0) {
                    String username = un.getText().toString();
                    userNameChecker r = new userNameChecker();
                    r.execute(username);
                } else
                    error.setText("can not be empty");
            }

        });
            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(buttonView.isChecked())
                    {
                        password.setTransformationMethod(null);
                        password2.setTransformationMethod(null);
                    }
                    else{
                        password.setTransformationMethod(new PasswordTransformationMethod());
                        password2.setTransformationMethod(new PasswordTransformationMethod());
                    }
                }
            });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 111) {
            if (resultCode == RESULT_OK) {
                Intent returnIntent = getIntent();
                Bundle bundle  = data.getExtras();
                Users u = bundle.getParcelable("user");
                Date signD = new Date();
                String passwordinput = SHA256.getSHA(password.getText().toString().trim());
                c = new Credential(un.getText().toString().trim(),signD,passwordinput,u);
                PostCredential p = new PostCredential();
                p.execute();
                returnIntent.putExtra("username",un.getText().toString().trim());
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        }
    }

    private class PostCredential extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {

            RestClient.createRest("credential/",c);
            String result = "1";
            return result;
        }}
    private class userNameChecker extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {

            String result = RestClient.getRest("credential/", params[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            error.setText("");
            errors = "";
            if (!password.getText().toString().equals(password2.getText().toString())) {
                valid = false;
                errors = errors.concat("two passwords are different\n");
            }
            if (password.getText().toString().length() < 6) {
                valid = false;
                errors = errors.concat("Password length should be greater than 6\n");
            }
            if (result.length() > 0) {
                valid = false;
                errors = errors.concat("username exist");
            }
            if (!valid)
                error.setText(errors);
            else{
                    Intent intent = new Intent(sign_up_credential.this, SignUpActitvity.class);
                    intent.putExtra("username", un.getText().toString().trim());
                    intent.putExtra("password", password.getText().toString().trim());
                    startActivityForResult(intent, 111);
            }
        }
    }


}
