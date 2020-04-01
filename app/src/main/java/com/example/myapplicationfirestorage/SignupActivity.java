package com.example.myapplicationfirestorage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

public class SignupActivity extends AppCompatActivity {
    private EditText email , pass;
    private ProgressBar pgr ;
    private FirebaseAuth mauth;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        try {
            mauth = FirebaseAuth.getInstance();
            email = findViewById(R.id.emailET);
            pass = findViewById(R.id.passET);
            pgr = findViewById(R.id.prgbar);
            btn = findViewById(R.id.btnsignup);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ifuserexist();
                }
            });

        }catch (Exception e)
        {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
    }

    private void ifuserexist()
    {
        try {
            if(!email.getText().toString().isEmpty())
            {
                if(mauth!=null)
                {
                    pgr.setVisibility(View.VISIBLE);
                    btn.setEnabled(false);
                    mauth.fetchSignInMethodsForEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            boolean check = task.getResult().getSignInMethods().isEmpty();
                            if(check)
                            {
                                signup();
                            }else
                            {
                                pgr.setVisibility(View.INVISIBLE);
                                btn.setEnabled(true);
                                Toast.makeText(SignupActivity.this, "User Already Exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pgr.setVisibility(View.INVISIBLE);
                            btn.setEnabled(true);
                            Toast.makeText(SignupActivity.this, "Failed To check if user exists"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }else
                {
                    Toast.makeText(this, "Fire Base is Not connected", Toast.LENGTH_SHORT).show();
                }
            }
            else if(email.getText().toString().isEmpty())
            {
                Toast.makeText(this, "Please Enter email", Toast.LENGTH_SHORT).show();
                email.requestFocus();
            }
        }catch (Exception e)

        {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void signup(){

        try {

            if(!email.getText().toString().isEmpty() && !pass.getText().toString().isEmpty())
            {
                if(mauth!=null)
                {

                    mauth.createUserWithEmailAndPassword(email.getText().toString(),pass.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            pgr.setVisibility(View.INVISIBLE);
                            Toast.makeText(SignupActivity.this, "User Signed Up", Toast.LENGTH_SHORT).show();
                            if(authResult!=null)
                            {
                                startActivity(new Intent(getBaseContext(),MainActivity.class));
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignupActivity.this, "User not signed up "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }else
                {

                }
            }else{
                pgr.setVisibility(View.INVISIBLE);
                btn.setEnabled(true);
                Toast.makeText(this, "Email or password cannot be empty ", Toast.LENGTH_SHORT).show();
                email.requestFocus();
            }
        }catch (Exception e)
        {
            btn.setEnabled(true);
            pgr.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Email or password cannot be empty "+e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }


}
