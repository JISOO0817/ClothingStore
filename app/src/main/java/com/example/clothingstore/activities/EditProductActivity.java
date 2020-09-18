package com.example.clothingstore.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clothingstore.Constants;
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

public class EditProductActivity extends AppCompatActivity {

    private String productId;
    private ImageButton backBtn;
    private ImageView productIconIv;
    private EditText titleEt,descriptionEt,quantityEt,priceEt,discountedPriceEt,discountedNoteEt;
    private TextView categoryTv;
    private Button editProductBtn;
    private SwitchCompat discountSwitch;


    //퍼미션
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    //이미지 선택
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    //퍼미션 arrays
    private String[] cameraPermissions;
    private String[] storagePermissions;

    //uri
    private Uri image_uri;

    private FirebaseAuth auth;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        productId = getIntent().getStringExtra("productId");
        backBtn = findViewById(R.id.backBtn);
        productIconIv = findViewById(R.id.productIconIv);
        descriptionEt = findViewById(R.id.descriptionEt);
        titleEt = findViewById(R.id.titleEt);
        quantityEt = findViewById(R.id.quantityEt);
        discountedPriceEt = findViewById(R.id.discountedPriceEt);
        discountedNoteEt = findViewById(R.id.discountedNoteEt);
        categoryTv = findViewById(R.id.categoryTv);
        editProductBtn = findViewById(R.id.editProductBtn);
        discountSwitch = findViewById(R.id.discountSwitch);
        priceEt = findViewById(R.id.priceEt);


        discountedPriceEt.setVisibility(View.GONE);
        discountedNoteEt.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();
        loadProductDetail();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("잠시만 기다려주세요...");
        progressDialog.setCanceledOnTouchOutside(false);


        //퍼미션 arrays 초기화

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        discountSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    discountedPriceEt.setVisibility(View.VISIBLE);
                    discountedNoteEt.setVisibility(View.VISIBLE);
                }else{

                    discountedPriceEt.setVisibility(View.GONE);
                    discountedNoteEt.setVisibility(View.GONE);
                }
            }
        });

        productIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이미지 선택 다이얼로그
                showImagePickDialog();

            }
        });

        categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //카테고리 선택

                categoryDialog();
            }
        });

        editProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
    private void loadProductDetail() {
        DatabaseReference ref  = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(auth.getUid()).child("Products").child(productId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String productId = ""+snapshot.child("productId").getValue();
                        String productTitle = ""+snapshot.child("productTitle").getValue();
                        String productDescription = ""+snapshot.child("productDescription").getValue();
                        String productCategory = ""+snapshot.child("productCategory").getValue();
                        String productQuantity = ""+snapshot.child("productQuantity").getValue();
                        String productIcon = ""+snapshot.child("productIcon").getValue();
                        String originalPrice = ""+snapshot.child("originalPrice").getValue();
                        String discountPrice = ""+snapshot.child("discountPrice").getValue();
                        String discountNote = ""+snapshot.child("discountNote").getValue();
                        String discountAvailable = ""+snapshot.child("discountAvailable").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();
                        String uid = ""+snapshot.child("uid").getValue();


                        if(discountAvailable.equals("true")){
                            discountSwitch.setChecked(true);

                            discountedPriceEt.setVisibility(View.VISIBLE);
                            discountedNoteEt.setVisibility(View.VISIBLE);
                        }else{

                            discountedPriceEt.setVisibility(View.GONE);
                            discountedNoteEt.setVisibility(View.GONE);
                        }

                        titleEt.setText(productTitle);
                        quantityEt.setText(productQuantity);
                        categoryTv.setText(productCategory);
                        discountedNoteEt.setText(discountNote);
                        discountedPriceEt.setText(discountPrice);
                        descriptionEt.setText(productDescription);
                        priceEt.setText(originalPrice);

                        try{

                            Picasso.get().load(productIcon).placeholder(R.drawable.ic_baseline_add_shopping_white).into(productIconIv);;

                        }catch(Exception e){
                            productIconIv.setImageResource(R.drawable.ic_baseline_add_shopping_white);
                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String title,description,category,quantity,price,discountPrice,discountNote;
    private boolean discountAvailable = false;

    private void inputData() {

        title = titleEt.getText().toString().trim();
        description = descriptionEt.getText().toString().trim();
        category = categoryTv.getText().toString().trim();
        quantity = quantityEt.getText().toString().trim();
        discountPrice = discountedPriceEt.getText().toString().trim();
        discountNote = discountedNoteEt.getText().toString().trim();
        price = priceEt.getText().toString().trim();
        discountAvailable = discountSwitch.isChecked();

        if(TextUtils.isEmpty(title)){
            Toast.makeText(this, "상품명을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(description)){
            Toast.makeText(this, "상품 설명을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(price)){
            Toast.makeText(this, "가격을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(quantity)){
            Toast.makeText(this, "재고 수를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(discountAvailable){
            discountPrice = discountedPriceEt.getText().toString().trim();
            discountNote = discountedNoteEt.getText().toString().trim();
            if(TextUtils.isEmpty(discountPrice) || TextUtils.isEmpty(discountNote)){
                Toast.makeText(this, "빈칸을 채워주세요,", Toast.LENGTH_SHORT).show();
                return;
            }
        }else{

            discountPrice = "0";
            discountNote = "";
        }

        updateProduct();



    }

    private void updateProduct() {

        progressDialog.setMessage("업데이트 중입니다.");
        progressDialog.show();

        if(image_uri == null){

            Toast.makeText(this, "상품 이미지를 추가해 주세요.", Toast.LENGTH_SHORT).show();

        }else{

            //이미지 업로드(이미지 이름과 경로 그리고 파이어베이스 스토리지 사용)

            String filePathAndName = "product_images/" + "" + productId;

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // 업로드했던 이미지 url 가져오기
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();
                            if(uriTask.isSuccessful()){
                                HashMap<String,Object> hashMap = new HashMap<>();
                                hashMap.put("productTitle",""+title);
                                hashMap.put("productDescription",""+description);
                                hashMap.put("productCategory",""+category);
                                hashMap.put("productQuantity",""+quantity);
                                hashMap.put("productIcon",""+downloadImageUri);
                                hashMap.put("discountPrice",""+discountPrice);
                                hashMap.put("discountNote",""+discountNote);
                                hashMap.put("originalPrice",""+price);
                                hashMap.put("discountAvailable",""+discountAvailable);


                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(auth.getUid()).child("Products").child(productId)
                                        .updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                progressDialog.dismiss();
                                                Toast.makeText(EditProductActivity.this, "상품을 업데이트 하였습니다.", Toast.LENGTH_SHORT).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        progressDialog.dismiss();
                                        Toast.makeText(EditProductActivity.this, "상품 업데이트에 실패하였습니다.", Toast.LENGTH_SHORT).show();

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

    private void categoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("상품 카테고리")
                .setItems(Constants.categories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Constants 클래스 카테고리 가져오기
                        String category = Constants.categories[which];

                        //카테고리 텍스트뷰에 셋
                        categoryTv.setText(category);

                    }
                })
                .show();
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
                productIconIv.setImageURI(image_uri);
            }else if(requestCode == IMAGE_PICK_CAMERA_CODE){

                productIconIv.setImageURI(image_uri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);




    }

}