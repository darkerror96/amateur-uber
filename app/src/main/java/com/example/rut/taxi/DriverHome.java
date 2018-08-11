package com.example.rut.taxi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverHome extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener{

    private DatabaseReference myDriver;
    private String uid;
    private DatabaseReference du;
    private DatabaseReference ud;
    private String ukey;
    private String rstatus;

    private String plat;
    private String plong;

    private GoogleMap mMap;
    com.example.rut.taxi.GPSTracker gps;


    private Button a;
    private Button na;
    private Button sride;

    private DatabaseReference driverdb;
    private String name;
    private String contactno;
    private String review;
    private String totalride;
    private String fuelamt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        myDriver = FirebaseDatabase.getInstance().getReference().child("LoggedInDrivers");
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        du = FirebaseDatabase.getInstance().getReference().child("DriverUser");
        ud = FirebaseDatabase.getInstance().getReference().child("UserDriver");
        driverdb = FirebaseDatabase.getInstance().getReference().child("DriversDB").child(uid);

        a = (Button)findViewById(R.id.ava_btn);
        na = (Button)findViewById(R.id.unava_btn);
        sride = (Button)findViewById(R.id.ride_btn);

        a.setOnClickListener(this);
        na.setOnClickListener(this);
        sride.setOnClickListener(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drivermenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.logout) {

            myDriver.child(uid).removeValue();
            FirebaseAuth.getInstance().signOut();

            Intent ni = new Intent(DriverHome.this, MainActivity.class);
            startActivity(ni);
            return true;
        }
        else if (i == R.id.editprofile)
        {
            driverdb.child("Name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                        name = dataSnapshot.getValue().toString();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

            driverdb.child("ContactNo").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    contactno = dataSnapshot.getValue().toString();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

            driverdb.child("Review").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    review = dataSnapshot.getValue().toString();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

            driverdb.child("Total Rides").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    totalride = dataSnapshot.getValue().toString();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

            if (name == null || contactno == null || review == null || totalride == null)
            {
                Toast.makeText(getApplicationContext(),"Try again, Loading...", Toast.LENGTH_LONG).show();
            }
            else
            {
                Intent ni = new Intent(DriverHome.this, EditProfile.class);
                ni.putExtra("n", name);
                ni.putExtra("cno", contactno);
                ni.putExtra("r", review);
                ni.putExtra("tr", totalride);
                startActivity(ni);
            }
            return true;
        }
        else if (i == R.id.bill)
        {
            driverdb.child("Fuel Amount").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    fuelamt = dataSnapshot.getValue().toString();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

            if (fuelamt == null)
            {
                Toast.makeText(getApplicationContext(),"Try again, Loading...", Toast.LENGTH_LONG).show();
            }
            else
            {
                Intent ni = new Intent(DriverHome.this, DriverBill.class);
                ni.putExtra("fpamt", fuelamt);
                startActivity(ni);
            }
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        gps = new com.example.rut.taxi.GPSTracker(DriverHome.this);

        // check if GPS enabled
        if (gps.canGetLocation())
        {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "+ latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            LatLng home = new LatLng(latitude, longitude);
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            mMap.addMarker(new MarkerOptions()
                    .position(home)
                    .title("Current Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home,17));

        }
        else
        {
            // can't get location GPS or Network is not enabled Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    @Override
    public void onClick(View view)
    {
        int i = view.getId();
        if (i == R.id.ava_btn)
        {
            myDriver.child(uid).setValue("1");
            Toast.makeText(getApplicationContext(),"Status:- Available", Toast.LENGTH_LONG).show();
        }
        else if(i == R.id.unava_btn)
        {
            myDriver.child(uid).setValue("0");
            Toast.makeText(getApplicationContext(),"Status:- Unavailable", Toast.LENGTH_LONG).show();
        }
        else if(i== R.id.ride_btn)
        {
            du.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ukey = dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if(ukey != null)
            {
                ud.child(ukey).child("Ride Status").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        rstatus = dataSnapshot.getValue().toString();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                if(rstatus != null)
                {
                    if (rstatus.equals("1"))
                    {
                        ud.child(ukey).child("Pickup").child("Latitude").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                plat = dataSnapshot.getValue().toString();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        ud.child(ukey).child("Pickup").child("Longitude").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                plong = dataSnapshot.getValue().toString();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        if (plat != null & plong != null)
                        {
                            //Toast.makeText(getApplicationContext(), plat+" "+plong, Toast.LENGTH_LONG).show();
                            Intent ni = new Intent(DriverHome.this, Driver_Pickup.class);
                            ni.putExtra("Lat",plat);
                            ni.putExtra("Long",plong);
                            ni.putExtra("UKey",ukey);
                            startActivity(ni);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Loading User Location...", Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Your Ride has not been confirmed till...", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Loading...", Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Loading...", Toast.LENGTH_LONG).show();
            }
        }
        else
        {

        }
    }
}
