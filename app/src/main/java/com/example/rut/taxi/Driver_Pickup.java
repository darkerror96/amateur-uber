package com.example.rut.taxi;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Driver_Pickup extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;

    private String plat;
    private String plong;
    private Double lat;
    private Double lon;

    private String dlat;
    private String dlong;

    private Button r;
    private TextView tv;

    private DatabaseReference DriverUser;
    private DatabaseReference myDriver;
    private DatabaseReference ud;
    private String driverId;
    private String userId;
    private String ukey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver__pickup);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        myDriver = FirebaseDatabase.getInstance().getReference().child("LoggedInDrivers");
        driverId = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        DriverUser = FirebaseDatabase.getInstance().getReference().child("DriverUser");
        ud = FirebaseDatabase.getInstance().getReference().child("UserDriver");

        Bundle bill = getIntent().getExtras();
        plat = bill.getString("Lat");
        plong = bill.getString("Long");
        lat = Double.valueOf(plat);
        lon = Double.valueOf(plong);

        ukey = bill.getString("UKey");

        r = (Button)findViewById(R.id.btn);
        tv = (TextView)findViewById(R.id.key_tv);

        r.setOnClickListener(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng home = new LatLng(lat, lon);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.addMarker(new MarkerOptions()
                .position(home)
                .title("User's Pickup Point")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home,16));

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
            myDriver.child(uid).removeValue();
            FirebaseAuth.getInstance().signOut();

            Intent ni = new Intent(this, MainActivity.class);
            startActivity(ni);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {


        if (tv.getText().length() != 0)
        {
            ud.child(ukey).child("Destination").child("Latitude").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dlat = dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            ud.child(ukey).child("Destination").child("Longitude").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dlong = dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if (dlat != null & dlong != null)
            {
                userId = ukey.substring(0,4);
                String ik = tv.getText().toString();
                if (userId.equals(ik))
                {
                    //Toast.makeText(Driver_Pickup.this,dlat+"Matched"+dlong, Toast.LENGTH_LONG).show();

                    Intent dh = new Intent(Driver_Pickup.this, Driver_Destination.class);
                    dh.putExtra("Lat",dlat);
                    dh.putExtra("Long",dlong);
                    dh.putExtra("UKey",ukey);
                    startActivity(dh);
                }
                else
                {
                    Toast.makeText(Driver_Pickup.this,"Entered key didn't matched with User key...", Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(Driver_Pickup.this,"Comparing both keys...", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(Driver_Pickup.this,"Enter Key which is given to user...", Toast.LENGTH_LONG).show();
        }
    }
}

