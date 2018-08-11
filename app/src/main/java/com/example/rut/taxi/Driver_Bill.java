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

public class Driver_Bill extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference myDriver;
    private DatabaseReference ud;
    private DatabaseReference tride;
    private String dkey;

    private TextView td;
    private TextView ta;

    private Button paid;

    private String ukey;
    private String dis;
    private String amt;

    private int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver__bill);

        Bundle bill = getIntent().getExtras();
        dis = bill.getString("TD");
        amt = bill.getString("TA");
        ukey = bill.getString("UKey");

        td = (TextView)findViewById(R.id.td);
        td.setText(dis);
        ta = (TextView)findViewById(R.id.ta);
        ta.setText(amt);

        myDriver = FirebaseDatabase.getInstance().getReference().child("LoggedInDrivers");
        ud = FirebaseDatabase.getInstance().getReference().child("UserDriver").child(ukey).child("Ride Status");
        dkey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        tride = FirebaseDatabase.getInstance().getReference().child("DriversDB").child(dkey).child("Total Rides");

        paid = (Button)findViewById(R.id.p_btn);
        paid.setOnClickListener(this);
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

            Intent ni = new Intent(Driver_Bill.this, MainActivity.class);
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
        if(i == R.id.p_btn)
        {
            if (count == 0)
            {
                Toast.makeText(Driver_Bill.this,"Confirm Payment once again...", Toast.LENGTH_LONG).show();
                count++;
            }
            else
            {
                ud.setValue("2");
                tride.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        String totalride = dataSnapshot.getValue().toString();
                        int tr = Integer.valueOf(totalride);
                        tr = tr + 1;
                        String totride = String.valueOf(tr);
                        tride.setValue(totride);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}});

                Toast.makeText(Driver_Bill.this,"Keep up the good work...", Toast.LENGTH_LONG).show();

                Intent ni = new Intent(Driver_Bill.this, DriverHome.class);
                startActivity(ni);
            }
        }
    }
}
