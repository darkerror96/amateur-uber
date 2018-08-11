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

public class Bill extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference myUser;
    private DatabaseReference myDriver;
    private DatabaseReference ud;
    private String userId;

    private TextView td;
    private TextView ta;

    private Button paid;
    private String status;
    private String dkey;

    private String dis;
    private String amt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        Bundle bill = getIntent().getExtras();
        dis = bill.getString("D");
        amt = bill.getString("A");

        td = (TextView)findViewById(R.id.td);
        td.setText(dis);
        ta = (TextView)findViewById(R.id.ta);
        ta.setText(amt);

        myUser = FirebaseDatabase.getInstance().getReference().child("LoggedInUsers");
        myDriver = FirebaseDatabase.getInstance().getReference().child("LoggedInDrivers");
        userId= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        ud = FirebaseDatabase.getInstance().getReference().child("UserDriver").child(userId);


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
            myUser.child(uid).removeValue();
            FirebaseAuth.getInstance().signOut();

            Intent ni = new Intent(Bill.this, MainActivity.class);
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
            ud.child("Ride Status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    status=dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            ud.child("Driver Key").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dkey = dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //Toast.makeText(Bill.this,status+dkey, Toast.LENGTH_LONG).show();

            if (status != null & dkey != null)
            {
                if(status.equals("2"))
                {
                    myDriver.child(dkey).setValue("1");

                    //Toast.makeText(Bill.this, "Review time", Toast.LENGTH_LONG).show();
                    Intent dh = new Intent(Bill.this, Review.class);
                    startActivity(dh);
                }
                else
                {
                    Toast.makeText(Bill.this,"Wait for driver to update payment status...", Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(Bill.this,"Wait for driver to update payment status...", Toast.LENGTH_LONG).show();
            }
        }
    }
}
