import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.text.NumberFormat;

public class NaiveBayes{
	
	private HashMap<String, Integer> vocabulary;
	private HashMap<String, Double> classPriors;
	private HashMap<String, Integer> numClass;
	private HashMap<String, Integer> numClassPosition;
	private HashMap<String, HashMap<String, Double>> wordProb;
	
	public NaiveBayes() throws FileNotFoundException{
		vocabulary = new HashMap<String, Integer>();
		classPriors = new HashMap<String, Double>();
		numClass = new HashMap<String, Integer>();
		numClassPosition = new HashMap<String, Integer>();
		wordProb = new HashMap<String, HashMap<String, Double>>();
	}
	
	/*
	 * Learn necessary information for classifying text
	 */
	public void learn(String trainFolder) throws FileNotFoundException{
		System.out.println("-----Start learning-----");
	
		File folder = new File(trainFolder);
	    int numClassCounter = 0;
	    int numClassPositionCounter = 0;
	    int numWordText = 0;
	    int totalTexts = 0;
	    String nextString;
	    HashMap<String,Integer> textWords = new HashMap<String,Integer>();
	    
	    /*
	     * Collect all the vocabulary, tokens, etc in the training example
	     * Meanwhile record the number of one word in each class, the number 
	     * of text and the number fo total words in each class
	     */
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
	            			//System.out.println(nextString);
	            			nextString = nextString.toLowerCase();
	            			
	            			if (!nextString.isEmpty() && vocabulary.get(nextString)==null){
	            				vocabulary.put(nextString, 1);
	            			}
	            			else if(!nextString.isEmpty() && vocabulary.get(nextString)!=null){
	            				vocabulary.put(nextString, vocabulary.get(nextString)+1);
	            			}
	            			
	            			if (wordProb.get(classFolder.getName()).get(nextString) == null){
	            				wordProb.get(classFolder.getName()).put(nextString, (double) 1);
	            				numWordText++;
	            			}
	            			else{
	            				if (textWords.get(nextString)==null){
	            					wordProb.get(classFolder.getName()).put(nextString, wordProb.get(classFolder.getName()).get(nextString)+1);
	            					numWordText++;
	            				}
	            			}
	            			textWords.put(nextString,0);
	            		}
	            		textWords.clear();
	            	}	
	            }
	        	
	        	numClass.put(classFolder.getName(), numClassCounter);
	        	totalTexts+=numClassCounter;
	        	numClassCounter = 0;
	        	//numClassPosition.put(classFolder.getName(), numClassPositionCounter);
	        	numClassPosition.put(classFolder.getName(), numWordText);
	        	numClassPositionCounter = 0;
	        	numWordText = 0;
	        }
	    }
		
		String nextKey;
		String nextKey2;
		Iterator<String> it = vocabulary.keySet().iterator();
		Iterator<String> it2;
		/*
		 * Remove words of which number is greater than 100 and less than 3
		 * Also remove the according word in the vocabulary of each class
		 */
		while(it.hasNext()){
			nextKey = it.next();
			
			if (vocabulary.get(nextKey)>100 || vocabulary.get(nextKey)<3){
				it.remove();
				it2 = wordProb.keySet().iterator();
				while(it2.hasNext()){
					nextKey2 = it2.next();
					if (wordProb.get(nextKey2).get(nextKey)!=null)
						numClassPosition.put(nextKey2, (int) (numClassPosition.get(nextKey2)-wordProb.get(nextKey2).get(nextKey)));
					wordProb.get(nextKey2).remove(nextKey);
				}
			}
		}
		System.out.println(vocabulary.size());
		
		/*
		 * Calculate the total number of text containing words in vocabulary
		 */
		it = vocabulary.keySet().iterator();
		while(it.hasNext()){
			nextKey = it.next();
			
		}
		
		/*
		 * Calculate priors for each class
		 */
		it = numClass.keySet().iterator();
		
		while(it.hasNext()){
			nextKey = it.next();
			classPriors.put(nextKey, numClass.get(nextKey)/(double)totalTexts);
			//System.out.println(nextKey+":"+classPriors.get(nextKey));
		}
		
		/*
		 * calculate the probability of a certain word in each class
		 * M-estimate smoothing is used to avoid overfitting
		 */
		it = wordProb.keySet().iterator();
		double tmpProb = 0;
		
		while(it.hasNext()){
			nextKey = it.next();
			it2 = vocabulary.keySet().iterator();
			while(it2.hasNext()){
				nextKey2 = it2.next();
				//if this class does not contain the word, nk is zero
				if (wordProb.get(nextKey).get(nextKey2)!=null){
					tmpProb = (double)(wordProb.get(nextKey).get(nextKey2)+1)/(numClassPosition.get(nextKey)+vocabulary.size());
				}
				else{
					tmpProb = 1.0/(numClassPosition.get(nextKey)+vocabulary.size());
				}
				wordProb.get(nextKey).put(nextKey2, tmpProb);
			}
		}
		System.out.println("-----End learning-----");
	}
	
	public void test(String testFolder) throws FileNotFoundException{
		System.out.println("-----Start testing-----");
		File folder = new File(testFolder);
		int totalTest = 0;
		int totalCorrect = 0;
		int classTest = 0;
		int classCorrect = 0;
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMinimumFractionDigits(1);
		
		for (File classFolder : folder.listFiles()){
			if (classFolder.isDirectory()){
				
				System.out.println(classFolder.getName());
				
				for (File text : classFolder.listFiles()){
					if (text.isFile()){
						totalTest++;
						classTest++;
            		
            			if (calculateMAP(text).equals(classFolder.getName())){
            				totalCorrect++;
            				classCorrect++;
            			}
					}
				}
				
				System.out.println("Accuracy:"+nf.format((double)classCorrect/classTest));
				classCorrect = 0;
				classTest = 0;
				
			}
		}
		System.out.println("Total Accuracy:"+nf.format((double)totalCorrect/totalTest));
		System.out.println("-----End testing-----");
	}
	
	public void classify(String pathName) throws FileNotFoundException{
		System.out.println("-----Start classifying-----");
		File path = new File(pathName);
		if (path.isDirectory()){
			for (File file: path.listFiles()){
				System.out.println(file.getName()+"->"+calculateMAP(file));
			}
		}
		if (path.isFile()){
			System.out.println(path.getName()+"->"+calculateMAP(path));
		}
		System.out.println("-----End classifying-----");
	}
	
	private String calculateMAP(File text) throws FileNotFoundException{
		Scanner s;
		String nextString;
		String MAP = "";
		double MaxP = 0;
		double posterior = 0;
		
		Iterator<String> it = wordProb.keySet().iterator();
		
		while(it.hasNext()){
			s = new Scanner(text);

			String className = it.next();
			posterior = classPriors.get(className)*1E200;
			
			while(s.hasNext()){           			
    			nextString = s.next();
    			nextString = nextString.replaceAll("[^a-zA-Z]", "");
    			nextString = nextString.toLowerCase();   	
    			
    			if (vocabulary.get(nextString)!=null){
    				posterior*=wordProb.get(className).get(nextString)*1E3;
    			}
    		}
			
			if (posterior>MaxP){
				MaxP = posterior;
				MAP = className;
			}
		}
		return MAP;
	}
	
	public static void main(String[] args) throws IOException{
		NaiveBayes n = new NaiveBayes();
		n.learn("20news-bydate/20news-bydate-train");
		n.test("20news-bydate/20news-bydate-test");
	}
}