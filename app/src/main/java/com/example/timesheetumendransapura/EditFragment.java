package com.example.timesheetumendransapura;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
//This is from the Timesheetdata to get the create button function
public class EditFragment extends DialogFragment {

    private EditText projectEditText, taskEditText, startDateEditText, endDateEditText;
    private Spinner statusSpinner, assignToSpinner;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.selection, null);
        builder.setView(view);

        retrieveAssignedToValues();
        projectEditText = view.findViewById(R.id.editTextText2);
        taskEditText = view.findViewById(R.id.task);
        startDateEditText = view.findViewById(R.id.startDateEditText);
        endDateEditText = view.findViewById(R.id.endDateEditText);
        statusSpinner = view.findViewById(R.id.sstatus);
        assignToSpinner = view.findViewById(R.id.sassignto);

        Button saveButton = view.findViewById(R.id.saveButton);
        Button closeButton = view.findViewById(R.id.closeButton);

        // Set up DatePickerDialog for the Date_From
        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(startDateEditText);
            }
        });

        // Set up DatePickerDialog for the Date_To
        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(endDateEditText);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToFirebase();
                dismiss(); // Close the dialog
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Close the dialog
            }
        });

        return builder.create();
    }

    private void showDatePickerDialog(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
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

    private void retrieveAssignedToValues() {
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
                populateAssignedToSpinner(assignedToList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Make a toast if got time
            }
        });
    }

    private void populateAssignedToSpinner(List<String> assignedToList) {
        // Set up the adapter for the "Assigned To" spinner
        ArrayAdapter<String> assignedToAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, assignedToList);
        assignedToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assignToSpinner.setAdapter(assignedToAdapter);
    }


    private void saveDataToFirebase() {
        // Get data from UI components
        String Project = projectEditText.getText().toString();
        String Task = taskEditText.getText().toString();
        String Date_From = startDateEditText.getText().toString();
        String Date_To = endDateEditText.getText().toString();
        String Status = statusSpinner.getSelectedItem().toString();
        String Assigned_To = assignToSpinner.getSelectedItem().toString();

        // Create TimesheetData object
        TimesheetData timesheetData = new TimesheetData();
        timesheetData.setProject(Project);
        timesheetData.setTask(Task);
        timesheetData.setDateFrom(Date_From);
        timesheetData.setDateTo(Date_To);
        timesheetData.setStatus(Status);
        timesheetData.setAssignedTo(Assigned_To);

        // Save to Firebase
        saveToFirebase(timesheetData);
    }

    private void saveToFirebase(TimesheetData timesheetData) {
        // Firebase Database reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Timesheet");

        // Get key is the same as Firebase and also the Timesheet
        String entryKey = databaseReference.push().getKey();

        // Save the data to Firebase under the generated key
        databaseReference.child(entryKey).setValue(timesheetData);
    }
}
