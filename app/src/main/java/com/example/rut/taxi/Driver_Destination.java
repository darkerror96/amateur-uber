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

public class Driver_Destination extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;

    private String dlat;
    private String dlong;
    private Double lat;
    private Double lon;

    private Button r;

    private DatabaseReference myDriver;
    private DatabaseReference ud;
    private String ukey;

    private String dis;
    private String amt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver__destination);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        myDriver = FirebaseDatabase.getInstance().getReference().child("LoggedInDrivers");
        ud = FirebaseDatabase.getInstance().getReference().child("UserDriver");

        Bundle bill = getIntent().getExtras();
        dlat = bill.getString("Lat");
        dlong = bill.getString("Long");
        lat = Double.valueOf(dlat);
        lon = Double.valueOf(dlong);

        ukey = bill.getString("UKey");

        r = (Button)findViewById(R.id.btn);
        r.setOnClickListener(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng home = new LatLng(lat, lon);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.addMarker(new MarkerOptions()
                .position(home)
                .title("User's Destination")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home,15));

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

        //Toast.makeText(Driver_Destination.this,"Reached", Toast.LENGTH_LONG).show();

        ud.child(ukey).child("Total Distance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dis=dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ud.child(ukey).child("Total Amount").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                amt=dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (dis != null & amt != null)
        {
            //Toast.makeText(Driver_Destination.this,dis+" "+amt, Toast.LENGTH_LONG).show();

            Intent dh = new Intent(Driver_Destination.this, Driver_Bill.class);
            dh.putExtra("TD",dis);
            dh.putExtra("TA",amt);
            dh.putExtra("UKey",ukey);
            startActivity(dh);
        }
        else
        {
            Toast.makeText(Driver_Destination.this,"Confirm once again...", Toast.LENGTH_LONG).show();
        }
    }
}

