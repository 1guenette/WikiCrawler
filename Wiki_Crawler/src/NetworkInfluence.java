// LEAVE THIS FILE IN THE DEFAULT PACKAGE
//  (i.e., DO NOT add 'package cs311.pa1;' or similar)

// DO NOT MODIFY THE EXISTING METHOD SIGNATURES
//  (you may, however, add member fields and additional methods)

// DO NOT INCLUDE LIBRARIES OUTSIDE OF THE JAVA STANDARD LIBRARY
//  (i.e., you may only include libraries of the form java.*)

/**
* @author David Bis
* @author Luke Schoeberle
* @author Sam Guenette
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

public class NetworkInfluence {
	
	public DirectedGraph graph;
	
	// NOTE: graphData is an absolute file path that contains graph data, NOT the raw graph data itself
	public NetworkInfluence(String graphData) throws FileNotFoundException {
		// implementation
		graph = new DirectedGraph(); //instantiate the graph
		Scanner scan  = new Scanner(new File(graphData));
		//scan.nextInt(); //discard the number of vertices
		while(scan.hasNext()) {
			String node1 = scan.next(); //read in each String
			String node2 = scan.next();
			graph.addEdge(node1, node2); //add an edge in the graph between them
		}		
		scan.close();
	}

	public int outDegree(String v) {
		return graph.outDegree(v); //return the outDegree
	}

	public ArrayList<String> shortestPath(String u, String v) {
		if(u.equals(v)) 
			return new ArrayList<>(Arrays.asList(u)); //return the special case of u = v
		Queue<String> q = new LinkedList<>(); //init the queue
		HashSet<String> visited = new HashSet<>(); //init the visited set
		HashMap<String, String> preds = new HashMap<>(); //use predecessors for tracking paths
		q.add(u);
		visited.add(u); //add the first node to both sets
		boolean foundV = false; //store a flag for tracking whether we found a path
		while(!q.isEmpty()) {
			String curr = q.poll(); //get and remove the head of the queue
			if(curr.equals(v)) { //if we found the target
				foundV = true; //update the flag
				break; //exit the loop 
			}
			for(String n : graph.graph.get(curr)) //for each neighbor
				if(!visited.contains(n)) { //add only unvisited neighbors
					q.add(n);
					visited.add(n);
					preds.put(n, curr); //set the predecessors
				}			
		} //finish bfs
		if(!foundV) //return an empty array list if there was no path
			return new ArrayList<>();
		ArrayList<String> path = new ArrayList<>(); //initialize the path
		String curr = v;
		while(!curr.equals(u)) {
			path.add(curr); //add each element in the path
			curr = preds.get(curr); //get the next predecessor
		}
		path.add(u); //add the start of the path
		reverse(path); //reverse the graph to get the right order
		return path; //return the correct path
	}
	
	private void reverse(ArrayList<String> path) {
		int size = path.size(); //store the size
		for(int i = 0; i < size / 2; i++) { //iterate through half the array
			String temp = path.get(i);
			path.set(i, path.get(size - i - 1)); //swap
			path.set(size - i - 1, temp);
		}						
	}	

	public int distance(String u, String v) {
		return shortestPath(u,v).size() - 1; //return the size of the path - 1
	}

	public int distance(ArrayList<String> s, String v) {
		DirectedGraph rGraph = graph.reverseGraph(); //get the reverse graph
		HashSet<String> sSet = new HashSet<>(s); //convert the array into a hashSet for contains checks
		if(sSet.contains(v)) ///return 0 if v is in s
			return 0;
		Queue<String> q = new LinkedList<>(); //init the queue
		HashSet<String> visited = new HashSet<>(); //init the visited set
		HashMap<String, Integer> levels = new HashMap<>(); //track levels of each node
		q.add(v);
		visited.add(v); //add the first node to both sets
		levels.put(v, 0); //mark u's level as 0
		while(!q.isEmpty()) {
			String curr = q.poll(); //get and remove the head of the queue
			if(sSet.contains(curr))
				return levels.get(curr); //return the level if we found a path to a node in s
			for(String n : rGraph.graph.get(curr)) //for each neighbor
				if(!visited.contains(n)) { //add only unvisited neighbors
					q.add(n);
					visited.add(n);
					levels.put(n, levels.get(curr) + 1); //increment the level value
				}			
		} //finish bfs
		return -1; //return -1 if no path was found to the set
	}

	public float influence(String u) {
		Queue<String> q = new LinkedList<>(); //init the queue
		HashSet<String> visited = new HashSet<>(); //init the visited set
		HashMap<String, Double> levelWeights = new HashMap<>(); //track weighted levels of each node
		q.add(u);
		visited.add(u); //add the first node to both sets
		levelWeights.put(u, 1.0); //mark u's level (weight = 1/(2^0) = 1)
		while(!q.isEmpty()) {
			String curr = q.poll(); //get and remove the head of the queue
			for(String n : graph.graph.get(curr)) //for each neighbor
				if(!visited.contains(n)) { //add only unvisited neighbors
					q.add(n);
					visited.add(n);
					levelWeights.put(n, 0.5 * levelWeights.get(curr)); //change the level value (1/2 * the current value)
				}			
		} //finish bfs
		float total = 0;
		Set<String> lSet = levelWeights.keySet();
		for(String s : lSet) //sum up all the level values
			total += levelWeights.get(s);
		return total;
	}

	public float influence(ArrayList<String> s) {
		HashMap<Integer, Integer> levelFreqs = new HashMap<>(); //hash map for storing level frequencies
		Set<String> vSet = graph.graph.keySet();
		for(String v : vSet) {
			int level = distance(s, v); //compute distance from the set for every node
			if(level == -1) //ignore unreachable nodes
				continue;
			if(levelFreqs.containsKey(level))
				levelFreqs.put(level, levelFreqs.get(level) + 1); //increment the frequencies
			else
				levelFreqs.put(level, 1); //otherwise init the frequency to 1
		}
		double factor = 1;
		float sum = 0;
		int size = levelFreqs.keySet().size();
		for(int i = 0; i < size; i++) { //sum up all of the frequencies multiplied by the factor
			sum += factor * levelFreqs.get(i);
			factor *= 0.5; //increase the exponent
		}
		return sum;
	}
	
	public ArrayList<String> mostInfluentialDegree(int k) { 
		class Pair { //pair for the binary heap
			String vertex;
			int val;
			
			Pair(String s, int d) {
				vertex = s;
				val = d;
			}
		}
		PriorityQueue<Pair> heap = new PriorityQueue<>((a,b) -> (b.val - a.val)); //create a max heap
		Set<String> vSet = graph.graph.keySet();
		if(k >= vSet.size()) //return vSet if k >= |v|
			return new ArrayList<>(vSet);
		for(String v : vSet)
			heap.add(new Pair(v, graph.outDegree(v))); //add each outDegree to the heap
		ArrayList<String> results = new ArrayList<>(k);
		for(int i = 0; i < k && !heap.isEmpty(); i++) //repeatedly pop the max (as long as they queue is not empty)
			results.add(heap.poll().vertex);
		return results;
	}

	public ArrayList<String> mostInfluentialModular(int k) {
		class Pair { //pair for the binary heap
			String vertex;
			float val;
			
			Pair(String s, float d) {
				vertex = s;
				val = d;
			}
		}
		PriorityQueue<Pair> heap = new PriorityQueue<>((a,b) -> {
			float diff = b.val - a.val;
			return (int) (diff / Math.abs(diff)); //max heap comparator
		}); //create a max heap
		Set<String> vSet = graph.graph.keySet();
		if(k >= vSet.size()) //return vSet if k >= |v|
			return new ArrayList<>(vSet);
		for(String v : vSet)
			heap.add(new Pair(v, influence(v))); //add each influence to the heap
		ArrayList<String> results = new ArrayList<>(k);
		for(int i = 0; i < k && !heap.isEmpty(); i++)
			results.add(heap.poll().vertex); //repeatedly pop the max (as long as the heap is not empty)
		return results;
	}

	public ArrayList<String> mostInfluentialSubModular(int k) {
		class Pair { //pair for keeping track of vertices along with influence
			String vertex;
			float val;
			
			Pair(String s, float d) {
				vertex = s;
				val = d;
			}
		}
		Set<String> vSet = graph.graph.keySet();
		if(k >= vSet.size()) //return vSet if k >= |v|
			return new ArrayList<>(vSet);
		ArrayList<String> result = new ArrayList<>(); //result list and set (for contains)
		HashSet<String> resultSet = new HashSet<>();
		for(int i = 0; i < k; i++) { //iterate k times
			Pair max = null; //init the best option to null
			for(String v : vSet) { //for each unvisited array
				if(!resultSet.contains(v)) {
					result.add(v); //try adding it
					float vInf = influence(result); //calculate the result
					if(max == null || vInf > max.val)
						max = new Pair(v, vInf); //update the max if applicable
					result.remove(v); //remove it temporarily
				}
			}
			result.add(max.vertex);
			resultSet.add(max.vertex); //add on the max to both result structures
		}
		return result;
	}
}
