package com.example.myapplicationfirestorage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class VideoUpload extends AppCompatActivity {
    private EditText videonameET;
    private VideoView videoView;
    private Button uploadBtn;
    private StorageReference firebaseStorage;
    private FirebaseFirestore db;
    private Uri objecturi;
    public static final int reqcodeVIdeo = 2;
    private boolean isvideoselected = false;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);
        try {
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.please_wait_layout);
            dialog.setCancelable(false);
            videonameET = findViewById(R.id.videonameET);
            videoView = findViewById(R.id.videoview);
            uploadBtn = findViewById(R.id.videouploadbtn);
            firebaseStorage = FirebaseStorage.getInstance().getReference("Videos");
            db = FirebaseFirestore.getInstance();
            videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectvideofromgallery();
                }
            });

            uploadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadvideo();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void selectvideofromgallery() {
        try {
            dialog.show();
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, reqcodeVIdeo);
        } catch (Exception w) {
            Toast.makeText(this, "Selecting video from gallery " + w.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == reqcodeVIdeo && resultCode == RESULT_OK && data != null) {
            objecturi = data.getData();
            if (objecturi != null) {
                try {
                    dialog.dismiss();
                    MediaController mc =new MediaController(this);
                    videoView.setMediaController(mc);
                    videoView.setVideoURI(objecturi);
                    videoView.requestFocus();
                    videoView.start();
                    isvideoselected = true;

                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            } else {
                    dialog.dismiss();
                Toast.makeText( this, "Video Not Seleted", Toast.LENGTH_SHORT).show();

            }

        } else {
            Toast.makeText(this, "Video Not Seleted", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadvideo()
    {
        try {

            if(isvideoselected && !videonameET.getText().toString().isEmpty())
            {
                dialog.show();

                    final String filename  = videonameET.getText().toString() +"."+getextention(objecturi);
                    final StorageReference fileref = firebaseStorage.child(filename);
                UploadTask uploadTask = fileref.putFile(objecturi);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful())
                        {
                            dialog.dismiss();
                            throw task.getException();

                        }
                        return fileref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        dialog.dismiss();
                        if(task.isSuccessful())
                        {
                            Map<String , Object> datamap = new HashMap<>();
                            datamap.put("Url" , task.getResult().toString());
                            db.collection("VideoLinks").document(videonameET.getText().toString()).set(datamap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(VideoUpload.this, "Video Uploaded Succesfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(VideoUpload.this, "Video Not uploaded Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else
                        {

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(VideoUpload.this, "Video Not Uploaded Successfully"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
            else if(!isvideoselected){
                Toast.makeText(this, "Please Select Video", Toast.LENGTH_SHORT).show();
            }else if(videonameET.getText().toString().isEmpty())
            {
                Toast.makeText(this, "Please Enter Video Name Video", Toast.LENGTH_SHORT).show();

            }

        }catch (Exception e)
        {
            Toast.makeText(this, "Uploading Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getextention(Uri objecturi) {
            try {
                ContentResolver contentResolver = getContentResolver();
                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                String exten =  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(objecturi));
                return exten;
            }catch (Exception e)
            {
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return null;
    }
}
