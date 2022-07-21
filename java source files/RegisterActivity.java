package app.my.otpverification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private String name;
    private String email;
    private String password;
    private String phone;
    private String confirmpassword;
    ProgressBar progressBar;
    Button verifyEmail;
    TextView update;
    Button register;
    Button refresh;
    Button skip;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        phone = getIntent().getStringExtra("phone");
        progressBar = findViewById(R.id.CheckVerification);
        verifyEmail = findViewById(R.id.verifyEmail);
        update = findViewById(R.id.update);
        register = findViewById(R.id.register);
        refresh = findViewById(R.id.refresh);
        skip = findViewById(R.id.skip);
    }

    public void sendVerificationMail(View view){
        email = ((EditText)findViewById(R.id.email)).getText().toString().trim();
        password = ((EditText)findViewById(R.id.password)).getText().toString().trim();
        verifyEmail.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegisterActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                update.setText("Verification email has been sent. Check your spam inbox in case it is not visible in your inbox");
                                progressBar.setVisibility(View.GONE);
                                refresh.setVisibility(View.VISIBLE);
                            }
                            else{
                                update.setText(task.getException().toString());
                                verifyEmail.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                skip.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
                else{
                    update.setText(task.getException().toString());
                    verifyEmail.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    skip.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void refreshVerificationStatus(View view){
        email = ((EditText)findViewById(R.id.email)).getText().toString().trim();
        password = ((EditText)findViewById(R.id.password)).getText().toString().trim();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email,password);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser.isEmailVerified()){
            update.setText("Email Verified");
            refresh.setVisibility(View.GONE);
            skip.setVisibility(View.VISIBLE);
            register.setVisibility(View.VISIBLE);
        }
        else
            update.setText("Email not verified yet, please check your mail");
    }

    public void register(View view) {
        name = ((EditText)findViewById(R.id.name)).getText().toString().trim();
        email = ((EditText)findViewById(R.id.email)).getText().toString().trim();
        password = ((EditText)findViewById(R.id.password)).getText().toString().trim();
        confirmpassword = ((EditText)findViewById(R.id.confirmpassword)).getText().toString().trim();

        if(!password.equals(confirmpassword))
            Toast.makeText(RegisterActivity.this,"Please enter the same password in both boxes!!",Toast.LENGTH_SHORT).show();
        else{
            db = FirebaseFirestore.getInstance();
            Map<String, Object> user = new HashMap<>();
            user.put("name", name);
            user.put("email", email);
            user.put("password", password);
            user.put("phone", phone);
            user.put("image","");
            db.collection("users")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(RegisterActivity.this,"Registration Successful!!",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this,"Error in registration",Toast.LENGTH_SHORT).show();
                        }
                    });
            Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
            startActivity(intent);

        }
    }
}