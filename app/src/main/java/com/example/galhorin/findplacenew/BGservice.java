package com.example.galhorin.findplacenew;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;


import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

/**
 * Created by galhorin on 2/9/2016.
 */
public class BGservice extends IntentService {

    public static ArrayList<Messages> answers;
    public static String selection;//the selection you get from the user
    String city, prevSelect;//the city the user is in at this moment
    static boolean flagExist = false, haveNewMS = false;
    public static int i;
    GPSData gpsLoc;//gps object to get location
    Firebase mRef;//firebase object to create path to the database
    AlertManager alert;

    //const for the service
    public BGservice() {
        super("BGservice");
    }

    public void onCreate() {
        super.onCreate();
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Firebase messageRef;//firebase child gets the messages from the data base that are relevant user needs
        //this loop make checks every time if there is a new selection for what the user is looking for
        while (true) {
            if (i == 1) {
                i=0;
                if ((!prevSelect.equals(selection.toString())) || (!city.equals(gpsLoc.getCity().toString()))) {
                    //reset answers list
                    answers.clear();
                    prevSelect = selection;
                    city = gpsLoc.getCity();
                    //check if the new selection is not null
                    if ((selection != null) && (!selection.isEmpty())) {
                        //set messageRef to take the new list items
                        messageRef = mRef.child("" + selection.toString() + "/" + city + "/");
                        messageRef.addChildEventListener(new ChildEventListener() {
                            //when you get new child to the data base add it to the list of answers
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Messages nMessage = new Messages();
                                nMessage.setNickname(dataSnapshot.getKey().toString());
                                nMessage.setDescription(dataSnapshot.getValue(String.class));
                                answers.add(nMessage);
                                haveNewMS=true;
                            }

                            //when a data base item has been changed check if you have it if so change it to the new details
                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                for (int indexChange = 0; indexChange < answers.size(); indexChange++) {
                                    if (answers.get(indexChange).getNickname() == dataSnapshot.getKey().toString()) {
                                        answers.get(indexChange).setDescription(dataSnapshot.getValue(String.class));
                                        haveNewMS = true;
                                        break;
                                    }
                                }
                            }

                            //when a data base item has been removed check if you already had it if so remove it from the list
                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                for (int indexRemove = 0; indexRemove < answers.size(); indexRemove++) {
                                    if (answers.get(indexRemove).getNickname() == dataSnapshot.getKey().toString()) {
                                        answers.remove(indexRemove);
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                //IGNORE
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                }
            }
            //after getting all the massages update the user that he got new massages
            if(haveNewMS)
            {
                haveNewMS=false;
                alert.displayNote();
            }
        }

    }


    //this method runs when the service is created
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //initializing the objects
        gpsLoc = new GPSData(this);
        answers = new ArrayList<>();
        i = 0;
        prevSelect = "";
        selection = "";

        city = gpsLoc.getCity();//get the city the user is in right now
        mRef = new Firebase("https://findplaces.firebaseio.com/");//get a connection to the data base

        alert=new AlertManager();
        alert.myNotificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        alert.con=getApplicationContext();
        return START_STICKY;
    }


    //stops the service when stop service method called
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
