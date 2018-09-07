import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class test {

	public static void main(String[] args) throws FileNotFoundException {	
//		DirectedGraph g = new DirectedGraph();
//		g.addEdge("v0", "v1");
//		g.addEdge("v1", "v2");
///		g.addEdge("v1", "v4");
//		g.addEdge("v2", "v3");
//		g.addEdge("v3", "v4");
		//g.addEdge("v0", "v4");
		ArrayList<String> s = new ArrayList<>();
		//s.add("Software");
//		System.out.println(mostInfluentialSubModular(3, g));
/*		s.add("v0");
		s.add("v3");
		System.out.println(distance(s, "v2", g));
*/
		
		
		//WikiCrawler w = new WikiCrawler("/wiki/Computer_Science", 100, s, "WikiCS.txt");
		//w.crawl();
		NetworkInfluence a = new NetworkInfluence("WikiCS.txt");
		ArrayList<String> top10 =  a.mostInfluentialDegree(10);
		
		int n = 1;
		for(String x : a.graph.graph.keySet()) 
		{
			System.out.println(n + " " + x);
			n++;
		}
		
		
	}
	
	public static ArrayList<String> mostInfluentialSubModular(int k, DirectedGraph graph) {
		Set<String> vSet = graph.graph.keySet();
		if(k >= vSet.size()) //return vSet if k >= |v|
			return new ArrayList<>(vSet);
		ArrayList<String> result = new ArrayList<>(); //result set
		HashSet<String> resultSet = new HashSet<>();
		for(int i = 0; i < k; i++) { //iterate k times
			Pair max = null;
			for(String v : vSet) { //for each unvisited array
				if(!resultSet.contains(v)) {
					result.add(v); //try adding it
					float vInf = influence(result, graph); //calculate the result
					if(max == null || vInf > max.val)
						max = new Pair(v, vInf);
					result.remove(v); //remove it temporarily
				}
			}
			result.add(max.vertex);
			resultSet.add(max.vertex); //add on the max
		}
		return result;
	}
	
	public static int dist(ArrayList<String> s, String v, DirectedGraph g) {
		DirectedGraph rGraph = g.reverseGraph();
		HashSet<String> sSet = new HashSet<>(s); //convert the array into a hashSet for contains checks
		if(sSet.contains(v)) ///return 0 if v is in the set
			return 0;
		Queue<String> q = new LinkedList<>(); //init the queue
		HashSet<String> visited = new HashSet<>(); //init the visited set
		HashMap<String, Integer> levels = new HashMap<>(); //track levels of each node
		q.add(v);
		visited.add(v); //add the first node to both sets
		levels.put(v, 0); //mark u's level as 0
		while(!q.isEmpty()) {
			String curr = q.poll(); //get the head of the queue
			if(sSet.contains(curr))
				return levels.get(curr); //return the level if we found a path
			for(String n : rGraph.graph.get(curr)) //for each neighbor
				if(!visited.contains(n)) { //add them to the sets if they have not been visited
					q.add(n);
					visited.add(n);
					levels.put(n, levels.get(curr) + 1); //increment the level value
				}			
		} //finish bfs
		return -1; //return -1 if no path was found to the set
	}
	
	public static float influence(ArrayList<String> s, DirectedGraph graph) {
		HashMap<Integer, Integer> levelFreqs = new HashMap<>(); //hash map for storing level frequencies
		Set<String> vSet = graph.graph.keySet();
		for(String v : vSet) {
			int level = distance(s, v, graph); //compute distance from the set for every node
			if(level == -1) //ignore unreachable vertices
				continue;
			if(levelFreqs.containsKey(level))
				levelFreqs.put(level, levelFreqs.get(level) + 1); //increment the frequencies
			else
				levelFreqs.put(level, 1);
		}
		float factor = 1;
		float sum = 0;
		int size = levelFreqs.keySet().size();
		for(int i = 0; i < size; i++) { //sum up all of the frequencies multiplied by the factor
			sum += factor * levelFreqs.get(i);
			factor *= 0.5;
		}
		return sum;
	}
	
	public static int distance(ArrayList<String> s, String v, DirectedGraph g) {
		int min = Integer.MAX_VALUE; //initialize min to the maximum possible value
		for(int i = 0; i< s.size(); i++) {
			String curr = s.get(i);
			if(curr.equals(v)) //exit if v is in the set
				return 0;
			int dist = distance(curr, v, g); //compute the distance
			if(dist >= 0 && dist < min) //update the distance if it is smaller and we found a path
				min = dist;
		}
		return min != Integer.MAX_VALUE ? min : -1; //return the minimum only if a path was found
	}
	
	public static int distance(String u, String v, DirectedGraph g) {
		return shortestPath(u, v, g).size() - 1; //return the size of the path - 1 (number of edges)
	}
	
	public static ArrayList<String> shortestPath(String u, String v, DirectedGraph graph) {
		if(u.equals(v))
			return new ArrayList<>();
		Queue<String> q = new LinkedList<>(); //init the queue
		HashSet<String> visited = new HashSet<>(); //init the visited set
		HashMap<String, String> preds = new HashMap<>(); //use predecessors for tracking paths
		q.add(u);
		visited.add(u); //add the first node to both sets
		boolean foundV = false; //store a flag for tracking whether we found a path
		while(!q.isEmpty()) {
			String curr = q.poll(); //get the head of the queue
			if(curr.equals(v)) { 
				foundV = true; //update the flag if we found the target
				break; //exit the loop if we hit the target
			}
			for(String n : graph.graph.get(curr)) //for each neighbor
				if(!visited.contains(n)) { //add them to the sets if they have not been visited
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
			path.add(curr); //add each element in the path backwards
			curr = preds.get(curr); //get the next predecessor
		}
		path.add(u); //add the start of the path
		reverse(path); //reverse the graph to fix the path order
		return path; //return the correct path
	}
	
	private static void reverse(ArrayList<String> path) {
		int size = path.size(); //store the size
		for(int i = 0; i < size / 2; i++) { //iterate through half the array
			String temp = path.get(i);
			path.set(i, path.get(size - i - 1)); //swap the i_th element and the (size - i - 1)th element
			path.set(size - i - 1, temp);
		}						
	}	
	
	static class Pair {
		String vertex;
		float val;
		
		Pair(String s, float d) {
			vertex = s;
			val = d;
		}
		
		public String toString() {
			return vertex + " " + val;
		}
	}
	
//	private static ArrayList<String> mostInfluentialDegree(int k, DirectedGraph graph) {
//		PriorityQueue<Pair> q = new PriorityQueue<>((a,b) -> (int) (b.val - a.val)); //create a max heap
//		Set<String> vSet = graph.graph.keySet();
//		for(String v : vSet)
//			q.add(new Pair(v, graph.outDegree(v))); //add each outDegree to the queue
//		ArrayList<String> results = new ArrayList<>(k);
//		for(int i = 0; i < k && !q.isEmpty(); i++)
//			results.add(q.poll().vertex);
//		return results;
//	}
	
	public static float influence(String u, DirectedGraph graph) {
		Queue<String> q = new LinkedList<>(); //init the queue
		HashSet<String> visited = new HashSet<>(); //init the visited set
		HashMap<String, Double> levels = new HashMap<>(); //track levels of each node
		q.add(u);
		visited.add(u); //add the first node to both sets
		levels.put(u, 1.0); //mark u's level as 0 (weight 1/(2^0) = 1)
		while(!q.isEmpty()) {
			String curr = q.poll(); //get the head of the queue
			for(String n : graph.graph.get(curr)) //for each neighbor
				if(!visited.contains(n)) { //add them to the sets if they have not been visited
					q.add(n);
					visited.add(n);
					levels.put(n, 0.5 * levels.get(curr)); //set the level value (0.5 * the current value)
				}			
		} //finish bfs
		float total = 0;
		Set<String> vSet = levels.keySet();
		for(String s : vSet) //sum up all the level values
			total += levels.get(s);
		return total;
	}
	
	private static ArrayList<String> mostInfluentialModular(int k, DirectedGraph graph) {
		PriorityQueue<Pair> q = new PriorityQueue<>((a,b) -> {
			float diff = b.val - a.val;
			return diff == 0 ? 0 : (diff > 0 ? 1 : -1); 
		}); //create a max heap
		Set<String> vSet = graph.graph.keySet();
		for(String v : vSet)
			q.add(new Pair(v, influence(v, graph))); //add each influence to the queue
		ArrayList<String> results = new ArrayList<>(k);
		for(int i = 0; i < k && !q.isEmpty(); i++)
			results.add(q.poll().vertex);
		return results;
	}
}
