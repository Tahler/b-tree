package edu.neumont.csc360.btree.storage.cache;

import sun.misc.LRUCache;

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
            data = node.getData().getValue();
            this.lruList.moveToHead(node);
        }
        return data;
    }

    public void updateValue(K key, V value) {
        LinkedList.Node<KeyValuePair<K, V>> node = this.map.get(key);
        V data = null;
        if (node != null) {
            node.getData().setValue(value);
            this.lruList.moveToHead(node);
        }
    }

    public V remove(K key) {
        LinkedList.Node<KeyValuePair<K, V>> node = this.map.remove(key);
        V data = null;
        if (node != null) {
            data = this.lruList.remove(node).getValue();
        }
        return data;
    }

    public void clear() {
        this.map.clear();
        this.lruList.clear();
    }

    private void diminishIfNecessary() {
        if (this.size > this.sizeLimit) {
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

        public K getKey() {
            return this.key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return this.value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }
}
