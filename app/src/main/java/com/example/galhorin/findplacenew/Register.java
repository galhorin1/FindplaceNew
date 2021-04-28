package com.example.galhorin.findplacenew;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

/**
 * Created by galhorin on 12/20/2015.
 */
public class Register extends AppCompatActivity {

    EditText password, mail,uNick;//account details
    Button register, back;//to finish registering
    Boolean GoodDetails = true;
    Firebase mRef;

    ArrayList <String> nicks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mRef = new Firebase("https://findplaces.firebaseio.com/");

        //connect to the xml
        password = (EditText) findViewById(R.id.Upass);
        mail = (EditText) findViewById(R.id.Umail);
        uNick = (EditText) findViewById(R.id.Unick);

        back = (Button) findViewById(R.id.Back);
        register = (Button) findViewById(R.id.Finish);

        //when button pressed go to myhandler
        register.setOnClickListener(myhandler);
        back.setOnClickListener(myhandler);


        nicks=new ArrayList<>();

        getNickList();
    }

    View.OnClickListener myhandler = new View.OnClickListener() {
        public void onClick(View v) {

            if (v.getId() == back.getId()) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            } else {
                //check if the password is good to use
                if (IsEmptyField(password)) {
                    password.setError("Password is Empty");
                    GoodDetails = false;
                } else if ((password.getText().toString().length() < 8) || (password.getText().toString().length() > 15)) {
                    password.setError("Password is need to be 8-15 chars");
                    GoodDetails = false;
                }


                //check if user nickname is invalid if it is invalid show why
                if(IsEmptyField(uNick))
                {
                    uNick.setError("Nickname is empty");
                    GoodDetails=false;
                }else if (uNick.getText().toString().contains("\\\", ;:'.<>!@#$%^&*()_-+=/|][{}~`?"))
                {
                    uNick.setError("Nickname cannot contain signs");
                    GoodDetails=false;
                }else
                {
                    for (String name:nicks)
                    {
                        if (name.equals(uNick.getText().toString()))
                        {
                            uNick.setError("Nickname is already in use");
                            GoodDetails=false;
                        }
                    }
                }

                //check if the email is valid to use
                if (!(IsEmailValid(mail.getText().toString()))) {
                    mail.setError("Email is invalid");
                    GoodDetails = false;
                }


                if (GoodDetails) {
                    mRef.createUser(mail.getText().toString(), password.getText().toString(), new Firebase.ResultHandler() {
                        //if account was successfully created go to on success
                        @Override
                        public void onSuccess() {
                            Toast.makeText(Register.this,"Successfully registered the account you may now login",Toast.LENGTH_LONG).show();
                            //bound the user mail and nickname in the database
                            Firebase addUser = mRef.child("/Users/"+uNick.getText().toString());
                            addUser.setValue(mail.getText().toString());
                            Intent intent = new Intent(Register.this, Login.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            Toast.makeText(Register.this, "Could not register the account Error:" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }

        }
    };

    //checks if the mail is valid or not
    boolean IsEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //checks if the field is empty or not
    boolean IsEmptyField(EditText s) {
        return (s.getText().toString().isEmpty());
    }


    public void getNickList()
    {
        Firebase checkNick = mRef.child("/Users/");
        checkNick.addChildEventListener(new ChildEventListener() {
            //when you get new child to the data base add it to the list of nicks for future work
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                nicks.add(dataSnapshot.getKey().toString());
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
}
