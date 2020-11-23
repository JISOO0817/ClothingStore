package com.example.clothingstore.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.clothingstore.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class WriteReviewActivity extends AppCompatActivity {

    private String marketUid;
    private CircularImageView marketIv;
    private TextView marketNameTv;
    private RatingBar ratingBar;
    private EditText reviewEt;
    private Button saveBtn;
    private ImageButton backBtn;
    private ImageView reviewImage;
    private FirebaseAuth auth;


    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    private String[] cameraPermissions;
    private String[] storagePermissions;

    private Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        marketUid = getIntent().getStringExtra("marketUid");

        auth = FirebaseAuth.getInstance();
        loadMarkeyInfo();
        loadMyReview();  //해당 마켓에 리뷰를 단 적이 있다면 가져옴


        marketIv = findViewById(R.id.marketIv);
        marketNameTv = findViewById(R.id.marketNameTv);
        ratingBar =findViewById(R.id.ratingBar);
        reviewEt = findViewById(R.id.reviewEt);
        saveBtn = findViewById(R.id.saveBtn);
        backBtn = findViewById(R.id.backBtn);
        reviewImage = findViewById(R.id.reviewImageIv);

        cameraPermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });

        reviewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                showImageDialog();
            }
        });


    }

    private void showImageDialog() {

        String[] options = {"카메라로 찍기","앨범에서 가져오기"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("이미지 가져오기")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0) {

                            if (checkCameraPermission()) {
                                pickFromCamera();
                            } else {
                                requestCameraPermission();
                            }
                        }else{
                            if(checkStoragePermission()){
                                pickFromGallery();
                            }else{
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }
    private void pickFromGallery(){

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera(){

        //높은 퀄리티 이미지를 픽하기 위한 미디어스토어 이용
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image_Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Image_Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
                ==(PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);

        return result1 && result2;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE);
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

        if(resultCode == RESULT_OK){

            if(requestCode == IMAGE_PICK_GALLERY_CODE){

                image_uri = data.getData();
                reviewImage.setImageURI(image_uri);
            }else if(requestCode == IMAGE_PICK_CAMERA_CODE){

                reviewImage.setImageURI(image_uri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);




    }


    private void loadMarkeyInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(marketUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String marketName = ""+snapshot.child("marketName").getValue();
                String marketImage = ""+snapshot.child("profileImage").getValue();

                marketNameTv.setText(marketName);

                try{
                    Picasso.get().load(marketImage).placeholder(R.drawable.ic_store_gray).into(marketIv);
                }catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadMyReview() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(marketUid).child("Ratings").child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //리뷰를 이미 작성했다면
                        if(snapshot.exists()){

                            String uid = ""+snapshot.child("uid").getValue();
                            String rating = ""+snapshot.child("rating").getValue();
                            String review = ""+snapshot.child("review").getValue();
                            String timestamp = ""+snapshot.child("timestamp").getValue();
                            String image = ""+snapshot.child("image").getValue();

                            float myRating = Float.parseFloat(rating);
                            ratingBar.setRating(myRating);
                            reviewEt.setText(review);

                            try{
                                Picasso.get().load(image).placeholder(R.drawable.ic_photo).into(reviewImage);
                            }catch (Exception e){}
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void inputData() {
        final  String rating = ""+ratingBar.getRating();
        final String review = reviewEt.getText().toString().trim();
        final String timestamp = ""+System.currentTimeMillis();

        String filePathAndName = "review_images/" + ""+timestamp;


        if(image_uri == null){
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid",""+auth.getUid());
            hashMap.put("rating",rating);
            hashMap.put("review",review);
            hashMap.put("timestamp",timestamp);
            hashMap.put("image","");

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(marketUid).child("Ratings").child(auth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(WriteReviewActivity.this, "리뷰가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(WriteReviewActivity.this, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                }
            });


        }else{

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if(uriTask.isSuccessful()){

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid",""+auth.getUid());
                                hashMap.put("rating",rating);
                                hashMap.put("review",review);
                                hashMap.put("timestamp",timestamp);
                                hashMap.put("image",""+downloadImageUri);


                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(marketUid).child("Ratings").child(auth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(WriteReviewActivity.this, "리뷰가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(WriteReviewActivity.this, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }
                        }
                    });

        }





    }
}