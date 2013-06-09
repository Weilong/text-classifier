import java.io.FileNotFoundException;
import java.io.IOException;

public class TextClassifier{
	
	private NaiveBayes classifier;
	
	public TextClassifier() throws FileNotFoundException{
		classifier = new NaiveBayes();
	}
	
	public NaiveBayes getClassifier(){
		return classifier;
	}
	
	public static void main(String[] args) throws IOException{
		TextClassifier tc = new TextClassifier();
		tc.getClassifier().learn(args[0]);
		tc.getClassifier().classify(args[1]);
		//tc.getClassifier().learn("20news-bydate/20news-bydate-train");
		//tc.getClassifier().classify("20news-bydate/20news-bydate-test/alt.atheism/53068");
	}
}