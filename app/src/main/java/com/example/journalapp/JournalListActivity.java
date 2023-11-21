package com.example.journalapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.journalapp.model.Journal;
import com.example.journalapp.ui.JournalRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import util.JournalUser;

public class JournalListActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private StorageReference storageReference;
    private List<Journal> journalList;
    private RecyclerView recyclerView;
    private  JournalRecyclerAdapter journalRecyclerAdapter;

    private CollectionReference collectionReference = db.collection("Journal");
    private TextView noPostEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        //Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        //Widgets
        noPostEntry = findViewById(R.id.list_no_posts);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Post Arraylist
        journalList = new ArrayList<>();

    }
    //Adding the Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.action_add) {
            // Going to Add Post Activity
            if (user != null && firebaseAuth != null) {
                startActivity(new Intent(
                        JournalListActivity.this,
                        AddJournalActivity.class
                ));
            }
        }

           if (item.getItemId() == R.id.sign_out){
                //Signing Out the User
                if (user != null && firebaseAuth != null){
                    firebaseAuth.signOut();

                    startActivity(new Intent(
                            JournalListActivity.this,
                            MainActivity.class
                    ));
                }
        }
        return super.onOptionsItemSelected(item);
    }
    //Getting All posts


    @Override
    protected void onStart() {
        super.onStart();

        collectionReference.whereEqualTo("userId",
                JournalUser.getInstance().getUserId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()){
                            for (QueryDocumentSnapshot journals : queryDocumentSnapshots){
                                Journal journal = journals.toObject(Journal.class);
                                journalList.add(journal);
                            }
                            //RecyclerView
                            journalRecyclerAdapter = new JournalRecyclerAdapter(
                                    JournalListActivity.this, journalList);
                            recyclerView.setAdapter(journalRecyclerAdapter);
                            journalRecyclerAdapter.notifyDataSetChanged();
                        }else {
                            noPostEntry.setVisibility(View.INVISIBLE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(JournalListActivity.this, "Oops! Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}