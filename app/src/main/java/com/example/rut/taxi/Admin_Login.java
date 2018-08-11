package com.example.rut.taxi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Admin_Login extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference myAdmin;

    private EditText email;
    private EditText pwd;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__login);

        mAuth = FirebaseAuth.getInstance();
        myAdmin = FirebaseDatabase.getInstance().getReference().child("LoggedInAdmins");

        email = (EditText)findViewById(R.id.e_et);
        pwd = (EditText)findViewById(R.id.p_et);

        login = (Button)findViewById(R.id.l_btn);
        login.setOnClickListener(this);

    }

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
        if (!validateForm())
        {
            return;
        }

        String em = email.getText().toString();
        String password = pwd.getText().toString();

        mAuth.signInWithEmailAndPassword(em, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful())
                        {
                            FirebaseUser user=task.getResult().getUser();
                            String username = usernameFromEmail(user.getEmail());
                            myAdmin.child(user.getUid()).setValue(username);

                            //Toast.makeText(Admin_Login.this, "Successful Login",Toast.LENGTH_SHORT).show();

                            Intent dh = new Intent(Admin_Login.this, AdminHome.class);
                            startActivity(dh);
                        }
                        else
                        {
                            Toast.makeText(Admin_Login.this, "Register using another Email...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
