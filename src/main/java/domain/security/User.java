package domain.security;

/**
 * Clase que representa un usuario del sistema
 */
public class User {
    private String username;
    private String encryptedPassword;
    private UserRole role;
    private boolean isActive;

    public User(String username, String password, UserRole role) {
        this.username = username;
        this.encryptedPassword = PasswordEncryption.encrypt(password);
        this.role = role;
        this.isActive = true;
    }

    // Constructor para crear usuario con contraseña ya encriptada
    public User(String username, String encryptedPassword, UserRole role, boolean isActive) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.role = role;
        this.isActive = isActive;
    }

    public boolean validatePassword(String password) {
        return PasswordEncryption.verify(password, this.encryptedPassword);
    }

    public void changePassword(String newPassword) {
        this.encryptedPassword = PasswordEncryption.encrypt(newPassword);
    }

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", role=" + role +
                ", isActive=" + isActive +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}