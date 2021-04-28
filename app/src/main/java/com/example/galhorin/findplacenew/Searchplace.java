package com.example.galhorin.findplacenew;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by galhorin on 12/27/2015.
 */
public class Searchplace extends AppCompatActivity {

    private Spinner unitTypeSpinner;
    private ImageButton searchbutton;
    private ListView items_list;
    private RadioButton ans, quest;
    private String uNick,pName,pDetails,rootofQuest;

    RadioGroup radioGroup;
    //gps class to get location info
    public GPSData GPSLocation;
    public static boolean Flag;//flag in false= the service never run yet,true= the service is running in the background
    //
    public static final String MyPREFERENCES = "User";
    SharedPreferences sharedPref;


    //firebase connector
    Firebase mRef;

    Messages message;
    Intent loginIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchplace);


        Flag = false;
        //when log out sends you to the login activity
        loginIntent = new Intent(this, Login.class);

        // Fills the spinner with the unit options
        addItemsToUnitTypeSpinner();

        // Add listener to the Spinner
        addListenerToUnitTypeSpinner();

        //init mRef to get access to the database
        mRef = new Firebase("https://findplaces.firebaseio.com/");

        //Get a reference to the image button
        searchbutton = (ImageButton) findViewById(R.id.search_btn);

        //get a reference to the items list
        items_list = (ListView) findViewById(R.id.items_list);

        //get a reference to the Radio buttons
        ans = (RadioButton) findViewById(R.id.answers_btn);
        quest = (RadioButton) findViewById(R.id.questions_btn);

        //get a reference to the Radio group to be able to check whats is chosen
        radioGroup = (RadioGroup) findViewById(R.id.ansOquest);

        //init gps manager
        GPSLocation = new GPSData(this);
        //init message
        message = new Messages();

        //declare handlerClick as the method when a button is pressed
        View.OnClickListener myhandler = new View.OnClickListener() {
            public void onClick(View v) {
                handlerClick(v);
            }
        };

        //get connected to the shared perf for the user details
        sharedPref = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        uNick=sharedPref.getString("uNick","");

        //add on click listener to the buttons
        ans.setOnClickListener(myhandler);
        quest.setOnClickListener(myhandler);
        searchbutton.setOnClickListener(myhandler);

        pName="";
        pDetails="";
        rootofQuest="";
        uNick=sharedPref.getString("uNick","");

        items_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if((items_list.getItemAtPosition(position) instanceof String) && (!items_list.getItemAtPosition(position).toString().equals("We are sorry but there are no answers in your area."))) {
                    rootofQuest=items_list.getItemAtPosition(position).toString();
                    sendDialog();
                }
            }
        });

    }

    public void addItemsToUnitTypeSpinner() {

        // Get a reference to the spinner
        unitTypeSpinner = (Spinner) findViewById(R.id.unit_type_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> unitTypeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.suggestions, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        unitTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        unitTypeSpinner.setAdapter(unitTypeSpinnerAdapter);

    }


    private void handlerClick(View v) {
        Messages[] msArray;
        if (v.getId() == searchbutton.getId()) {
            GPSLocation.updateLocation();
            if (GPSLocation.getIsGPSEnabled() || GPSLocation.getIsNetworkEnabled()) {
                //geoPoint to know the location of the user(saved in the database as one object)
                BGservice.i = 1;
                Toast.makeText(Searchplace.this, "Loading answers please refresh answers tab", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(Searchplace.this, "Your  gps and network are disabled please enable them", Toast.LENGTH_LONG).show();
            }
        } else if ((v.getId() == ans.getId()) || (v.getId() == quest.getId())) {
            //show the list chosen
            if (ans.getId() == radioGroup.getCheckedRadioButtonId()) {
                msArray = new Messages[BGservice.answers.size()];
                if (msArray.length == 0) {
                    String noAnswers[] = new String[1];
                    noAnswers[0]="We are sorry but there are no answers in your area.";
                    ArrayAdapter<String> adapter;
                    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, noAnswers);
                    items_list.setAdapter(adapter);
                } else {
                    for (int i = 0; i < msArray.length; i++) {
                        msArray[i] = new Messages(BGservice.answers.get(i));
                        ListAdapter theAdapter = new Adapt(this, msArray);
                        items_list.setAdapter(theAdapter);
                    }
                }
            } else {
                if (quest.getId() == radioGroup.getCheckedRadioButtonId()) {
                    String arrayofQuestions[] = getResources().getStringArray(R.array.suggestions);
                    ArrayAdapter<String> adapter;
                    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayofQuestions);
                    items_list.setAdapter(adapter);
                }
            }
        }
    }

    //checks what is the current spinner selected item
    public void addListenerToUnitTypeSpinner() {
        unitTypeSpinner = (Spinner) findViewById(R.id.unit_type_spinner);
        unitTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3) {
                BGservice.selection = unitTypeSpinner.getSelectedItem().toString();
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO maybe add something here later
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    // Called when a options menu item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.exit) {
            Flag = true;
            mRef.unauth();
            startActivity(loginIntent);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("password");
            editor.commit();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private  void sendDialog()
    {
        final EditText placeInfo;
        final AlertDialog.Builder alertDetails;

        placeInfo=new EditText(this);

        alertDetails=new AlertDialog.Builder(this);
        alertDetails.setTitle(R.string.app_name);

        if(pName.isEmpty()||pName==null) {
            placeInfo.setHint("Place name :");
            alertDetails.setView(placeInfo);
        }else
        {
            placeInfo.setHint("Place description :");
            alertDetails.setView(placeInfo);
        }

        alertDetails.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //check if the place name field is empty or null if so put the user text to the place name
                if(pName.isEmpty()||pName==null) {
                    //A place name cannot be short then 2 letters
                    if(placeInfo.getText().toString().length()>2) {
                        pName = placeInfo.getText().toString();
                        sendDialog();
                    }
                    else
                    {
                        sendDialog();
                    }
                }else
                {
                    //if the name place was already full then put the user text to the place details
                    //*note : a description must be longet the 4 letters
                    if(placeInfo.getText().toString().length()<4)
                    {
                        sendDialog();
                    }else {
                        pDetails = placeInfo.getText().toString();
                        sendItem();
                    }
                }

            }
        });
        alertDetails.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //reset the values ,the user do not want to help :/
                rootofQuest="";
                pName="";
                pDetails="";
            }
        });
        AlertDialog sendDialog=alertDetails.create();
        sendDialog.show();
    }

    // check if the item is valid if so send to the database
    public void sendItem()
    {
        Toast.makeText(this,rootofQuest+" : "+pName+" : "+pDetails,Toast.LENGTH_LONG).show();
        //send the object to the database server
        Firebase questRef=new Firebase("https://findplaces.firebaseio.com/"+rootofQuest+"/"+GPSLocation.getCity()+"/"+uNick+"/");
        questRef.setValue(pName + " : " + pDetails);
        //after sending the object clear the fields for future work
        rootofQuest="";
        pName="";
        pDetails="";
    }

}
