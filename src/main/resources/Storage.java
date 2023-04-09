public interface Storage<T> {
    T add(T t);
    T update(T t);
    T delete(Long id);
    T get(Long id);
    boolean isContains(Long id);
    List<T> getAll();
}