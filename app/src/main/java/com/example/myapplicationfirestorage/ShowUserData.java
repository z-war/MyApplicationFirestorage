package com.example.myapplicationfirestorage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class ShowUserData extends AppCompatActivity {
    GoogleSignInOptions gso;
    GoogleSignInAccount clientacount;
    GoogleSignInClient client;
    TextView name, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_data);
        try {
            name = findViewById(R.id.user_name);
            phone = findViewById(R.id.user_phone);
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            client = GoogleSignIn.getClient(this,gso);
            clientacount = GoogleSignIn.getLastSignedInAccount(this);
            name.setText(clientacount.getDisplayName().toString());
            phone.setText(clientacount.getEmail().toString());
        } catch (Exception e) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
    }

    public void signout(View v)
    {
        client.signOut();
        startActivity(new Intent(this , LoginActivty.class));
        Toast.makeText(this, "Login Again To continue ", Toast.LENGTH_SHORT).show();

    }
}
