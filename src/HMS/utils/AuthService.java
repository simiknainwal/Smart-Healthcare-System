package HMS.utils;

import HMS.db.UserDAO;
import HMS.db.PatientDAO;
import HMS.db.DoctorDAO;
import HMS.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * AuthService — Handles user authentication, password hashing, and signup validation.
 */
public class AuthService {

    private final UserDAO userDAO;
    private final PatientDAO patientDAO;
    private final DoctorDAO doctorDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
        this.patientDAO = new PatientDAO();
        this.doctorDAO = new DoctorDAO();
    }

    /**
     * Hashes a plaintext password using SHA-256.
     * @param password the plaintext password
     * @return the hex string of the hashed password
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Attempts to log in a user.
     * @return the User object if successful, null if credentials are invalid.
     */
    public User login(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user != null) {
            String hashedInput = hashPassword(password);
            if (user.getPasswordHash().equals(hashedInput)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Registers a new DOCTOR or PATIENT account.
     * Throws an IllegalArgumentException if validation fails.
     */
    public void signup(String username, String password, String role, String linkedId) {
        // 1. Validate username
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (userDAO.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        // 2. Validate password length (8-12 characters)
        if (password == null || password.length() < 8 || password.length() > 12) {
            throw new IllegalArgumentException("Password must be between 8 and 12 characters.");
        }

        // 3. Validate role
        if (!role.equals("DOCTOR") && !role.equals("PATIENT")) {
            throw new IllegalArgumentException("Invalid role for signup.");
        }

        // 4. Validate linked ID
        if (linkedId == null || linkedId.trim().isEmpty()) {
            throw new IllegalArgumentException("Linked ID cannot be empty.");
        }

        // Check if ID exists in the respective table
        if (role.equals("PATIENT")) {
            if (patientDAO.getById(linkedId) == null) {
                throw new IllegalArgumentException("Patient ID not found in system.");
            }
        } else if (role.equals("DOCTOR")) {
            if (doctorDAO.getById(linkedId) == null) {
                throw new IllegalArgumentException("Doctor ID not found in system.");
            }
        }

        // Check if ID is already linked to another account
        if (userDAO.findByLinkedId(linkedId) != null) {
            throw new IllegalArgumentException("This ID is already linked to an existing account.");
        }

        // 5. All valid -> Create user
        String hashedPw = hashPassword(password);
        String createdAt = DateUtil.todayFormatted();
        User newUser = new User(username, hashedPw, role, linkedId, createdAt);
        userDAO.insert(newUser);
    }

    /**
     * Updates the username and/or password for an existing user.
     * Throws an IllegalArgumentException if validation fails.
     */
    public void updateCredentials(User currentUser, String newUsername, String newPassword) {
        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        
        if (!newUsername.equals(currentUser.getUsername()) && userDAO.findByUsername(newUsername) != null) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        if (newPassword == null || newPassword.length() < 8 || newPassword.length() > 12) {
            throw new IllegalArgumentException("Password must be between 8 and 12 characters.");
        }

        String hashedPw = hashPassword(newPassword);
        userDAO.updateCredentials(currentUser.getUsername(), newUsername, hashedPw);
        
        // Update the current user object in memory
        currentUser.setUsername(newUsername);
        currentUser.setPasswordHash(hashedPw);
    }

    /**
     * Seeds the default admin account if it doesn't exist.
     * Called automatically during database initialization.
     */
    public static void seedDefaultAdmin() {
        UserDAO dao = new UserDAO();
        if (dao.findByUsername("admin") == null) {
            String hashedPw = hashPassword("admin123");
            String createdAt = DateUtil.todayFormatted();
            User adminUser = new User("admin", hashedPw, "ADMIN", "", createdAt);
            dao.insert(adminUser);
            System.out.println("[Auth] Default admin account seeded.");
        }
    }
}
