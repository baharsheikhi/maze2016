import java.util.ArrayList;
import java.util.Iterator;

import tester.Tester;
//an interface to represent stacks and queues
interface ICollection<T> {

    void add(T item);

    boolean isEmpty();

    T remove();
}
//to represent a stack
class Stack<T> implements ICollection<T> {
    Deque<T> contents;

    Stack(Deque<T> contents) {
        this.contents = contents;
    }

    // adds an item to the head of the list
    void push(T item) {
        this.contents.addAtHead(item);
    }
    //is the stack empty?
    public boolean isEmpty() {
        return this.contents.size() == 0;
    }

    // removes and returns the head of the list
    T pop() {
        return this.contents.removeFromHead();
    }
    //adds an item to the stack
    public void add(T item) {
        this.push(item);

    }
    //removes an item from the stack
    public T remove() {
        return this.pop();

    }

}
//to represent a queue 
class Queue<T> implements ICollection<T> {
    Deque<T> contents;

    Queue(Deque<T> contents) {
        this.contents = contents;
    }

    // adds an item to the tail of the list
    void enqueue(T item) {
        this.contents.addAtTail(item);
    }
    //is the queue empty
    public boolean isEmpty() {
        return this.contents.size() == 0;
    }

    // removes and returns the head of the list
    T dequeue() {
        return this.contents.removeFromTail();
    }
    //adds t to the iterator 
    public void add(T item) {
        this.enqueue(item);

    }
    //removes a t from the queue
    public T remove() {
        return this.dequeue();

    }

}

// Represents a boolean-valued question over values of type T
interface IPred<T> {
    boolean apply(T t);
}

// a class that represents an ipred that tests to see if this node's length
// is less then the given
class SpecifiedLength implements IPred<String> {
    int len;

    SpecifiedLength(int max) {
        this.len = max;
    }

    // does the ipred
    public boolean apply(String s) {
        return s.length() == len;
    }
}

// a class represents an IPred that tests to see if this node is the same
// as the given
class SameNodeData<T> implements IPred<T> {
    ANode<T> node;

    SameNodeData(ANode<T> node) {
        this.node = node;
    }

    // does the Ipred
    public boolean apply(T t) {
        return this.node.sameData(t);
    }
}

// an abstract class to represent nodes
abstract class ANode<T> {
    ANode<T> next;
    ANode<T> prev;

    // constructor
    ANode(ANode<T> next, ANode<T> prev) {
        this.next = next;
        this.prev = prev;
    }

    // EFFECT: adds next and prev to the next and prev fields of ANode
    void updateNode(ANode<T> next, ANode<T> prev) {
        this.next = next;
        this.prev = prev;
    }

    // EFFECT: makes the next that
    void updateNextNode(ANode<T> that) {
        this.next = that;
    }

    // EFFECT: makes the prev that
    void updatePrevNode(ANode<T> that) {
        this.prev = that;
    }

    // returns 1 if node, 0 if sentinel
    abstract int sizeHelpHelp();

    // calls sizeHelpHelp on the next of this ANode
    abstract int sizeHelp();

    // EFFECT: sets this.prev's next to next, and the next's prev to this.prev
    void removeHelp() {
        this.prev.updateNextNode(this.next);
        this.next.updatePrevNode(this.prev);
    }

    // accepts the ipred and applies it to this inode
    abstract boolean accept(IPred<T> pred);

    abstract ANode<T> findHelp(IPred<T> pred);

    // is this node's data the same as the given data
    boolean sameData(T t) {
        return false;
    }

    // returns the data of this sentinel
    T giveData() {
        return null;
    }
    //to help the contains method
    abstract boolean containsHelp(T that);
    //is the node a sentinel?
    abstract boolean isSentinel();

}

// a class to represent nodes
class Node<T> extends ANode<T> {
    T data;

    // constructor that just takes in data
    Node(T data) {
        super(null, null);
        this.data = data;
    }

    // a constructor that takes in a next and previous as well
    // EFFECT: updates n's nodes and p's nodes according to this node
    Node(T data, ANode<T> n, ANode<T> p) {
        super(n, p);
        this.data = data;

        if (n == null || p == null) {
            throw new IllegalArgumentException("nah");
        }

        n.updateNode(n.next, this);
        p.updateNode(this, p.prev);

    }

    // is this node's data the same as the given T
    boolean sameData(T t) {
        return this.data == t;
    }

    // returns its data
    T giveData() {
        return this.data;
    }

    // calls sizeHelpHelp onto the next of this node
    int sizeHelp() {
        return this.next.sizeHelpHelp();
    }

    // returns 1 and recurs
    int sizeHelpHelp() {
        return 1 + this.sizeHelp();
    }

    // applies this Ipred to the data of this node
    boolean accept(IPred<T> pred) {
        return (pred.apply(this.data));
    }

    // helps the find method
    ANode<T> findHelp(IPred<T> pred) {
        if (this.accept(pred)) {
            return this;
        } 
        else {
            return this.next.findHelp(pred);
        }
    }
    //to help the contains method 
    public boolean containsHelp(T that) {
        if (this.data == that) {
            return true;
        } 
        else {
            return this.next.containsHelp(that);
        }
    }
    //is this a sentinel 
    public boolean isSentinel() {
        return false;
    }
}

// to represent sentinels: the beginning and end of a list
class Sentinel<T> extends ANode<T> {
    // constructor
    // EFFECT: updates this sentinel's nodes to this sentinel
    Sentinel() {
        super(null, null);
        this.updateNode(this, this);
    }

    // calls sizeHelpHelp on this sentinel's next
    int sizeHelp() {
        return this.next.sizeHelpHelp();
    }

    // returns 0: ends the recursion
    int sizeHelpHelp() {
        return 0;
    }

    // applies the ipred to this sentinel: true
    boolean accept(IPred<T> pred) {
        return true;
    }

    // helps the find method
    ANode<T> findHelp(IPred<T> pred) {
        return this;
    }

    public boolean containsHelp(T that) {
        return false;
    }

    public boolean isSentinel() {
        return true;
    }
}

// to represents Deques of ANodes
class Deque<T> implements Iterable<T> {
    Sentinel<T> header;

    // constructor that heads header to a new Sentinel
    Deque() {
        this.header = new Sentinel<T>();
    }

    // Convenience constructor
    Deque(Sentinel<T> header) {
        this.header = header;
    }

    // returns the size of the deque
    int size() {
        return this.header.sizeHelp();
    }

    // EFFECT: adds the given T to the head of the deque
    void addAtHead(T t) {
        new Node<T>(t, this.header.next, this.header);
    }

    // EFFECT: adds to the the tail of the deque
    void addAtTail(T t) {
        new Node<T>(t, this.header, this.header.prev);
    }

    // removes this node from the deque at the head and returns its data
    // EFFECT: removes this node
    T removeFromHead() {
        if (this.header.next.giveData() == null) {
            throw new RuntimeException("Can't remove node from an empty list");
        }

        T temp = this.header.next.giveData();

        this.header.next.removeHelp();
        return temp;
    }

    // removes this node from the deque at the head and returns its data
    // EFFECT: removes this node
    T removeFromTail() {
        if (this.header.next.giveData() == null) {
            throw new RuntimeException("Can't remove node from an empty list");
        }

        T temp = this.header.prev.giveData();

        this.header.prev.removeHelp();
        return temp;
    }

    // finds the node that satisfies the IPred and returns it
    ANode<T> find(IPred<T> pred) {
        return this.header.next.findHelp(pred);
    }

    // EFFECT: removes the given node
    void removeNode(ANode<T> node) {
        IPred<T> p = new SameNodeData<T>(node);
        this.find(p).removeHelp();
    }
    //does the deque contain the element?
    public boolean dequeContains(T next) {
        return this.header.next.containsHelp(next);
    }
    //returns an iterator
    public Iterator<T> iterator() {
        return new DequeIterator<T>(this);
    }
}
//to iterate over a deque 
class DequeIterator<T> implements Iterator<T> {
    Deque<T> items;

    DequeIterator(Deque<T> items) {
        this.items = items;
    }
    //do the items have a next?
    public boolean hasNext() {
        return !this.items.header.next.isSentinel();
    }
    //returns the next item in the deque
    public T next() {
        return this.items.removeFromHead();
    }
    //no use 
    public void remove() {

    }

}
//to test deque methods 
class ExamplesDeque {
    Node<String> abc;
    Node<String> bcd;
    Node<String> cde;
    Node<String> def;
    Sentinel<String> s1;
    Deque<String> deque1;

    Sentinel<String> emptyS;
    Deque<String> deque2;

    // the initial conditions of deque and ANode data
    void initData() {
        this.abc = new Node<String>("abc");
        this.bcd = new Node<String>("bcd");
        this.cde = new Node<String>("cde");
        this.def = new Node<String>("def");
        this.s1 = new Sentinel<String>();

        this.abc.updateNode(this.bcd, this.s1);
        this.bcd.updateNode(this.cde, this.abc);
        this.cde.updateNode(this.def, this.bcd);
        this.def.updateNode(this.s1, this.cde);
        this.s1.updateNode(this.abc, this.def);

        this.emptyS = new Sentinel<String>();

        this.deque1 = new Deque<String>(s1);
        this.deque2 = new Deque<String>(emptyS);

    }
    //to test deque iterators 
    void testIter(Tester t) {
        this.initData();
        ArrayList<String> store = new ArrayList<String>();
        t.checkExpect(this.deque1.header.next.giveData(), "abc");
        this.deque1.removeFromHead();
        t.checkExpect(this.deque1.header.next.giveData(), "bcd");
        this.deque1.removeFromHead();
        t.checkExpect(this.deque1.header.next.giveData(), "cde");
        this.deque1.removeFromHead();
        t.checkExpect(this.deque1.header.next.giveData(), "def");

        this.initData();

        DequeIterator<String> dequeIter = 
                new DequeIterator<String>(this.deque1);

        t.checkExpect(dequeIter.hasNext(), true);
        t.checkExpect(dequeIter.next(), this.abc.data);
        t.checkExpect(dequeIter.hasNext(), true);
        t.checkExpect(dequeIter.next(), this.bcd.data);
        t.checkExpect(dequeIter.hasNext(), true);
        t.checkExpect(dequeIter.next(), this.cde.data);

    }

}
