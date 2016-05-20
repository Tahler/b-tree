package edu.neumont.csc360.btree;

public class KeyValuePair {
    protected int key;
    protected int value;

    public KeyValuePair(int key, int value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
