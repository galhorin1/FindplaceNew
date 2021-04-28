package com.example.galhorin.findplacenew;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class Login extends AppCompatActivity {


    EditText userMail, password;//userMail and password fields in the layout in order to login
    Button Login, Register;//login and register buttons
    Intent Goto;//intent to go one of the other layouts as needed
    String Upass, Umail, suserMail, spassword,nick;//for the instant user login
    public static String conUpass;//the user name to be used in other layouts and classes if needed
    Intent service;//background service for notifications
    //my refrence for the firebase database server
    Firebase mRef;

    //
    public static final String MyPREFERENCES = "User";
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //create the service (still not running)
        service = new Intent(this, BGservice.class);
        //refer mRef to the database of the users
        mRef = new Firebase("https://findplaces.firebaseio.com/");

        //connect the xml fields to the java
        userMail = (EditText) findViewById(R.id.Umail);
        password = (EditText) findViewById(R.id.Upass);

        //when button pressed enter this function
        View.OnClickListener myhandler = new View.OnClickListener() {
            public void onClick(View v) {
                if (v.getId() == R.id.Login) {
                    login();
                } else if (v.getId() == R.id.Register) {
                    transfer(Register.class);
                }
            }

        };


        //connect the xml buttons to the java and add a listener to them (myHandler func when pressed)
        Login = (Button) findViewById(R.id.Login);
        Login.setOnClickListener(myhandler);

        Register = (Button) findViewById(R.id.Register);
        Register.setOnClickListener(myhandler);

        nick="";
        //shared pref used to instant login (takes the details and put them in the fields suserMail,spassword)
        sharedPref = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        Umail = "";
        suserMail = sharedPref.getString("userMail", Umail);
        conUpass = suserMail;//put the userMail to the static field so other classes will be able to access it too
        Upass = "";
        spassword = sharedPref.getString("password", Upass);
        userMail.setText(suserMail.toString());
        password.setText(spassword.toString());

        //checks if the user lugged out so it will stop the notification service
        if (Searchplace.Flag) {
            stopService(service);
        }
        if ((IsEmailValid(suserMail)) && (spassword.length() > 7)) {
            if ((mRef.getAuth() == null)) {
                login();
            } else {
                transfer(Searchplace.class);
            }
        }

    }


    public void login() {
        boolean flag = true;
        if (!(IsEmailValid(userMail.getText().toString()))) {
            userMail.setError("Email is invalid");
            flag = false;
        }
        if (IsEmptyField(password)) {
            password.setError("password is empty");
            flag = false;
        }
        if (flag) {
            getNick();
            Toast.makeText(Login.this, "Trying to login please wait a few sec", Toast.LENGTH_SHORT).show();
            //
            mRef.authWithPassword(userMail.getText().toString(), password.getText().toString(), new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    sharedPref = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("userMail", userMail.getText().toString());
                    editor.putString("password", password.getText().toString());
                    editor.putString("uNick",nick);
                    editor.commit();
                    startService(service);
                    transfer(Searchplace.class);
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Toast.makeText(Login.this, firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    private void transfer(Class<?> tClass) {
        Goto = new Intent(Login.this, tClass);
        startActivity(Goto);
        finish();
    }

    boolean IsEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public void getNick()
    {
        Firebase checkNick = mRef.child("/Users/");
        checkNick.addChildEventListener(new ChildEventListener() {
            //when you get new child to the data base add it to the list of nicks for future work
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String nMail=dataSnapshot.getValue(String.class);
                if(nMail.equals(userMail.getText().toString()))
                {
                    nick=dataSnapshot.getKey().toString();
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

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

    boolean IsEmptyField(EditText s) {
        return (s.getText().toString().isEmpty());
    }
}