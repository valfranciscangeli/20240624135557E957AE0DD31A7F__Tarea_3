package experimento;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

import desarrollo.FiltroBloom;
import desarrollo.GrepSearch;

public class Experimento {

    public static boolean[] bisquedaBloom(String path, List<String> palabras, FiltroBloom bloomF) {
        boolean[] resultado = new boolean[palabras.size()];
        for(int i=0; i<palabras.size();i++){
            if(bloomF.mightContain(palabras.get(i))){
                resultado[i]= (GrepSearch.buscarEnArchivo(path, new ArrayList<>(Collections.singletonList(palabras.get(i)))))[0];

            }else{
                resultado[i]=false;
            }
        }
        return resultado;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        String path = "archivos/Popular-Baby-Names-Final.csv";
        List<String> nombres = cargarNombres(path);

        int[] nValues = {1024, 4096, 16384, 65536};
        double[] proporciones = {0, 0.25, 0.5, 0.75, 1};

        for (int size : nValues) {
            for (double p : proporciones) {
                List<String> palabrasABuscar = generateWordsToSearch(nombres, size, p);
                
                // sin filtro de Bloom
                long inicio = System.currentTimeMillis();
                boolean[] resultadoBusqueda = GrepSearch.buscarEnArchivo(path, palabrasABuscar);
                long fin = System.currentTimeMillis();
                long tiempoEjecucion = fin - inicio;

                // con filtro de Bloom
                FiltroBloom bloomFilter = new FiltroBloom(size * 10, 7);
                for (String word : nombres) {
                    bloomFilter.add(word);
                }
                inicio = System.currentTimeMillis();
                boolean[] resultadoBloom = bisquedaBloom(path, palabrasABuscar, bloomFilter);
                fin = System.currentTimeMillis();
                long tiempoEjecucionBloom = fin - inicio;

                // Calcular porcentaje de error
                double falsePositiveRate = proporcionFalsoPositivo(resultadoBusqueda, resultadoBloom);

                System.out.println("Tamaño: " + size + ", Proporción: " + p);
                System.out.println("Sin filtro de Bloom: " + tiempoEjecucion + " ms");
                System.out.println("Con filtro de Bloom: " + tiempoEjecucionBloom + " ms");
                System.out.println("Tasa de falsos positivos: " + falsePositiveRate + "%");
            }
        }
    }

    private static List<String> cargarNombres(String path) {
        List<String> names = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                names.add(line.split(",")[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return names;
    }

    private static List<String> generateWordsToSearch(List<String> allNames, int size, double proportion) {
        List<String> palabrasABuscar = new ArrayList<>();
        int inFileCount = (int) (size * proportion);
        int outFileCount = size - inFileCount;

        for (int i = 0; i < inFileCount; i++) {
            palabrasABuscar.add(allNames.get(i));
        }
        for (int i = 0; i < outFileCount; i++) {
            palabrasABuscar.add("NonExistentName" + i);
        }

        return palabrasABuscar;
    }

    private static double proporcionFalsoPositivo(boolean[] resultadoBusqueda, boolean[] resultadoBloom) {
        int falsoPositivo = 0;
        int totalNegativos = 0;

        for (int i = 0; i < resultadoBusqueda.length; i++) {
            if (!resultadoBusqueda[i]) {
                totalNegativos++;
                if (resultadoBloom[i]) {
                    falsoPositivo++;
                }
            }
        }

        return (double) falsoPositivo / totalNegativos * 100;
    }
}
