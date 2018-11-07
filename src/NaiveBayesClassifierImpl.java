import java.util.HashMap;
import java.util.Map;
import java.lang.Math;

/**
 * Your implementation of a naive bayes classifier. Please implement all four methods.
 */

public class NaiveBayesClassifierImpl implements NaiveBayesClassifier {
	private Instance[] m_trainingData;
	private int m_v;
	private double m_delta;
	public int m_sports_count, m_business_count;
	public int m_sports_word_count, m_business_word_count;
	private HashMap<String,Integer> m_map[] = new HashMap[2];

  /**
   * Trains the classifier with the provided training data and vocabulary size
   */
  @Override
  public void train(Instance[] trainingData, int v) {
    // For all the words in the documents, count the number of occurrences. Save in HashMap
    // e.g.
    // m_map[0].get("catch") should return the number of "catch" es, in the documents labeled sports
    // Hint: m_map[0].get("asdasd") would return null, when the word has not appeared before.
    // Use m_map[0].put(word,1) to put the first count in.
    // Use m_map[0].replace(word, count+1) to update the value
  	  m_trainingData = trainingData;
  	  m_v = v;
  	  m_map[0] = new HashMap<>();
  	  m_map[1] = new HashMap<>();
  	  
  	  //Loop through each instance in trainingData
  	  for (Instance currInst : m_trainingData) {
  		  //Loop through each string in words array
  		int index = 1;
  		if(currInst.label == Label.SPORTS)
  			index = 0;
  		  for (String word : currInst.words) {
  			   //to be updated if business label is seen
//  			  word = word.toLowerCase(); //change word to lower case
//  			  if (word.equals("sports") || word.equals("business")) {
//  				  //change index value if label is business
//  				  if (word.equalsIgnoreCase("business")) {
//  					  index = 1;
//  				  } //else leave index as 0
//  			  } //since current word is a label, check the next word
  			  //Current word is not a label, hence check if it exists in hashmap {
  				  //current word does not exist in hashamp, thus add it
  				  if (m_map[index].get(word) == null) {
  					  m_map[index].put(word, 1);
  				  }
  				  //Add one count to word in hashmap
  				  else {
  					  m_map[index].replace(word, m_map[index].get(word) + 1);
  				  }
  			  }
  			  
  		  }
  	  
  	documents_per_label_count(m_trainingData);
  	words_per_label_count(m_trainingData);
  }

  /*
   * Counts the number of documents for each label
   */
  public void documents_per_label_count(Instance[] trainingData){
    m_sports_count = 0;
    m_business_count = 0;
    for (Instance currInst : trainingData) {
    	if (currInst.label.equals(Label.SPORTS)) { 
    		m_sports_count++;
    	}
    	else {
    		m_business_count++;
    	}
    }
  }

  /*
   * Prints the number of documents for each label
   */
  public void print_documents_per_label_count(){
  	  System.out.println("SPORTS=" + m_sports_count);
  	  System.out.println("BUSINESS=" + m_business_count);
  }


  /*
   * Counts the total number of words for each label
   */
  public void words_per_label_count(Instance[] trainingData){

    m_sports_word_count = 0;
    m_business_word_count = 0;
    
    for (Instance currInst : trainingData) {
    	Label label = currInst.label;
    	if (label.equals(Label.SPORTS)) { 
    		m_sports_word_count += currInst.words.length;
    	}
    	else { //business
    		m_business_word_count += currInst.words.length;
    	}
    }
  }

  /*
   * Prints out the number of words for each label
   */
  public void print_words_per_label_count(){
  	  System.out.println("SPORTS=" + m_sports_word_count);
  	  System.out.println("BUSINESS=" + m_business_word_count);
  }

  /**
   * Returns the prior probability of the label parameter, i.e. P(SPORTS) or P(BUSINESS)
   */
  @Override
  public double p_l(Label label) {
    // Calculate the probability for the label. No smoothing here.
    // Just the number of label counts divided by the number of documents.
    double ret = 0;
    if (label.equals(Label.SPORTS)) {
    	ret = (double)m_sports_count / (double)(m_sports_count + m_business_count);
    }
    else {
    	ret = (double)m_business_count / (double)(m_sports_count + m_business_count);
    }
    return ret;
  }

  /**
   * Returns the smoothed conditional probability of the word given the label, i.e. P(word|SPORTS) or
   * P(word|BUSINESS)
   */
  @Override
  public double p_w_given_l(String word, Label label) {
    // Calculate the probability with Laplace smoothing for word in class(label)
    double ret = 0;
    m_delta = 0.00001;
    
    //double sum = 0;
    //Based on specified label, set corresponding parameters for calculating prob
    if (label.equals(Label.SPORTS)) {
    	double token = 0;
    	if (m_map[0].get(word) != null) {
    		
	    	token = m_map[0].get(word); //access hashmap for the token count
    	}
    	ret = (double) (token + m_delta) / (double) (m_v * m_delta + (double)this.m_sports_word_count);
    }
    else {
    	double token = 0;
    	if (m_map[1].get(word) != null) {
    		
	    	token = m_map[1].get(word); //access hashmap for the token count
    	}
    	ret = (double) (token + m_delta) / (double) (m_v * m_delta + (double)this.m_business_word_count);
    }
    
    //(number of tokens of word + delta) / (v*delta + total sum of words)
    
    
    return ret;
  }

  /**
   * Classifies an array of words as either SPORTS or BUSINESS.
   */
  @Override
  public ClassifyResult classify(String[] words) {
    // Sum up the log probabilities for each word in the input data, and the probability of the label
    // Set the label to the class with larger log probability
	//log(label) + summation{prob(word given label)}
	  
    ClassifyResult ret = new ClassifyResult();
    ret.label = Label.SPORTS;
    ret.log_prob_sports = 0;
    ret.log_prob_business = 0;
    double sLogProb = 0.0;
    double bLogProb = 0.0;
    bLogProb = Math.log(this.p_l(Label.BUSINESS));
    sLogProb = Math.log(this.p_l(Label.SPORTS));
    
    //Sports prob
//    double sLogProb = 0.0;
//    if (this.p_l(Label.SPORTS) > 0) {
//    	sLogProb = Math.log(this.p_l(Label.SPORTS)); //to compute final value of log prob sports
//    }
    for (String s : words) {
    	sLogProb += Math.log(this.p_w_given_l(s, Label.SPORTS));
    }
    ret.log_prob_sports += sLogProb; //update value of log prob for sports
    
    //Business prob
//    double bLogProb = 0.0;
//    if (this.p_l(Label.BUSINESS) > 0) {
//    	bLogProb = Math.log(this.p_l(Label.BUSINESS));
//    }
   // bLogProb = Math.log(this.p_l(Label.BUSINESS));
    //sLogProb = Math.log(this.p_l(Label.SPORTS));
    
    for (String b : words) {
    	bLogProb += Math.log(this.p_w_given_l(b, Label.BUSINESS));
    }
    ret.log_prob_business += bLogProb; //update value of log prob for business
    
    //Check to see which log prob is greater
    //if business > sports, change ret's labe;
    if (ret.log_prob_business > ret.log_prob_sports) {
    	ret.label = Label.BUSINESS;
    }
    
    return ret; 
  }
  
  /*
   * Constructs the confusion matrix
   */
  @Override
  public ConfusionMatrix calculate_confusion_matrix(Instance[] testData){
    // Count the true positives, true negatives, false positives, false negatives
    int TP, FP, FN, TN;
    TP = 0;
    FP = 0;
    FN = 0;
    TN = 0;

    //Loop through each instance in testData
    for (Instance curr : testData) {
    	ClassifyResult result = this.classify(curr.words);
    	//Positive
    	if(result.label.equals(Label.SPORTS)) {
    		//True
    		if(result.label.equals(curr.label)) {
    			TP++;
    		}
    		//False
    		else {
    			FP++;
    		}
    	}
    	//Negative
    	else {
    		//True
    		if (result.label.equals(curr.label)) {
    			TN++;
    		}
    		//False
    		else {
    			FN++;
    		}
    	}
    }
    return new ConfusionMatrix(TP,FP,FN,TN);
  }
  
  
}
