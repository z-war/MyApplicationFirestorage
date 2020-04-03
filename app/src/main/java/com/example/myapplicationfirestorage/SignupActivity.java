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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

public class SignupActivity extends AppCompatActivity {
    private EditText email, pass;
    private ProgressBar pgr;

    private FirebaseAuth mauth;
    Button btn;

    GoogleSignInOptions gso;
    GoogleSignInClient client;

    SignInButton google_sign_in_btn;
    private static int Rec_code = 1;

    LoginButton fb_signUp_btn;
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;

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

            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            client = GoogleSignIn.getClient(this, gso);

            google_sign_in_btn = findViewById(R.id.sign_in_btn_google);
            google_sign_in_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    google_sign_up();
                }
            });

            callbackManager = CallbackManager.Factory.create();

            if(AccessToken.getCurrentAccessToken()!=null)
            {
                Toast.makeText(this, "User Already Signed In ", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this , LoginActivty.class));
            }
            fb_signUp_btn = findViewById(R.id.fb_login_btn);

            fb_signUp_btn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Toast.makeText(SignupActivity.this, "Sign Up Succesfull", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getBaseContext() , LoginActivty.class));
                }

                @Override
                public void onCancel() {
                    Toast.makeText(SignupActivity.this, "Sign Up Canceled", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException error) {
                    Toast.makeText(SignupActivity.this, "Sign Up Falied"+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
    }

    private void ifuserexist() {
        try {
            if (!email.getText().toString().isEmpty()) {
                if (mauth != null) {
                    pgr.setVisibility(View.VISIBLE);
                    btn.setEnabled(false);

                    mauth.fetchSignInMethodsForEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            boolean check = task.getResult().getSignInMethods().isEmpty();
                            if (check) {
                                signup();
                            } else {
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

                            Toast.makeText(SignupActivity.this, "Failed To check if user exists" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(this, "Fire Base is Not connected", Toast.LENGTH_SHORT).show();
                }
            } else if (email.getText().toString().isEmpty()) {

                Toast.makeText(this, "Please Enter email", Toast.LENGTH_SHORT).show();
                email.requestFocus();
            }
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void signup() {

        try {

            if (!email.getText().toString().isEmpty() && !pass.getText().toString().isEmpty()) {
                if (mauth != null) {

                    mauth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    pgr.setVisibility(View.INVISIBLE);
                                    Toast.makeText(SignupActivity.this, "User Signed Up", Toast.LENGTH_SHORT).show();

                                    if (authResult != null) {
                                        startActivity(new Intent(getBaseContext(), MainActivity.class));
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignupActivity.this, "User not signed up " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {

                }
            } else {
                pgr.setVisibility(View.INVISIBLE);
                btn.setEnabled(true);

                Toast.makeText(this, "Email or password cannot be empty ", Toast.LENGTH_SHORT).show();
                email.requestFocus();
            }
        } catch (Exception e) {
            btn.setEnabled(true);
            pgr.setVisibility(View.INVISIBLE);

            Toast.makeText(this, "Email or password cannot be empty " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    private void google_sign_up() {
        try {
            pgr.setVisibility(View.VISIBLE);
            GoogleSignInAccount cur_user = GoogleSignIn.getLastSignedInAccount(this);
            if (cur_user == null) {
                Intent intent = client.getSignInIntent();
                startActivityForResult(intent, Rec_code);
            } else {
                pgr.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "user Already Signed Up try again ", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, ShowUserData.class));
            }
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Rec_code) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handletask(task);
        }
        else
        {
           callbackManager.onActivityResult(requestCode,resultCode,data);
        }
    }

    private void handletask(Task<GoogleSignInAccount> task) {
        try {
            pgr.setVisibility(View.INVISIBLE);
            GoogleSignInAccount account = task.getResult(ApiException.class);

            Toast.makeText(this, "User Sign Up Successfull " + account.getDisplayName(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getBaseContext(), ShowUserData.class));

        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
