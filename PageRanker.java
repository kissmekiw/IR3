//Name(s): Pitchapa Lapnimitanan, Teerasit Wongpa
//ID 6088074, 6088224
//Section 3
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class implements PageRank algorithm on simple graph structure.
 * Put your name(s), ID(s), and section here.
 *
 */
public class PageRanker {
	
	/**
	 * This class reads the direct graph stored in the file "inputLinkFilename" into memory.
	 * Each line in the input file should have the following format:
	 * <pid_1> <pid_2> <pid_3> .. <pid_n>
	 * 
	 * Where pid_1, pid_2, ..., pid_n are the page IDs of the page having links to page pid_1. 
	 * You can assume that a page ID is an integer.
	 */
	private static Map<Integer, Set<Integer>> temp = new HashMap<Integer, Set<Integer>>();
	private Map<Integer, Double> pageRank = new HashMap<Integer, Double>();
    Map<Integer, Set<Integer>> pageOutLinks = new HashMap<Integer, Set<Integer>>();
	private Set<Integer> sinkNode = new LinkedHashSet<Integer>();
    private Set<Double> perplexity = new LinkedHashSet<Double>();
	private double d = 0.85;
    private double prePerplexity = -1234;
    private double newPerplexity = -123456;
	
	public void loadData(String inputLinkFilename){
		try(BufferedReader r = Files.newBufferedReader(Paths.get(inputLinkFilename))) {
			String s;
			
			while((s = r.readLine()) != null) {
				String[] data = s.split(" ");
				int P = Integer.parseInt(data[0]);

				Set<Integer> M = new HashSet<Integer>();
				for(int i=1;i<data.length;i++) {
					M.add(Integer.parseInt(data[i]));
				}
				temp.put(P, M);
				/////////////////
//				M.forEach(i ->{
//					if (pageOutLinks.containsKey(data[i])) {
//	                    pageOutLinks.get(M.).add(data[0]);
//	                } else {
//	                    Set<Integer> tempOutLinks = new LinkedHashSet<>();
//	                    tempOutLinks.add(Integer.parseInt(temp[0]));
//	                    pageOutLinks.put(Integer.parseInt(temp[i]), tempOutLinks);
//	                }
//				});
				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method will be called after the graph is loaded into the memory.
	 * This method initialize the parameters for the PageRank algorithm including
	 * setting an initial weight to each page.
	 */
	public void initialize(){
		temp.keySet().forEach(k ->{
			double n = temp.get(k).size();
			double w = 1/Math.abs(n);
			pageRank.put(k, w);
		});
		System.out.println(pageRank);
	}
	
	/**
	 * Computes the perplexity of the current state of the graph. The definition
	 * of perplexity is given in the project specs.
	 */
	public double getPerplexity(){
		double perplexity = 0.0;
        double power = 0.0;
        for (Integer i : pageRank.keySet()) {
            double score = pageRank.get(i);
            power += score * (Math.log(score) / Math.log(2));
        }
        perplexity = Math.pow(2, -(power));
        return perplexity;
		}
	
	/**
	 * Returns true if the perplexity converges (hence, terminate the PageRank algorithm).
	 * Returns false otherwise (and PageRank algorithm continue to update the page scores). 
	 */
	public boolean isConverge(){
		int count = 1;
		int pre = (int) (prePerplexity % 10);
        int next = (int) (newPerplexity % 10);
        if (pre == next) {
            count += 1;
            if (count == 4) {
                return true;
            } else return false;
        } else {
            prePerplexity = newPerplexity;
            count = 1;
        }
		return false;
	}
	
	/**
	 * The main method of PageRank algorithm. 
	 * Can assume that initialize() has been called before this method is invoked.
	 * While the algorithm is being run, this method should keep track of the perplexity
	 * after each iteration. 
	 * 
	 * Once the algorithm terminates, the method generates two output files.
	 * [1]	"perplexityOutFilename" lists the perplexity after each iteration on each line. 
	 * 		The output should look something like:
	 *  	
	 *  	183811
	 *  	79669.9
	 *  	86267.7
	 *  	72260.4
	 *  	75132.4
	 *  
	 *  Where, for example,the 183811 is the perplexity after the first iteration.
	 *
	 * [2] "prOutFilename" prints out the score for each page after the algorithm terminate.
	 * 		The output should look something like:
	 * 		
	 * 		1	0.1235
	 * 		2	0.3542
	 * 		3 	0.236
	 * 		
	 * Where, for example, 0.1235 is the PageRank score of page 1.
	 * 
	 */
	public void runPageRank(String perplexityOutFilename, String prOutFilename){
		 while (!isConverge()) {
	            Map<Integer, Double> newPR = new HashMap<Integer, Double>();
	            double sinkPR = 0;

	            for (Integer sink : sinkNode) {
	                sinkPR += pageRank.get(sink);
	            }
	            temp.keySet().forEach(k->{
	            	double PRScore = (1-d)/temp.size();
//	            	PRScore += (d*sinkPR)/temp.size();
	            	if(temp.containsKey(k)) {
	            		for (Integer q : temp.get(k)) {
	            			PRScore += (d*pageRank.get(q))/pageOutLinks.get(q).size();
	            		}
	            	}
	            	newPR.put(k, PRScore);
	            	pageRank.put(k, newPR.get(k));
	            });
	            


	            newPerplexity = getPerplexity();
	            perplexity.add(newPerplexity);

	            try {
	                FileWriter fw = new FileWriter(prOutFilename);
	                FileWriter fw1 = new FileWriter(perplexityOutFilename);
	                for (Integer p : pageRank.keySet()) {
	                    fw.append(p + " " + pageRank.get(p) + "\n");
	                }
	                for (Double per : perplexity) {
	                    fw1.append(per + "\n");
	                }
	                fw.close();
	                fw1.close();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	          

//	            System.out.println(getPerplexity());
//	            System.out.println(newPR);
	       }
	}
	
	
	/**
	 * Return the top K page IDs, whose scores are highest.
	 */
	public Integer[] getRankedPages(int K){return null;}
	
	public static void main(String args[])
	{
	long startTime = System.currentTimeMillis();
		PageRanker pageRanker =  new PageRanker();
		pageRanker.loadData("test2.dat");
//		System.out.println(temp);
		pageRanker.initialize();
		pageRanker.runPageRank("perplexity.out", "pr_scores.out");
		Integer[] rankedPages = pageRanker.getRankedPages(100);
	double estimatedTime = (double)(System.currentTimeMillis() - startTime)/1000.0;
		
		System.out.println("Top 100 Pages are:\n"+Arrays.toString(rankedPages));
		System.out.println("Proccessing time: "+estimatedTime+" seconds");
	}
}
