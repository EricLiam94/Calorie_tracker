package com.example.calorie_tracker;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import com.example.calorie_tracker.Models.Users;
import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import android.widget.TextView;
import android.widget.Toast;
import com.example.calorie_tracker.Models.*;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActitvity extends AppCompatActivity {
    boolean valid =true;
    String surname;
    String fname;
    String gender;
    String weight;
    String height;
    int level;
    Users u;
    String email="1";
    String address;
    String postcode;
    String steps;
    String[]  dobList;
    int year,month,day;
    DatePickerDialog dpd;
    private Calendar calendar;
     private Button dobSelector;
     private TextView dateView;
     boolean emailValidation =true;
    String src = "users/maxId";

    EditText nameText,snameText,weighttext,heighttext,emailText,addresset,postcodeet,stepet;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        Button signup = findViewById(R.id.sign_bt) ;
        dobSelector = findViewById(R.id.dobselect);
        dateView =findViewById(R.id.showdob);
        calendar=Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                nameText = findViewById(R.id.fnet);
                snameText = findViewById(R.id.snet);
                RadioGroup rg = findViewById(R.id.rg);
                int radioId = rg.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(radioId);
                gender = rb.getText().toString().trim();
                weighttext= findViewById(R.id.weightet);
                heighttext = findViewById(R.id.heightet);
                Spinner sp = findViewById(R.id.spinner);
                level = Integer.parseInt(sp.getSelectedItem().toString().trim());
                emailText = findViewById(R.id.emailet);
                addresset = findViewById(R.id.addresset);
                postcodeet = findViewById(R.id.postet);
                stepet = findViewById(R.id.stepet);
                Validation vali = new Validation();
                vali.execute("");
//                surname = snameText.getText().toString().trim();
//                weight =  Double.parseDouble(weighttext.getText().toString().trim());
//                height = Double.parseDouble(heighttext.getText().toString().trim());
//                email = emailText.getText().toString().trim();
//                address = addresset.getText().toString().trim();
//                postcode = postcodeet.getText().toString().trim();
//                steps  = Integer.parseInt(stepet.getText().toString().trim());
                Log.e("valid",String.valueOf(valid));

            }
        });
        dobSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpd = new DatePickerDialog(SignUpActitvity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int newyear, int newmonth, int dayOfMonth) {
                            day=dayOfMonth;
                            month = newmonth+1;
                            year = newyear;
                            showDate(year,month,day);
                            dateView.setError(null);

                        Context context = getApplicationContext();
                        CharSequence text = "Your birthday is "+year+"/" + month+"/"+day;
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                },year,month,day);
                Date max = new Date(System.currentTimeMillis());
                dpd.getDatePicker().setMaxDate(max.getTime());
                dpd.show();
            }
        });
    }
    public boolean isEmailValid(String email)
    {
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(year).append("-")
                .append(month).append("-").append(day));
    }

    private class Validation extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {valid= true;

            fname = nameText.getText().toString().trim();
            surname = snameText.getText().toString().trim();
            weight = weighttext.getText().toString().trim();
            height = heighttext.getText().toString().trim();
            email = emailText.getText().toString().trim();
            address = addresset.getText().toString().trim();
            postcode= postcodeet.getText().toString().trim();
            steps = stepet.getText().toString().trim();
            isValid(nameText,fname);
            isValid(snameText,surname);
            isValid(weighttext,weight);
            isValid(heighttext,height);
            isValid(emailText,email);
            isValid(addresset,address);
            isValid(postcodeet,postcode);
            isValid(stepet,steps);
            dobList = dateView.getText().toString().trim().split("-");
            try{
                if(dobList.length==1) {valid = false;
                    Log.e("size",String.valueOf(dobList.length));
                    throw new Exception("DOB");
                }}catch(Exception e){dateView.setError("Please select your birthday");}
            if(valid) {
                if(!isEmailValid(email)) {
                    valid = false;
                    emailText.setError("Invalid format");
                }
            }
        }


        @Override
        protected String doInBackground(String... params) {
            String id="";
            try {
                id = RestClient.scrape(src).trim();//toDO
                int uid = Integer.parseInt(id);
                Log.e("id",String.valueOf(uid));
            }catch (Exception e){e.printStackTrace();}
            checkEmail(email);
            if(valid)
            {
                Date dob = new Date();
                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                     dob= dateformat.parse(dateView.getText().toString().trim());
                }catch (Exception e){e.printStackTrace();}
                gender = gender.equals("Male")?"m":"f";

                double inputHeight = Double.parseDouble(height);
                double inputWeight = Double.parseDouble(weight);
                u = new Users(Integer.parseInt(id),fname,surname,dob,gender,address,postcode,email,
                        inputHeight,inputWeight,level,Integer.parseInt(steps));
                RestClient.createRest("users/",u);
            }
            return id;
        }

        @Override
        protected void onPostExecute(String result) {

            if(valid){
                Intent intent = getIntent();
                intent.putExtra("id",result);
                Bundle bundle=new Bundle();
                bundle.putParcelable("user",u);
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();}
             else if(!emailValidation){
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Email address exist", Toast.LENGTH_SHORT);
                toast.show(); }
              else{
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Please fill in information correctly", Toast.LENGTH_SHORT);
                toast.show(); }
            }

        }

    public void isValid(EditText elem,String input)
    {
        try {
            if(input.length()==0)
                throw new Exception("length exception");
        }catch(Exception e) {
            valid = false;
            elem.setError("Invalid");
        }
    }

    private void checkEmail(String e)
    {
        emailValidation= true;
        String url = "users/email/";
        String result = RestClient.getRest(url,e).trim();

        if(result.length()>0){
            result = result.substring(1, result.length()-1);
            if (result.length()>0){
            Log.e("result",result);
            emailValidation=false;
             valid = false;}
        }}
}
