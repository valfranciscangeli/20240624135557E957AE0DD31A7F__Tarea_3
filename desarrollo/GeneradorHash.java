package desarrollo;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GeneradorHash {

    public static List<Function<String, Integer>> generateHashFunctions(int k, int m) {
        List<Function<String, Integer>> hashFunctions = new ArrayList<>();

        for (int i = 0; i < k; i++) {
            final int seed = i;
            hashFunctions.add((String input) -> hashMasSemilla(input, seed, m));
        }

        return hashFunctions;
    }

    private static int hashMasSemilla(String valor, int semilla, int m) {
        try {
            // usamos SHA-256 como hash base por ser el mas utilizado por seguridad
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String agregarSalt = valor + semilla;
            byte[] hashBytes = digest.digest(agregarSalt.getBytes());
            int hash = 0;
            for (byte b : hashBytes) {
                hash = (hash * 31 + (b & 0xFF)) % m;
            }
            return Math.abs(hash) + 1; // asegurarse de que esta en el rango [1, m]
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
