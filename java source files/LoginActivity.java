package app.my.otpverification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    String name;
    String email;
    String phone;
    String password;
    String etpassword;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = FirebaseFirestore.getInstance();
        phone = getIntent().getStringExtra("phone");

        db.collection("users")
                .whereEqualTo("phone",phone)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                name = document.getString("name");
                                email = document.getString("email");
                                password = document.getString("password");
                            }
                        } else
                            Log.w("error", "Error getting documents.", task.getException());
                    }
                });
    }

    public void login(View view) {
        etpassword = ((TextInputEditText)findViewById(R.id.etpassword)).getText().toString().trim();
        if(!password.equals(etpassword))
            Toast.makeText(LoginActivity.this,"Incorrect Password!!",Toast.LENGTH_SHORT).show();
        else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("phone", phone);
            startActivity(intent);
        }
    }
}