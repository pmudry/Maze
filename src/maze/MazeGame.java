package maze;

import maze.data.MazeContainer;
import maze.data.MazeElem;
import maze.data.MazeUtils.Direction;
import maze.data.MazeUtils.Player;
import maze.display.GraphicDisplay;
import maze.display.KeyboardListener;
import maze.solvers.AStar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * Game logic for moving the players and selectively showing the solution
 * 
 * @author Pierre-André Mudry, Romain Cherix
 * @version 1.5
 */
public class MazeGame {

	// The data structure holding the maze
	MazeContainer mc;

	GraphicDisplay gd;

	KeyboardListener kl;

	/**
	 * By default, you are the first player but this can change.
	 */
	Player player = Player.PLAYER1;

	MazeGame(MazeContainer mc) {
		gd = new GraphicDisplay(mc, 15);

		// Link key presses with the actions
		kl = new KeyboardListener(mc, this);
		gd.registerKeyListener(kl);

		setNewMaze(mc);
	}

	/**
	 * Dynamically changes the maze that is displayed
	 * 
	 * @param mc
	 */
	protected void setNewMaze(MazeContainer mc) {
		this.mc = mc;
		// Update graphical maze
		gd.setNewMaze(mc);
	}

	/**
	 * Call this when you want a new game
	 * @param mazeID
	 */
	public void generateNewMaze(int mazeID){			
		this.setNewMaze(new MazeContainer(mc.nCellsX, mc.nCellsY, mazeID));
	}	
	
	/**
	 * Displays the solution on the screen for a player during a whole second
	 */
	public void displaySolution() {
		Point p1 = findPlayer(player);

		int[][] solution = AStar.solve(mc, p1.x, p1.y);
		gd.setSolution(solution);

		Timer timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gd.clearSolution();
			}
		});

		timer.setRepeats(false);
		timer.start();
	}
	
	/**
	 * Gives us the location of player inside the maze
	 * 
	 * @param p The player we want
	 * @return The location of the player
	 */
	private Point findPlayer(Player p) {
		/**
		 * We go through all the elements
		 */
		for (int j = 0; j < mc.nCellsY; j++) {
			for (int i = 0; i < mc.nCellsX; i++) {
				MazeElem e = mc.maze[i][j];

				// Once found, get it back
				if ((p == Player.PLAYER1) && e.p1Present)
					return new Point(i, j);

				if ((p == Player.PLAYER2) && e.p2Present)
					return new Point(i, j);
			}
		}

		// This means that the player hasn't been found
		return null;
	}

	/**
	 * Check if some player has reached the exit of the maze
	 */
	public boolean checkWinner() {
		for (int j = 0; j < mc.nCellsY; j++) {
			for (int i = 0; i < mc.nCellsX; i++) {
				MazeElem el = mc.maze[i][j];

				if (el.isExit && el.p1Present) {
					JOptionPane.showMessageDialog(
							null, "You won !", "We have a winner !", JOptionPane.INFORMATION_MESSAGE);	
					return true;
				}			
			}
		}		
		return false;
	}

	/**
	 * Method used to move a player inside the maze
	 * 
	 * @param d Which direction do you want to go to ?
	 */
	public void movePlayer(Direction d) {	
		boolean movementDone = false;
		
		for (int j = 0; j < mc.nCellsY; j++){
			for (int i = 0; i < mc.nCellsX; i++) {

				MazeElem[][] maze = mc.maze;
				MazeElem e = maze[i][j];

				if (e.p1Present && player == Player.PLAYER1) {
					movementDone = true;
					
					switch (d) {
					case UP:
						if (!e.wallNorth) {
							e.p1Present = false;
							maze[i][j - 1].p1Present = true;
						}
						break;

					case DOWN:
						if (!e.wallSouth) {
							e.p1Present = false;
							maze[i][j + 1].p1Present = true;
						}
						break;

					case RIGHT:
						if (!e.wallEast) {
							e.p1Present = false;
							maze[i + 1][j].p1Present = true;
						}
						break;

					case LEFT:
						if (!e.wallWest) {
							e.p1Present = false;
							maze[i - 1][j].p1Present = true;
						}
						break;
					}
										
				}				
				
				if(movementDone) break;
			}
			if(movementDone) break;
		}
		
		/**
		 * When the move has been done, see if there is a winner
		 */
		if(checkWinner())
			generateNewMaze(new Random().nextInt());
	}

	/**
	 * Only for fun, generate multiple of labyrinths per second
	 */
	public void multiShuffle()
	{		
		Timer timer = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Random rnd = new Random();
				mc = new MazeContainer(15, 15, rnd.nextInt());		
				gd.setNewMaze(mc);
			}
		});
		
		timer.setRepeats(true);
		timer.start();		
	}
	
	public static void main(String[] args) {
		MazeContainer mc = new MazeContainer(15, 15);
		MazeGame mg = new MazeGame(mc);		
				
//		mg.multiShuffle();

		System.out.println("* Welcome to the maze *");
		System.out.println("w-a-s-d -> move");
		System.out.println("n -> generate a new maze");
		System.out.println("q -> display the solution");

		// TODO Students should implement next line
		// mg.movePlayer(Direction.DOWN);

		// This shows a nice message window
		// JOptionPane.showMessageDialog(null, "Title of the window", "Text of the window !", JOptionPane.INFORMATION_MESSAGE);
	}
}