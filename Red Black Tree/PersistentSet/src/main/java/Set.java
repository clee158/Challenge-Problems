import java.util.Iterator;

/**
 * Created by Chaeyoon on 2/15/2017.
 */

public interface Set<E> {
    boolean add(E e);

    boolean remove(E e);

    boolean isEmpty();

    Iterator<E> iterator();

    int size();
}
