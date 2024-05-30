import java.util.ArrayList;
import java.util.Random;

// Maze class
class Maze {
    // Define attributes
    private int width;
    private int length;
    private ArrayList<String> map = new ArrayList<String>();

    private String wallTile = "█";
    private String openTile = " ";
    private String unwalkedTile = "/";
    private String walkTile = "o";
    // private String navigateTile = "";

    // Seed for Random object in generateMaze() function
    private long seed = 0;

    /* Alternative set of tiles with emojis that are easier to read
    private String wallTile = "⬛";
    private String openTile = "⬜";
    private String unwalkedTile = "🟨";
    private String walkTile = "🟩";
    private String navigateTile = "🟦";
     */

    // Constructor for no width and length
    public Maze() {
        // Default value is 11x11
        this.width = 11;
        this.length = 11;
    }

    // Constructor for defining width and length
    public Maze(int newWidth, int newLength) {
        if (newWidth % 2 == 0) {
            newWidth += 1;
        }
        if (newLength % 2 == 0) {
            newLength += 1;
        }
        this.width = newWidth;
        this.length = newLength;
    }

    // Getter for width
    public int getWidth() {
        return this.width;
    }

    // Getter for length
    public int getLength() {
        return this.length;
    }

    public long getSeed() {
        return this.seed;
    }

    // Setter for width
    public void setWidth(int newWidth) {
        this.width = newWidth;
    }

    // Setter for length
    public void setLength(int newLength) {
        this.length = newLength;
    }

    // Setter for width and length
    public void setSize(int newWidth, int newLength) {
        this.width = newWidth;
        this.length = newLength;
    }

    public void setSeed(long newSeed) {
        this.seed = newSeed;
    }

    // Constructs the frame of the maze
    public void constructGrid(){
        // Construct left wall of maze by placing a single wall tile for every row
        for (int i = 0; i < this.length; i += 1) {
            this.map.add(wallTile);
        }

        // Construct top and bottom of maze by adding as many wall tiles as needed to match width
        for (int i = 1; i < this.width; i += 1) {
            this.map.set(0, this.map.get(0) + wallTile);
            this.map.set(this.length - 1, this.map.getLast() + wallTile);
        }

        // Fill out middle of maze with spaces, then construct right wall
        for (int i = 1; i < this.length - 1; i += 1) {
            for (int j = 1; j < this.width - 1; j += 1) {
                this.map.set(i, this.map.get(i) + unwalkedTile);
            }
            this.map.set(i, this.map.get(i) + wallTile);
        }
    }

    // Method for reading a tile (Cartesian coordinates)
    public String getTile(int x, int y) {
        return this.map.get(this.length - y - 1).substring(x, x + 1);
    }

    // Method for setting a tile (Cartesian coordinates)
    public void setTile(int x, int y, String newTile) {
        String newString = this.map.get(this.length - 1 - y).substring(0, x) + newTile + this.map.get(this.length - 1 - y).substring(x + 1);
        this.map.set(this.length - 1 - y, newString);
    }

    // Prints the maze
    public void printMaze() {
        for (int i = 0; i < this.length; i += 1) {
            System.out.println(this.map.get(i));
        }
        System.out.println();
    }

    // Checks if there are unmapped tiles in the maze
    public boolean checkFull() {
        for (int i = 0; i < this.length; i += 1){
            for (int j = 0; j < this.width; j += 1) {
                if (this.getTile(j, i).equals(unwalkedTile)) {
                    return false;
                }
            }
        }
        return true;
    }

    // Method for constructing walls around walk tiles
    public void buildWalls() {
        // Check for walk tiles
        for (int y = 1; y < this.length - 1; y += 1){
            for (int x = 1; x < this.width - 1; x += 1) {
                if (this.getTile(x, y).equals(walkTile)) {
                    // Check the 3x3 grid surrounding the tile
                    for (int i = -1; i < 2; i += 1) {
                        for (int j = -1; j < 2; j += 1) {
                            // And replace all tiles in the grid with walls unless they're walk or open tiles
                            if (!(this.getTile(x + j, y + i).equals(walkTile)) && !(this.getTile(x + j, y + i).equals(openTile))) {
                                this.setTile(x + j, y + i, wallTile);
                            }
                        }
                    }
                }
            }
        }
    }

    // Method for replacing tiles in the maze with another character
    public void replaceTile(String oldTile, String newTile) {
        for (int i = 0; i < this.length; i += 1){
            for (int j = 0; j < this.width; j += 1) {
                if (this.getTile(j, i).equals(oldTile)) {
                    this.setTile(j, i, newTile);
                }
            }
        }
    }

    // Employs a loop-erasing algorithm to erase all walk tiles in a created loop by reverse-tracing a path
    public void eraseLoop(int loopX, int loopY, String direction) {
        int x = loopX;
        int y = loopY;
        boolean hasReturned = false;

        // Start by erasing tiles in the direction opposite of the last walk
        switch (direction) {
            case "up":
                this.setTile(x, y - 1, unwalkedTile);
                this.setTile(x, y - 2, unwalkedTile);
                y -= 2;
                break;
            case "down":
                this.setTile(x, y + 1, unwalkedTile);
                this.setTile(x, y + 2, unwalkedTile);
                y += 2;
                break;
            case "left":
                this.setTile(x + 1, y, unwalkedTile);
                this.setTile(x + 2, y, unwalkedTile);
                x += 2;
                break;
            case "right":
                this.setTile(x - 1, y, unwalkedTile);
                this.setTile(x - 2, y, unwalkedTile);
                x -= 2;
                break;
        }
        this.printMaze();

        // Then, execute algorithm until returning to the point of the loop (checked later)
        while (!hasReturned) {
            // Determine direction
            if (this.getTile(x, y + 1).equals(walkTile)) direction = "up";
            if (this.getTile(x, y - 1).equals(walkTile)) direction = "down";
            if (this.getTile(x - 1, y ).equals(walkTile)) direction = "left";
            if (this.getTile(x + 1, y).equals(walkTile)) direction = "right";

            // Erase tiles in direction
            switch (direction) {
                case "up":
                    // Check if next tile in direction is origin of loop, if so break
                    if ((x == loopX) && (y == loopY)) {
                        hasReturned = true;
                        break;
                    }
                    // Otherwise, erase tiles
                    this.setTile(x, y + 1, unwalkedTile);
                    this.setTile(x, y + 2, unwalkedTile);
                    y += 2;
                    break;
                case "down":
                    if ((x == loopX) && (y == loopY)) {
                        hasReturned = true;
                        break;
                    }
                    this.setTile(x, y - 1, unwalkedTile);
                    this.setTile(x, y - 2, unwalkedTile);
                    y -= 2;
                    break;
                case "left":
                    if ((x == loopX) && (y == loopY)) {
                        hasReturned = true;
                        break;
                    }
                    this.setTile(x - 1, y, unwalkedTile);
                    this.setTile(x - 2, y, unwalkedTile);
                    x -= 2;
                    break;
                case "right":
                    if ((x == loopX) && (y == loopY)) {
                        hasReturned = true;
                        break;
                    }
                    this.setTile(x + 1, y, unwalkedTile);
                    this.setTile(x + 2, y, unwalkedTile);
                    x += 2;
                    break;
            }
            this.printMaze();
        }
    }

    // Generates the Maze using Wilson's algorithm
    public void generateMaze() {
        // Initialize x and y coordinate variables and random number generator
        Random rng;
        String movement = "";
        int x;
        int y;

        // If seed has been unchanged, create entirely random object. Otherwise, create with defined seed
        if (seed == 0) {
            rng = new Random();
        }
        else rng = new Random(this.seed);

        // Randomly pick a set of odd coordinates within the bounds of the maze
        x = rng.nextInt(0, this.width - 1);
        y = rng.nextInt(0, this.length - 1);
        if (x % 2 == 0) x += 1;
        if (y % 2 == 0) y += 1;

        // Make that tile a finished maze tile
        this.setTile(x, y, openTile);

        // Until the maze is completely filled
        while (!checkFull()) {
            // Randomly select a set of odd coordinates
            x = rng.nextInt(0, this.width - 1);
            y = rng.nextInt(0, this.length - 1);
            if (x % 2 == 0) x += 1;
            if (y % 2 == 0) y += 1;

            // If these coordinates are on an unfinished portion of the maze
            if (this.getTile(x, y).equals(unwalkedTile)) {
                // Begin walk
                setTile(x, y, walkTile);
                movement = "none";
                this.printMaze();

                // Keep walk going until break is initiated
                while (true) {
                    // Randomly select horizontal or vertical movement (vertical is true)
                    if (rng.nextBoolean()) {
                        // Randomly select positive or negative movement (positive is true)
                        if (rng.nextBoolean()) {
                            y += 2;
                            // Undo changes if movement goes out of bounds or runs into itself
                            if ((y >= this.length) || (movement.equals("down"))) {
                                y -= 2;
                                movement = "none";
                            }
                            // Set direction
                            else movement = "up";
                        }
                        else {
                            y -= 2;
                            // Undo changes if movement goes out of bounds or runs into itself
                            if ((y < 0) || (movement.equals("up"))) {
                                y += 2;
                                movement = "none";
                            }
                            // Set direction
                            else movement = "down";
                        }
                    }
                    // Horizontal movement
                    else {
                        // Randomly select positive or negative movement (positive is true)
                        if (rng.nextBoolean()) {
                            x += 2;
                            // Undo changes if movement goes out of bounds or loops or runs into itself
                            if ((x >= this.width) || (movement.equals("left"))) {
                                x -= 2;
                                movement = "none";
                            }
                            // Set direction
                            else movement = "right";
                        }
                        else {
                            x -= 2;
                            // Undo changes if movement goes out of bounds or runs into itself
                            if ((x < 0) || (movement.equals("right"))) {
                                x += 2;
                                movement = "none";
                            }
                            else movement = "left";
                        }
                    }

                    // Assigns tile in the direction of movement before placing second, final piece
                    switch(movement) {
                        case "up":
                            this.setTile(x, y - 1, walkTile);
                            break;
                        case "down":
                            this.setTile(x, y + 1, walkTile);
                            break;
                        case "left":
                            this.setTile(x + 1, y, walkTile);
                            break;
                        case "right":
                            this.setTile(x - 1, y, walkTile);
                            break;
                    }

                    // If the walk creates a loop with itself, erase loop
                    if (!movement.equals("none")) {
                        if (getTile(x, y).equals(walkTile)){
                            this.eraseLoop(x, y, movement);
                        }
                    }
                    this.printMaze();

                    // If the walk runs into a finished part of the maze, build walls around the walk and start new walk
                    if (getTile(x, y).equals(openTile)){
                        this.buildWalls();
                        this.replaceTile(walkTile, openTile);
                        break;
                    }
                    // Else, place final walk tile and start loop again
                    else this.setTile(x, y, walkTile);
                }
            }
        }
    }

    // Employs the A* algorithm to navigate generated mazes
    public void navigateMaze() {

    }
}