package util;

import util.math.MathUtils;

import java.util.*;
import java.util.function.BinaryOperator;

public class ArrayAndListTools {
    private ArrayAndListTools() {};

    public static <T> T mappedElement(double value, double min, double max, T[] array) {
        double n = (value - min) / (max - min);
        int index = (int)(n * array.length);
        return array[index];
    }

    public static char mappedElement(double value, double min, double max, char[] array) {
        double n = (value - min) / (max - min);
        int index = (int)(n * array.length);
        return array[index];
    }

    public static <T> void swap(T[] arr, int i, int j) {
        T temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void swap(double[] arr, int i, int j) {
        double temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static <T> T randomElement(List<T> list) {
        return list.get((int) MathUtils.random(0, list.size()));
    }

    public static <T> T randomElement(T[] arr) {
        return arr[(int) MathUtils.random(0, arr.length)];
    }

    public static char[] pick(char[] array, int... indices) {
        if(indices == null || indices.length == 0) return new char[0];

        char[] arr = new char[indices.length];
        int index = 0;
        for(int i : indices) {
            arr[index++] = array[i];
        }
        return arr;
    }

    private static class Pair<K, V>{
        final K v1;
        final V v2;

        private Pair(K v1, V v2) {
            this.v1 = v1;
            this.v2 = v2;
        }
    }

    public static <K, V> List<K> sortUsing(List<K> list, List<V> control, Comparator<? super V> comparator) {
        if(list.size() != control.size()) throw new IllegalArgumentException();

        List<Pair<K,V>> paired = new ArrayList<>(list.size());
        for(int i = 0; i < list.size(); i++) {
            paired.add(new Pair<>(list.get(i), control.get(i)));
        }
        paired.sort((p1, p2) -> comparator.compare(p1.v2, p2.v2));

        list.clear();
        for (Pair<K, V> pair : paired) {
            list.add(pair.v1);
        }

        return list;
    }

    public static <T> void shuffleArray(T[] array) {
        int index;
        T temp;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    public static <T> List<List<T>> getPermutations(T[] array) {
        List<List<T>> permutations = new ArrayList<>((int) MathUtils.factorial(array.length));
        permute(0, array, permutations);
        return permutations;
    }

    private static <T> void permute(int start, T[] input, List<List<T>> permutations) {
        if (start == input.length) {
            permutations.add(new ArrayList<>(Arrays.asList(input)));
            return;
        }
        for (int i = start; i < input.length; i++) {
            swap(input, i, start);
            permute(start + 1, input, permutations);
            swap(input, i, start);
        }
    }

    public static double[] binaryOperation(double[] arr1, double[] arr2, BinaryOperator<Double> operator) {
        int max = Math.min(arr1.length, arr2.length);
        double[] result = new double[max];
        for(int i = 0; i < max; i++) {
            result[i] = operator.apply(arr1[i], arr2[i]);
        }
        return result;
    }


}
