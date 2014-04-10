
package galileo.query;

import java.util.HashSet;
import java.util.Set;

public class PayloadFilter<T> {

    private Set<T> filterItems;
    private boolean excludesItems = false;

    public PayloadFilter() {
        filterItems = new HashSet<>();
    }

    public PayloadFilter(boolean excludesItems) {
        this();
        this.excludesItems = excludesItems;
    }

    public PayloadFilter(Set<T> items) {
        this.filterItems = items;
    }

    public PayloadFilter(boolean excludesItems, Set<T> items) {
        this(items);
        this.excludesItems = excludesItems;
    }

    public void add(T item) {
        filterItems.add(item);
    }

    public Set<T> getItems() {
        return filterItems;
    }

    /**
     * Reports whether this PayloadFilter includes or excludes the items it
     * contains.  If this method returns true, then any matching items in the
     * filter will be removed from query results.  On the other hand, if this
     * returns false, then only items in the filter will be retained in query
     * results.
     */
    public boolean excludesItems() {
        return excludesItems;
    }
}
