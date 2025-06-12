package domain.security;

import domain.list.CircularLinkedList;
import domain.list.ListException;

/**
 * Servicio de autenticación que gestiona usuarios usando Circular Linked List
 */
public class AuthenticationService {
    private static AuthenticationService instance;
    private CircularLinkedList users;
    private User currentUser;

    private AuthenticationService() {
        this.users = new CircularLinkedList();
        this.currentUser = null;
        initializeDefaultUsers();
    }

    /**
     * Singleton pattern
     */
    public static AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    /**
     * Inicializa usuarios por defecto del sistema
     */
    private void initializeDefaultUsers() {
        try {
            // Usuario administrador por defecto
            User admin = new User("admin", "admin123", UserRole.ADMINISTRATOR);
            users.add(admin);

            // Usuario regular por defecto
            User user = new User("user", "user123", UserRole.USER);
            users.add(user);

        } catch (ListException e) {
            System.err.println("Error initializing default users: " + e.getMessage());
        }
    }

    /**
     * Autentica un usuario con username y password
     */
    public AuthenticationResult authenticate(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            return new AuthenticationResult(false, "Username y password son requeridos", null);
        }

        try {
            User user = findUserByUsername(username.trim());

            if (user == null) {
                return new AuthenticationResult(false, "Usuario no encontrado", null);
            }

            if (!user.isActive()) {
                return new AuthenticationResult(false, "Usuario inactivo", null);
            }

            if (!user.validatePassword(password)) {
                return new AuthenticationResult(false, "Contraseña incorrecta", null);
            }

            this.currentUser = user;
            return new AuthenticationResult(true, "Autenticación exitosa", user);

        } catch (ListException e) {
            return new AuthenticationResult(false, "Error en autenticación: " + e.getMessage(), null);
        }
    }

    /**
     * Busca un usuario por username
     */
    private User findUserByUsername(String username) throws ListException {
        return (User) users.find(element -> {
            if (element instanceof User) {
                User user = (User) element;
                return user.getUsername().equalsIgnoreCase(username);
            }
            return false;
        });
    }

    /**
     * Registra un nuevo usuario
     */
    public boolean registerUser(String username, String password, UserRole role) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            return false;
        }

        try {
            // Verificar si el usuario ya existe
            User existingUser = findUserByUsername(username.trim());
            if (existingUser != null) {
                return false; // Usuario ya existe
            }

            // Crear nuevo usuario
            User newUser = new User(username.trim(), password, role);
            users.add(newUser);
            return true;

        } catch (ListException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cierra la sesión actual
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Obtiene el usuario actualmente autenticado
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Verifica si hay un usuario autenticado
     */
    public boolean isAuthenticated() {
        return currentUser != null;
    }

    /**
     * Verifica si el usuario actual es administrador
     */
    public boolean isCurrentUserAdmin() {
        return isAuthenticated() && currentUser.getRole() == UserRole.ADMINISTRATOR;
    }

    /**
     * Cambia la contraseña del usuario actual
     */
    public boolean changeCurrentUserPassword(String oldPassword, String newPassword) {
        if (!isAuthenticated()) {
            return false;
        }

        if (!currentUser.validatePassword(oldPassword)) {
            return false;
        }

        currentUser.changePassword(newPassword);
        return true;
    }

    /**
     * Obtiene todos los usuarios (solo para administradores)
     */
    public CircularLinkedList getAllUsers() throws ListException {
        if (!isCurrentUserAdmin()) {
            throw new ListException("Access denied: Admin privileges required");
        }
        return users;
    }

    /**
     * Activa o desactiva un usuario (solo para administradores)
     */
    public boolean setUserStatus(String username, boolean active) {
        if (!isCurrentUserAdmin()) {
            return false;
        }

        try {
            User user = findUserByUsername(username);
            if (user != null && !user.getUsername().equals(currentUser.getUsername())) {
                user.setActive(active);
                return true;
            }
        } catch (ListException e) {
            System.err.println("Error setting user status: " + e.getMessage());
        }

        return false;
    }

    /**
     * Clase para encapsular el resultado de autenticación
     */
    public static class AuthenticationResult {
        private final boolean success;
        private final String message;
        private final User user;

        public AuthenticationResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public User getUser() {
            return user;
        }
    }
}