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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverBill extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference driverdbfuel;
    private DatabaseReference myDriver;
    private String did;

    private String oldamt;
    private String newamt;

    private TextView pamttv;
    private EditText newamtet;
    private Button fuelbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_bill);

        did = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        driverdbfuel = FirebaseDatabase.getInstance().getReference().child("DriversDB").child(did);
        myDriver = FirebaseDatabase.getInstance().getReference().child("LoggedInDrivers");

        Bundle bill = getIntent().getExtras();
        oldamt = bill.getString("fpamt");

        pamttv = (TextView)findViewById(R.id.pamt_field);
        newamtet = (EditText)findViewById(R.id.newamt_field);
        fuelbtn = (Button)findViewById(R.id.addfuelbtn);

        pamttv.setText(oldamt);

        fuelbtn.setOnClickListener(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.logout) {

            myDriver.child(did).removeValue();
            FirebaseAuth.getInstance().signOut();

            Intent ni = new Intent(DriverBill.this, MainActivity.class);
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
        if (i == R.id.addfuelbtn)
        {
            if (!newamtet.getText().toString().isEmpty())
            {
                newamt = newamtet.getText().toString();
                int a1 = Integer.parseInt(oldamt);
                int a2 = Integer.parseInt(newamt);
                int res = a1+a2;
                oldamt = Integer.toString(res);

                driverdbfuel.child("Fuel Amount").setValue(oldamt);
                pamttv.setText(oldamt);


                Toast.makeText(getApplicationContext(),"Fuel Amount Updated...", Toast.LENGTH_LONG).show();
            }
            else if (newamtet.getText().toString().isEmpty())
            {
                Toast.makeText(getApplicationContext(),"Enter fuel amount...", Toast.LENGTH_LONG).show();
            }
        }
    }
}
