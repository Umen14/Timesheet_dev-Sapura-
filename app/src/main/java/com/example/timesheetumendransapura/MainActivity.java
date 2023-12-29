package com.example.timesheetumendransapura;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    //List all the functions below ones the xml is done
    private RecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    private SearchView searchView;
    private RecyclerViewAdapter timesheetAdapter;
    private Button createButton;
    private Button searchButton;

    private TableRow tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerView);
        timesheetAdapter = new RecyclerViewAdapter(this);
        recyclerView.setAdapter(timesheetAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the button
        createButton = findViewById(R.id.create);
        searchView = findViewById(R.id.editTextSearchHere);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the list based on the search query
                filterTimesheetList(newText);
                return true;
            }
        });

        searchButton = findViewById(R.id.button);
        tableLayout = findViewById(R.id.tablelayout);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tableLayout.getVisibility() == View.GONE) {
                    tableLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    tableLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                }


            }
        });

        // Retrieving  data from Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Timesheet");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<FirebaseAdapter> timesheetList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FirebaseAdapter entry = snapshot.getValue(FirebaseAdapter.class);

                    // Just to make sure its working
                    Log.d("FirebaseData", "Project: " + entry.getProject() + ", Task: " + entry.getTask() + ", Date_From: " + entry.getDate_From() + ", Date_To: " + entry.getDate_To() + ", Status: " + entry.getStatus() + ", Assigned_To: " + entry.getAssigned_To());

                    timesheetList.add(entry);
                }

                // Update the adapter with the new data
                timesheetAdapter.setData(timesheetList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Log.e("FirebaseError", "Error fetching data", databaseError.toException());
            }
        });

        //Create button to testing
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the EditFragment as a dialog
                EditFragment editFragment = new EditFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                editFragment.show(fragmentManager, "EditFragment");
            }
        });



    }

    //To filter the search
    private void filterTimesheetList(String query) {
        List<FirebaseAdapter> filteredList = new ArrayList<>();
        List<FirebaseAdapter> originalList = timesheetAdapter.getOriginalData();

        if (TextUtils.isEmpty(query)) {
            timesheetAdapter.setData(originalList);

            timesheetAdapter.updateDataAndFetchFromFirebase(originalList);
        } else {
            if (originalList != null) {
                for (FirebaseAdapter entry : originalList) {
                    if (containsIgnoreCase(entry.getProject(), query) ||
                            containsIgnoreCase(entry.getTask(), query) ||
                            containsIgnoreCase(entry.getStatus(), query) ||
                            containsIgnoreCase(entry.getDate_From(), query) ||
                            containsIgnoreCase(entry.getDate_To(), query) ||
                            containsIgnoreCase(entry.getAssigned_To(), query)) {
                        filteredList.add(entry);
                    }
                }
            }

            if (filteredList.isEmpty()) {
                // If the filtered list is empty
                showToast("No matching items found");
            }

            // Update the adapter with the filtered data
            timesheetAdapter.setData(filteredList);
        }
    }

    private boolean isMatch(String text, String query) {
        return text != null && text.toLowerCase().equals(query.toLowerCase());
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean containsIgnoreCase(String text, String query) {
        return text != null && text.toLowerCase().contains(query.toLowerCase());
    }


}
