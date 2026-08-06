package com.assignpro.backend.dto;

public class LeadImportResponse {

    private int totalRecords;
    private int imported;
    private int duplicates;
    private int failed;
    private int invalid;
    private String message;

    public LeadImportResponse() {
    }

    public LeadImportResponse(int totalRecords, int imported, int duplicates, String message) {
        this.totalRecords = totalRecords;
        this.imported = imported;
        this.duplicates = duplicates;
        this.message = message;
    }

    public LeadImportResponse(int totalRecords, int imported, int duplicates, int failed, int invalid, String message) {
        this.totalRecords = totalRecords;
        this.imported = imported;
        this.duplicates = duplicates;
        this.failed = failed;
        this.invalid = invalid;
        this.message = message;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getImported() {
        return imported;
    }

    public void setImported(int imported) {
        this.imported = imported;
    }

    public int getDuplicates() {
        return duplicates;
    }

    public void setDuplicates(int duplicates) {
        this.duplicates = duplicates;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public int getInvalid() {
        return invalid;
    }

    public void setInvalid(int invalid) {
        this.invalid = invalid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}