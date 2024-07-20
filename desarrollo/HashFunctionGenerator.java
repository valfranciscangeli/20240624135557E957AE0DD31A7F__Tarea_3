package desarrollo;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class HashFunctionGenerator {

    public static List<Function<String, Integer>> generateHashFunctions(int k, int m) {
        List<Function<String, Integer>> hashFunctions = new ArrayList<>();

        for (int i = 0; i < k; i++) {
            final int seed = i;
            hashFunctions.add((String input) -> hashWithSeed(input, seed, m));
        }

        return hashFunctions;
    }

    private static int hashWithSeed(String input, int seed, int m) {
        try {
            // Usamos SHA-256 como hash base por ser el más utilizado por seguridad
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String saltedInput = input + seed;
            byte[] hashBytes = digest.digest(saltedInput.getBytes());
            int hash = 0;
            for (byte b : hashBytes) {
                hash = (hash * 31 + (b & 0xFF)) % m;
            }
            return Math.abs(hash) + 1; // Asegurarse de que esté en el rango [1, m]
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // // Método principal para probar las funciones hash
    // public static void main(String[] args) {
    //     int k = 5;  // Número de funciones hash
    //     int m = 100; // Rango de salida deseado
    //     List<Function<String, Integer>> hashFunctions = generateHashFunctions(k, m);

    //     // Ejemplo de uso de las funciones hash generadas
    //     String input = "example";
    //     for (int i = 0; i < k; i++) {
    //         System.out.println("Hash " + (i + 1) + ": " + hashFunctions.get(i).apply(input));
    //     }
    // }
}
