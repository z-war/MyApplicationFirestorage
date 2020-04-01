package com.example.myapplicationfirestorage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {

            db = FirebaseFirestore.getInstance();

        } catch (Exception e) {
            Toast.makeText(this, "Error Initializing "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void toupload(View v)
    {
        Intent intent  = new Intent(getBaseContext(),UploadImage.class);
        startActivity(intent);
    }
    public void touploadVideo(View v)
    {
        Intent intent  = new Intent(getBaseContext(),VideoUpload.class);
        startActivity(intent);
    }
    public void todownloadVideo(View v)
    {
        Intent intent  = new Intent(getBaseContext(),VideoDownlaod.class);
        startActivity(intent);
    }
    public void todownload(View v)
    {
        Intent intent  = new Intent(getBaseContext(),DownloadImage.class);
        startActivity(intent);
    }
    public void tosignup(View v)
    {
        startActivity(new Intent(getBaseContext(),SignupActivity.class));
    }

    public void tosignin(View v)
    {
        startActivity(new Intent(getBaseContext(),LoginActivty.class));
    }
    public void adduser(View v) {
        try {

            Map<String, Object> datamap = new HashMap<>();
            datamap.put("address", "Lahore");
            db.collection("Bookings").document("Booking1").set(datamap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(MainActivity.this, "Data Added Successfully", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Data Not Added Successfully" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error in adding user to firebase " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
