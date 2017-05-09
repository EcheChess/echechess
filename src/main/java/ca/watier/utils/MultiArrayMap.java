/*
 *    Copyright 2014 - 2017 Yannick Watier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ca.watier.utils;


import java.util.*;

/**
 * Created by Yannick on 5/3/2016.
 */
public class MultiArrayMap<K, V> {

    private final Map<K, List<V>> containerMap;

    public MultiArrayMap() {
        containerMap = new HashMap<K, List<V>>();
    }

    public int size() {
        return containerMap.size();
    }

    public boolean containsKey(Object key) {
        return containerMap.containsKey(key);
    }

    /**
     * @param item - The item to be checked
     * @return A Set of key(s) where the item has been found, an empty Set otherwise
     */
    public Set<K> containsValue(V item) {
        Set<K> value = new HashSet<K>();

        for (Map.Entry<K, List<V>> kListEntry : containerMap.entrySet()) {
            if (kListEntry.getValue().contains(item)) {
                value.add(kListEntry.getKey());
            }
        }

        return value;
    }

    /**
     * @param key - The key to beb used
     * @return An unmodifiable List containing the values associated to the key, or null if the value is not present
     */
    public List<V> get(K key) {
        List<V> list = containerMap.get(key);
        return (list != null) ? Collections.unmodifiableList(list) : null;
    }

    public void put(K key, V value) {
        getAssociation(key).add(value);
    }

    /**
     * @param key - The key
     * @return The list associated to the current key
     */
    private List<V> getAssociation(K key) {
        List<V> associationList;

        if (!containerMap.containsKey(key)) {
            associationList = new ArrayList<V>();
            containerMap.put(key, associationList);
        } else {
            associationList = containerMap.get(key);
        }

        return associationList;
    }

    /**
     * @param key - The key to be removed
     * @return The unmodifiable List of values associated with the key to be removed
     */
    public List<V> remove(K key) {
        return Collections.unmodifiableList(containerMap.remove(key));
    }

    /**
     * @param m - The map to be merged with the current
     */
    public void putAll(MultiArrayMap<? extends K, ? extends V> m) {
        if (m == null || m.isEmpty()) {
            return;
        }

        for (Map.Entry<? extends K, ? extends List<? extends V>> entry : m.entrySet()) {
            getAssociation(entry.getKey()).addAll(entry.getValue());
        }
    }

    public boolean isEmpty() {
        return containerMap.isEmpty();
    }

    public Set<Map.Entry<K, List<V>>> entrySet() {
        return containerMap.entrySet();
    }

    public void clear() {
        containerMap.clear();
    }

    public Set<K> keySet() {
        return containerMap.keySet();
    }

    /**
     * @return An unmodifiable Set containing all the values
     */
    public Collection<V> values() {
        Set<V> values = new HashSet<V>();

        for (List<V> value : containerMap.values()) {
            values.addAll(value);
        }

        return Collections.unmodifiableSet(values);
    }

    @Override
    public String toString() {
        return "MultiArrayMap{" + containerMap +
                '}';
    }
}
