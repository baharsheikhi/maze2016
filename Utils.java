import java.util.*;

import javalib.worldimages.Posn;
import tester.Tester;

//a class to represent utils 
class Utils {
    // are the two posns equal?
    boolean posnEquals(Posn p1, Posn p2) {
        return p1.x == p2.x && p1.y == p2.y;
    }

    // to help the no reps method
    <T> boolean noRepsHelp(ArrayList<T> arr, int indx) {
        if (indx == arr.size()) {
            return true;
        }

        T temp = arr.remove(0);

        if (arr.contains(temp)) {
            return false;
        }

        else {
            return this.noRepsHelp(arr, indx++);
        }
    }

    // checks to see if there are any repetition
    <T> boolean noReps(ArrayList<T> arr) {
        return this.noRepsHelp(arr, 0);
    }

    // swaps the elements of the arraylist at the given indices
    <T> void swap(ArrayList<T> arr, int one, int two) {
        T temp = arr.get(one);
        arr.set(one, arr.get(two));
        arr.set(two, temp);
    }

    // Returns the index where the pivot element ultimately ends up in the
    // sorted source
    // EFFECT: Modifies the source list in the range [loIdx, hiIdx) such that
    // all values to the left of the pivot are less than (or equal to) the pivot
    // and all values to the right of the pivot are greater than it
    <T> int partition(ArrayList<T> source, IComparator<T> comp, int loIdx, 
            int hiIdx, T pivot) {
        int curLo = loIdx;
        int curHi = hiIdx - 1;
        while (curLo < curHi) {
            // Advance curLo until we find a too-big value (or overshoot the end
            // of the list)
            while (curLo < hiIdx && comp.comp(source.get(curLo), pivot) <= 0) {
                curLo = curLo + 1;
            }
            // Advance curHi until we find a too-small value (or undershoot the
            // start of the list)
            while (curHi >= loIdx && comp.comp(source.get(curHi), pivot) > 0) {
                curHi = curHi - 1;
            }
            if (curLo < curHi) {
                swap(source, curLo, curHi);
            }
        }
        swap(source, loIdx, curHi); // place the pivot in the remaining spot
        return curHi;
    }

    // In ArrayUtils
    // EFFECT: Sorts the given ArrayList according to the given comparator
    <T> void quicksort(ArrayList<T> arr, IComparator<T> comp) {
        quicksortHelp(arr, comp, 0, arr.size());
    }

    // EFFECT: sorts the source array according to comp, in the range of indices
    // [loIdx, hiIdx)
    <T> void quicksortHelp(ArrayList<T> source, IComparator<T> comp, int loIdx, 
            int hiIdx) {
        // Step 0: check for completion
        if (loIdx >= hiIdx) {
            return; // There are no items to sort
        }
        // Step 1: select pivot
        T pivot = source.get(loIdx);
        // Step 2: partition items to lower or upper portions of the temp list
        int pivotIdx = partition(source, comp, loIdx, hiIdx, pivot);
        // Step 3: sort both halves of the list
        quicksortHelp(source, comp, loIdx, pivotIdx);
        quicksortHelp(source, comp, pivotIdx + 1, hiIdx);
    }
}

// to represent function objects that compare
interface IComparator<T> {
    int comp(T t1, T t2);
}

// a class to represent edge weight equality
class CompEdge implements IComparator<Edge> {

    // returns negative if e2 is greater
    // than e1
    // positive if e1 is greater than e2
    // 0 if =
    public int comp(Edge e1, Edge e2) {
        return e1.weight - e2.weight;
    }
}

// a class to represent edge node equality
class CompEdgeEquality implements IComparator<Edge> {
    // to compare
    public int comp(Edge t1, Edge t2) {
        Utils util = new Utils();
        // TODO Auto-generated method stub
        if (util.posnEquals(t1.from.id, t2.from.id) && util.posnEquals(t1.to.id, 
                t2.to.id)) {
            return 0;
        }

        else {
            return -1;
        }
    }

}

// a class to represent number equality
class NumberEquality implements IComparator<Integer> {

    // to compare
    public int comp(Integer t1, Integer t2) {
        return t1 - t2;
    }

}

// to test the methods above
class ExamplesArray {

    ArrayList<Integer> ints1;
    ArrayList<Integer> ints2;
    ArrayList<Integer> ints2Sorted;
    ArrayList<Integer> ints3;
    Utils util;
    IComparator<Integer> comp;

    // the initial conditions
    void initData() {
        this.ints1 = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5));
        this.ints2 = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 1, 5));
        this.ints3 = new ArrayList<Integer>(0);
        this.ints2Sorted = new ArrayList<Integer>(Arrays.asList(1, 1, 2, 3, 5));
        this.util = new Utils();
        this.comp = new NumberEquality();

    }

    // tests repetition
    void testRepst(Tester t) {
        this.initData();
        t.checkExpect(this.util.noReps(this.ints1), true);
        t.checkExpect(this.util.noReps(this.ints2), false);
        t.checkExpect(this.util.noReps(this.ints3), true);
    }

    // tests posns
    void testPosns(Tester t) {
        this.initData();
        t.checkExpect(util.posnEquals(new Posn(0, 0), new Posn(0, 0)), true);
        t.checkExpect(util.posnEquals(new Posn(0, 0), new Posn(2, 0)), false);
    }

    // tests quicksort
    void testSort(Tester t) {
        this.initData();
        this.util.quicksort(this.ints2, comp);
        t.checkExpect(this.ints2, this.ints2Sorted);
    }

}
