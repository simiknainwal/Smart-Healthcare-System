package HMS.model;

/**
 * User — Represents a login account in the system.
 *
 * This is NOT part of the Person hierarchy.
 * A User is an authentication entity that links to a Patient or Doctor
 * via the linkedId field.
 *
 * Roles:
 *   ADMIN   — full system access (pre-seeded, no signup)
 *   DOCTOR  — linked to a Doctor record
 *   PATIENT — linked to a Patient record
 */
public class User {

    private String username;
    private String passwordHash;
    private final String role;        // "ADMIN", "DOCTOR", or "PATIENT"
    private final String linkedId;    // e.g. "D001" or "P003", empty for admin
    private final String createdAt;   // timestamp string

    public User(String username, String passwordHash, String role,
                String linkedId, String createdAt) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.linkedId = linkedId;
        this.createdAt = createdAt;
    }

    // ==================== GETTERS ====================

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public String getLinkedId() {
        return linkedId;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
