package com.example.rut.taxi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Review extends AppCompatActivity implements View.OnClickListener {

    private String uid;
    private DatabaseReference myUser;
    private DatabaseReference DriverDB;
    private DatabaseReference UserDriver;
    private String key;

    private Button done;
    private EditText cmt;
    private RatingBar rbar;
    private Float star;
    private String rs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myUser = FirebaseDatabase.getInstance().getReference().child("LoggedInUsers");
        DriverDB = FirebaseDatabase.getInstance().getReference().child("DriversDB");
        UserDriver = FirebaseDatabase.getInstance().getReference().child("UserDriver");

        cmt = (EditText)findViewById(R.id.r_tv);
        done = (Button)findViewById(R.id.done_btn);
        rbar = (RatingBar)findViewById(R.id.ratingBar);

        done.setOnClickListener(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.logout)
        {
            myUser.child(uid).removeValue();
            FirebaseAuth.getInstance().signOut();

            Intent ni = new Intent(Review.this, MainActivity.class);
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
        int i= view.getId();
        if(i == R.id.done_btn)
        {
            star = rbar.getRating();
            //Toast.makeText(Review.this,star+" Comment "+cmt.getText(), Toast.LENGTH_LONG).show();

            UserDriver.child(uid).child("Driver Key").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    key = dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if(key != null)
            {
                DriverDB.child(key).child("Review").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        rs = dataSnapshot.getValue().toString();
                        //Toast.makeText(Review.this,rs, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                if (rs != null)
                {
                    Float review = Float.valueOf(rs);
                    Float avgstar = (review+star)/2;
                    DriverDB.child(key).child("Review").setValue(String.valueOf(avgstar));

                    Toast.makeText(Review.this,"Thank you. Do visit us again...", Toast.LENGTH_LONG).show();

                    Intent dh = new Intent(Review.this, UserHome.class);
                    startActivity(dh);

                }
                else
                {
                    Toast.makeText(Review.this,"Please wait for a while...", Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(Review.this,"Loading...", Toast.LENGTH_LONG).show();
            }
        }
    }
}
