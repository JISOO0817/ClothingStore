package com.example.clothingstore.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.clothingstore.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileEditUserActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private Button updateBtn,searchBtn;
    private ImageView profileIv;
    private EditText nameEt,phoneEt,addressEt,addressEt2;


    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    private String[] cameraPermission;
    private String[] storagePermission;

    private ProgressDialog progressDialog;

    private Uri image_url;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit_user);

        backBtn = findViewById(R.id.backBtn);
        nameEt = findViewById(R.id.nameEt);
        phoneEt = findViewById(R.id.phoneEt);
        addressEt = findViewById(R.id.addressEt);
        addressEt2 = findViewById(R.id.addressEt2);
        updateBtn = findViewById(R.id.updateBtn);
        searchBtn = findViewById(R.id.searchBtn);
        profileIv = findViewById(R.id.profileIv);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("잠시만 기다려 주세요...");
        progressDialog.setCanceledOnTouchOutside(false);

        auth = FirebaseAuth.getInstance();
        checkUser();


        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private String name, phone, address1,address2;
    private boolean marketOpen;
    private void inputData() {
        name = nameEt.getText().toString().trim();
        phone = phoneEt.getText().toString().trim();
        address1 = addressEt.getText().toString().trim();
        address2 = addressEt2.getText().toString().trim();

        updateProfile();
    }

    private void updateProfile() {
        progressDialog.setMessage("프로필 업데이트 중입니다...");
        progressDialog.show();

        if(image_url == null){

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("name",""+name);
            hashMap.put("phone",""+phone);
            hashMap.put("address1",""+address1);
            hashMap.put("address2",""+address2);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(auth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileEditUserActivity.this, "프로필 업데이트 중입니다...", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileEditUserActivity.this, "프로필 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

        }else{

            String filePathAndName = "profile_images/" + ""+auth.getUid();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_url)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if( uriTask.isSuccessful()){
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("name",""+name);
                                hashMap.put("phone",""+phone);
                                hashMap.put("address1",""+address1);
                                hashMap.put("address2",""+address2);
                                hashMap.put("profileImage",""+downloadImageUri);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(auth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                Toast.makeText(ProfileEditUserActivity.this, "프로필 업데이트를 완료하였습니다...", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ProfileEditUserActivity.this, "프로필 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        }
    }


    private void checkUser() {
        FirebaseUser user = auth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(ProfileEditUserActivity.this, LoginActivity.class));
            finish();
        }else{
            loadMyInfo();
        }
    }

    private void loadMyInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            String accountType=""+ds.child("accountType").getValue();
                            String address1 = ""+ds.child("address1").getValue();
                            String address2 = ""+ds.child("address2").getValue();
                            String email = ""+ds.child("email").getValue();
                            String name = ""+ds.child("name").getValue();
                            String online = ""+ds.child("online").getValue();
                            String phone = ""+ds.child("phone").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();
                            String timestamp = ""+ds.child("timestamp").getValue();
                            String uid = ""+ds.child("uid").getValue();

                            nameEt.setText(name);
                            phoneEt.setText(phone);
                            addressEt.setText(address1);
                            addressEt2.setText(address2);


                            try{
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_store_gray).into(profileIv);
                            }catch (Exception e){
                                profileIv.setImageResource(R.drawable.ic_person_gray);
                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void showImagePickDialog() {

        String[] options = {"카메라로 찍기","앨범에서 가져오기"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("이미지 가져오기")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(i == 0){

                            if(checkCameraPermission()){
                                pickFromCamera();
                            }
                            else{
                                requestCameraPermission();
                            }
                        }else{

                            if(checkStoragePermission()){
                                pickFromGallery();
                            }
                            else{
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();

    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                storagePermission,STORAGE_REQUEST_CODE);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                cameraPermission,CAMERA_REQUEST_CODE);
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_Image");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Image Description");

        image_url = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_url);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)  ==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        switch (requestCode){

            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted){
                        pickFromCamera();
                    }else{
                        Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        pickFromGallery();
                    }else{
                        Toast.makeText(this, "갤러리 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100){
            if(resultCode == RESULT_OK){

                String dt = data.getStringExtra("key");
                addressEt.setText(dt);

            }
        }else if(requestCode == IMAGE_PICK_GALLERY_CODE){
            image_url = data.getData();
            profileIv.setImageURI(image_url);
        }else if(requestCode == IMAGE_PICK_CAMERA_CODE){
            profileIv.setImageURI(image_url);
        }


    }
}