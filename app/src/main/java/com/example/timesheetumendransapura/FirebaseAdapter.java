package com.example.timesheetumendransapura;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FirebaseAdapter {
    private String Project;
    private String Task;
    private String Date_From;
    private String Date_To;
    private String Status;
    private String Assigned_To;

    private String key;


    public FirebaseAdapter() {
    }

    public String getProject() {
        return Project;
    }

    public String getTask() {
        return Task;
    }

    public String getDate_From() {
        return Date_From;
    }

    public String getDate_To() {
        return Date_To;
    }

    public String getStatus() {
        return Status;
    }

    public String getAssigned_To() {
        return Assigned_To;
    }

    public String getKey() {
        return key;
    }


    public void setKey(String key) {
        this.key = key;
    }

    public void setAssigned_To(String assigned_To) {
        this.Assigned_To = assigned_To;
    }

    public void setProject(String project) {
        this.Project = project;
    }

    public void setStatus(String status) {
        this.Status = status;
    }

    public void setDate_From(String date_from) {
        this.Date_From = date_from;
    }

    public void setDate_To(String date_to) {
        this.Date_To = date_to;
    }

    public void setTask(String task) {
        this.Task = task;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Project", Project);
        result.put("Task", Task);
        result.put("Assigned_To", Assigned_To);
        result.put("Date_From", Date_From);
        result.put("Date_To", Date_To);
        result.put("Status", Status);

        return result;
    }





}
