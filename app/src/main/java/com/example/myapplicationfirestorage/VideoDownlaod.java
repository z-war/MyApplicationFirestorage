package com.example.myapplicationfirestorage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.icu.text.LocaleDisplayNames;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class VideoDownlaod extends AppCompatActivity {

    private EditText downloadvideonameET;
    private VideoView downloadedvideoview;
    private Button doenloadvideobtn;
    private FirebaseFirestore db;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_downlaod);
        try{
            downloadvideonameET = findViewById(R.id.downlaod_video_ET);
            downloadedvideoview = findViewById(R.id.downloadVV);
            doenloadvideobtn = findViewById(R.id.videodownloadbtn);
            db = FirebaseFirestore.getInstance();
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.please_wait_layout);
            dialog.setCancelable(false);
            doenloadvideobtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadvideo();
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
    }

    public void downloadvideo()
    {
        if(!downloadvideonameET.getText().toString().isEmpty())
        {
            try {
                dialog.show();
                db.collection("VideoLinks").document(downloadvideonameET.getText().toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists())
                        {
                            String str = documentSnapshot.getString("Url");
                            Uri uri = Uri.parse(str);
                            downloadedvideoview.setVideoURI(uri);

                            downloadedvideoview.requestFocus();
                            downloadedvideoview.start();
                            dialog.dismiss();

                        }else
                        {
                            dialog.dismiss();
                            Toast.makeText(VideoDownlaod.this, "Link Not Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(VideoDownlaod.this, "Error Finding Video Link "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }catch (Exception e)

            {
                Toast.makeText(this, "Error in download video "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else
        {
            Toast.makeText(this, "Please Enter VIDEO NAME", Toast.LENGTH_SHORT).show();
        }
    }
}
