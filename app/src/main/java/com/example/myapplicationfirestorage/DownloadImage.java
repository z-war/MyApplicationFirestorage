package com.example.myapplicationfirestorage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DownloadImage extends AppCompatActivity {
    private EditText downimagenameET;
    private ImageView downImgView;
    private Button downbtn;
    private Dialog dialog;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_image);
        try {
            downimagenameET = findViewById(R.id.down_image_nameET);
            downImgView = findViewById(R.id.down_imagetouploadIV);
            downbtn = findViewById(R.id.downimgbtn);
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.please_wait_layout);
            dialog.setCancelable(false);
            db = FirebaseFirestore.getInstance();
            downbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    download();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void download() {
        try {
            if (!downimagenameET.getText().toString().isEmpty()) {

                dialog.show();
                db.collection("links").document(downimagenameET.getText().toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            dialog.dismiss();
                            String url = documentSnapshot.getString("Url");
                            Toast.makeText(DownloadImage.this, "Image Found Downloading"+url, Toast.LENGTH_LONG).show();
                            Glide.with(getApplicationContext()).load(url).into(downImgView);
                            Toast.makeText(DownloadImage.this, "Glide is Loading ", Toast.LENGTH_SHORT).show();

                        } else {
                            dialog.dismiss();
                            Toast.makeText(DownloadImage.this, "No document found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(DownloadImage.this, "Error in Downloading", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Please Enter Document Name", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            dialog.dismiss();
            Toast.makeText(this, "Error in downlaoding " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
