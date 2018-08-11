package com.example.rut.taxi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Edit_Ride extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference ud;
    private DatabaseReference du;
    private DatabaseReference myUser;
    private DatabaseReference myDriver;
    private String userId;


    private Button key;
    private Button pick;
    private Button dest;
    private Button done;
    private Button cancel;

    private int count=0;
    private String dcn;
    private String status;

    private String dis;
    private String amt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__ride);


        myUser = FirebaseDatabase.getInstance().getReference().child("LoggedInUsers");
        myDriver = FirebaseDatabase.getInstance().getReference().child("LoggedInDrivers");
        userId= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        ud = FirebaseDatabase.getInstance().getReference().child("UserDriver").child(userId);
        du = FirebaseDatabase.getInstance().getReference().child("DriverUser");


        key = (Button)findViewById(R.id.key_btn);
        pick = (Button)findViewById(R.id.pick_btn);
        dest = (Button)findViewById(R.id.dest_btn);
        done = (Button)findViewById(R.id.r_btn);
        cancel = (Button)findViewById(R.id.c_btn);

        key.setOnClickListener(this);
        pick.setOnClickListener(this);
        dest.setOnClickListener(this);
        done.setOnClickListener(this);
        cancel.setOnClickListener(this);

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

            Intent ni = new Intent(Edit_Ride.this, MainActivity.class);
            startActivity(ni);
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view)
    {
        int i = view.getId();
        if (i == R.id.pick_btn)
        {
            Toast.makeText(getApplicationContext(), "Edit Pickup Point", Toast.LENGTH_LONG).show();
            //Intent ni = new Intent(Edit_Ride.this, .class);
            //startActivity(ni);
        }
        else if (i == R.id.dest_btn)
        {
            Toast.makeText(getApplicationContext(), "Edit Destination", Toast.LENGTH_LONG).show();
            //Intent ni = new Intent(Edit_Ride.this, .class);
            //startActivity(ni);
        }
        else if (i == R.id.key_btn)
        {
            String k = userId.substring(0,4);
            Toast.makeText(getApplicationContext(), "Generated Key:- "+k, Toast.LENGTH_LONG).show();
        }
        else if (i == R.id.r_btn)
        {

            ud.child("Total Distance").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dis=dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            ud.child("Total Amount").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    amt=dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if(dis != null & amt != null)
            {
                //Toast.makeText(getApplicationContext(), dis + amt, Toast.LENGTH_LONG).show();
                Intent ni = new Intent(Edit_Ride.this, Bill.class);
                ni.putExtra("D",dis);
                ni.putExtra("A",amt);
                startActivity(ni);
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Confirm once again...", Toast.LENGTH_LONG).show();
            }
        }
        else if (i == R.id.c_btn)
        {

            ud.child("Driver ContactNo").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dcn=dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            ud.child("Driver Key").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    status=dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if(count>0)
            {
                count=0;

                String sms = "Above mentioned confirmed Ride is canceled by user due to certain circumstances...";
                if(dcn != null & sms != null)
                {
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        ArrayList<String> messageParts = smsManager.divideMessage(sms);
                        smsManager.sendMultipartTextMessage(dcn, null, messageParts, null, null);

                        //Toast.makeText(getApplicationContext(), "Request for Ride Sent!", Toast.LENGTH_LONG).show();

                        myDriver.child(status).setValue("1");
                        ud.removeValue();
                        du.child(status).removeValue();

                        Intent dh = new Intent(Edit_Ride.this, UserHome.class);
                        startActivity(dh);

                    }
                    catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Request failed, Please try again later!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
            else
            {
                count++;
                Toast.makeText(Edit_Ride.this, "Do you want to cancel the Ride? If yes, press button again...", Toast.LENGTH_LONG).show();
            }
        }
    }
}
