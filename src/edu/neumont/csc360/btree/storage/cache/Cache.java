package edu.neumont.csc360.btree.storage.cache;

import java.util.HashMap;
import java.util.Map;

public class Cache<K, V> {
    private Map<K, LinkedList.Node<KeyValuePair<K, V>>> map;
    private LinkedList<KeyValuePair<K, V>> lruList;
    private int sizeLimit;
    private int size;

    public Cache(int sizeLimit) {
        this.map = new HashMap<>();
        this.lruList = new LinkedList<>();
        this.sizeLimit = sizeLimit;
        this.size = 0;
    }

    public boolean containsKey(K key) {
        return this.map.containsKey(key);
    }

    public void put(K key, V value) {
        if (this.containsKey(key)) {
            LinkedList.Node<KeyValuePair<K, V>> node = this.map.get(key);
            this.lruList.remove(node);
        }
        KeyValuePair<K, V> kvp = new KeyValuePair<>(key, value);
        this.lruList.prepend(kvp);
        LinkedList.Node<KeyValuePair<K, V>> inserted = this.lruList.getHead();
        this.map.put(key, inserted);
        this.diminishIfNecessary();
    }

    public V get(K key) {
        LinkedList.Node<KeyValuePair<K, V>> node = this.map.get(key);
        V data = null;
        if (node != null) {
            data = node.getData().value;
            this.lruList.moveToHead(node);
        }
        return data;
    }

    public void updateValue(K key, V value) {
        LinkedList.Node<KeyValuePair<K, V>> node = this.map.get(key);
        if (node != null) {
            node.getData().value = value;
            this.lruList.moveToHead(node);
        }
    }

    public V remove(K key) {
        LinkedList.Node<KeyValuePair<K, V>> node = this.map.remove(key);
        V data = node == null
                ? null
                : this.lruList.remove(node).value;
        return data;
    }

    public void clear() {
        this.map.clear();
        this.lruList.clear();
    }

    private void diminishIfNecessary() {
        while (this.size > this.sizeLimit) {
            KeyValuePair<K, V> removedKvp = this.lruList.removeTail();
            this.map.remove(removedKvp.key);
        }
    }

    private class KeyValuePair<K, V> {
        private K key;
        private V value;

        public KeyValuePair(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
