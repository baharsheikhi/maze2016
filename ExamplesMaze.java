import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javalib.worldimages.Posn;
import tester.*;

public class ExamplesMaze {
    Node<Vertex> abcNode;
    Node<Vertex> bcdNode;
    Node<Vertex> cdeNode;
    Node<Vertex> defNode;
    Sentinel<Vertex> s1;
    
    Deque<Vertex> deque1;
    Stack<Vertex> stack1;
    Queue<Vertex> queue1;
        
    //////////////////////
    
    Graph graph1;
    Graph graph1Copy;
    Graph graph1Copy2;

    Graph graphBuild;
    Graph graphSpan;

    Vertex aVertice;
    Vertex bVertice;
    Vertex cVertice;
    Vertex dVertice;
    Vertex eVertice;
    Vertex fVertice;
    Vertex gVertice;

    Edge ec;
    Edge cd;
    Edge ab;
    Edge be;
    Edge bc;
    Edge fd;
    Edge ae;
    Edge bf;

    Posn a;
    Posn b;
    Posn c;
    Posn d;
    Posn e;
    Posn f;

    // a 0
    // b 1
    // c 2
    // d 3
    // e 4
    // f 5

    HashMap<Posn, Posn> graphSpanReps;
    ArrayList<Vertex> graphSpanNodes;
    ArrayList<Edge> graphSpanEdges;
    ArrayList<Edge> graphFinalEdges;
    MazeWorld maze1;
    Player playerWithRight;
    Player playerNoRight;
    Player playerWithDown;
    Player playerNoDown;
    Player playerDownBlocked;
    Player playerUpBlocked;
    //the initial conditions 
    void initData() { 
        this.graph1 = new Graph(3, 2);
        // this.graph1Copy = new Graph(3, 2);
        // this.graph1Copy2 = new Graph(3, 2);

        this.graphBuild = new Graph();
        this.graphSpan = new Graph();

        this.aVertice = new Vertex(0, 0);
        this.bVertice = new Vertex(1, 0);
        this.cVertice = new Vertex(2, 0);
        this.dVertice = new Vertex(0, 1);
        this.eVertice = new Vertex(1, 1);
        this.fVertice = new Vertex(2, 1);
        
        this.abcNode = new Node<Vertex>(this.aVertice);
        this.bcdNode = new Node<Vertex>(this.bVertice);
        this.cdeNode = new Node<Vertex>(this.cVertice);
        this.defNode = new Node<Vertex>(this.dVertice);
        this.s1 = new Sentinel<Vertex>();
        
        this.abcNode.updateNode(this.bcdNode, this.s1);
        this.bcdNode.updateNode(this.cdeNode, this.abcNode);
        this.cdeNode.updateNode(this.defNode, this.bcdNode);
        this.defNode.updateNode(this.s1, this.cdeNode);
        this.s1.updateNode(this.abcNode, this.defNode);
        
        this.deque1 = new Deque<Vertex>(this.s1);
        this.stack1 = new Stack<Vertex>(this.deque1);
        this.queue1 = new Queue<Vertex>(this.deque1);
        
        this.graphSpanNodes = new ArrayList<Vertex>(
                Arrays.asList(this.aVertice, this.bVertice, this.cVertice, 
                        this.dVertice, 
                        this.eVertice, this.fVertice));

        this.ec = new Edge(this.eVertice, this.cVertice, 15);
        this.cd = new Edge(this.cVertice, this.dVertice, 35);
        this.ab = new Edge(this.aVertice, this.bVertice, 30);
        this.be = new Edge(this.bVertice, this.eVertice, 35);
        this.bc = new Edge(this.bVertice, this.cVertice, 40);
        this.fd = new Edge(this.fVertice, this.dVertice, 50);
        this.ae = new Edge(this.aVertice, this.eVertice, 50);
        this.bf = new Edge(this.bVertice, this.fVertice, 50);
        
        //the edges before 
        this.eVertice.addEdge(this.ec);
        this.cVertice.addEdge(this.cd);
        this.aVertice.addEdge(this.ab);
        this.bVertice.addEdge(this.be);
        this.bVertice.addEdge(this.bc);
        this.bVertice.addEdge(this.bf);
        this.aVertice.addEdge(this.ae);
        this.fVertice.addEdge(this.fd);


        this.graphSpanEdges = new ArrayList<Edge>(
                Arrays.asList(this.ec, this.cd, this.ab, this.be, this.bc, 
                        this.fd, this.ae, this.bf));
        this.graphFinalEdges = new ArrayList<Edge>(
                Arrays.asList(this.ec, this.cd, this.ab, this.be, this.fd));

        this.a = new Posn(0, 0);
        this.b = new Posn(1, 0);
        this.c = new Posn(2, 0);
        this.d = new Posn(0, 1);
        this.e = new Posn(1, 1);
        this.f = new Posn(2, 1);

        this.graphSpanReps = new HashMap<Posn, Posn>();
        this.graphSpanReps.put(this.a, this.a);
        this.graphSpanReps.put(this.b, this.b);
        this.graphSpanReps.put(this.c, this.c);
        this.graphSpanReps.put(this.d, this.d);
        this.graphSpanReps.put(this.e, this.e);
        this.graphSpanReps.put(this.f, this.f);

        this.graphSpan.representatives = this.graphSpanReps;
        this.graphSpan.workList = this.graphSpanEdges;
        this.graphSpan.allVertices = this.graphSpanNodes;

        this.graphSpan.width = 3;
        this.graphSpan.height = 2;

        this.aVertice.id = this.a;
        this.bVertice.id = this.b;
        this.cVertice.id = this.c;
        this.dVertice.id = this.d;
        this.eVertice.id = this.e;
        this.fVertice.id = this.f;
        
        this.playerWithRight = new Player(this.aVertice, this.graphSpan);
        this.playerNoRight = new Player(this.bVertice, this.graphSpan);
        this.playerWithDown = new Player(this.bVertice, this.graphSpan);
        this.playerNoDown = new Player(this.dVertice, this.graphSpan);
        this.playerDownBlocked = new Player(this.cVertice, this.graphSpan);
        this.playerUpBlocked = new Player(this.fVertice, this.graphSpan);
        
        // this.maze1 = new MazeWorld(this.graph1);
    }
    //tests the build graph method 
    void testBuildGraph(Tester t) {
        this.initData();

        this.graphBuild.width = 3;
        this.graphBuild.height = 2;
        // check that the vertices are null
        t.checkExpect(this.graphBuild.allVertices, null);
        // Initializes the nodes
        this.graphBuild.allVertices = this.graphBuild.initializeNodes(3, 2);
        int count = 0;
        while (count < 5) {
            for (int y = 0; y < 2; y++) {
                for (int x = 0; x < 3; x++) {
                    t.checkExpect(this.graphBuild.allVertices.get(count).x, x);
                    t.checkExpect(this.graphBuild.allVertices.get(count).y, y);
                    // check that there are no edges yet
                    t.checkExpect(this.graphBuild.allVertices.get(
                            count).outEdges.size(), 0);
                    // System.out.println("ex: " +
                    // this.graphBuild.allVertices.get(count).x);
                    // System.out.println("why: " +
                    // this.graphBuild.allVertices.get(count).y);

                    count++;

                }
            }
        }

        // to prevent a null pointer
        this.graphBuild.workList = new ArrayList<Edge>();
        // apply the next method we are testing
        this.graphBuild.initializeEdges(this.graphBuild.allVertices);

        for (Vertex n : this.graphBuild.allVertices) {
            // check that the number of edges for each node is in the range of
            // 1-5
            t.checkExpect(n.outEdges.size() >= 0 && n.outEdges.size() <= 4, true);
            // the worklist is being added to
            t.checkExpect(this.graphBuild.workList.size() > 0, true);
            for (int i = 0; i < n.outEdges.size(); i++) {
                // System.out.println("FROM: " + n.outEdges.get(i).from.id);
                // System.out.println("TO: " + n.outEdges.get(i).to.id);
            }
        }

        // check how we sort the worklist
        Utils util = new Utils();
        util.quicksort(this.graphBuild.workList, new CompEdge());
        int start = this.graphBuild.workList.get(0).weight;
        t.checkExpect(this.graphBuild.workList.size() > 0, true);
        // check that the next weight is greater than the previous one
        for (int i = 0; i < this.graphBuild.workList.size(); i++) {
            t.checkExpect(this.graphBuild.workList.get(i).weight >= start, 
                    true);
            start = this.graphBuild.workList.get(i).weight;
        }

        // check that the to and from of each edge of each node is not null and
        // in the range of 0-4
        for (Vertex n : this.graphBuild.allVertices) {
            for (Edge e : n.outEdges) {
                t.checkExpect(e.to != null, true);
                t.checkExpect(e.from != null, true);
                t.checkExpect(e.to.x >= 0 && e.to.x <= 4, true);
                t.checkExpect(e.from.x >= 0 && e.from.x <= 4, true);
            }
        }

        // check that the hashmap is null
        t.checkExpect(this.graphBuild.representatives, null);
        this.graphBuild.representatives = this.graphBuild.initHash();
        // check that every key's value is set to itself
        int c = 0;
        while (c < (this.graphBuild.width * this.graphBuild.height)) {
            for (int y = 0; y < this.graphBuild.height; y++) {
                for (int x = 0; x < this.graphBuild.width; x++) {
                    t.checkExpect(this.graphBuild.representatives.get(
                            this.graphBuild.allVertices.get(c).id),
                            this.graphBuild.allVertices.get(c).id);
                    // every key's root should be itself
                    t.checkExpect(this.graphBuild.findRoot(
                            this.graphBuild.allVertices.get(c).id),
                            this.graphBuild.findRoot(
                                    this.graphBuild.allVertices.get(c).id));
                    // check that there should be as many roots as there are
                    // nodes ids
                    t.checkExpect(this.graphBuild.countRoots().size(), 
                            this.graphBuild.allVertices.size());
                    c++;
                }
            }
        }

        this.graphBuild.edgesInTree = new ArrayList<Edge>();
        t.checkExpect(this.graphBuild.workList.size() > 0);
        this.graphBuild.edgesInTree = this.graphBuild.makeEdges();
        t.checkExpect(this.graphBuild.edgesInTree.size() > 0);
        // one main root
        t.checkExpect(this.graphBuild.countRoots().size(), 1);
        // all the edges in edges in tree should be unique
        // no repetition in the edges in tree
        t.checkExpect(util.noReps(this.graphBuild.edgesInTree), true);
        // checking in a loop that all the roots are the same
        Posn ultimateRoot = 
                this.graphBuild.representatives.get(
                        this.graphBuild.allVertices.get(0).id);
        for (int i = 0; i < this.graphBuild.allVertices.size(); i++) {
            t.checkExpect(this.graphBuild.findRoot(
                    this.graphBuild.representatives.get(
                            this.graphBuild.allVertices.get(i).id)), 
                    ultimateRoot);

        }

    }
    //tests good vertices 
    void testGoodVertices(Tester t) {
        this.initData();
        Utils util = new Utils();
        this.graphSpan.edgesInTree = new ArrayList<Edge>();
        ArrayList<Edge> allEdgesBefore = new ArrayList<Edge>();
        //no one has any paths yet 
        t.checkExpect(this.aVertice.hasPathRight, false);
        t.checkExpect(this.bVertice.hasPathDown, false);
        t.checkExpect(this.dVertice.hasPathDown, false);
        t.checkExpect(this.cVertice.hasPathRight, false);
        t.checkExpect(this.eVertice.hasPathDown, false);
        t.checkExpect(this.fVertice.hasPathDown, false);
        t.checkExpect(this.fVertice.hasPathRight, false);
        //checking the initial conditions of all vertices 
        for (Vertex n : this.graphSpan.allVertices) {
            for (Edge e : n.outEdges) {
                allEdgesBefore.add(e);
            }
        } 
        //checking the initial conditions of all vertices 
        for (Edge e : this.graphSpanEdges) {
            t.checkExpect(allEdgesBefore.contains(e));
        } 
        //checking the initial conditions of all vertices 
        for (Edge e : allEdgesBefore) {
            t.checkExpect(this.graphSpanEdges.contains(e));
        }
        this.graphSpan.makeEdges();
        this.graphSpan.goodNodes(this.graphSpan.edgesInTree);
        //check that it removed a cycle
        t.checkExpect(this.graphSpan.allVertices.contains(this.bc), false);
        t.checkExpect(this.graphSpan.allVertices.contains(this.ae), false);
        //check that the edge parents are unique, therefore no cycles  
        ArrayList<Posn> checkNoCycles = new ArrayList<Posn>();
        for (Vertex n : this.graphSpan.allVertices) {
            checkNoCycles.add(n.id);
        }
        t.checkExpect(util.noReps(checkNoCycles), true);
        ArrayList<Edge> allEdgesAfter = new ArrayList<Edge>();
        //check that allVertices has been modified as desired 
        for (Vertex n : this.graphSpan.allVertices) {
            for (Edge e : n.outEdges) {
                allEdgesAfter.add(e);
            }
        }
        //check that allVertices has been modified as desired 
        for (Edge e : this.graphFinalEdges) {
            t.checkExpect(allEdgesAfter.contains(e));
        } 
        //check that allVertices has been modified as desired 
        for (Edge e : allEdgesAfter) {
            t.checkExpect(this.graphFinalEdges.contains(e));
        }
        //check that the list of edges is different 
        t.checkExpect(allEdgesAfter.containsAll(allEdgesBefore), false);
        
        //are has path right and down working properly?
        //this is a side effect of the goodNodes method 
        t.checkExpect(this.aVertice.hasPathRight, true);
        t.checkExpect(this.bVertice.hasPathDown, true);
        t.checkExpect(this.dVertice.hasPathDown, false);
        t.checkExpect(this.dVertice.hasPathRight, false);
        t.checkExpect(this.cVertice.hasPathRight, false);
        t.checkExpect(this.eVertice.hasPathDown, false);
        t.checkExpect(this.fVertice.hasPathDown, false);
        t.checkExpect(this.fVertice.hasPathRight, false);
        
        MazeWorld mazeGraphSpan = new MazeWorld(this.graphSpan);
        //mazeGraphSpan.bigBang(500, 500);
    }
    
    void testPlayer(Tester t) {
        this.initData();
        
        Utils util = new Utils();
        this.graphSpan.edgesInTree = new ArrayList<Edge>();
        this.graphSpan.makeEdges();
        this.graphSpan.goodNodes(this.graphSpan.edgesInTree);
        
        //the initial conditions
        t.checkExpect(this.playerWithRight.current, this.aVertice);
        t.checkExpect(this.playerNoRight.current, this.bVertice);
        t.checkExpect(this.playerWithDown.current, this.bVertice);
        t.checkExpect(this.playerNoDown.current, this.dVertice);
        t.checkExpect(this.playerDownBlocked.current, this.cVertice);
        t.checkExpect(this.playerUpBlocked.current, this.fVertice);        
        //test canMove
        t.checkExpect(this.playerNoDown.canMove("down"), false);
        t.checkExpect(this.playerWithDown.canMove("down"), true);
        t.checkExpect(this.playerNoRight.canMove("right"), false);
        t.checkExpect(this.playerWithRight.canMove("right"), true);
        t.checkExpect(this.playerUpBlocked.canMove("up"), false);
        t.checkExpect(new Player(this.eVertice, this.graphSpan).canMove("up"), true);
        t.checkExpect(this.playerWithDown.canMove("up"), false);
        t.checkExpect(this.playerNoRight.canMove("left"), true);
        t.checkExpect(this.playerDownBlocked.canMove("left"), false);
        t.checkExpect(this.playerUpBlocked.canMove("left"), false);
        //move the players
        this.playerWithDown.movePlayer("down");
        this.playerWithRight.movePlayer("right");
        this.playerNoRight.movePlayer("right");
        this.playerNoDown.movePlayer("down");
        this.playerDownBlocked.movePlayer("down");
        //checks side effects
        t.checkExpect(this.playerWithDown.current, this.eVertice);
        t.checkExpect(this.playerWithRight.current, this.bVertice);
        t.checkExpect(this.playerNoRight.current, this.bVertice);
        t.checkExpect(this.playerNoDown.current, this.dVertice);
        t.checkExpect(this.playerDownBlocked.current, this.cVertice);
        //continues moving and checking side effects
        this.playerWithDown.movePlayer("up");
        t.checkExpect(this.playerWithDown.current, this.bVertice);
        this.playerDownBlocked.movePlayer("up");
        t.checkExpect(this.playerDownBlocked.current, this.cVertice);
        this.playerUpBlocked.movePlayer("up");
        t.checkExpect(this.playerUpBlocked.current, this.fVertice);
        this.playerDownBlocked.movePlayer("left");
        t.checkExpect(this.playerDownBlocked.current, this.cVertice);
        this.playerNoRight.movePlayer("left");
        t.checkExpect(this.playerNoRight.current, this.aVertice);
        this.playerUpBlocked.movePlayer("left");
        t.checkExpect(this.playerUpBlocked.current, this.fVertice);   
    }

    //tests the big bang and runs it 
    void testBigBang(Tester t) {
        this.initData();

        Graph g = new Graph(20, 20);
        MazeWorld maze1 = new MazeWorld(g);
        
        t.checkExpect(maze1.graph.countRoots().size(), 1);
        
        System.out.println(g.allVertices);

        maze1.bigBang(g.width * 10, g.height * 10, .000005);
 

    }
    //test searching methods 
    void testSearch(Tester t) {
        this.initData();
        
   
    }

}
