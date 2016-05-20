package edu.neumont.csc360.btree;

import edu.neumont.csc360.btree.utils.ByteUtils;

public class Node {
    protected static final int NODE_METADATA_SIZE = 5;
    protected static final int KEY_VALUE_PAIR_SIZE = 8;

    // Not persisted
    private BTree bTree;
    private Node parent;
    private int index;

    // Persisted
    private boolean isLeaf;
    private int size;
    private KeyValuePair[] data;

    public Node(BTree bTree, Node parent, int index, boolean isLeaf, int size, KeyValuePair[] data) {
        this.bTree = bTree;
        this.parent = parent;
        this.index = index;
        this.isLeaf = isLeaf;
        this.size = size;
        this.data = data;
    }

    protected static Node fromByteArray(int[] bytes, BTree bTree, Node parent, int nodeIndex) {
        boolean isLeaf = ByteUtils.booleanFromByteInByteArray(bytes, 0);
        int size = ByteUtils.intFromBytesInByteArray(bytes, 1);

        int numKvps = (bytes.length - NODE_METADATA_SIZE) / KEY_VALUE_PAIR_SIZE;
        KeyValuePair[] data = new KeyValuePair[numKvps];
        for (int i = 0; i < numKvps; i++) {
            int offset = NODE_METADATA_SIZE + i * KEY_VALUE_PAIR_SIZE;

            int key = ByteUtils.intFromBytesInByteArray(bytes, offset);
            int value = ByteUtils.intFromBytesInByteArray(bytes, offset + 4);

            data[i] = new KeyValuePair(key, value);
        }

        return new Node(bTree, parent, nodeIndex, isLeaf, size, data);
    }

    protected int[] toByteArray() {
        int[] bytes = new int[NODE_METADATA_SIZE + this.data.length * KEY_VALUE_PAIR_SIZE];

        ByteUtils.writeBooleanToByteArray(this.isLeaf, bytes, 0);
        ByteUtils.writeBytesFromIntToByteArray(this.size, bytes, 1);
        for (int i = 0; i < this.data.length; i++) {
            KeyValuePair kvp = this.data[i];
            if (kvp != null) {
                int offset = NODE_METADATA_SIZE + i * KEY_VALUE_PAIR_SIZE;
                ByteUtils.writeBytesFromIntToByteArray(kvp.key, bytes, offset);
                ByteUtils.writeBytesFromIntToByteArray(kvp.value, bytes, offset + 4);
            }
        }

        return bytes;
    }

    /**
     * Traverses down the tree until reaching a leaf node, then inserts the key-value-pair.
     */
    public void addKey(int key, int value) {
        if (this.isLeaf) {
            if (this.isRoot()) {
                this.insertKey(key, value);
            } else {
                int oldMaxKey = this.maxKey();
                this.insertKey(key, value);
                int newMaxKey = this.maxKey();
                if (oldMaxKey != newMaxKey) {
                    this.parent.updateKeyFromChild(this.index, newMaxKey);
                }
            }
            this.rewrite();
        } else {
            Node child = this.childWithKey(key);
            child.addKey(key, value);
        }
    }

    public int getValue(int key) {
        int value;
        if (this.isLeaf) {
            KeyValuePair foundKvp = this.searchForKey(key);
            value = foundKvp.value; // technically could be null
        } else {
            Node child = this.childWithKey(key);
            value = child.getValue(key);
        }
        return value;
    }

    public void updateKeysValue(int key, int value) {
        if (this.isLeaf) {
            KeyValuePair toBeUpdated = this.searchForKey(key);
            toBeUpdated.value = value;
            this.rewrite();
        } else {
            Node child = this.childWithKey(key);
            child.updateKeysValue(key, value);
        }
    }

    /**
     * Traverses down the tree until reaching a leaf node, then removes the key-value-pair from that leaf node.
     */
    public void deleteKey(int key) {
        if (this.isLeaf) {
            this.removeKey(key);
        } else {
            Node child = this.childWithKey(key);
            child.deleteKey(key);
        }
    }

    private boolean isRoot() {
        return this.parent == null;
    }

    private KeyValuePair searchForKey(int key) {
        assert this.size > 0;

        KeyValuePair foundKvp = null;
        int loIndex = 0;
        int hiIndex = this.size - 1;
        while (loIndex <= hiIndex) {
            int midIndex = loIndex + (hiIndex - loIndex) / 2;
            KeyValuePair kvp = this.data[midIndex];
            int mid = kvp.key;
            if (key < mid) {
                hiIndex = midIndex - 1;
            } else if (key > mid) {
                loIndex = midIndex + 1;
            } else { // key == mid
                foundKvp = kvp;
                break;
            }
        }

        assert foundKvp != null;
        return foundKvp;
    }

    private KeyValuePair searchForValue(int value) {
        assert this.size > 0;

        KeyValuePair foundKvp = null;
        int loIndex = 0;
        int hiIndex = this.size - 1;
        while (loIndex <= hiIndex) {
            int midIndex = loIndex + (hiIndex - loIndex) / 2;
            KeyValuePair kvp = this.data[midIndex];
            int mid = kvp.value;
            if (value < mid) {
                hiIndex = midIndex - 1;
            } else if (value > mid) {
                loIndex = midIndex + 1;
            } else { // value == mid
                foundKvp = kvp;
                break;
            }
        }

        assert foundKvp != null;
        return foundKvp;
    }

    /**
     * Treats this node's values as routes to the Node containing the key.
     */
    private Node childWithKey(int key) {
        assert !this.isLeaf;

        int childIndex = this.data[this.size - 1].value; // default value, in case trying to find a key greater than any current keys
        for (int i = 0; i < this.size; i++) {
            KeyValuePair kvp = this.data[i];
            if (key <= kvp.key) {
                childIndex = kvp.value;
                break;
            }
        }

        return this.bTree.nodeStore.getNode(childIndex, this.bTree, this);
    }

    private void splitAndAdd(int key, int value) {
        assert this.isFull();

        // split data into two different arrays
        int dataLength = this.data.length;
        int firstHalfLength = dataLength / 2;
        int secondHalfLength = dataLength - firstHalfLength;
        KeyValuePair[] firstHalf = new KeyValuePair[dataLength];
        System.arraycopy(this.data, 0, firstHalf, 0, firstHalfLength);
        KeyValuePair[] secondHalf = new KeyValuePair[dataLength];
        System.arraycopy(this.data, firstHalfLength, secondHalf, 0, secondHalfLength);

        // adjust this node to have first half
        this.size = firstHalfLength;
        this.data = firstHalf;

        // create a new node with second half
        int otherNodeIndex = this.bTree.nodeStore.allocate();
        Node otherNode = new Node(this.bTree, this.parent, otherNodeIndex, this.isLeaf, secondHalfLength, secondHalf);

        // add the key that was supposed to be added
        if (key < this.maxKey()) {
            this.insertKey(key, value);
        } else {
            otherNode.insertKey(key, value);
        }

        int thisMax = this.maxKey();
        int otherMax = otherNode.maxKey();

        if (this.isRoot()) {
            int newRootIndex = this.bTree.nodeStore.allocate();
            KeyValuePair[] rootRoutes = new KeyValuePair[3];
            rootRoutes[0] = new KeyValuePair(thisMax, this.index);
            rootRoutes[1] = new KeyValuePair(otherMax, otherNode.index);
            Node newRoot = new Node(this.bTree, null, newRootIndex, false, 2, rootRoutes);

            this.bTree.nodeStore.putNode(newRootIndex, newRoot);

            this.bTree.root = newRoot;

            this.parent = newRoot;
            otherNode.parent = newRoot;
        } else {
            this.parent.updateKeyFromChild(this.index, thisMax);
            this.parent.insertKey(otherMax, otherNode.index);
        }

        this.rewrite();
        this.bTree.nodeStore.putNode(otherNodeIndex, otherNode);
    }

    private boolean isFull() {
        return this.size == this.bTree.nodeCapacity;
    }

    private boolean isEmpty() {
        return this.size == 0;
    }

    private int maxKey() {
        return this.size > 0 ? this.data[this.size - 1].key : 0;
    }

    private void insertKey(int key, int value) {
        if (this.isFull()) {
            this.splitAndAdd(key, value);
        } else {
            // find it, shifting as you insert
            int previousIndex = this.size;
            int currentIndex = previousIndex - 1;
            while (currentIndex >= 0 && key < this.data[currentIndex].key) {
                this.data[previousIndex] = this.data[currentIndex];
                previousIndex -= 1;
                currentIndex -= 1;
            }
            this.data[previousIndex] = new KeyValuePair(key, value);
            this.size += 1;
        }
    }

    private void updateKeyFromChild(int routeIndex, int newKey) {
        int oldMaxKey = this.maxKey();

        KeyValuePair kvpToUpdate = this.searchForValue(routeIndex);
        kvpToUpdate.key = newKey;

        int newMaxKey = this.maxKey();
        if (!this.isRoot() && oldMaxKey != newMaxKey) {
            this.parent.updateKeyFromChild(this.index, newMaxKey);
        }

        this.rewrite();
    }

    private void removeKey(int key) {
        int oldMaxKey = this.maxKey();
        KeyValuePair previous = null;
        int currentIndex = this.data.length - 1;
        boolean found = false;
        while (!found || currentIndex >= 0) {
            KeyValuePair current = this.data[currentIndex];
            int mid = current.key;
            found = mid == key;
            this.data[currentIndex] = previous;
            previous = current;
            currentIndex -= 1;
        }
        int deletedKey = previous.key;
        this.size -= 1;
        if (this.isEmpty()) {
            if (this.isRoot()) {
                this.isLeaf = true;
            } else {
                this.parent.removeKey(deletedKey);
                this.bTree.nodeStore.deallocate(this.index);
            }
        } else {
            if (oldMaxKey == deletedKey) {
                this.parent.updateKeyFromChild(this.index, this.maxKey());
            }
            this.rewrite();
        }
    }

    private void rewrite() {
        assert this.index >= 0;

        this.bTree.nodeStore.putNode(this.index, this);
    }

    protected static class KeyValuePair {
        private int key;
        private int value;

        public KeyValuePair(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
}
