package com.tuniway.util.validation;

import com.tuniway.model.User;

public abstract class DeletionHandler {
    protected DeletionHandler nextHandler;

    public DeletionHandler setNext(DeletionHandler handler) {
        this.nextHandler = handler;
        return handler;
    }

    public abstract DeletionResult canDelete(User user);

    protected DeletionResult checkNext(User user) {
        if (nextHandler != null) {
            return nextHandler.canDelete(user);
        }
        return new DeletionResult(true, "User can be deleted");
    }
}
