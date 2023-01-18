package graph;
import graph.WeightedGraph;
import maze.Juncture;
import maze.Maze;

/** 
 * <P>The MazeGraph is an extension of WeightedGraph. The constructor converts a Maze into a graph.</P>
 * 
 * @author Vivian Chen, 2021
 */
public class MazeGraph extends WeightedGraph<Juncture> {

	/** 
	 * <P>Constructor for the MazeGraph using the "maze" contained in the parameter to specify the 
	 * vertices (Junctures) and weighted edges.</P>
	 * 
	 * <P>The Maze should be a rectangular grid of "junctures", each defined by its X and Y 
	 * coordinates, using the usual convention of (0, 0) being the upper left corner.</P>
	 * 
	 * <P>Each juncture in the maze will be added as a vertex to this graph.</P>
	 * 
	 * <P>For every pair of adjacent junctures (A and B) which are not blocked by a wall, two edges 
	 * will be added: One from A to B, and another from B to A. The weight to be used for these 
	 * edges is provided by the Maze. </P>
	 * 
	 * @param maze - the maze to be used as the source of information for adding vertices and edges 
	 * to this MazeGraph.
	 */
	public MazeGraph(Maze maze) {
		super();
		
		for (int y = 0; y < maze.getMazeHeight(); y++) {
			for (int x = 0; x < maze.getMazeWidth(); x++) {
				
				Juncture curr = new Juncture(x, y);
				addVertex(curr);
				
				/* Enter following block if curr juncture isn't in the first row and there is no 
				 * wall above it */
				if ((y - 1) >= 0  && !maze.isWallAbove(curr)) { 
					
					Juncture above = new Juncture(x, y - 1); // juncture directly above curr
					addEdge(curr, above, maze.getWeightAbove(curr)); 
					addEdge(above, curr, maze.getWeightBelow(above));
					// edge between curr and above juncture added for both junctures
				}
				
				/* Enter following block if curr juncture isn't in the first column and there is no 
				 * wall to its left */
				if ((x - 1) >= 0 && !maze.isWallToLeft(curr)) {
					
					Juncture left = new Juncture(x - 1, y); // juncture directly left of curr
					addEdge(curr, left, maze.getWeightToLeft(curr));
					addEdge(left, curr, maze.getWeightToRight(left));
					// edge between curr and left juncture added for both junctures
				}
			}		
		}
	}
}
