package com.example.rut.taxi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import static com.example.rut.taxi.R.string.user;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference myUser;
    private DatabaseReference myDriver;
    private EditText email;
    private EditText pwd;
    private Button login;
    private Button register;
    private RadioButton u;
    private RadioGroup rg;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDriver = FirebaseDatabase.getInstance().getReference().child("LoggedInDrivers");
        myUser = FirebaseDatabase.getInstance().getReference().child("LoggedInUsers");
        mAuth = FirebaseAuth.getInstance();

        email = (EditText) findViewById(R.id.email_field);
        pwd = (EditText) findViewById(R.id.pwd_field);
        login = (Button) findViewById(R.id.login_btn);
        register = (Button) findViewById(R.id.reg_btn);
        rg=(RadioGroup)findViewById(R.id.rg1);
        login.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    /*@Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }*/

    private void signIn() {

        if (!validateForm()) {
            return;
        }

        String em = email.getText().toString();
        String password = pwd.getText().toString();

        /*Toast.makeText(MainActivity.this,rbss+em+password,Toast.LENGTH_SHORT).show();

        mAuth.signInWithEmailAndPassword(em, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this,"Successfully Login....",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,"Login Failed...",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/

        mAuth.signInWithEmailAndPassword(em, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful())
                        {

                            String rbss= (String) u.getText();

                            //Toast.makeText(MainActivity.this,rbss,Toast.LENGTH_SHORT).show();

                            if (rbss.equals("Driver"))
                            {

                                FirebaseUser user=task.getResult().getUser();
                                writeNewDriver(user.getUid());

                                //Toast.makeText(MainActivity.this, "Driver LogIn",Toast.LENGTH_SHORT).show();

                                Intent dh = new Intent(MainActivity.this, DriverHome.class);
                                startActivity(dh);
                            }
                            else if (rbss.equals("User"))
                            {

                                FirebaseUser user=task.getResult().getUser();
                                String username = usernameFromEmail(user.getEmail());
                                writeNewUser(user.getUid(),username);

                                //Toast.makeText(MainActivity.this, "User LogIn",Toast.LENGTH_SHORT).show();

                                Intent us = new Intent(MainActivity.this, UserHome.class);
                                startActivity(us);
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this, "Please Select Type", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
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

    private void writeNewUser(String userId, String name)
    {
            myUser.child(userId).setValue(name);
    }
    private void writeNewDriver(String userId) {    myDriver.child(userId).setValue("1");     }

    private boolean validateForm()
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
        return result;
    }

    @Override
    public void onClick(View v)
    {
        int i = v.getId();
        if (i == R.id.login_btn)
        {
            int rbid=rg.getCheckedRadioButtonId();
            if(rbid ==(-1))
            {
                Toast.makeText(MainActivity.this, "Please Select Type", Toast.LENGTH_SHORT).show();
            }
            else
            {
                u = (RadioButton) findViewById(rbid);
                signIn();
            }
        }
        else if (i == R.id.reg_btn)
        {
            //Toast.makeText(MainActivity.this, "Register...",Toast.LENGTH_SHORT).show();

            Intent us = new Intent(MainActivity.this, Registration.class);
            startActivity(us);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.admin_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int i = item.getItemId();
        if (i == R.id.alogin)
        {
            //Toast.makeText(MainActivity.this, "Admin Login", Toast.LENGTH_SHORT).show();
            Intent ni = new Intent(MainActivity.this, Admin_Login.class);
            startActivity(ni);
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }
}

