package com.example.rut.taxi;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

public class ConfirmRide extends AppCompatActivity implements View.OnClickListener {

    private TextView dn;
    private TextView dno;
    private TextView td;
    private TextView ta;
    private Button y;
    private Button n;

    private String dname;
    private String dcn;
    private String rate;
    private String totalamt;
    private int count=0;

    private DatabaseReference myUser;
    private DatabaseReference myDriver;
    private DatabaseReference ud;
    private DatabaseReference a;
    private DatabaseReference DriverUser;
    private String userId;

    private Double plat;
    private Double plong;
    private Double dlat;
    private Double dlong;
    private LatLng l1;
    private LatLng l2;
    private String dis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_ride);

        myUser = FirebaseDatabase.getInstance().getReference().child("LoggedInUsers");
        myDriver = FirebaseDatabase.getInstance().getReference().child("LoggedInDrivers");
        userId= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        ud = FirebaseDatabase.getInstance().getReference().child("UserDriver").child(userId);
        a = FirebaseDatabase.getInstance().getReference().child("Admin").child("PriceperKM");
        DriverUser = FirebaseDatabase.getInstance().getReference().child("DriverUser");

        dn=(TextView)findViewById(R.id.dname);
        dno=(TextView)findViewById(R.id.dno);
        td=(TextView)findViewById(R.id.tdist);
        ta=(TextView)findViewById(R.id.tamt);
        y=(Button)findViewById(R.id.ybtn);
        n=(Button)findViewById(R.id.nbtn);

        y.setOnClickListener(this);
        n.setOnClickListener(this);


    }


    private void rideconf() {

        ud.child("Driver Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dname=dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ud.child("Driver ContactNo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dcn=dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ud.child("Pickup").child("Latitude").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                plat= ((Double) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ud.child("Pickup").child("Longitude").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                plong= ((Double) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ud.child("Destination").child("Latitude").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dlat= ((Double) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ud.child("Destination").child("Longitude").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dlong= ((Double) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        a.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rate=dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if( dname == null & dcn == null & plat == null & plong == null & dlat == null & dlong == null & rate == null)
        {
            //Toast.makeText(ConfirmRide.this,"Please wait for a while...", Toast.LENGTH_LONG).show();
            //Toast.makeText(ConfirmRide.this,"Try now...", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Ride Details is loading...", Toast.LENGTH_LONG).show();
        }
        else
        {
            l1 = new LatLng(plat,plong);
            l2 = new LatLng(dlat,dlong);

            double distance = SphericalUtil.computeDistanceBetween(l1, l2);
            distance = distance/1000;
            long d = Math.round(distance);

            long lrate = (Long.valueOf(rate));
            long ld = lrate*d;

            if(d == 0)
            {
                d=1;
                ld=lrate;
            }
            dis = Long.toString(d);
            totalamt = Long.toString(ld);

            dn.setText(dname);
            dno.setText(dcn);
            td.setText(dis);
            ta.setText(totalamt);
            count++;
            if(count == 1)
            {
                Toast.makeText(getApplicationContext(),"Wait for selected driver's reply if you want to Confirm the Ride...", Toast.LENGTH_LONG).show();
                count++;
            }
            else if(count == 3)
            {
                String sms = "User has confirmed the above mentioned Ride. Click on Start Journey for further instructions,available on Home Page at bottom side ...";
                if(dcn != null & sms != null)
                {
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        ArrayList<String> messageParts = smsManager.divideMessage(sms);
                        smsManager.sendMultipartTextMessage(dcn, null, messageParts, null, null);

                        Toast.makeText(getApplicationContext(),"Driver has been notified about ride confirmation...", Toast.LENGTH_LONG).show();

                        ud.child("Total Distance").setValue(dis);
                        ud.child("Total Amount").setValue(totalamt);
                        ud.child("Ride Status").setValue("1");

                        ud.child("Driver Key").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                String dkey = dataSnapshot.getValue().toString();
                                myDriver.child(dkey).setValue("0");
                                DriverUser.child(dkey).setValue(userId);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        Intent dh = new Intent(ConfirmRide.this, Edit_Ride.class);
                        startActivity(dh);

                    }
                    catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Request failed, Please try again later!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Click on Yes to see Ride Details once...", Toast.LENGTH_LONG).show();
                }
            }
        }
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

            Intent ni = new Intent(ConfirmRide.this, MainActivity.class);
            startActivity(ni);
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        int i=view.getId();
        if(i == R.id.ybtn) {
            rideconf();
        }
        else if (i == R.id.nbtn)
        {

            String sms = "Above mentioned Ride is canceled by user due to certain circumstances...";
            if(dcn != null & sms != null)
            {
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    ArrayList<String> messageParts = smsManager.divideMessage(sms);
                    smsManager.sendMultipartTextMessage(dcn, null, messageParts, null, null);

                    //Toast.makeText(getApplicationContext(), "Request for Ride Sent!", Toast.LENGTH_LONG).show();

                    ud.removeValue();

                    Intent dh = new Intent(ConfirmRide.this, UserHome.class);
                    startActivity(dh);

                }
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Request failed, Please try again later!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Click on Yes to see Ride Details once...", Toast.LENGTH_LONG).show();
            }
        }
        else
        {}
    }
}
