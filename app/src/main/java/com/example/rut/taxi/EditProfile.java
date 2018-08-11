package com.example.rut.taxi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference myDriver;
    private DatabaseReference driverdbedit;

    private String name;
    private String contactno;
    private String review;
    private String totalride;

    private EditText net;
    private EditText cnet;

    private TextView rtv;
    private TextView trtv;

    private Button editdonebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        myDriver = FirebaseDatabase.getInstance().getReference().child("LoggedInDrivers");
        String did = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        driverdbedit = FirebaseDatabase.getInstance().getReference().child("DriversDB").child(did);

        net = (EditText)findViewById(R.id.n_field);
        cnet = (EditText)findViewById(R.id.no_field);

        rtv = (TextView)findViewById(R.id.review_field);
        trtv = (TextView)findViewById(R.id.ride_field);

        editdonebtn = (Button)findViewById(R.id.Edit_Done_Btn);

        Bundle bill = getIntent().getExtras();
        name = bill.getString("n");
        contactno = bill.getString("cno");
        review = bill.getString("r");
        totalride = bill.getString("tr");

        net.setText(name);
        cnet.setText(contactno);
        rtv.setText(review);
        trtv.setText(totalride);

        editdonebtn.setOnClickListener(this);

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

            Intent ni = new Intent(EditProfile.this, MainActivity.class);
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
        if(i == R.id.Edit_Done_Btn)
        {
            String newn,newcno;

            newn = net.getText().toString();
            newcno = cnet.getText().toString();

            driverdbedit.child("Name").setValue(newn);
            driverdbedit.child("ContactNo").setValue(newcno);

            Toast.makeText(getApplicationContext(),"Driver Database Updated...", Toast.LENGTH_LONG).show();
        }
    }
}
