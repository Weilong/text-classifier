import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.math.BigDecimal;

public class NaiveBayes{
	
	private String trainFolder;
	private HashMap<String, Integer> vocabulary;
	private HashMap<String, Double> classPriors;
	private HashMap<String, Integer> numClass;
	private HashMap<String, Integer> numClassPosition;
	private HashMap<String, HashMap<String, Double>> wordProb;
	
	public NaiveBayes(String trainFolder) throws FileNotFoundException{
		this.trainFolder = trainFolder;
		vocabulary = new HashMap<String, Integer>();
		classPriors = new HashMap<String, Double>();
		numClass = new HashMap<String, Integer>();
		numClassPosition = new HashMap<String, Integer>();
		wordProb = new HashMap<String, HashMap<String, Double>>();
	}
	
	public void learn(String textSet, String className) throws FileNotFoundException{
		
	}
	
	public void test(String testFolder) throws FileNotFoundException{
		File folder = new File(testFolder);
		String nextString;
		int totalText = 0;
		int correct = 0;
		
		for (File classFolder : folder.listFiles()){
			if (classFolder.isDirectory()){
				System.out.println(classFolder.getName());
				for (File text : classFolder.listFiles()){
					if (text.isFile()){
						totalText++;
						Scanner s;
	            		
	            		String MAP = "";
            			double MaxP = 0;
            			double posterior = 0;
	            		//BigDecimal MaxP = new BigDecimal("0");
            			//BigDecimal posterior = new BigDecimal("0");
            			//double prep = 1;
            			//System.out.println("posterior:"+posterior);
            			Iterator<String> it = wordProb.keySet().iterator();
            			
            			while(it.hasNext()){
            				s = new Scanner(text);
    	            		//s.useDelimiter("\\W");
            				
            				String className = it.next();
            				posterior = classPriors.get(className)*1E200;
            				//posterior = new BigDecimal(classPriors.get(className).toString());
            				while(s.hasNext()){           			
    	            			nextString = s.next();
    	            			nextString = nextString.replaceAll("[^a-zA-Z]", "");
    	            			nextString = nextString.toLowerCase();   	
    	            			
    	            			if (vocabulary.get(nextString)!=null){
    	            				//System.out.println(wordProb.get(className).get(nextString));
    	            				//System.out.println(posterior.toString());
    	            				//posterior = posterior.multiply(new BigDecimal(wordProb.get(className).get(nextString)));
    	            				posterior*=wordProb.get(className).get(nextString)*1E3;
    	            			}
    	            		}
            				//if (posterior.compareTo(MaxP)==1){
            				if (posterior>MaxP){
            					MaxP = posterior;
            					MAP = className;
            				}
            			}
            			//if (MAP.equals(""))
            				//System.out.println(MAP+":"+classFolder.getName());
            			if (MAP.equals(classFolder.getName())){
            				correct++;
            			}
					}
				}
				/*
				System.out.println("correct:"+correct+" totalText:"+totalText);
				System.out.println("Accuracy:"+(double)correct/totalText);
				correct = 0;
				totalText = 0;
				*/
			}
		}
		System.out.println("Accuracy:"+(double)correct/totalText);

	}
	
	public void classify(String text) throws FileNotFoundException{
		
	}
	
	public void prepareData() throws FileNotFoundException{
		
		File folder = new File(trainFolder);
	    int numClassCounter = 0;
	    int numClassPositionCounter = 0;
	    int totalTexts = 0;
	    String nextString;
	    
		for (File classFolder : folder.listFiles()){
	        if (classFolder.isDirectory()){
	            System.out.println(classFolder.getName());
	            
	            if (wordProb.get(classFolder.getName())==null)
	            	wordProb.put(classFolder.getName(), new HashMap<String, Double>());
	            
	        	for (File text : classFolder.listFiles()){
	        		if (text.isFile()){
	            		numClassCounter++;
	            		Scanner s = new Scanner(text);
	            		
	            		while(s.hasNext()){
	            			numClassPositionCounter++;
	            			nextString = s.next();
	            			nextString = nextString.replaceAll("[^a-zA-Z]", "");
	            			nextString = nextString.toLowerCase();
	            			if (!nextString.isEmpty() && vocabulary.get(nextString)==null){
	            				vocabulary.put(nextString, 1);
	            			}
	            			else if(!nextString.isEmpty() && vocabulary.get(nextString)!=null){
	            				vocabulary.put(nextString, vocabulary.get(nextString)+1);
	            			}
	            			
	            			if (wordProb.get(classFolder.getName()).get(nextString) == null){
	            				wordProb.get(classFolder.getName()).put(nextString, (double) 1);
	            			}
	            			else{
	            				wordProb.get(classFolder.getName()).put(nextString, wordProb.get(classFolder.getName()).get(nextString)+1);
	            			}
	            		}
	            	}
	            }
	        	//System.out.println(classFolder.getName()+" total texts:"+numClassCounter);
	        	//System.out.println(classFolder.getName()+" total positions:"+numClassPositionCounter);
	        	numClass.put(classFolder.getName(), numClassCounter);
	        	totalTexts+=numClassCounter;
	        	numClassCounter = 0;
	        	numClassPosition.put(classFolder.getName(), numClassPositionCounter);
	        	numClassPositionCounter = 0;
	        }
	    }
		String nextKey;
		Iterator<String> it = vocabulary.keySet().iterator();
		Iterator<String> it2;
		
		while(it.hasNext()){
			nextKey = it.next();
			
			if (vocabulary.get(nextKey)>100 || vocabulary.get(nextKey)<3){
				it.remove();
				it2 = wordProb.keySet().iterator();
				while(it2.hasNext()){
					wordProb.get(it2.next()).remove(nextKey);
				}
			}
		}
		System.out.println(vocabulary.size());
		
		it = numClass.keySet().iterator();
		
		while(it.hasNext()){
			nextKey = it.next();
			classPriors.put(nextKey, numClass.get(nextKey)/(double)totalTexts);
			System.out.println(nextKey+":"+classPriors.get(nextKey));
		}
		
		it = wordProb.keySet().iterator();
		String nextKey2;
		double tmpProb = 0;
		
		while(it.hasNext()){
			nextKey = it.next();
			it2 = vocabulary.keySet().iterator();
			while(it2.hasNext()){
				nextKey2 = it2.next();
				if (wordProb.get(nextKey).get(nextKey2)!=null){
					tmpProb = (double)(wordProb.get(nextKey).get(nextKey2)+1)/(numClassPosition.get(nextKey)+vocabulary.size());
				}
				else{
					tmpProb = 1.0/(numClassPosition.get(nextKey)+vocabulary.size());
				}
				wordProb.get(nextKey).put(nextKey2, tmpProb);
			}
		}
	}
	
	public static void main(String[] args) throws IOException{
		NaiveBayes n = new NaiveBayes("20news-bydate/20news-bydate-train");
		n.prepareData();
		n.test("20news-bydate/20news-bydate-test");
	}
}