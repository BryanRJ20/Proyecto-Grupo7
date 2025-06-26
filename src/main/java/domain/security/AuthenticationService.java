package domain.security;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import domain.list.CircularLinkedList;
import domain.list.ListException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de autenticación que gestiona usuarios usando Circular Linked List
 */
public class AuthenticationService {
    private static AuthenticationService instance;
    private CircularLinkedList users;
    private User currentUser;
    private static final String USERS_FILE_PATH = "src/main/resources/ucr/project/users.json";

    private AuthenticationService() {
        this.users = new CircularLinkedList();
        this.currentUser = null;
        loadUsersFromFile();
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
     * Carga usuarios desde archivo JSON o inicializa usuarios por defecto
     */
    private void loadUsersFromFile() {
        File file = new File(USERS_FILE_PATH);

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type listType = new TypeToken<List<UserData>>(){}.getType();
                Gson gson = new Gson();
                List<UserData> userData = gson.fromJson(reader, listType);

                if (userData != null) {
                    for (UserData data : userData) {
                        User user = new User(data.username, data.encryptedPassword,
                                UserRole.valueOf(data.role), data.isActive);
                        users.add(user);
                    }
                    System.out.println("✅ Loaded " + userData.size() + " users from " + USERS_FILE_PATH);
                    return;
                }
            } catch (Exception e) {
                System.err.println("❌ Error loading users from file: " + e.getMessage());
            }
        }

        // Si no se pudo cargar desde archivo, inicializar usuarios por defecto
        initializeDefaultUsers();
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

            // Guardar usuarios por defecto en archivo
            saveUsersToFile();
            System.out.println("✅ Default users initialized and saved");

        } catch (ListException e) {
            System.err.println("Error initializing default users: " + e.getMessage());
        }
    }

    /**
     * Guarda usuarios en archivo JSON
     */
    private void saveUsersToFile() {
        try {
            List<UserData> userData = new ArrayList<>();

            // Convertir usuarios a lista
            if (!users.isEmpty()) {
                for (int i = 1; i <= users.size(); i++) {
                    User user = (User) users.get(i);
                    userData.add(new UserData(
                            user.getUsername(),
                            user.getEncryptedPassword(),
                            user.getRole().name(),
                            user.isActive()
                    ));
                }
            }

            // Crear directorio si no existe
            File file = new File(USERS_FILE_PATH);
            file.getParentFile().mkdirs();

            // Guardar en JSON
            try (FileWriter writer = new FileWriter(file)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(userData, writer);
                System.out.println("✅ Saved " + userData.size() + " users to " + USERS_FILE_PATH);
            }

        } catch (Exception e) {
            System.err.println("❌ Error saving users to file: " + e.getMessage());
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

            // Guardar cambios en archivo
            saveUsersToFile();

            return true;

        } catch (ListException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método público para registrar usuario con validaciones adicionales
     */
    public RegistrationResult registerUserWithValidation(String username, String password, String confirmPassword, UserRole role) {
        // Validaciones básicas
        if (username == null || username.trim().isEmpty()) {
            return new RegistrationResult(false, "El nombre de usuario es requerido");
        }

        if (password == null || password.trim().isEmpty()) {
            return new RegistrationResult(false, "La contraseña es requerida");
        }

        if (!password.equals(confirmPassword)) {
            return new RegistrationResult(false, "Las contraseñas no coinciden");
        }

        // Validar longitud mínima de usuario
        if (username.trim().length() < 3) {
            return new RegistrationResult(false, "El nombre de usuario debe tener al menos 3 caracteres");
        }

        // Validar longitud mínima de contraseña
        if (password.length() < 6) {
            return new RegistrationResult(false, "La contraseña debe tener al menos 6 caracteres");
        }

        // Intentar registrar
        boolean success = registerUser(username.trim(), password, role);

        if (success) {
            return new RegistrationResult(true, "Usuario registrado exitosamente");
        } else {
            return new RegistrationResult(false, "El nombre de usuario ya existe");
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
        saveUsersToFile(); // Guardar cambios
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
                saveUsersToFile(); // Guardar cambios
                return true;
            }
        } catch (ListException e) {
            System.err.println("Error setting user status: " + e.getMessage());
        }

        return false;
    }

    /**
     * Clase para almacenar datos de usuario en JSON
     */
    private static class UserData {
        String username;
        String encryptedPassword;
        String role;
        boolean isActive;

        public UserData(String username, String encryptedPassword, String role, boolean isActive) {
            this.username = username;
            this.encryptedPassword = encryptedPassword;
            this.role = role;
            this.isActive = isActive;
        }
    }

    /**
     * Clase para encapsular el resultado de registro
     */
    public static class RegistrationResult {
        private final boolean success;
        private final String message;

        public RegistrationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
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