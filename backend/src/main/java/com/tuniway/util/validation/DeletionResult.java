package com.tuniway.util.validation;

public class DeletionResult {
    private boolean canDelete;
    private String message;

    public DeletionResult(boolean canDelete, String message) {
        this.canDelete = canDelete;
        this.message = message;
    }

    public boolean isCanDelete() { return canDelete; }
    public String getMessage() { return message; }
}