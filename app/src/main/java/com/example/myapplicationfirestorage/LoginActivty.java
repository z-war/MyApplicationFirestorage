package com.example.myapplicationfirestorage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivty extends AppCompatActivity {
    private static final int RC_SIGN_IN = 2;
    private EditText email , pass;
    private ProgressBar pgr ;
    private FirebaseAuth mauth;
    Button btn;
    GoogleSignInOptions gso;
    GoogleSignInClient client;
    SignInButton btn_google_sign_in;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activty);
        try{
            email = findViewById(R.id.LemailET);
            pass = findViewById(R.id.LpassET);
            pgr = findViewById(R.id.Lprgbar);
            btn = findViewById(R.id.Lbtnsignup);
            mauth  = FirebaseAuth.getInstance();
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signin();
                }
            });
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            client = GoogleSignIn.getClient(this,gso);
            btn_google_sign_in = findViewById(R.id.sign_in_btn_google);
            btn_google_sign_in.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    googlesignin();
                }
            });
        }catch (Exception e)
        {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void googlesignin()
    {
        try {
            pgr.setVisibility(View.VISIBLE);
            GoogleSignInAccount currentuser = GoogleSignIn.getLastSignedInAccount(this);
            if(currentuser==null)
            {

                    Intent signin = client.getSignInIntent();
                    startActivityForResult(signin,RC_SIGN_IN);
            }else
            {
                client.signOut();

                pgr.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Last User Logged Out Please Sign in again ", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e)
        {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pgr.setVisibility(View.INVISIBLE);
        if(requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignIntask(task);
        }
    }

    private void handleSignIntask(Task<GoogleSignInAccount> task) {
        try {
            pgr.setVisibility(View.INVISIBLE);
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Toast.makeText(this, "User Loggged In "+account.getDisplayName(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getBaseContext(),ShowUserData.class));

        }catch (Exception e)
        {
            pgr.setVisibility(View.INVISIBLE);

            Toast.makeText(this, "Not Signed In "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void signin()
    {
        try{

            if(!email.getText().toString().isEmpty() && !pass.getText().toString().isEmpty())
            {
                pgr.setVisibility(View.VISIBLE);
                btn.setEnabled(false);
                if(mauth!=null)
                {
                    if(mauth.getCurrentUser()!=null)
                    {
                        mauth.signOut();
                        Toast.makeText(this, "SignOut Successfully", Toast.LENGTH_SHORT).show();
                        mauth.signInWithEmailAndPassword(email.getText().toString() , pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(LoginActivty.this, "User Signed In Successfully", Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mauth.getCurrentUser();
                                    startActivity(new Intent(getBaseContext(),MainActivity.class));
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pgr.setVisibility(View.INVISIBLE);
                                btn.setEnabled(true);
                                Toast.makeText(LoginActivty.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else

                    {

                        mauth.signInWithEmailAndPassword(email.getText().toString() , pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(LoginActivty.this, "User Signed In Successfully", Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mauth.getCurrentUser();
                                    startActivity(new Intent(getBaseContext(),MainActivity.class));
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pgr.setVisibility(View.INVISIBLE);
                                btn.setEnabled(true);
                                Toast.makeText(LoginActivty.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }else
                {

                }
            }else{
                pgr.setVisibility(View.INVISIBLE);
                btn.setEnabled(true);
                Toast.makeText(this, "Email or Password cannot be empty", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            pgr.setVisibility(View.INVISIBLE);
            btn.setEnabled(true);
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
