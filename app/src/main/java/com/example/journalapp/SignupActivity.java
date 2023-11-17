package com.example.journalapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    EditText password_create;
    EditText email_create;
    EditText userName_create;
    Button creteBTN;

    private FirebaseAuth firebaseAuth;
    private  FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Firebase Connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        //Firebase Auth require Google Account to  on the device to run successfully

        password_create = findViewById(R.id.passwordCreate);
        email_create = findViewById(R.id.emailCreate);
        creteBTN = findViewById(R.id.create_button);
        userName_create = findViewById(R.id.userName_create);

        //Authintication
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null){
                    //User Already Logged In

                }else {
                    // No user yet!

                }
            }
        };

        creteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(email_create.getText().toString())
                && !TextUtils.isEmpty(password_create.getText().toString())){
                    String email = email_create.getText().toString().trim();
                    String password = password_create.getText().toString().trim();
                    String userName = userName_create.getText().toString().trim();
                    CreateUserEmailAccount(email,password,userName);
                }else {
                    Toast.makeText(SignupActivity.this,
                            "This Field is Mandatory",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private  void CreateUserEmailAccount(String email,String password,final String userName){
        if (!TextUtils.isEmpty(email_create.getText().toString())
                && !TextUtils.isEmpty(password_create.getText().toString())){

            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                //we take user to Next Activity: (AddJournal)
                                currentUser = firebaseAuth.getCurrentUser();
                                assert  currentUser != null;
                                final String currentUserId = currentUser.getUid();

                                //Create a UserMap so we can create a user in the User Collection in FireBase
                                Map<String, String> userObj = new HashMap<>();
                                userObj.put("userId",currentUserId);
                                userObj.put("userName",userName);

                                //Adding Users to Firestore
                                collectionReference.add(userObj)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                documentReference.get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (Objects.requireNonNull(task.getResult().exists())){
                                                                    String name = task.getResult().getString("userName");

                                                                    // If the user is registered successfully
                                                                    //Move the User to the AddJournal Activity

                                                                    Intent intent = new Intent(SignupActivity.this, AddJournalActivity.class);
                                                                    intent.putExtra("Username",name);
                                                                    intent.putExtra("userId",currentUserId);

                                                                    startActivity(intent);
                                                                }else {

                                                                }
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                //Display Failing Message
                                                                Toast.makeText(SignupActivity.this, "Oops! Something went Wrong", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        });
                            }
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}