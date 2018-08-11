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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Pickup extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, View.OnClickListener, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    com.example.rut.taxi.GPSTracker gps;
    private int count=0;

    private Button m;
    private double lon,lat;

    private DatabaseReference loc;
    private DatabaseReference myUser;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        myUser = FirebaseDatabase.getInstance().getReference().child("LoggedInUsers");
        userId= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        loc= FirebaseDatabase.getInstance().getReference().child("UserDriver");

        m=(Button)findViewById(R.id.btn);
        m.setOnClickListener(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        gps = new com.example.rut.taxi.GPSTracker(Pickup.this);

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

        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
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

    @Override
    public void onClick(View view) {

        //Toast.makeText(getApplicationContext(),lat+" "+lon, Toast.LENGTH_LONG).show();

        if (lat > 0.0 & lon > 0.0)
        {
            loc.child(userId).child("Pickup").child("Latitude").setValue(lat);
            loc.child(userId).child("Pickup").child("Longitude").setValue(lon);

            Intent dh = new Intent(Pickup.this, Destination.class);
            startActivity(dh);

        }
        else
        {
            Toast.makeText(getApplicationContext(),"Pickup Location is loading...Please wait...", Toast.LENGTH_LONG).show();
        }


    }
    @Override
    public void onMapLongClick(LatLng latLng) {

        if(count < 1) {

            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Pickup Point")
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            count++;
            lat=latLng.latitude;
            lon=latLng.longitude;
        }
        //Toast.makeText(getApplicationContext(),latLng.latitude+" "+latLng.longitude, Toast.LENGTH_LONG).show();

    }



    @Override
    public void onMarkerDragStart(Marker latLng) {

    }

    @Override
    public void onMarkerDrag(Marker latLng) {

    }

    @Override
    public void onMarkerDragEnd(Marker latLng) {

        LatLng dragPosition= latLng.getPosition();
        lat = dragPosition.latitude;
        lon = dragPosition.longitude;
        //Toast.makeText(getApplicationContext(),dragLat+" "+dragLong, Toast.LENGTH_LONG).show();
    }
}

