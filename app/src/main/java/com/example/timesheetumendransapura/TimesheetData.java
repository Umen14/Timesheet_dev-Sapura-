package com.example.timesheetumendransapura;

import com.google.firebase.database.PropertyName;

public class TimesheetData {
    private String Project;
    private String Task;
    private String Date_From;
    private String Date_To;
    private String Status;
    private String Assigned_To;



    public TimesheetData() {

    }

    @PropertyName("Project")
    public String getProject() {
        return Project;
    }

    @PropertyName("Project")
    public void setProject(String project) {
        this.Project = project;
    }

    @PropertyName("Task")
    public String getTask() {
        return Task;
    }

    @PropertyName("Task")
    public void setTask(String task) {
        this.Task = task;
    }

    @PropertyName("Date_From")
    public String getDateFrom() {
        return Date_From;
    }

    @PropertyName("Date_From")
    public void setDateFrom(String dateFrom) {
        this.Date_From = dateFrom;
    }

    @PropertyName("Date_To")
    public String getDateTo() {
        return Date_To;
    }

    @PropertyName("Date_To")
    public void setDateTo(String dateTo) {
        this.Date_To = dateTo;
    }

    @PropertyName("Status")
    public String getStatus() {
        return Status;
    }

    @PropertyName("Status")
    public void setStatus(String status) {
        this.Status = status;
    }

    @PropertyName("Assigned_To")
    public String getAssignedTo() {
        return Assigned_To;
    }

    @PropertyName("Assigned_To")
    public void setAssignedTo(String assignedTo) {
        this.Assigned_To = assignedTo;
    }
}
