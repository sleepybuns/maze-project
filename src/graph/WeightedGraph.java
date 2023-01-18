package graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;


/**
 * <P>This class represents a general "directed graph", which could be used for any purpose. The 
 * graph is viewed as a collection of vertices, which are sometimes connected by weighted, directed 
 * edges.</P> 
 * 
 * <P>This graph will never store duplicate vertices.</P>
 * 
 * <P>The weights will always be non-negative integers.</P>
 * 
 * <P>The WeightedGraph will be capable of performing three algorithms: Depth-First-Search, 
 * Breadth-First-Search, and Djikatra's.</P>
 * 
 * <P>The Weighted Graph will maintain a collection of "GraphAlgorithmObservers", which will be 
 * notified during the performance of the graph algorithms to update the observers on how the 
 * algorithms are progressing.</P>
 * 
 * @author Vivian Chen, 2021
 *
 * @param <V> the type of elements held in this collection
 */


public class WeightedGraph<V> {

	/* graph is a Map that maps vertices to another Map that maps their adjacent vertices to the 
	 * weight of the edges between them.
	 */
	private Map<V, Map<V, Integer>> graph;
	
	/* Collection of observers. The graph algorithms (DFS, BFS, and Dijkstra) will notify these 
	 * observers to let them know how the algorithms are progressing. 
	 */
	private Collection<GraphAlgorithmObserver<V>> observerList;
	

	/** 
	 * Initialize the data structures as empty collections, including the collection of 
	 * GraphAlgorithmObservers (observerList).
	 */
	public WeightedGraph() {
		graph = new HashMap<>();
		observerList = new ArrayList<>();
	}

	/** 
	 * Add a GraphAlgorithmObserver to the collection maintained by this graph (observerList).
	 * 
	 * @param observer - the observer added to this collection
	 */
	public void addObserver(GraphAlgorithmObserver<V> observer) {
		observerList.add(observer);
	}

	/** 
	 * Add a vertex to the graph. If the vertex is already in the graph, throw an 
	 * IllegalArgumentException.
	 * 
	 * @param vertex - vertex to be added to the graph
	 * @throws IllegalArgumentException if the vertex is already in the graph.
	 */
	public void addVertex(V vertex) {
		if (graph.containsKey(vertex)) {
			throw new IllegalArgumentException();
		} else {
			graph.put(vertex, new HashMap<>());
		}
	}
	
	/** 
	 * Searches for a given vertex. 
	 * 
	 * @param vertex - the vertex we are looking for
	 * @return true if the vertex is in the graph, false otherwise.
	 */
	public boolean containsVertex(V vertex) {
		return graph.containsKey(vertex);
	}

	/** 
	 * <P>Add an edge from one vertex of the graph to another, with the weight specified.</P>
	 * 
	 * <P>The two vertices must already be present in the graph.</P>
	 * 
	 * <P>This method throws an IllegalArgumentExeption in three cases:</P>
	 * <P>1. The "from" vertex is not already in the graph.</P>
	 * <P>2. The "to" vertex is not already in the graph.</P>
	 * <P>3. The weight is less than 0.</P>
	 * 
	 * @param from - the vertex the edge leads from
	 * @param to - the vertex the edge leads to
	 * @param weight - the (non-negative) weight of this edge
	 * @throws IllegalArgumentException when either vertex is not in the graph, or the weight is 
	 * negative.
	 */
	public void addEdge(V from, V to, Integer weight) {
		if (!graph.containsKey(from) || !graph.containsKey(to) || weight < 0) {
			throw new IllegalArgumentException();
		} else {
			graph.get(from).put(to, weight);
		}
		
	}

	/** 
	 * <P>Returns weight of the edge connecting one vertex to another. Returns null if the edge does 
	 * not exist.</P>
	 * 
	 * <P>Throws an IllegalArgumentException if either of the vertices specified are not in the 
	 * graph.</P>
	 * 
	 * @param from - vertex where edge begins
	 * @param to - vertex where edge terminates
	 * @return weight - weight of the edge, or null if there is no edge connecting these vertices
	 * @throws IllegalArgumentException if either of the vertices specified are not in the graph.
	 */
	public Integer getWeight(V from, V to) {
		if (!graph.containsKey(from) || !graph.containsKey(to)) {
			throw new IllegalArgumentException();
		} else {
			return graph.get(from).get(to);
		}
	}

	/** 
	 * <P>This method will perform a Breadth-First-Search on the graph. The search will begin at the 
	 * "start" vertex and conclude once the "end" vertex has been reached.</P>
	 * 
	 * <P>Before the search begins, this method will go through the collection of Observers, calling 
	 * notifyBFSHasBegun on each one.</P>
	 * 
	 * <P>Just after a particular vertex is visited, this method will go through the collection of 
	 * observers calling notifyVisit on each one (passing in the vertex being visited as the 
	 * argument.)</P>
	 * 
	 * <P>After the "end" vertex has been visited, this method will go through the collection of 
	 * observers calling notifySearchIsOver on each one, after which the method will terminate 
	 * immediately, without processing further vertices.</P> 
	 * 
	 * @param start - vertex where search begins
	 * @param end - the algorithm terminates just after this vertex is visited
	 */
	public void DoBFS(V start, V end) {
		
		for (GraphAlgorithmObserver<V> observer : observerList) {
			observer.notifyBFSHasBegun();
		}
		
		HashSet<V> visitedSet = new HashSet<>(); // Set of the visited vertices 
		ArrayDeque<V> queue = new ArrayDeque<>(); // Queue of the discovered vertices 
		
		queue.add(start); 
		
		while(!queue.isEmpty()) { // while queue is not empty
			
			V curr = queue.poll(); // dequeue the first element
			
			if (!visitedSet.contains(curr)) { // if curr vertex has not been visited
				
				visitedSet.add(curr); // visited curr
				
				for (GraphAlgorithmObserver<V> observer : observerList) {
					observer.notifyVisit(curr);
				}
				
				if (curr.equals(end)) { // if true, the search is over. end vertex has been visited
					for (GraphAlgorithmObserver<V> observer : observerList) {
						observer.notifySearchIsOver(); 
					}
					return; // search ends.
				}
				
				/* for each loop iterates over a set of all vertices that shares an edge with curr */
				for (V adjacent : graph.get(curr).keySet()) {
					if (!visitedSet.contains(adjacent)) { // if adjacent vertex has not been visited
						queue.add(adjacent); // adds to the end of queue
					}
				}
			}
		}
	}
	
	/** 
	 * <P>This method will perform a Depth-First-Search on the graph. The search will begin at the 
	 * "start" vertex and conclude once the "end" vertex has been reached.</P>
	 * 
	 * <P>Before the search begins, this method will go through the collection of Observers, calling 
	 * notifyDFSHasBegun on each one.</P>
	 * 
	 * <P>Just after a particular vertex is visited, this method will go through the collection of 
	 * observers calling notifyVisit on each one (passing in the vertex being visited as the
	 * argument.)</P>
	 * 
	 * <P>After the "end" vertex has been visited, this method will go through the collection of 
	 * observers calling notifySearchIsOver on each one, after which the method will terminate 
	 * immediately, without visiting further vertices.</P> 
	 * 
	 * @param start - vertex where search begins
	 * @param end - the algorithm terminates just after this vertex is visited
	 */
	public void DoDFS(V start, V end) {
		
		for (GraphAlgorithmObserver<V> observer : observerList) {
			observer.notifyDFSHasBegun();
		}
		
		HashSet<V> visitedSet = new HashSet<>(); // Set of the visited vertices 
		ArrayDeque<V> stack = new ArrayDeque<>(); // Stack of the discovered vertices
		
		stack.push(start);
		
		while(!stack.isEmpty()) { // if the stack is not empty
			
			V curr = stack.pop(); 
			
			if (!visitedSet.contains(curr)) { // if curr vertex has not been visited
				
				visitedSet.add(curr); // visited curr
				
				for (GraphAlgorithmObserver<V> observer : observerList) {
					observer.notifyVisit(curr);
				}
				
				if (curr.equals(end)) { // if true, the search is over. end vertex has been visited
					for (GraphAlgorithmObserver<V> observer : observerList) {
						observer.notifySearchIsOver();
					}
					return; // search ends.
				}
				
				/* for each loop iterates over a set of all vertices that shares an edge with curr */
				for (V adjacent : graph.get(curr).keySet()) {
					if (!visitedSet.contains(adjacent)) { // if adjacent vertex has not been visited
						stack.push(adjacent); 
					}
				}
			}
		}
	}
	
	/** 
	 * <P>Perform Dijkstra's algorithm, beginning at the "start" vertex.</P>
	 * 
	 * <P>The algorithm DOES NOT terminate when the "end" vertex is reached. It will continue until 
	 * every vertex in the graph has been added to the finished set.</P>
	 * 
	 * <P>Before the algorithm begins, this method goes through the collection of Observers, calling 
	 * notifyDijkstraHasBegun on each Observer.</P>
	 * 
	 * <P>Each time a vertex is added to the "finished set", this method goes through the collection 
	 * of Observers, calling notifyDijkstraVertexFinished on each one (passing the vertex that was 
	 * just added to the finished set as the first argument, and the optimal "cost" of the path 
	 * leading to that vertex as the second argument.)</P>
	 * 
	 * <P>After all of the vertices have been added to the finished set, the algorithm will 
	 * calculate the "least cost" path of vertices leading from the starting vertex to the ending 
	 * vertex. Next, it will go through the collection of observers, calling notifyDijkstraIsOver 
	 * on each one, passing in as the argument the "lowest cost" sequence of vertices that leads 
	 * from start to end (I.e. the first vertex in the list will be the "start" vertex, and the 
	 * last vertex in the list will be the "end" vertex.)</P>
	 * 
	 * @param start - vertex where algorithm will start
	 * @param end  - special vertex used as the end of the path reported to observers via the 
	 * notifyDijkstraIsOver method.
	 */
	public void DoDijsktra(V start, V end) {
		
		for (GraphAlgorithmObserver<V> observer : observerList) { 
			observer.notifyDijkstraHasBegun();
		}
		
		HashSet<V> finishedSet = new HashSet<>(); // set of finished vertices
		
		/* costMinHeap is a min-heap that contains VrtxCost objects. VrtxCost object here contain 
		 * the vertex and its cost of traversal from the starting vertex. VrtxCost objects 
		 * are naturally ordered by their costs, the first element of the min-heap will always have 
		 * the lowest cost. */
		PriorityQueue<VrtxCost<V>> costMinHeap = new PriorityQueue<>();
		
		/* mainData maps vertices to a VrtxCost object that holds its cost and predecessor vertex */
		HashMap<V, VrtxCost<V>> mainData = new HashMap<>(); 
		
		/* add the "start" vertex into collections*/
		VrtxCost<V> first = new VrtxCost<>(0, start); // "start" vertex has cost 0 and itself as predecessor
		costMinHeap.add(first);
		mainData.put(start, first); 
		
		while (finishedSet.size() != graph.size()) { 
			// if the two sizes aren't equal then not all vertices have been processed
			
			VrtxCost<V> curr;
			
			/* Do-while loop continue to assign curr as the first element of the min-heap until curr 
			 * has a vertex that isn't in the finishedSet. Prevents processing duplicate vertices as 
			 * they might exist in the min-heap. */
			do {
				curr = costMinHeap.poll(); 
			} while (finishedSet.contains(curr.vertex)); 
			
			finishedSet.add(curr.vertex); // vertex with the lowest cost has been found
			
			for (GraphAlgorithmObserver<V> observer : observerList) { 
				observer.notifyDijkstraVertexFinished(curr.vertex, curr.cost); 
			}
			
			/* for each loop iterates over a set of vertices that share an edge with vertex of curr */
			for (V adjacent : graph.get(curr.vertex).keySet()) {
				
				if (!finishedSet.contains(adjacent)) { // if adjacent vertex is not finished
					
					Integer weight = getWeight(curr.vertex, adjacent); 
					
					if (!mainData.containsKey(adjacent) || mainData.get(adjacent).cost > weight + curr.cost) {
						// if adj. vertex hasn't been discovered or has a lower cost than previously recorded
						
						Integer newCost = weight + curr.cost; // cost from "start" to adjacent vertex
						
						/* add VrtxCost object with adjacent vertex and newCost to min-heap */
						costMinHeap.add(new VrtxCost<>(newCost, adjacent));
						/* map adjacent and VrtxCost object with newCost and predecessor vertex */
						mainData.put(adjacent, new VrtxCost<>(newCost, curr.vertex));
						
					}
				}
			}
		}
		// all vertices have been added to the finished set
		
		LinkedList<V> path = new LinkedList<>();
		path.addFirst(end); 
		V predecessor = end; // assigned end vertex to prepare for the following while loop
		
		while (!predecessor.equals(start)) { 
			predecessor = mainData.get(predecessor).vertex; // assigns the next predecessor vertex
			path.addFirst(predecessor); 
		}
		// path is now a list with all the vertices from "start" to "end"
		
		for (GraphAlgorithmObserver<V> observer : observerList) {
			observer.notifyDijkstraIsOver(path);
		}
		// algorithm ends.
	}
	
	/* VrtxCost is a nested class that serves as a wrapper around an object V and an Integer, 
	 * "vertex" and "cost" respectively. This class implements Comparable and its natural ordering 
	 * is based on the integer value it holds. 
	 * 
	 * In the doDijsktra method, VrtxCost allows the use of a PriorityQueue as a min-heap to sort 
	 * vertices by their costs.
	 * 
	 * VrtxCost is also used in the mainData HashMap by storing the cost and predecessor of a vertex 
	 * in one convenient package. 
	 */
	private static class VrtxCost<V> implements Comparable<VrtxCost<V>>{
		private V vertex;
		private Integer cost; 
		
		public VrtxCost(Integer cost, V vertex) {
			this.vertex = vertex;
			this.cost = cost;
		}
		@Override
		public int compareTo(VrtxCost<V> other) {
			return cost - other.cost;
		}
	}
}
