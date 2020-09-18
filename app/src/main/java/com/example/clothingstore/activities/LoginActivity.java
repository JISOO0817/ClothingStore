package com.example.clothingstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clothingstore.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {


    private EditText emailEt,passwordEt;
    private TextView forgotTv,noAccountTv;
    private Button loginBtn;

    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        forgotTv = findViewById(R.id.forgotTv);
        noAccountTv = findViewById(R.id.noAccountTv);
        loginBtn = findViewById(R.id.loginBtn);

        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("잠시만 기다려 주세요...");

        noAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               chooseActivity();
                //startActivity(new Intent(LoginActivity.this,RegisterUserActivity.class));
            }
        });

        forgotTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private String email,password;
    private void loginUser() {

        email = emailEt.getText().toString().trim();
        password = passwordEt.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "유효한 이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password) || TextUtils.isEmpty(email)){
            Toast.makeText(this, "아이디 혹은 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
        }
        progressDialog.setMessage("로그인 중입니다...");
        progressDialog.show();

        auth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        makeMeOnline();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void makeMeOnline() {

        progressDialog.setMessage("계정 체크 중입니다...");

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("online","true");

        //데이터 베이스 업데이트

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(auth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //update successfully

                        checkUserType();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void checkUserType() {

        //addListenerForSingleValueEvnet (한 번만 호출되고 즉시 삭제)

        //판매자면 판매자 스크린, 구매자면 구매자 스크린
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("uid").equalTo(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String accountType = ""+ds.child("accountType").getValue();
                            if(accountType.equals("Seller")){
                                progressDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this, SellerActivity.class));
                                finish();
                            }else{
                                progressDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this, UserActivity.class));
                                finish();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void chooseActivity() {
        String[] options = {"구매자","판매자"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("회원가입")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0){

                            startActivity(new Intent(LoginActivity.this, RegisterUserActivity.class));
                        }else{
                            startActivity(new Intent(LoginActivity.this, RegisterSellerActivity.class));
                        }
                    }
                }).show();

    }


    @Override
    public void onBackPressed() {
        
    }
}