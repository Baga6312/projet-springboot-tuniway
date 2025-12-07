package com.tuniway.service;

import com.tuniway.model.User;
import com.tuniway.util.validation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service that builds and executes the Chain of Responsibility
 * for user deletion validation
 */
@Service
public class DeletionValidationChainService {

    private final ReviewCheckHandler reviewCheckHandler;
    private final ClientReservationCheckHandler clientReservationCheckHandler;
    private final ClientTourCheckHandler clientTourCheckHandler;
    private final GuideReservationCheckHandler guideReservationCheckHandler;
    private final GuideTourCheckHandler guideTourCheckHandler;

    @Autowired
    public DeletionValidationChainService(
            ReviewCheckHandler reviewCheckHandler,
            ClientReservationCheckHandler clientReservationCheckHandler,
            ClientTourCheckHandler clientTourCheckHandler,
            GuideReservationCheckHandler guideReservationCheckHandler,
            GuideTourCheckHandler guideTourCheckHandler) {
        this.reviewCheckHandler = reviewCheckHandler;
        this.clientReservationCheckHandler = clientReservationCheckHandler;
        this.clientTourCheckHandler = clientTourCheckHandler;
        this.guideReservationCheckHandler = guideReservationCheckHandler;
        this.guideTourCheckHandler = guideTourCheckHandler;
    }

    /**
     * Validates if a user can be deleted by running through
     * all validation handlers in the chain
     *
     * @param user The user to validate for deletion
     * @return DeletionResult indicating if deletion is allowed
     */
    public DeletionResult validateDeletion(User user) {
        // Build the chain
        DeletionHandler chain = buildChain();

        // Execute validation
        return chain.canDelete(user);
    }

    /**
     * Builds the complete validation chain in the correct order
     *
     * @return The first handler in the chain
     */
    private DeletionHandler buildChain() {
        reviewCheckHandler
                .setNext(clientReservationCheckHandler)
                .setNext(clientTourCheckHandler)
                .setNext(guideReservationCheckHandler)
                .setNext(guideTourCheckHandler);

        return reviewCheckHandler;
    }

    /**
     * Gets a description of all validation steps in the chain
     *
     * @return String describing the validation chain
     */
    public String getChainDescription() {
        return "User Deletion Validation Chain:\n" +
                "1. Review Check - Ensures user has no reviews\n" +
                "2. Client Reservation Check - Ensures client has no reservations\n" +
                "3. Client Tour Check - Ensures client has no personalized tours\n" +
                "4. Guide Reservation Check - Ensures guide has no reservations\n" +
                "5. Guide Tour Check - Ensures guide has no personalized tours";
    }
}