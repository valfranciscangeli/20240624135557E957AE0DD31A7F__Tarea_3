package desarrollo;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.function.Function;

public class FiltroBloom {
    private int[] mArray;
    private List<Function<String, Integer>> hashArray;
    private int k;
    private int m;

    // constructor del filtro de Bloom
    public FiltroBloom(int m, int k) throws NoSuchAlgorithmException {
        this.mArray = new int[m];
        this.hashArray = HashFunctionGenerator.generateHashFunctions(k,m);
        this.k=k;
        this.m=m;
    }

    // aplica las k funciones de hash a un valor
    private Integer[] getHashIndices(String value) {
        Integer[] hashIndices = new Integer[k];
        for (int i = 0; i < k; i++) {
            hashIndices[i]=hashArray.get(i).apply(value)-1;
            
        }
        return hashIndices;
    }

    // se anhade un valor al filtro, los indices de hash pasan a ser 1
    public void add(String value) {
        Integer[] hashIndices = getHashIndices(value);
        for (int index : hashIndices) {
            mArray[index]=1;
        }
    }

    // si el valor esta, todos los indices de la funcion de hash (k valores) deben ser 1 en el array M
    public boolean mightContain(String value) {
        Integer[] hashIndices = getHashIndices(value);
        int total=0;
        for (int index : hashIndices) {
            total+=mArray[index];
        }
        return total==k;
    }
}
