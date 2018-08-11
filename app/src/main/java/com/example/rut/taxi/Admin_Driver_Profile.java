package com.example.rut.taxi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Admin_Driver_Profile extends AppCompatActivity implements View.OnClickListener {

    private String key;

    private DatabaseReference myDriver;
    private DatabaseReference myAdmin;

    private TextView name;
    private TextView no;
    private TextView review;
    private TextView tride;
    private Button gdpbtn;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__driver__profile);

        Bundle bill = getIntent().getExtras();
        key = bill.getString("Key");

        myAdmin = FirebaseDatabase.getInstance().getReference().child("LoggedInAdmins");
        myDriver = FirebaseDatabase.getInstance().getReference().child("DriversDB").child(key);

        name = (TextView)findViewById(R.id.n_tv);
        no = (TextView)findViewById(R.id.cno_tv);
        review = (TextView)findViewById(R.id.r_tv);
        tride = (TextView)findViewById(R.id.tr_tv);

        gdpbtn = (Button)findViewById(R.id.gp_btn);
        back = (Button)findViewById(R.id.back_btn);
        gdpbtn.setOnClickListener(this);
        back.setOnClickListener(this);
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
            myAdmin.child(uid).removeValue();
            FirebaseAuth.getInstance().signOut();

            Intent ni = new Intent(Admin_Driver_Profile.this, MainActivity.class);
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
        if (i == R.id.gp_btn)
        {
            myDriver.child("Name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {   name.setText(dataSnapshot.getValue().toString());}
                @Override
                public void onCancelled(DatabaseError databaseError) {}});
            myDriver.child("ContactNo").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {   no.setText(dataSnapshot.getValue().toString());}
                @Override
                public void onCancelled(DatabaseError databaseError) {}});
            myDriver.child("Review").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {   review.setText(dataSnapshot.getValue().toString());}
                @Override
                public void onCancelled(DatabaseError databaseError) {}});
            myDriver.child("Total Rides").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {   tride.setText(dataSnapshot.getValue().toString());}
                @Override
                public void onCancelled(DatabaseError databaseError) {}});

        }
        else if (i == R.id.back_btn)
        {
            Intent ni = new Intent(Admin_Driver_Profile.this,AdminHome.class);
            startActivity(ni);
        }
    }
}
