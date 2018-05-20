import java.io.File;
import java.io.FileNotFoundException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

/**
 * @author abhanshu 
 * This class is a template for implementation of 
 * HW1 for CS540 section 2
 */
/**
 * Data structure to store each node.
 */
class Location {
	private int x;
	private int y;
	private Location parent;

	public Location(int x, int y, Location parent) {
		this.x = x;
		this.y = y;
		this.parent = parent;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Location getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return x + " " + y;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Location) {
			Location loc = (Location) obj;
			return loc.x == x && loc.y == y;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * (hash + x);
		hash = 31 * (hash + y);
		return hash;
	}
}

public class KingsKnightmare {
	// represents the map/board
	private static boolean[][] board;
	// represents the goal node
	private static Location king;
	// represents the start node
	private static Location knight;
	// y dimension of board
	private static int n;
	// x dimension of the board
	private static int m;
	// enum defining different algo types
	enum SearchAlgo {
		BFS, DFS, ASTAR;
	}

	public static void main(String[] args) {
		if (args != null && args.length > 0) {
			// loads the input file and populates the data variables
			SearchAlgo algo = loadFile(args[0]);
			if (algo != null) {
				switch (algo) {
					case DFS :
						executeDFS();
						break;
					case BFS :
						executeBFS();
						break;
					case ASTAR :
						executeAStar();
						break;
					default :
						break;
				}
			}
		}
	}

	/**
	 * Implementation of Astar algorithm for the problem
	 */
	private static void executeAStar() {
		PriorityQ<Location> queue = new PriorityQ<>();
		// PriorityQueueAStar<Location> visited = new PriorityQueueAStar<>();
		Set<Location> visited = new HashSet<>();
		queue.add(knight, 0);
		boolean isGoalReachable = false;
		SimpleEntry<Location, Integer> curr;
		while (!queue.isEmpty()) {
			curr = queue.poll();
			// visited.add(curr.getKey(), curr.getValue());
			if (curr.getKey().equals(king)) {
				isGoalReachable = true;
				printPath(curr.getKey(), visited.size());
				break;
			} else {
				visited.add(curr.getKey());
				List<Location> children = getOrderedSuccessors(curr.getKey());
				int currentHueristic = heuristicValue(curr.getKey());
				for (Location child : children) {
					int existingScore = queue.getPriorityScore(child);
					// int processedScore = visited.getPriorityScore(child);
					int score = 3 + curr.getValue() - currentHueristic
							+ heuristicValue(child);
					if (existingScore == -1
							&& /* processedScore == -1 */ !visited
									.contains(child)) {
						queue.add(child, score);
					} else if (existingScore > score) {
						queue.modifyEntry(child, score);
					}
				}
			}
		}
		if (!isGoalReachable) {
			System.out.println("NOT REACHABLE");
			System.out.println("Expanded Nodes: " + visited.size());
		}
	}

	/**
	 * 
	 * @param child
	 * @return heuristic value of the node Computes the heuristic value of node
	 *         from the goal state using Manhattan distance
	 */
	private static int heuristicValue(Location child) {
		return Math.abs(child.getX() - king.getX())
				+ Math.abs(child.getY() - king.getY());
	}

	/**
	 * Implementation of BFS algorithm
	 */
	private static void executeBFS() {
		Queue<Location> queue = new LinkedList<>();
		Set<Location> visited = new HashSet<Location>();
		queue.add(knight);
		boolean isGoalReachable = false;
		Location curr;
		while (!queue.isEmpty() && !isGoalReachable) {
			curr = queue.poll();
			visited.add(curr);
			List<Location> children = getOrderedSuccessors(curr);
			for (Location child : children) {
				if (child.equals(king)) {
					isGoalReachable = true;
					printPath(child, visited.size());
					break;
				} else if (!queue.contains(child) && !visited.contains(child)) {
					queue.add(child);
				}
			}
		}
		if (!isGoalReachable) {
			System.out.println("NOT REACHABLE");
			System.out.println("Expanded Nodes: " + visited.size());
		}
	}

	/**
	 * Implemention of DFS algorithm
	 */
	private static void executeDFS() {
		Stack<Location> stack = new Stack<>();
		Set<Location> visited = new HashSet<Location>();
		stack.push(knight);
		boolean isGoalReachable = false;
		Location curr;
		while (!stack.isEmpty() && !isGoalReachable) {
			curr = stack.pop();
			visited.add(curr);
			List<Location> children = getOrderedSuccessors(curr);
			for (Location child : children) {
				if (child.equals(king)) {
					isGoalReachable = true;
					printPath(child, visited.size());
					break;
				} else if (!stack.contains(child) && !visited.contains(child)) {
					stack.push(child);
				}
			}
		}
		if (!isGoalReachable) {
			System.out.println("NOT REACHABLE");
			System.out.println("Expanded Nodes: " + visited.size());
		}
	}

	/**
	 * 
	 * @param curr
	 * @param visitedNodes
	 *            Prints the path
	 */
	private static void printPath(Location curr, int visitedNodes) {
		List<Location> path = new ArrayList<>();
		while (curr != null) {
			path.add(curr);
			curr = curr.getParent();
		}
		for (int i = path.size() - 1; i >= 0; i--) {
			System.out.println(path.get(i));
		}
		System.out.println("Expanded Nodes: " + visitedNodes);
	}

	/**
	 * 
	 * @param loc
	 * @return returns all the children of a node in an ordered fashion
	 */
	private static List<Location> getOrderedSuccessors(Location loc) {
		List<Location> successors = new ArrayList<>(8);
		if (loc.getX() + 2 < m && loc.getY() + 1 < n
				&& !board[loc.getY() + 1][loc.getX() + 2]) {
			successors.add(new Location(loc.getX() + 2, loc.getY() + 1, loc));
		}
		if (loc.getX() + 1 < m && loc.getY() + 2 < n
				&& !board[loc.getY() + 2][loc.getX() + 1]) {
			successors.add(new Location(loc.getX() + 1, loc.getY() + 2, loc));
		}
		if (loc.getX() - 1 > -1 && loc.getY() + 2 < n
				&& !board[loc.getY() + 2][loc.getX() - 1]) {
			successors.add(new Location(loc.getX() - 1, loc.getY() + 2, loc));
		}
		if (loc.getX() - 2 > -1 && loc.getY() + 1 < n
				&& !board[loc.getY() + 1][loc.getX() - 2]) {
			successors.add(new Location(loc.getX() - 2, loc.getY() + 1, loc));
		}
		if (loc.getX() - 2 > -1 && loc.getY() - 1 > -1
				&& !board[loc.getY() - 1][loc.getX() - 2]) {
			successors.add(new Location(loc.getX() - 2, loc.getY() - 1, loc));
		}
		if (loc.getX() - 1 > -1 && loc.getY() - 2 > -1
				&& !board[loc.getY() - 2][loc.getX() - 1]) {
			successors.add(new Location(loc.getX() - 1, loc.getY() - 2, loc));
		}
		if (loc.getX() + 1 < m && loc.getY() - 2 > -1
				&& !board[loc.getY() - 2][loc.getX() + 1]) {
			successors.add(new Location(loc.getX() + 1, loc.getY() - 2, loc));
		}
		if (loc.getX() + 2 < m && loc.getY() - 1 > -1
				&& !board[loc.getY() - 1][loc.getX() + 2]) {
			successors.add(new Location(loc.getX() + 2, loc.getY() - 1, loc));
		}
		return successors;
	}

	/**
	 * 
	 * @param filename
	 * @return Algo type This method reads the input file and populates all the
	 *         data variables for further processing
	 */
	private static SearchAlgo loadFile(String filename) {
		File file = new File(filename);
		try {
			Scanner sc = new Scanner(file);
			SearchAlgo algo = SearchAlgo
					.valueOf(sc.nextLine().trim().toUpperCase());
			n = sc.nextInt();
			m = sc.nextInt();
			sc.nextLine();
			board = new boolean[n][m];
			for (int i = 0; i < n; i++) {
				String line = sc.nextLine();
				for (int j = 0; j < m; j++) {
					if (line.charAt(j) == '1') {
						board[i][j] = true;
					} else if (line.charAt(j) == 'S') {
						knight = new Location(j, i, null);
					} else if (line.charAt(j) == 'G') {
						king = new Location(j, i, null);
					}
				}
			}
			sc.close();
			return algo;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
