package galileo.dataset;

import java.util.Collection;

/**
 * Defines a very basic map that consists of Key, Value pairs wherein the Key
 * can be ascertained by the Value directly.
 *
 * @author malensek
 */
public interface SimpleMap<K, V> {

    /**
     * Places an item in this data structure.
     */
    public void put(V item);

    /**
     * Retrieves an item from this data structure.
     *
     * @param key Key of the item to retrieve; for instance, the item's name.
     */
    public V get(K key);


    /**
     * Retrieves all the values contained in this data structure.
     */
    public Collection<V> values();

    /**
     * Reports the current size of the data structure.
     */
    public int size();
}
