package com.example.calorie_tracker.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

public class Users implements Parcelable {
    private Integer id;
    private String firstname;
    private String surname;
    private Date dob;
    private String gender;
    private String address;
    private String postcode;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    private String email;
    private double height;
    private double weight;
    private int activityLevel;
    private int stepsPermile;

    public Users(Integer id, String firstname, String surname, Date dob, String gender, String address, String postcode, String email, double height, double weight, int activityLevel, int stepsPermile) {
        this.id = id;
        this.firstname = firstname;
        this.surname = surname;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
        this.postcode = postcode;
        this.email = email;
        this.height = height;
        this.weight = weight;
        this.activityLevel = activityLevel;
        this.stepsPermile = stepsPermile;
    }

    protected Users(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        firstname = in.readString();
        surname = in.readString();
        gender = in.readString();
        address = in.readString();
        postcode = in.readString();
        email = in.readString();
        height = in.readDouble();
        weight = in.readDouble();
        activityLevel = in.readInt();
        stepsPermile = in.readInt();
    }

    public static final Creator<Users> CREATOR = new Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel in) {
            return new Users(in);
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(int activityLevel) {
        this.activityLevel = activityLevel;
    }

    public int getStepsPermile() {
        return stepsPermile;
    }

    public void setStepsPermile(int stepsPermile) {
        this.stepsPermile = stepsPermile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(firstname);
        dest.writeString(surname);
        dest.writeString(gender);
        dest.writeString(address);
        dest.writeString(postcode);
        dest.writeString(email);
        dest.writeDouble(height);
        dest.writeDouble(weight);
        dest.writeInt(activityLevel);
        dest.writeInt(stepsPermile);
    }
}
