package com.tuniway.util.validation;

import com.tuniway.model.Admin;
import com.tuniway.model.User;
import com.tuniway.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class AdminCheckHandler extends DeletionHandler {
    private final UserService userService;

    public AdminCheckHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public DeletionResult canDelete(User user) {
        // Prevent deletion of the last admin
        if (user instanceof Admin) {
            long adminCount = userService.getAllUsers().stream()
                    .filter(u -> u instanceof Admin)
                    .count();

            if (adminCount <= 1) {
                return new DeletionResult(false,
                        "Cannot delete user: This is the last admin in the system");
            }
        }
        return checkNext(user);
    }
}