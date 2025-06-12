package domain.security;

/**
 * Enum que define los roles disponibles en el sistema
 */
public enum UserRole {
    ADMINISTRATOR("Administrador", "Acceso total al sistema"),
    USER("Usuario", "Acceso restringido para reservas y consultas");

    private final String displayName;
    private final String description;

    UserRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasFullAccess() {
        return this == ADMINISTRATOR;
    }

    public boolean canManageUsers() {
        return this == ADMINISTRATOR;
    }

    public boolean canManageFlights() {
        return this == ADMINISTRATOR;
    }

    public boolean canViewStatistics() {
        return this == ADMINISTRATOR;
    }

    public boolean canMakeReservations() {
        return true; // Ambos roles pueden hacer reservas
    }

    public boolean canViewReservations() {
        return true; // Ambos roles pueden ver sus reservas
    }
}