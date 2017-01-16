//import tester.*;
import javalib.impworld.*;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.TextImage;
import javalib.worldimages.WorldEnd;
import javalib.worldimages.WorldImage;

import java.awt.Color;
//import javalib.worldimages.*;
import java.util.*;

//to represent a mazeWorld 
class MazeWorld extends World {
    static final int WIDTH = 100;
    static final int HEIGHT = 100;
    Graph graph;
    ArrayList<Vertex> vertexes;

    // light BLUE
    ArrayList<Vertex> renderer;
    ArrayList<Vertex> allLight;
    Player player;
    // Dark Blue
    ArrayList<Vertex> pathway;
    ArrayList<Vertex> toDraw;
    int tick;
    int tock;

    // constructor
    MazeWorld(Graph grr) {
        this.graph = grr;
        this.vertexes = this.graph.allVertices;
        this.player = new Player(this);
        this.pathway = new ArrayList<Vertex>();
        this.renderer = new ArrayList<Vertex>();
        this.toDraw = new ArrayList<Vertex>();
        this.tick = 0;
        int tock = 0;
    }

    // to make the scene on every tick
    public WorldScene makeScene() {
        WorldScene scene = new WorldScene(this.graph.width * 10, 
                this.graph.height * 10);

        for (Vertex n : this.renderer) {
            n.nodeRender(scene, this.graph.width, this.graph.height, 
                    this.pathway);
        }

        this.player.playerScene(scene);
        return scene;
    }

    // control key movements
    // EFFECT: sets the solution pathway according to which key is pressed
    public void onKeyEvent(String ke) {
        if (ke.equals("b")) {
            this.pathway = this.graph.bfs(graph.allVertices.get(0),
                    graph.allVertices.get(graph.allVertices.size() - 1));
        }

        if (ke.equals("d")) {
            this.pathway = this.graph.dfs(graph.allVertices.get(0),
                    graph.allVertices.get(graph.allVertices.size() - 1));
        }

        this.player.movePlayer(ke);

    }

    // the on tick
    // EFFECT: increments tick and tock
    public void onTick() {
        if (tick < this.vertexes.size()) {
            this.renderer.add(this.vertexes.get(tick));
            tick++;
        }

        if (tock < this.pathway.size()) {
            this.renderer.add(this.pathway.get(tock));
            tock++;
        }
    }

    // the last scene of the game
    public WorldScene lastScene(String s) {
        WorldScene scene = this.makeScene();
        scene.placeImageXY(new TextImage(s, 150, Color.RED), 150, 150);

        return scene;
    }

    // ends the world
    public WorldEnd worldEnds() {
        if (this.player.isOnLast()) {
            return new WorldEnd(true, this.lastScene("You win!"));
        } 
        else {
            return new WorldEnd(false, this.makeScene());
        }
    }
}

// to represent players
class Player {
    Vertex current;
    Graph graph;

    // the player starts in the top left corner
    Player(MazeWorld world) {
        this.current = world.vertexes.get(0);
        this.graph = world.graph;
    }

    // constructor for testing
    Player(Vertex current, Graph graph) {
        this.current = current;
        this.graph = graph;
    }

    // EFFECT: sets this player's node to the given node
    void setCurrent(Vertex newNode) {
        this.current = newNode;
    }

    // can the play move in the desired direction
    boolean canMove(String direction) {
        if (direction.equals("down")) {
            return this.current.hasPathDown;
        }

        if (direction.equals("right")) {
            return this.current.hasPathRight;
        }

        if (direction.equals("left")) {
            return this.current.x > 0 && this.currentLeft().hasPathRight;
        }

        if (direction.equals("up")) {
            return this.current.y > 0 && this.currentUp().hasPathDown;

        }

        else {
            return false;
        }

    }

    // the vertex that is above the current of the player
    Vertex currentUp() {
        return this.graph.allVertices.get(this.current.x + 
                (this.graph.width * (this.current.y - 1)));
    }

    // the vertex that is to the left of the current of the player
    Vertex currentLeft() {
        return this.graph.allVertices.get(this.current.x - 1 + 
                (this.graph.width * this.current.y));
    }

    // the vertex that is to the right of the current of the player
    Vertex currentRight() {
        return this.graph.allVertices.get(this.current.x + 1 + 
                (this.graph.width * this.current.y));
    }

    // the vertex that is below the current of the player
    Vertex currentDown() {
        return this.graph.allVertices.get(this.current.x + 
                (this.graph.width * (this.current.y + 1)));
    }

    // EFFECT: moves the player in the desired direction
    void movePlayer(String ke) {
        if (ke.equals("right") && this.canMove("right")) {
            this.currentRight().changeTravelled();
            this.setCurrent(this.currentRight());
        }

        if (ke.equals("down") && this.canMove("down")) {
            this.currentDown().changeTravelled();
            this.setCurrent(this.currentDown());

        }

        if (ke.equals("up") && this.canMove("up")) {
            this.currentUp().changeTravelled();
            this.setCurrent(this.currentUp());
        }

        if (ke.equals("left") && this.canMove("left")) {
            this.currentLeft().changeTravelled();
            this.setCurrent(this.currentLeft());

        }
    }

    // is this player on the last vertice of the graph?
    boolean isOnLast() {
        return this.current.x == this.graph.width - 1 && 
                this.current.y == this.graph.height - 1;

    }

    // returns an image of the world
    WorldImage playerImage() {
        return new RectangleImage(Vertex.NODE_SIZE, Vertex.NODE_SIZE, 
                OutlineMode.SOLID, Color.BLUE);
    }

    // EFFECT: sets the player onto the scene
    void playerScene(WorldScene scene) {
        scene.placeImageXY(this.playerImage(), 
                this.current.x * Vertex.NODE_SIZE + Vertex.NODE_SIZE / 2,                                                                                          
                this.current.y * Vertex.NODE_SIZE + Vertex.NODE_SIZE / 2); 
    }
}