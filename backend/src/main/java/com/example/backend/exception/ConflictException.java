package com.example.backend.exception;

/**
 * Thrown when the request collides with the current state of a resource,
 * e.g. a duplicate username, a duplicate film title in the same store,
 * or attempting to rent a copy that's already rented out.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
