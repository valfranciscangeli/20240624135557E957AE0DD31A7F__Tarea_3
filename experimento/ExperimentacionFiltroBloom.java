package experimento;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import desarrollo.FiltroBloom;

public class ExperimentacionFiltroBloom {

    public static void main(String[] args) {
        try {
            // Ruta del archivo CSV que contiene los nombres
            String filePath = "Popular-Baby-Names-Final.csv";
            // Cargar los nombres del archivo CSV
            List<String> nombres = cargarDatos(filePath);

            // Tamaños de secuencia a probar: 1024, 4096, 16384, 65536
            int[] tamanosN = {1024, 4096, 16384, 65536}; // |N| ∈ {2^10, 2^12, 2^14, 2^16}
            // Proporciones de nombres presentes en el CSV a probar: 0, 0.25, 0.5, 0.75, 1
            double[] proporcionesP = {0, 0.25, 0.5, 0.75, 1}; // p ∈ {0, 1/4, 1/2, 3/4, 1}

            // Para cada tamaño de secuencia y proporción, realizar la experimentación
            for (int tamanoN : tamanosN) {
                for (double p : proporcionesP) {
                    // Generar una secuencia de nombres con la proporción especificada
                    List<String> secuenciaNombres = generarSecuenciaNombres(nombres, tamanoN, p);
                    // Realizar la búsqueda y medir tiempos y errores
                    realizarBusqueda(filePath, secuenciaNombres, tamanoN, p);
                }
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    // Método para cargar datos desde un archivo CSV
    public static List<String> cargarDatos(String filePath) throws IOException {
        List<String> nombres = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Leer cada línea del archivo y agregarla a la lista de nombres
            while ((line = br.readLine()) != null) {
                nombres.add(line.trim());
            }
        }
        return nombres;
    }

    // Método para generar una secuencia de nombres con una proporción específica de nombres presentes en el CSV
    public static List<String> generarSecuenciaNombres(List<String> nombres, int tamanoN, double p) {
        List<String> secuencia = new ArrayList<>();
        Random random = new Random();
        int cantidadEnCSV = (int) (tamanoN * p);

        // Añadir nombres del CSV a la secuencia
        for (int i = 0; i < cantidadEnCSV; i++) {
            secuencia.add(nombres.get(random.nextInt(nombres.size())));
        }

        // Añadir nombres aleatorios a la secuencia
        for (int i = cantidadEnCSV; i < tamanoN; i++) {
            secuencia.add(generarNombreAleatorio());
        }

        return secuencia;
    }

    // Método para generar un nombre aleatorio
    public static String generarNombreAleatorio() {
        String letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        int longitud = 5 + random.nextInt(6); // Longitud entre 5 y 10 caracteres

        // Generar un nombre aleatorio
        for (int i = 0; i < longitud; i++) {
            sb.append(letras.charAt(random.nextInt(letras.length())));
        }

        return sb.toString();
    }

    // Método para realizar la búsqueda y medir tiempos y errores
    public static void realizarBusqueda(String filePath, List<String> secuenciaNombres, int tamanoN, double p) throws NoSuchAlgorithmException, IOException {
        // Configurar el filtro de Bloom
        double percentage = 0.2; // Porcentaje del tamaño de la bitset
        int bitSetSize = (int) (tamanoN * percentage);
        int numHashFunctions = 3; // Número de funciones hash

        FiltroBloom filtroBloom = new FiltroBloom(bitSetSize, numHashFunctions);
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Añadir cada nombre del archivo CSV al filtro de Bloom
            while ((line = br.readLine()) != null) {
                filtroBloom.add(line.trim());
            }
        }

        // Realizar búsquedas sin usar el filtro de Bloom y medir el tiempo
        long tiempoSinFiltro = buscarSinFiltro(filePath, secuenciaNombres);

        // Realizar búsquedas usando el filtro de Bloom y medir el tiempo
        long tiempoConFiltro = buscarConFiltro(filtroBloom, filePath, secuenciaNombres);

        // Calcular el porcentaje de error del filtro de Bloom
        double porcentajeError = calcularPorcentajeError(filtroBloom, secuenciaNombres);

        // Imprimir los resultados
        System.out.println("Tamaño N: " + tamanoN + ", Proporción p: " + p);
        System.out.println("Tiempo sin filtro de Bloom: " + tiempoSinFiltro + " ms");
        System.out.println("Tiempo con filtro de Bloom: " + tiempoConFiltro + " ms");
        System.out.println("Porcentaje de error: " + porcentajeError + "%");
        System.out.println("---------------------------------------------");
    }

    // Método para realizar la búsqueda sin usar el filtro de Bloom y medir el tiempo
    public static long buscarSinFiltro(String filePath, List<String> secuenciaNombres) throws IOException {
        long inicio = System.currentTimeMillis();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            for (String nombre : secuenciaNombres) {
                String line;
                // Buscar cada nombre en el archivo CSV
                while ((line = br.readLine()) != null) {
                    if (line.trim().equals(nombre)) {
                        break;
                    }
                }
            }
        }
        return System.currentTimeMillis() - inicio;
    }

    // Método para realizar la búsqueda usando el filtro de Bloom y medir el tiempo
    public static long buscarConFiltro(FiltroBloom filtroBloom, String filePath, List<String> secuenciaNombres) throws IOException {
        long inicio = System.currentTimeMillis();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            for (String nombre : secuenciaNombres) {
                // Si el filtro de Bloom indica que el nombre podría estar, buscar en el archivo CSV
                if (filtroBloom.mightContain(nombre)) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.trim().equals(nombre)) {
                            break;
                        }
                    }
                }
            }
        }
        return System.currentTimeMillis() - inicio;
    }

    // Método para calcular el porcentaje de error del filtro de Bloom
    public static double calcularPorcentajeError(FiltroBloom filtroBloom, List<String> secuenciaNombres) {
        int falsosPositivos = 0;
        int verdaderosNegativos = 0;

        for (String nombre : secuenciaNombres) {
            // Contar los falsos positivos y los verdaderos negativos
            if (filtroBloom.mightContain(nombre) && !nombre.equals("IMANI") && !nombre.equals("DSFDG") && !nombre.equals("PEDRO") && !nombre.equals("ISABELLA") && !nombre.equals("SDFGHN")) {
                falsosPositivos++;
            } else {
                verdaderosNegativos++;
            }
        }

        // Calcular el porcentaje de error
        return (falsosPositivos / (double) (falsosPositivos + verdaderosNegativos)) * 100;
    }
}
