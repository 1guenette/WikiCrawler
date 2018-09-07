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
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class WikiCrawler {
	static final String BASE_URL = "https://en.wikipedia.org"; //required base url
	
	private String seed; //relative address of the seed URL
	
	private int maxPages; //max number of pages to be searched
	
	private ArrayList<String> keywords; //words to be found in the graph
	
	private static File outputFile; //the File to which the graph is being outputted
	
	private int numRequests = 0;
	
	private static PrintWriter pw;
	
	private Queue<String> q;
	
	private HashSet<String> visited;
	
	public WikiCrawler(String seedUrl, int max, ArrayList<String> topics, String fileName) {
		seed = seedUrl;
		maxPages = max;
		keywords = topics;
		outputFile = new File(fileName);
		try {
			pw = new PrintWriter(outputFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void crawl() {
		visited = new HashSet<>();
		q = new LinkedList<>();
		String actualText = getWikiText(seed);
		if(!search(actualText)) return; // return if the seed doesn't contain the keywords
		q.add(seed);
		visited.add(seed);
		while(!q.isEmpty()) {
			String relAddress = q.poll();
			
			actualText = getWikiText(relAddress);
			if(!search(actualText)) continue;
			
			ArrayList<String> links = findLinks(actualText);
			for(String link : links){
				if(!relAddress.equals(link)){
					System.out.println(relAddress + " " + link);
					writeToFile(relAddress, link);
				}
			}

			System.out.println("QUEUE: " + q.size());
		}
		pw.flush();
		pw.close();
		System.out.println("QUEUE: " + q.size());
		System.out.println("VISITED: " + visited);

	}
	
	public String getURL(String url) 
	{
		String content = null;
		URLConnection connection = null;
		try {
		  connection =  new URL(url).openConnection();
		  numRequests = numRequests + 1 % 25;
		  
		  // Sleep after every 25 requests
		  if(numRequests == 0){
			  System.out.println("SLEEPING");
			  Thread.sleep(3000);
		  }
		  
		  Scanner scanner = new Scanner(connection.getInputStream());
		  scanner.useDelimiter("\\Z");
		  content = scanner.next();
		  scanner.close();
		}catch ( Exception ex ) {
		    ex.printStackTrace();
		}
		return content;
		
	}
	

	private boolean search(String text) {
		for(String s : keywords)
			if(!text.contains(s)) //naive implementation for now
				return false;
		return true;
	}
	
	private ArrayList<String> findLinks(String text) {
		int linkIndex = 0;
		ArrayList<String> links = new ArrayList<String>();
		while(linkIndex != -1) {
			linkIndex = text.indexOf("href=\"/wiki/", linkIndex + 1);
			if(linkIndex > 0) {
				int endIndex = text.indexOf("\"", linkIndex + 6);
				String link = text.substring(linkIndex+6, endIndex);
				if(!link.contains("#") && !link.contains(":") && !links.contains(link)) {
					String linkText = "";
					if(!keywords.isEmpty())
						linkText = getWikiText(link);
					
					if(visited.size() < maxPages && !visited.contains(link) && search(linkText)){
						visited.add(link);
						q.add(link);
						System.out.println(visited);
						links.add(link);
					} else {
						if(visited.contains(link)){
							links.add(link);
						}
					}
				}
//				System.out.println("LINKS: " + links.size());
				System.out.println("LINK INDEX: " + linkIndex);
			}
		}
		return links;
	}
	
	private static void writeToFile(String root, String to){
		pw.println(root + " " + to);
	}
	
	private String getWikiText(String relAddress){
		String text = getURL(BASE_URL + relAddress);
		int startOfText = text.indexOf("<p>");
		String actualText = text.substring(startOfText + 1);
		return actualText;
	}
}
