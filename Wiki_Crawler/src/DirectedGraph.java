import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DirectedGraph {
	
	public HashMap<String, HashSet<String>> graph;
	
	public DirectedGraph rGraph;
	
	public DirectedGraph(){
		graph = new HashMap<>();
	}	
	
	public void addEdge(String start, String end) {
		if(!graph.containsKey(start))
			graph.put(start, new HashSet<>()); //instantiate the starting HashSet if necessary
		if(!start.equals(end))
			graph.get(start).add(end); //add the edge (as long as it's not a self-loop)
		if(!graph.containsKey(end))
			graph.put(end, new HashSet<>()); //instantiate the ending HashSet if necessary
	}
	
	public int outDegree(String node) {
		if(!graph.containsKey(node)) //return -1 for invalid values
			return -1;
		return graph.get(node).size(); //return the neighbor size
	}
	
	public DirectedGraph reverseGraph() {
		if(rGraph == null) { //consruct the reverseGraph only once
			rGraph = new DirectedGraph();
			Set<String> vSet = graph.keySet();
			for(String v : vSet)
				for(String n : graph.get(v))
					rGraph.addEdge(n, v); //add all of the reverse edges
		}
		return rGraph;
	}
}
