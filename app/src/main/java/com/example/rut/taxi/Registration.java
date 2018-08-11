package com.example.rut.taxi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity implements View.OnClickListener {

    private EditText email;
    private EditText pwd;
    private EditText cno;
    private Button submit;
    private RadioButton u;
    private RadioGroup rg;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference myDriver;
    private String em;
    private String pd;
    private String no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();
        myDriver = FirebaseDatabase.getInstance().getReference().child("DriversDB");

        email = (EditText) findViewById(R.id.email_et);
        pwd = (EditText) findViewById(R.id.pwd_et);
        cno = (EditText) findViewById(R.id.cno_et);
        submit = (Button) findViewById(R.id.submit_btn);
        rg=(RadioGroup)findViewById(R.id.rg1);
        submit.setOnClickListener(this);
    }

    private boolean validateUser()
    {
        boolean result = true;
        if (TextUtils.isEmpty(email.getText().toString()))
        {
            email.setError("Required");
            result = false;
        }
        else
        {
            email.setError(null);
        }

        if (TextUtils.isEmpty(pwd.getText().toString()))
        {
            pwd.setError("Required");
            result = false;
        }
        else
        {
            pwd.setError(null);
        }

        cno.setError("Not required");

        return result;
    }

    private boolean validateDriver()
    {
        boolean result = true;
        if (TextUtils.isEmpty(email.getText().toString()))
        {
            email.setError("Required");
            result = false;
        }
        else
        {
            email.setError(null);
        }

        if (TextUtils.isEmpty(pwd.getText().toString()))
        {
            pwd.setError("Required");
            result = false;
        }
        else
        {
            pwd.setError(null);
        }
        if (TextUtils.isEmpty(cno.getText().toString()))
        {
            cno.setError("Required");
            result = false;
        }
        else
        {
            cno.setError(null);
        }
        return result;
    }

    private void registerUser()
    {
        Toast.makeText(Registration.this, "Registering User...", Toast.LENGTH_SHORT).show();

        firebaseAuth.createUserWithEmailAndPassword(em,pd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(Registration.this, "Login using registered Email and Password now...", Toast.LENGTH_LONG).show();
                            Intent us = new Intent(Registration.this,MainActivity.class);
                            startActivity(us);
                        }
                        else
                        {
                            Toast.makeText(Registration.this,"Register using another Email Id...", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void registerDriver()
    {
        Toast.makeText(Registration.this,"Registering Driver...", Toast.LENGTH_SHORT).show();

        firebaseAuth.createUserWithEmailAndPassword(em,pd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            FirebaseUser user=task.getResult().getUser();
                            String username = usernameFromEmail(user.getEmail());
                            String did = user.getUid();

                            myDriver.child(did).child("Name").setValue(username);
                            myDriver.child(did).child("ContactNo").setValue(no);
                            myDriver.child(did).child("Review").setValue("5");
                            myDriver.child(did).child("Total Rides").setValue("0");

                            Toast.makeText(Registration.this, "Once registered, contact Admin with all originals documents within 5-6 days...", Toast.LENGTH_LONG).show();

                            Intent us = new Intent(Registration.this,MainActivity.class);
                            startActivity(us);
                        }
                        else
                        {
                            Toast.makeText(Registration.this,"Register using another Email Id...", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private String usernameFromEmail(String email)
    {
        if (email.contains("@"))
        {
            return email.split("@")[0];
        }
        else
        {
            return email;
        }
    }

    @Override
    public void onClick(View view)
    {
        int i = view.getId();
        if (i == R.id.submit_btn)
        {
            int rbid=rg.getCheckedRadioButtonId();
            if(rbid == (-1))
            {
                Toast.makeText(Registration.this, "Please Select Type", Toast.LENGTH_LONG).show();
            }
            else
            {
                u = (RadioButton) findViewById(rbid);
                String rbss= (String) u.getText();

                em = email.getText().toString();
                pd = pwd.getText().toString();
                no = cno.getText().toString();

                int pno = pwd.getText().length();
                int no = cno.getText().length();

                if (rbss.equals("User"))
                {
                    if (!validateUser())
                    {   return;     }

                    if (em.contains("@cab.com") & pno >= 6)
                    {
                        //Toast.makeText(Registration.this, "User Done", Toast.LENGTH_LONG).show();
                        registerUser();
                    }
                    else if (!em.contains("@cab.com"))
                    {
                        Toast.makeText(Registration.this, "Email Id should be of type...                     eg :-  yourname@cab.com", Toast.LENGTH_LONG).show();
                    }
                    else if (pno < 6)
                    {
                        Toast.makeText(Registration.this, "Password length must be atleast 6 characters...", Toast.LENGTH_LONG).show();
                    }


                }
                else if (rbss.equals("Driver"))
                {
                    if (!validateDriver())
                    {   return;     }

                    if (em.contains("@cab.com") & no == 10 & pno >= 6)
                    {
                        //Toast.makeText(Registration.this, "Driver Done", Toast.LENGTH_LONG).show();
                        registerDriver();
                    }
                    else if (!em.contains("@cab.com"))
                    {
                        Toast.makeText(Registration.this, "Email Id should be of type...                     eg :-  yourname@cab.com", Toast.LENGTH_LONG).show();
                    }
                    else if (pno < 6)
                    {
                        Toast.makeText(Registration.this, "Password length must be atleast 6 characters...", Toast.LENGTH_LONG).show();
                    }
                    else if (no != 10)
                    {
                        Toast.makeText(Registration.this, "Enter valid 10 digit mobile number...", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
}
