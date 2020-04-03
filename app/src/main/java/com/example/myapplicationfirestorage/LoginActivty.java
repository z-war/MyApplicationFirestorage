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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivty extends AppCompatActivity {
    private static final int RC_SIGN_IN = 2;
    private EditText email , pass;
    private ProgressBar pgr ;
    private FirebaseAuth mauth;
    Button btn;
    GoogleSignInOptions gso;
    GoogleSignInClient client;
    SignInButton btn_google_sign_in;
    LoginButton fb_btn_login;
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    TextView FacebookDataTextView;
    private static final String EMAIL = "email";
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
//            FacebookSdk.sdkInitialize(this);
//            AppEventsLogger.activateApp(this);

            callbackManager  = CallbackManager.Factory.create();
            FacebookDataTextView = (TextView)findViewById(R.id.TextView1);

            fb_btn_login = findViewById(R.id.sign_in_btn_fb);
            fb_btn_login.setReadPermissions(Arrays.asList(EMAIL));

            if(AccessToken.getCurrentAccessToken()!=null)
            {
                GraphLoginRequest(AccessToken.getCurrentAccessToken());
                Toast.makeText(this, "Facebook User Already Logged In", Toast.LENGTH_SHORT).show();
            }else
            {
            }

            accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                    if(currentAccessToken==null)
                    {
                        FacebookDataTextView.setText("");
                    }
                }
            };

            fb_btn_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    GraphLoginRequest(AccessToken.getCurrentAccessToken());
                    Toast.makeText(LoginActivty.this, "Login SuccessFull", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(LoginActivty.this, "Login Cancelled", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException error) {
                    Toast.makeText(LoginActivty.this, "Login Failed + "+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }catch (Exception e)
        {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void GraphLoginRequest(AccessToken accessToken) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {

                        try {

                            // Adding all user info one by one into TextView.
                            FacebookDataTextView.setText("ID: " + jsonObject.getString("id"));

                            FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nName : " + jsonObject.getString("name"));

                            FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nFirst name : " + jsonObject.getString("first_name"));

                            FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nLast name : " + jsonObject.getString("last_name"));

                            FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nEmail : " + jsonObject.getString("email"));

                            FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nGender : " + jsonObject.getString("gender"));

                            FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nLink : " + jsonObject.getString("link"));

                            FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nTime zone : " + jsonObject.getString("timezone"));

                            FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nLocale : " + jsonObject.getString("locale"));

                            FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nUpdated time : " + jsonObject.getString("updated_time"));

                            FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nVerified : " + jsonObject.getString("verified"));
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle bundle = new Bundle();
        bundle.putString(
                "fields",
                "id,name,link,email,gender,last_name,first_name,locale,timezone,updated_time,verified"
        );
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();
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
        else {
            callbackManager.onActivityResult(requestCode, resultCode, data);

            Toast.makeText(this, "User Logged In Succesfully ", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSignIntask(Task<GoogleSignInAccount> task) {
        try {
            pgr.setVisibility(View.INVISIBLE);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Map<String,Object> mydatamap = new HashMap<>();

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
