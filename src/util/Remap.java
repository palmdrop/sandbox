package util;

public interface Remap<T> {
    T map(final T value, final T min, final T max);
}
