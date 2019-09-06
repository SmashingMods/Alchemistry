package al132.alchemistry.utils;

import java.util.function.Function;

public class ListUtils {


    //From kotlin stdlib
    public static <T> int indexOfFirst(java.util.List<T> list, Function<T, Boolean> predicate) {
        int index = 0;
        for (T item : list) {
            if (predicate.apply(item))
                return index;
            index++;
        }
        return -1;
    }

}
