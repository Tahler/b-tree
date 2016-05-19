package edu.neumont.csc360.btree.cache;

public class LinkedList<T> {
    private int size;
    private Node<T> head;
    private Node<T> tail;

    public LinkedList() {
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    /**
     * Expects only one node, not a linkedlist
     * @param newTail A single node with no prev or next.
     */
    public void append(Node<T> newTail) {
        newTail.prev = null;
        newTail.next = null;

        if (this.head == null) {
            this.head = newTail;
            this.tail = this.head;
        } else {
            newTail.prev = this.tail;
            newTail.next = null;
            this.tail.next = newTail;
            this.tail = newTail;
        }
        this.size += 1;
    }

    public void append(T data) {
        this.append(new Node<>(data));
    }

    /**
     * Expects only one node, not a linkedlist
     * @param newHead A single node with no prev or next.
     */
    public void prepend(Node<T> newHead) {
        newHead.prev = null;
        newHead.next = null;

        if (this.head == null) {
            this.head = newHead;
            this.tail = this.head;
        } else {
            newHead.prev = null;
            newHead.next = this.head;
            this.head.prev = newHead;
            this.head = newHead;
        }
        this.size += 1;
    }

    public void prepend(T data) {
        this.prepend(new Node<T>(data));
    }

    public T removeHead() {
        return this.remove(this.head);
    }

    public T removeTail() {
        return this.remove(this.tail);
    }

    public T remove(Node<T> node) {
        if (node != null) {
            Node<T> prev = node.prev;
            Node<T> next = node.next;

            if (prev == null) {
                this.head = next;
            } else {
                prev.next = next;
            }

            if (next == null) {
                this.tail = prev;
            } else {
                next.prev = prev;
            }
        }
        return node == null ? null : node.data;
    }

    public void moveToHead(Node<T> node) {
        this.remove(node);
        this.prepend(node);
    }

    public int size() {
        return this.size;
    }

    public Node<T> getHead() {
        return head;
    }

    public Node<T> getTail() {
        return this.tail;
    }

    public void clear() {
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    public static class Node<T> {
        private Node<T> prev;
        private Node<T> next;
        private T data;

        public Node() {
            this(null, null, null);
        }

        public Node(T data) {
            this(null, null, data);
        }

        public Node(Node<T> prev, Node<T> next, T data) {
            this.prev = prev;
            this.next = next;
            this.data = data;
        }

        public Node<T> getPrev() {
            return this.prev;
        }

        public void setPrev(Node<T> prev) {
            this.prev = prev;
        }

        public Node<T> getNext() {
            return this.next;
        }

        public void setNext(Node<T> next) {
            this.next = next;
        }

        public T getData() {
            return this.data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }
}
