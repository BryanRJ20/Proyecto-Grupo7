package domain.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Clase para manejar la encriptación de contraseñas
 * Utiliza SHA-256 con salt para mayor seguridad
 */
public class PasswordEncryption {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    /**
     * Encripta una contraseña usando SHA-256 con salt
     */
    public static String encrypt(String password) {
        try {
            // Generar salt aleatorio
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Crear hash con la contraseña y salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());

            // Combinar salt y hash
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);

            // Codificar en Base64
            return Base64.getEncoder().encodeToString(combined);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error en encriptación", e);
        }
    }

    /**
     * Verifica si una contraseña coincide con su versión encriptada
     */
    public static boolean verify(String password, String encryptedPassword) {
        try {
            // Decodificar el password encriptado
            byte[] combined = Base64.getDecoder().decode(encryptedPassword);

            // Extraer salt (primeros 16 bytes)
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);

            // Crear hash de la contraseña ingresada con el mismo salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedInputPassword = md.digest(password.getBytes());

            // Extraer hash original (resto de bytes)
            byte[] originalHash = new byte[combined.length - SALT_LENGTH];
            System.arraycopy(combined, SALT_LENGTH, originalHash, 0, originalHash.length);

            // Comparar hashes
            return MessageDigest.isEqual(hashedInputPassword, originalHash);

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Genera una contraseña temporal aleatoria
     */
    public static String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }
}