package com.example.myapplicationfirestorage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

public class UploadImage extends AppCompatActivity {
    private Button btnupload;
    private EditText imgnameET;
    private ImageView imgtouploadIV;
    private Dialog dialog;
    public static final int reqcode = 123;
    private Uri objecturi;
    private boolean isimgselected = false;
    private StorageReference objStorageref;
    private FirebaseFirestore objectFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        try {
            btnupload = findViewById(R.id.uploadimgbtn);
            imgnameET = findViewById(R.id.image_nameET);
            imgtouploadIV = findViewById(R.id.imagetouploadIV);
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.please_wait_layout);
            dialog.setCancelable(false);
            objStorageref = FirebaseStorage.getInstance().getReference("MyImages");
            objectFirestore = FirebaseFirestore.getInstance();
            imgtouploadIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectimgfromgallery();
                }
            });
            btnupload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadimagetofirestorage();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    private void selectimgfromgallery() {
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, reqcode);

        } catch (Exception e) {
            Toast.makeText(this, "selectimgfromgallery " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadimagetofirestorage() {
        if (isimgselected && !imgnameET.getText().toString().isEmpty()) {
            dialog.show();
            String imagename = imgnameET.getText().toString() + "." + getextention(objecturi);
            final StorageReference ref = objStorageref.child(imagename);
            UploadTask objectUploadTask = ref.putFile(objecturi);
            objectUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        dialog.dismiss();
                        throw task.getException();
                    }
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        Map<String, Object> objmap = new HashMap<>();
                        objmap.put("url", task.getResult().toString());
                        objectFirestore.collection("links").document(imgnameET.getText().toString()).set(objmap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UploadImage.this, "Image UPloaded Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UploadImage.this, "Image Url Not Stored IN FireStore", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(UploadImage.this, "Fails To Upoad Image", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (!isimgselected) {
            Toast.makeText(this, "Please Select Image", Toast.LENGTH_SHORT).show();
        } else if (imgnameET.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please Enter Image name", Toast.LENGTH_SHORT).show();

        }
    }

    private String getextention(Uri objecturi) {
        try {
            ContentResolver objContentResolver = getContentResolver();
            MimeTypeMap object = MimeTypeMap.getSingleton();

            String ex = object.getExtensionFromMimeType(objContentResolver.getType(objecturi));

            return ex;
        } catch (Exception e) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == reqcode && resultCode == RESULT_OK && data != null) {
            objecturi = data.getData();
            if (objecturi != null) {
                try {
                    Bitmap objectbitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), objecturi);
                    imgtouploadIV.setImageBitmap(objectbitmap);
                    isimgselected = true;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Data is null", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No Image is selected", Toast.LENGTH_SHORT).show();
        }
    }
}
