package com.example.galhorin.findplacenew;

import android.app.Application;

import com.firebase.client.Firebase;


/**
 * Created by galhorin on 1/5/2016.
 */
public class Database extends Application {
    public void onCreate()
    {
        //connect to the firebase database (happens when the app is lunched)
        Firebase.setAndroidContext(this);
        super.onCreate();
    }
}
