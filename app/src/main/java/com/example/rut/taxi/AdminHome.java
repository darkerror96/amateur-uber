package com.example.rut.taxi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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

public class AdminHome extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference myAdmin;
    private DatabaseReference myAdminDB;
    private DatabaseReference myDriverDB;

    private EditText ppkm;
    private Button gubtn;
    private Button gadbtn;
    private Button gdpbtn;

    private int count=0;
    private int countmatch=0;

    private ListView l;
    private ArrayList<String> driver = new ArrayList<>();

    private String k;
    private String val;
    private String no;

    private String sdn;
    private String sdk;
    private int itemPosition = -1;
    private String itemValue = null;

    private HashMap<String, String> kn = new HashMap<>();
    private HashMap<String, String> cn = new HashMap<>();

    private ArrayList<String> klist = new ArrayList<>();
    private ArrayList<String> nlist = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        myAdmin = FirebaseDatabase.getInstance().getReference().child("LoggedInAdmins");
        myAdminDB = FirebaseDatabase.getInstance().getReference().child("Admin").child("PriceperKM");
        myDriverDB = FirebaseDatabase.getInstance().getReference().child("DriversDB");

        ppkm = (EditText)findViewById(R.id.ppkm_et);
        gubtn = (Button)findViewById(R.id.edit_btn);
        gadbtn = (Button)findViewById(R.id.d_btn);
        gdpbtn = (Button)findViewById(R.id.get_btn);

        l = (ListView) findViewById(R.id.list_view);

        gubtn.setOnClickListener(this);
        gadbtn.setOnClickListener(this);
        gdpbtn.setOnClickListener(this);

        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                itemPosition = position;
                itemValue = (String) l.getItemAtPosition(position);
                Toast.makeText(AdminHome.this, "Selected Driver : " + itemValue, Toast.LENGTH_LONG).show();
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
            myAdmin.child(uid).removeValue();
            FirebaseAuth.getInstance().signOut();

            Intent ni = new Intent(AdminHome.this, MainActivity.class);
            startActivity(ni);
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }

    private void GetDList()
    {
        final ArrayAdapter<String> ad = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, driver);
        l.setAdapter(ad);

        myDriverDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                k = dataSnapshot.getKey();
                klist.add(k);

                DatabaseReference num = FirebaseDatabase.getInstance().getReference().child("DriversDB").child(k).child("ContactNo");
                num.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        no = dataSnapshot.getValue().toString();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                DatabaseReference name = FirebaseDatabase.getInstance().getReference().child("DriversDB").child(k).child("Name");
                name.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        val = dataSnapshot.getValue().toString();
                        nlist.add(val);
                        cn.put(no,val);
                        driver.add(val);
                        ad.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    public void onClick(View view)
    {
        int i =view.getId();
        if (i == R.id.edit_btn)
        {
            if (ppkm.getText().length() != 0)
            {
                if (count > 0)
                {
                    myAdminDB.setValue(ppkm.getText().toString());
                    Toast.makeText(AdminHome.this,"Price / Km value updated...", Toast.LENGTH_LONG).show();
                }
                else
                {
                    count++;
                    Toast.makeText(AdminHome.this,"Confirm once again...", Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                myAdminDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Toast.makeText(AdminHome.this,"Current Price / Km : "+dataSnapshot.getValue(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
        else if (i == R.id.d_btn)
        {
            driver.clear();
            GetDList();
        }
        else if (i == R.id.get_btn)
        {
            if (itemPosition != (-1))
            {
                String n1 = null, n2 = null;
                Iterator l = klist.iterator();
                Iterator l1 = nlist.iterator();

                while(l.hasNext() & l1.hasNext())
                {
                    kn.put((String) l.next(),(String) l1.next());
                    //Toast.makeText(UserHome.this, (String) l.next()+ (String) l1.next(), Toast.LENGTH_LONG).show();
                    //Toast.makeText(UserHome.this, (Integer) size, Toast.LENGTH_SHORT).show();
                }

                for (Map.Entry m : cn.entrySet())
                {
                    n1 = (String) m.getValue();
                    if (itemValue.contains(n1))
                    {
                        countmatch++;
                        sdn = n1;
                        //Toast.makeText(UserHome.this, "Selected Driver Mobile No: " + m.getKey(), Toast.LENGTH_LONG).show();
                    }
                }

                for (Map.Entry m1 : kn.entrySet())
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

                    //Toast.makeText(AdminHome.this,sdn+" "+sdk, Toast.LENGTH_LONG).show();

                    Intent ni = new Intent(AdminHome.this,Admin_Driver_Profile.class);
                    ni.putExtra("Key",sdk);
                    startActivity(ni);
                }
            }
            else
            {
                Toast.makeText(AdminHome.this, "Please select any one Driver from above list...", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(AdminHome.this,"", Toast.LENGTH_LONG).show();
        }
    }
}
