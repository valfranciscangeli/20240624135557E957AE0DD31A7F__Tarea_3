package desarrollo;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GrepSearch {

    // busqueda tipo grep optimizada
    public static boolean[] buscarEnArchivo(String path, List<String> palabrasABuscar) {
        boolean[] resultados = new boolean[palabrasABuscar.size()];
        Set<String> encontrado = new HashSet<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                for (int i = 0; i < palabrasABuscar.size(); i++) {
                    String word = palabrasABuscar.get(i);
                    if (!resultados[i] && line.contains(word)) {
                        resultados[i] = true;
                        encontrado.add(word);
                    }
                }
                // si ya se encontraron todas, salir
                if (encontrado.size() == palabrasABuscar.size()) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultados;
    }

    // funcion main para probar la busqueda
    public static void main(String[] args) {
        String path = "archivos/Popular-Baby-Names-Final.csv";
        List<String> palabras = List.of("IMANI", "DSFDG", "PEDRO", "ISABELLA", "SDFGHN", "PEPE");

        boolean[] resultado = buscarEnArchivo(path, palabras);

        for (int i = 0; i < resultado.length; i++) {
            System.out.println(palabras.get(i) + ": " + resultado[i]);
        }
    }
}
