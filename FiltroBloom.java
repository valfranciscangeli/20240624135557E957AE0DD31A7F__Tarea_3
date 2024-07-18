import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;

public class FiltroBloom {
    private BitSet bitSet;
    private int bitSetSize;
    private int numHashFunctions;
    private MessageDigest digest;

    public FiltroBloom(int bitSetSize, int numHashFunctions) throws NoSuchAlgorithmException {
        this.bitSetSize = bitSetSize;
        this.numHashFunctions = numHashFunctions;
        this.bitSet = new BitSet(bitSetSize);
        this.digest = MessageDigest.getInstance("MD5");
    }

    private int[] getHashIndices(String value) {
        byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
        int[] hashIndices = new int[numHashFunctions];
        for (int i = 0; i < numHashFunctions; i++) {
            int hash = 0;
            for (int j = 0; j < 4; j++) {
                hash <<= 8;
                hash |= ((int) bytes[(i * 4 + j) % bytes.length]) & 0xFF;
            }
            hashIndices[i] = Math.abs(hash) % bitSetSize;
        }
        return hashIndices;
    }

    public void add(String value) {
        int[] hashIndices = getHashIndices(value);
        for (int index : hashIndices) {
            bitSet.set(index);
        }
    }

    public boolean mightContain(String value) {
        int[] hashIndices = getHashIndices(value);
        for (int index : hashIndices) {
            if (!bitSet.get(index)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        try {
            // Configuración del filtro de Bloom
            int bitSetSize = 1000; // Ajustar según sea necesario
            int numHashFunctions = 3; // Ajustar según sea necesario

            FiltroBloom filtroBloom = new FiltroBloom(bitSetSize, numHashFunctions);

            // Leer archivo CSV y agregar elementos al filtro de Bloom
            try (BufferedReader br = new BufferedReader(new FileReader("Popular-Baby-Names.csv"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    filtroBloom.add(line.trim());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Búsqueda de elementos
            String[] nombresABuscar = {"Nombre1", "Nombre2", "Nombre3"}; // Ajustar según sea necesario
            for (String nombre : nombresABuscar) {
                if (filtroBloom.mightContain(nombre)) {
                    System.out.println(nombre + " podría estar en el conjunto.");
                } else {
                    System.out.println(nombre + " no está en el conjunto.");
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
