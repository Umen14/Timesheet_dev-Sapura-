package com.example.timesheetumendransapura;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timesheetumendransapura.FirebaseAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<FirebaseAdapter> timesheetList;

    private Context context;
    private RecyclerViewAdapter adapter;



    public RecyclerViewAdapter(Context context) {

        this.timesheetList = new ArrayList<>();
        this.context = context;
        this.adapter = this;



    }
    public List<FirebaseAdapter> getOriginalData() {
        return timesheetList;
    }


    private int selectedPosition = RecyclerView.NO_POSITION;

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }
    // ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewProject;
        TextView textViewTask;
        TextView textViewDate_From;
        TextView textViewDate_To;
        TextView textViewStatus;
        TextView textViewAssign_To;
        ImageView deleteIcon;

        ImageView editIcon;

        public FirebaseAdapter getItem() {
            return timesheetList.get(getAdapterPosition());
        }
        void bindData(FirebaseAdapter entry) {
            // Bind data to your item views based on the position
            textViewProject.setText(entry.getProject());
            textViewTask.setText(entry.getTask());
            textViewAssign_To.setText(entry.getAssigned_To());
            textViewDate_From.setText(entry.getDate_From());
            textViewDate_To.setText(entry.getDate_To());
            textViewStatus.setText(entry.getStatus());
        }
        ViewHolder(View itemView) {
            super(itemView);
            // Initialize your item views here
            textViewProject = itemView.findViewById(R.id.textViewProject);
            textViewTask = itemView.findViewById(R.id.textViewTask);
            textViewDate_From = itemView.findViewById(R.id.textViewDateFrom);
            textViewDate_To = itemView.findViewById(R.id.textViewDateTo);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewAssign_To = itemView.findViewById(R.id.textViewAssign);

            // Initialize the delete icon
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call the non-static method using the adapter instance
                    adapter.deleteItemAndUpdateFirebase(getAdapterPosition());
                }
            });

            editIcon = itemView.findViewById(R.id.editIcon);

        }


    }


    // Method to update data
    public void setData(List<FirebaseAdapter> timesheetList) {
        this.timesheetList = timesheetList;
        notifyDataSetChanged();
        //fetchDataFromFirebase();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your item layout and create a ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timesheet, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to your item views based on the position
        FirebaseAdapter entry = timesheetList.get(position);



        holder.textViewProject.setText(entry.getProject());
        holder.textViewTask.setText(entry.getTask());
        holder.textViewAssign_To.setText(entry.getAssigned_To());
        holder.textViewDate_From.setText(entry.getDate_From());
        holder.textViewDate_To.setText(entry.getDate_To());
        holder.textViewStatus.setText(entry.getStatus());

        // Highlight the selected item
        holder.itemView.setActivated(position == selectedPosition);


        // Set click listener for the delete icon
        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedPosition(holder.getAdapterPosition());
                showDeleteConfirmationDialog(holder.getAdapterPosition(), context);
            }
        });

        // Set click listener for the edit icon
        holder.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the edit dialog
                showEditDialog(entry);
            }
        });


    }

    private void showEditDialog(FirebaseAdapter entry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.edit, null);

        // Initialize the views in the dialog layout
        EditText taskEditText = view.findViewById(R.id.eeditTextText2);
        EditText editTextTask = view.findViewById(R.id.etask);
        EditText editTextStartDate = view.findViewById(R.id.estartDateEditText);
        EditText editTextEndDate = view.findViewById(R.id.eendDateEditText);
        Spinner spinnerStatus = view.findViewById(R.id.esstatus);
        Spinner spinnerAssignTo = view.findViewById(R.id.esassignto);


        // Set initial values from the selected entry
        taskEditText.setText(entry.getProject());
        editTextTask.setText(entry.getTask());
        editTextStartDate.setText(entry.getDate_From());
        editTextEndDate.setText(entry.getDate_To());

        int statusPosition = getStatusPosition(entry.getStatus());
        spinnerStatus.setSelection(statusPosition);

        // Set up DatePickerDialog for the Date_From
        editTextStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(editTextStartDate);
            }
        });

        // Set up DatePickerDialog for the Date_To
        editTextEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(editTextEndDate);
            }
        });

        // Set click listener for the Save button in the dialog
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Update the selected entry with the edited values
                entry.setProject(editTextTask.getText().toString());
                entry.setTask(taskEditText.getText().toString());
                entry.setDate_From(editTextStartDate.getText().toString());
                entry.setDate_To(editTextEndDate.getText().toString());
                entry.setStatus(spinnerStatus.getSelectedItem().toString());
                entry.setAssigned_To(spinnerAssignTo.getSelectedItem().toString());


                Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show();
                // Save the changes to Firebase
                updateFirebaseEntry(entry);

                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        // Set click listener for the Cancel button in the dialog
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog without saving changes
                dialog.dismiss();
            }
        });
        retrieveAssignedToValues(spinnerAssignTo);
        builder.setView(view);
        builder.create().show();
    }

    private int getStatusPosition(String status) {
        String[] statuses = context.getResources().getStringArray(R.array.status_array);

        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equals(status)) {
                return i;
            }
        }

        return 0;
    }

    private void showDatePickerDialog(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context, // or requireContext() if you are in a Fragment
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int dayOfMonth, int month, int year) {
                        // Update the EditText with the selected date
                        editText.setText(String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth));
                    }
                },
                year, month, day);

        datePickerDialog.show();
    }
    private void retrieveAssignedToValues(Spinner assignToSpinner) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Timesheet");

        // Use a ValueEventListener to fetch the data
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> assignedToList = new ArrayList<>();

                // Iterate through the data to extract unique "AssignedTo" values
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FirebaseAdapter firebaseAdapter = snapshot.getValue(FirebaseAdapter.class);
                    if (firebaseAdapter != null) {
                        String assignedTo = firebaseAdapter.getAssigned_To();
                        if (assignedTo != null && !assignedTo.isEmpty() && !assignedToList.contains(assignedTo)) {
                            assignedToList.add(assignedTo);
                        }
                    }
                }

                // Populate the spinner with the unique "AssignedTo" values
                populateAssignedToSpinner(assignedToList, assignToSpinner);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Do if got time for toast
            }
        });
    }

    private void populateAssignedToSpinner(List<String> assignedToList, Spinner assignToSpinner) {
        // Set up the adapter for the "Assigned To" spinner
        ArrayAdapter<String> assignedToAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, assignedToList);
        assignedToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assignToSpinner.setAdapter(assignedToAdapter);
    }

    public void updateDataAndFetchFromFirebase(List<FirebaseAdapter> timesheetList) {
        setData(timesheetList);
        fetchDataFromFirebase();
    }
    private void fetchDataFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Timesheet");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                timesheetList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FirebaseAdapter entry = snapshot.getValue(FirebaseAdapter.class);

                    entry.setKey(snapshot.getKey());

                    timesheetList.add(entry);
                }
                //Check here if the data is not fetching
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors, if any
            }
        });
    }


    private void updateFirebaseEntry(FirebaseAdapter entry) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Timesheet");

        String entryId = entry.getKey();


        if (entryId != null && !entryId.isEmpty()) {

            DatabaseReference entryReference = databaseReference.child(entryId);

            // Update the fields
            Map<String, Object> updates = new HashMap<>();
            updates.put("Project", entry.getProject());
            updates.put("Task", entry.getTask());
            updates.put("Date_From", entry.getDate_From());
            updates.put("Date_To", entry.getDate_To());
            updates.put("Status", entry.getStatus());
            updates.put("Assigned_To", entry.getAssigned_To());

            // Use updateChildren to update only specific fields
            entryReference.updateChildren(updates);
        } else {
            // Check for error just in case
            Log.e("UpdateEntry", "Invalid entryId");
        }
    }


    public  void showDeleteConfirmationDialog(final int position, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to delete this item?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete the item and update Firebase
                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                deleteItemAndUpdateFirebase(position);
            }

        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing or add any additional logic
            }
        });
        builder.show();
    }


    public void deleteItemAndUpdateFirebase(int position) {
        // Get the item to be deleted
        FirebaseAdapter firebaseAdapter = timesheetList.get(position);

        // Delete the item directly from Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Timesheet");

        // Use a query to find the item with the specified properties
        databaseReference
                .orderByChild("Assigned_To").equalTo(firebaseAdapter.getAssigned_To())
                .limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue();  // Remove the item from Firebase
                        }

                        // Remove the item from the local list
                        timesheetList.remove(position);

                        // Notify the adapter about the data change
                        notifyItemRemoved(position);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                    }
                });
    }

    @Override
    public int getItemCount() {
        return timesheetList.size();
    }
}
