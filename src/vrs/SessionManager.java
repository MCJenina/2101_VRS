/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vrs;

/**
 *
 * @author jenina
 */
public final class SessionManager {
    private static int customerId = 0; // Default value for no user

    // Private constructor to prevent instantiation
    private SessionManager() {
        throw new UnsupportedOperationException("SessionManager is a utility class and cannot be instantiated.");
    }

    // Set customer ID (only allowed once)
    public static void setCustomerId(int id) {
        if (customerId != 0) {
            throw new IllegalStateException("Session already initialized. Cannot change customerId.");
        }
        customerId = id;
    }

    // Get the current customer ID
    public static int getCustomerId() {
        return customerId;
    }

    // Clear session (e.g., on logout)
    public static void clearSession() {
        customerId = 0;
    }
}
