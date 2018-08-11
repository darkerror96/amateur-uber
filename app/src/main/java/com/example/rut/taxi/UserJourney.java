package com.example.rut.taxi;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class UserJourney extends AppCompatActivity implements View.OnClickListener{

    private EditText d;
    private EditText p;
    private Button req;
    private Button loc;

    private String phoneNo;
    private String userId;

    private String plat;
    private String plong;
    private String dlat;
    private String dlong;
    private String sms;
    private int count=0;
    private String padd;
    private String dadd;

    private DatabaseReference myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_journey);

        myUser = FirebaseDatabase.getInstance().getReference().child("LoggedInUsers");
        userId= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        d =(EditText) findViewById(R.id.dt);
        p =(EditText) findViewById(R.id.pt);
        req = (Button) findViewById(R.id.send);
        loc = (Button) findViewById(R.id.edbtn);

        req.setOnClickListener(this);
        loc.setOnClickListener(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.logout) {

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            myUser.child(uid).removeValue();
            FirebaseAuth.getInstance().signOut();

            Intent ni = new Intent(this, MainActivity.class);
            startActivity(ni);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void editLoc() {

        DatabaseReference pl1 = FirebaseDatabase.getInstance().getReference().child("UserDriver").child(userId).child("Pickup").child("Latitude");
        pl1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                plat = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference pl2 = FirebaseDatabase.getInstance().getReference().child("UserDriver").child(userId).child("Pickup").child("Longitude");
        pl2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                plong = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference dl1 = FirebaseDatabase.getInstance().getReference().child("UserDriver").child(userId).child("Destination").child("Latitude");
        dl1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dlat = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference dl2 = FirebaseDatabase.getInstance().getReference().child("UserDriver").child(userId).child("Destination").child("Longitude");
        dl2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dlong = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference pn = FirebaseDatabase.getInstance().getReference().child("UserDriver").child(userId).child("Driver ContactNo");
        pn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                phoneNo = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Toast.makeText(UserJourney.this,phoneNo, Toast.LENGTH_LONG).show();
        //Toast.makeText(UserJourney.this,plat, Toast.LENGTH_LONG).show();
        //Toast.makeText(UserJourney.this,plong, Toast.LENGTH_LONG).show();
        //Toast.makeText(UserJourney.this,dlat, Toast.LENGTH_LONG).show();
        //Toast.makeText(UserJourney.this,dlong, Toast.LENGTH_LONG).show();


        if (plat == null & plong == null & dlat == null & dlong == null & phoneNo == null)
        {
            Toast.makeText(UserJourney.this, "Loading Location...", Toast.LENGTH_LONG).show();
        }
        else
        {
            double p1 = Double.parseDouble(plat);
            double p2 = Double.parseDouble(plong);
            double d1 = Double.parseDouble(dlat);
            double d2 = Double.parseDouble(dlong);
            PickupAdd pgh = new PickupAdd();
            DestAdd dgh = new DestAdd();

            LocationAddress pickup = new LocationAddress();
            pickup.getAddressFromLocation(p1, p2, getApplicationContext(), pgh);

            LocationAddress destination = new LocationAddress();
            destination.getAddressFromLocation(d1, d2, getApplicationContext(), dgh);

            if (p.getText().length() != 0)
            {
                if (count >= 3)
                {
                    Toast.makeText(UserJourney.this,"Edit Address if needed...", Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(UserJourney.this, "Loading Address...", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class PickupAdd extends Handler {

        String locationAddress;
        @Override
        public void handleMessage(Message message)
        {
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            p.setText(locationAddress);
        }
    }

    public class DestAdd extends Handler {

        String locationAddress;
        @Override
        public void handleMessage(Message message)
        {
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            d.setText(locationAddress);
        }
    }

    private void sendMes()
    {
        sms = "-----Pickup Point-----\n" + p.getText().toString() + "\n-----Destination-----\n" + d.getText().toString() + "\n---------------------\nDo you want to accept request?";
        if(phoneNo != null & sms != null) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                ArrayList<String> messageParts = smsManager.divideMessage(sms);
                smsManager.sendMultipartTextMessage(phoneNo, null, messageParts, null, null);

                Toast.makeText(getApplicationContext(), "Request for Ride Sent!", Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), phoneNo+"\n"+sms, Toast.LENGTH_LONG).show();
                p.setText(null);
                d.setText(null);

                Intent dh = new Intent(UserJourney.this, ConfirmRide.class);
                startActivity(dh);

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Request failed, Please try again later!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Building message...", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.send)
        {
            //Toast.makeText(getApplicationContext(), "Request For Ride", Toast.LENGTH_LONG).show();
            if(count >= 3)
            {
                sendMes();
            }
            else if (count == 0)
            {
                Toast.makeText(UserJourney.this, "Click on Check Address first...", Toast.LENGTH_LONG).show();
            }
            else if (count != 3)
            {
                Toast.makeText(UserJourney.this, "Click on Check Address again...", Toast.LENGTH_LONG).show();
            }
            else
            {}
        }
        else if (i == R.id.edbtn)
        {
            count++;
            editLoc();
        }
        else
        {}
    }
}
