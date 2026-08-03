package com.assignpro.backend.dto;

public class LeadImportResponse {

    private int totalRecords;
    private int successRecords;
    private int failedRecords;
    private String message;

    public LeadImportResponse() {
    }

    public LeadImportResponse(int totalRecords, int successRecords,
                              int failedRecords, String message) {
        this.totalRecords = totalRecords;
        this.successRecords = successRecords;
        this.failedRecords = failedRecords;
        this.message = message;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getSuccessRecords() {
        return successRecords;
    }

    public void setSuccessRecords(int successRecords) {
        this.successRecords = successRecords;
    }

    public int getFailedRecords() {
        return failedRecords;
    }

    public void setFailedRecords(int failedRecords) {
        this.failedRecords = failedRecords;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}