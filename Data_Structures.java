import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javalib.impworld.WorldScene;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.Posn;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.WorldImage;

//a class to represent nodes 
class Vertex {
    int x;
    int y;
    Posn id;
    int index;
    boolean hasPathRight;
    boolean hasPathDown;
    public static final int NODE_SIZE = 10;
    boolean hasBeenTravelled;

    ArrayList<Edge> outEdges; // edges from this node

    // Initialization constructor
    Vertex(int x, int y) {
        this.x = x;
        this.outEdges = new ArrayList<Edge>();
        this.y = y;
        this.id = new Posn(x, y);
        this.hasBeenTravelled = false;
    }
    //EFFECT: sets the has been travelled boolean of the player equal to true 
    void changeTravelled() {
        this.hasBeenTravelled = true;
    }

    // EFFECT: add the given edge to this Node's outEdges
    void addEdge(Edge edge) {
        this.outEdges.add(edge);
    }

    // EFFECT: sets hasPathRight
    void setHasPathRight(Boolean set) {
        this.hasPathRight = set;
    }

    // EFFECT: sets hasPathDown
    void setHasPathDown(Boolean set) {
        this.hasPathDown = set;
    }
    //returns the current up
    Vertex currentUp(Graph g) {
        return g.allVertices.get(this.x + (g.width * (this.y - 1)));
    }
    //returns the current left 
    Vertex currentLeft(Graph g) {
        return g.allVertices.get(this.x - 1 + (g.width * this.y));
    }
    //returns true if the node has a path left  
    boolean hasPathLeft(Graph g) {
        return (this.x > 0) && this.currentLeft(g).hasPathRight;
    }
    //returns true if the node has a path up 
    boolean hasPathUp(Graph g) {
        return (this.y > 0) && (this.currentUp(g).hasPathDown);
    }

    // renders the node
    WorldScene nodeRender(WorldScene scene, int width, int height, 
            ArrayList<Vertex> vertices) {
        WorldImage lightBlue = new RectangleImage(Vertex.NODE_SIZE, 
                Vertex.NODE_SIZE, OutlineMode.SOLID, Color.CYAN);
        WorldImage darkBlue = new RectangleImage(Vertex.NODE_SIZE, 
                Vertex.NODE_SIZE, OutlineMode.SOLID, Color.BLUE);
        WorldImage yellow = new RectangleImage(Vertex.NODE_SIZE, 
                Vertex.NODE_SIZE, OutlineMode.SOLID, Color.YELLOW);

        if (this.hasBeenTravelled) {
            scene.placeImageXY(lightBlue, this.x * Vertex.NODE_SIZE 
                    + Vertex.NODE_SIZE / 2,
                    this.y * Vertex.NODE_SIZE + Vertex.NODE_SIZE / 2);
        }

        if (vertices.contains(this)) {
            scene.placeImageXY(darkBlue, this.x * Vertex.NODE_SIZE + 
                    Vertex.NODE_SIZE / 2,
                    this.y * Vertex.NODE_SIZE + Vertex.NODE_SIZE / 2);
        }

        if (this.x == width - 1 && this.y == height - 1) {
            scene.placeImageXY(yellow, this.x * Vertex.NODE_SIZE + 
                    Vertex.NODE_SIZE / 2,
                    this.y * Vertex.NODE_SIZE + Vertex.NODE_SIZE / 2);
        }

        if (this.x == 0 && this.y == 0) {
            scene.placeImageXY(lightBlue, this.x * Vertex.NODE_SIZE + 
                    Vertex.NODE_SIZE / 2,
                    this.y * Vertex.NODE_SIZE + Vertex.NODE_SIZE / 2);
        }

        if (!this.hasPathDown) {
            WorldImage line = new RectangleImage(Vertex.NODE_SIZE, 1, 
                    OutlineMode.SOLID, Color.BLACK);
            scene.placeImageXY(line, (this.x * Vertex.NODE_SIZE) + 
                    Vertex.NODE_SIZE / 2,
                    ((this.y + 1) * Vertex.NODE_SIZE));

        }

        if (!this.hasPathRight) {
            WorldImage line = new RectangleImage(1, Vertex.NODE_SIZE, 
                    OutlineMode.SOLID, Color.BLACK);
            scene.placeImageXY(line, ((this.x + 1) * Vertex.NODE_SIZE),
                    (this.y * Vertex.NODE_SIZE) + Vertex.NODE_SIZE / 2);

        }

        return scene;
    }
}

// a class to represent edges
class Edge {
    Vertex from;
    Vertex to;
    int weight;

    Edge(Vertex from, Vertex to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    Edge(Vertex from, Vertex to) {
        this.from = from;
        this.to = to;
        this.weight = 0;
    }

}

// a class to represent graphs
class Graph {
    int width;
    int height;
    ArrayList<Vertex> allVertices;
    HashMap<Posn, Posn> representatives;
    ArrayList<Edge> edgesInTree;
    ArrayList<Edge> workList; // all edges in graph, sorted by edge weights;
    Deque<Vertex> alreadySeen;

    // real constructor
    Graph(int width, int height) {
        this.width = width;
        this.height = height;
        // width * height is how many nodes you're going to have
        // creates an arraylist of given dimensions, sets its list of nodes
        // randomly
        this.allVertices = this.initializeNodes(width, height);
        // initializes the edge lists to a list w null elements
        this.edgesInTree = new ArrayList<Edge>();
        this.workList = new ArrayList<Edge>();
        // sets the edges in the list of nodes and then adds these edges
        // into the worklist
        this.initializeEdges(this.allVertices);
        // initializes the hashmap
        this.representatives = this.initHash();

        // sorts the workList's edges based on weight
        Utils util = new Utils();
        util.quicksort(this.workList, new CompEdge());
        // make the edges in tree
        this.edgesInTree = this.makeEdges();
        // updates the vertices so that there are no cycles
        this.goodNodes(this.edgesInTree);
        this.alreadySeen = new Deque<Vertex>();
    }

    Graph() {
        // constructor for testing
    }

    // initializes the node's ids
    // Node: all the edges in outEdges of node are null
    ArrayList<Vertex> initializeNodes(int width, int height) {
        ArrayList<Vertex> ret = new ArrayList<Vertex>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ret.add(new Vertex(x, y));
            }
        }

        return ret;
    }

    // EFFECT: sets this graph's node's edges randomly
    // Decisions made: to set the edge weight randomly according to
    // the size of the given nodes
    // How many connections each node should have minimum: 0 and
    // maximum: Graph.MAX_EDGES
    // add the edge to the list of edges in the graph
    void initializeEdges(ArrayList<Vertex> nodes) {
        Random randy = new Random();

        for (Vertex n : nodes) {
            if (n.x >= this.width - 1) {
                if (n.y >= this.height - 1) {
                    return;
                }

                Edge e = new Edge(n, nodes.get(n.x + 
                        (this.width * (n.y + 1))), randy.nextInt(100));
                n.addEdge(e);
                this.workList.add(e);
            }

            else if (n.y >= this.height - 1) {
                Edge e = new Edge(n, nodes.get(n.x + 1 + 
                        (this.width * n.y)), randy.nextInt(100));
                n.addEdge(e);
                this.workList.add(e);
            }

            else {
                Edge ex = new Edge(n, nodes.get(n.x + 
                        (this.width * (n.y + 1))), randy.nextInt(100));
                Edge ey = new Edge(n, nodes.get(n.x + 1 + 
                        (this.width * n.y)), randy.nextInt(100));
                n.addEdge(ex);
                n.addEdge(ey);
                this.workList.add(ex);
                this.workList.add(ey);
            }
        }
    }

    // returns an initial hashmap, so that every node's representative is itself
    HashMap<Posn, Posn> initHash() {
        HashMap<Posn, Posn> init = new HashMap<Posn, Posn>();

        for (Vertex n : this.allVertices) {
            init.put(n.id, n.id);
        }

        return init;
    }

    // finds the key that has itself as its link, in other words, the "root"
    // of the spanning tree
    Posn findRoot(Posn starting) {
        if (this.representatives.get(starting) == starting) {
            return starting;
        } 
        else {
            return this.findRoot(this.representatives.get(starting));
        }
    }

    // returns the ids of the "roots" in the hashmap
    ArrayList<Posn> countRoots() {
        ArrayList<Posn> roots = new ArrayList<Posn>();

        for (Vertex n : this.allVertices) {
            if (!roots.contains(this.findRoot(n.id))) {
                roots.add(this.findRoot(n.id));
            }
        }

        return roots;
    }

    // runs Kruskal's algorithm on the workList of edges
    // produces the list of edges
    ArrayList<Edge> makeEdges() {
        while (this.edgesInTree.size() < this.allVertices.size() - 1) {
            Edge temp = this.workList.get(0);
            this.workList.remove(0);

            if (this.findRoot(this.representatives.get(temp.to.id)) != this
                    .findRoot(this.representatives.get(temp.from.id))) {
                this.edgesInTree.add(temp);
                this.union((this.representatives.get(
                        this.findRoot(this.representatives.get(temp.to.id)))),
                        this.representatives.get(
                                this.findRoot(this.representatives.get(
                                        temp.from.id))));
            }
        }

        return this.edgesInTree;
    }

    // EFFECT: sets the key's value to the new value
    void union(Posn key, Posn value) {
        this.representatives.put(key, value);
    }

    // EFFECT: updates the edges in the vertex's outedges based on
    // what is in the given arraylist of edges
    void goodNodes(ArrayList<Edge> edges) {
        ArrayList<Edge> temp = new ArrayList<Edge>();

        for (Vertex n : this.allVertices) {
            for (Edge e : n.outEdges) {
                if (this.edgesInTree.contains(e)) {
                    temp.add(e);
                }
            }
            n.outEdges.clear();
            n.outEdges.addAll(temp);

            for (Edge e : n.outEdges) {
                if (e.from == n) {
                    if (e.to.y - 1 == n.y && e.to.x == n.x) {
                        n.setHasPathDown(true);
                    }
                    if (e.to.x - 1 == n.x && e.to.y == n.y) {
                        n.setHasPathRight(true);
                    }
                } else {
                    if (e.from.y - 1 == n.y && e.from.x == n.x) {
                        n.setHasPathDown(true);
                    }
                    if (e.from.x - 1 == n.x && e.from.y == n.y) {
                        n.setHasPathRight(true);
                    }
                }
            }
        }
    }
    //to construct the pathway
    ArrayList<Vertex> reconstruct(HashMap<Vertex, Vertex> hash, 
            Vertex v, Vertex from, ArrayList<Vertex> acc) {
        acc.add(v);

        if (hash.get(v) == from) {
            System.out.println("firstReconstruct: " + acc.size());
            return acc;
        }

        else {
            return this.reconstruct(hash, hash.get(v), from, acc);
        }
    }
    //to help the different search methods 
    ArrayList<Vertex> searchHelp(Vertex from, Vertex to, 
            ICollection<Vertex> worklist) {
        HashMap<Vertex, Vertex> hash = new HashMap<Vertex, Vertex>();

        worklist.add(from);
        from.changeTravelled();

        while (!worklist.isEmpty()) {
            Vertex next = worklist.remove();

            if (alreadySeen.dequeContains(next)) {
                // "discard it"
            }

            else if (next == to) {
                // add this edge to hashmap
                hash.put(from, next);
                System.out.println(hash.get(from) == from);
                return this.reconstruct(hash, next, from, 
                        new ArrayList<Vertex>());
            }

            else {
                for (Edge edgy : this.edgesInTree) {
                    if (edgy.to == next) {
                        if (!alreadySeen.dequeContains(edgy.from)) {
                            worklist.add(edgy.from);
                            hash.put(edgy.from, next);

                        }

                        edgy.to.changeTravelled();
                    }

                    else if (edgy.from == next) {
                        if (!alreadySeen.dequeContains(edgy.to)) {
                            worklist.add(edgy.to);
                            hash.put(edgy.to, next);

                        }

                        edgy.from.changeTravelled();
                    }
                }
            }
            alreadySeen.addAtHead(next);
        }
        return this.reconstruct(hash, to, from, new ArrayList<Vertex>());
    }
    //returns the path for binary search
    ArrayList<Vertex> bfs(Vertex from, Vertex to) {
        Sentinel<Vertex> s = new Sentinel<Vertex>();
        Deque<Vertex> d = new Deque<Vertex>(s);

        return this.searchHelp(from, to, new Queue<Vertex>(d));
    }
    //returns the path for depth first search
    ArrayList<Vertex> dfs(Vertex from, Vertex to) {
        Sentinel<Vertex> s = new Sentinel<Vertex>();
        Deque<Vertex> d = new Deque<Vertex>(s);

        return this.searchHelp(from, to, new Stack<Vertex>(d));
    }

}
