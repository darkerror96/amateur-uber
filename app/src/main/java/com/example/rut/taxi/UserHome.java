package com.example.rut.taxi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UserHome extends AppCompatActivity implements View.OnClickListener {

    //private static final String TAG = "UserHome";
    private DatabaseReference myRef;
    private DatabaseReference myUser;
    private DatabaseReference ud;
    private ListView l;
    private Button dg;

    private ArrayList<String> driver = new ArrayList<>();

    private String name = null;
    private String star = null;
    private String no = null;
    private String k = null;
    private String available = null;

    private HashMap<String, String> hcn = new HashMap<>();
    private HashMap<String, String> hkn = new HashMap<>();
    private ArrayList<String> klist = new ArrayList<>();
    private ArrayList<String> nlist = new ArrayList<>();

    private int itemPosition = -1;
    private String itemValue = null;
    private int countmatch = 0;
    private String sdc;
    private String sdn;
    private String sdk;
    private String userKey;

    private Button cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        dg = (Button) findViewById(R.id.d_btn);
        cd = (Button) findViewById(R.id.cnfd_btn);

        myRef = FirebaseDatabase.getInstance().getReference().child("LoggedInDrivers");
        myUser = FirebaseDatabase.getInstance().getReference().child("LoggedInUsers");
        ud = FirebaseDatabase.getInstance().getReference().child("UserDriver");
        userKey = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        l = (ListView) findViewById(R.id.lv);

        dg.setOnClickListener(this);
        cd.setOnClickListener(this);
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                itemPosition = position;
                itemValue = (String) l.getItemAtPosition(position);
                Toast.makeText(UserHome.this, "Selected Driver:   " + itemValue, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void GetList() {

        final ArrayAdapter<String> ad = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, driver);
        l.setAdapter(ad);

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                available = dataSnapshot.getValue().toString();

                if(available.equals("1"))
                {
                    k = dataSnapshot.getKey();
                    klist.add(k);
                    DatabaseReference dname0 = FirebaseDatabase.getInstance().getReference("DriversDB").child(k).child("ContactNo");
                    dname0.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            no = dataSnapshot.getValue().toString();
                            //Toast.makeText(UserHome.this,no, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    DatabaseReference dname1 = FirebaseDatabase.getInstance().getReference("DriversDB").child(k).child("Name");
                    dname1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            name = dataSnapshot.getValue().toString();
                            //Toast.makeText(UserHome.this,no+name, Toast.LENGTH_LONG).show();
                            hcn.put(no, name);
                            //hkn.put(k, name);
                            nlist.add(name);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    DatabaseReference dname2 = FirebaseDatabase.getInstance().getReference("DriversDB").child(k).child("Review");
                    dname2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            star = dataSnapshot.getValue().toString();
                            String value = name + "        |        " + star + " Star";
                            driver.add(value);
                            ad.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
                else
                {}
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.d_btn)
        {
            driver.clear();
            GetList();
        }
        else if (i == R.id.cnfd_btn)
        {
            if (itemPosition != (-1))
            {
                String n1 = null, n2 = null;
                Iterator l = klist.iterator();
                Iterator l1 = nlist.iterator();

                while(l.hasNext() & l1.hasNext())
                {
                    hkn.put((String) l.next(),(String) l1.next());
                    //Toast.makeText(UserHome.this, (String) l.next()+ (String) l1.next(), Toast.LENGTH_LONG).show();
                    //Toast.makeText(UserHome.this, (Integer) size, Toast.LENGTH_SHORT).show();
                }

                for (Map.Entry m : hcn.entrySet())
                {
                    n1 = (String) m.getValue();
                    if (itemValue.contains(n1))
                    {
                        countmatch++;
                        sdn = n1;
                        sdc = (String) m.getKey();
                        //Toast.makeText(UserHome.this, "Selected Driver Mobile No: " + m.getKey(), Toast.LENGTH_LONG).show();
                    }
                }

                for (Map.Entry m1 : hkn.entrySet())
                {
                    n2 = (String) m1.getValue();
                    if (sdn.equals(n2))
                    {
                        sdk = (String) m1.getKey();
                        //Toast.makeText(UserHome.this, "Selected Driver Key: " + sdk, Toast.LENGTH_LONG).show();
                    }
                }

                if (countmatch == 1 )
                {
                    itemPosition = -1;
                    countmatch = 0;

                    ud.child(userKey).child("Driver Name").setValue(sdn);
                    ud.child(userKey).child("Driver Key").setValue(sdk);
                    ud.child(userKey).child("Driver ContactNo").setValue(sdc);
                    ud.child(userKey).child("Ride Status").setValue("0");

                    Intent ni = new Intent(UserHome.this, Pickup.class);
                    startActivity(ni);

                }
            }
            else
            {
                Toast.makeText(UserHome.this, "Please select any one Driver from above list...", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(UserHome.this,"", Toast.LENGTH_LONG).show();
        }
    }
}


