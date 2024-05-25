// Import classes
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Maze maze = new Maze(11, 11);

        maze.constructGrid();
        maze.printMaze();
        maze.generateMaze();
        maze.printMaze();
    }
}