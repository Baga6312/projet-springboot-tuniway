package com.tuniway.util.validation;

import com.tuniway.model.Utilisateur;

public abstract class DeletionHandler {
    protected DeletionHandler nextHandler;

    public DeletionHandler setNext(DeletionHandler handler) {
        this.nextHandler = handler;
        return handler;
    }

    public abstract DeletionResult canDelete(Utilisateur user);

    protected DeletionResult checkNext(Utilisateur user) {
        if (nextHandler != null) {
            return nextHandler.canDelete(user);
        }
        return new DeletionResult(true, "User can be deleted");
    }
}
