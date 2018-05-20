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
    // TODO : Implement
  	  m_trainingData = trainingData;
  	  m_v = v;
  	  m_map[0] = new HashMap<>();
  	  m_map[1] = new HashMap<>();
  	  for(int i=0;i< trainingData.length;i++)
	  {
	  	  int l = 1;
	  	  if( trainingData[i].label == Label.SPORTS )
		  {
		  	  l = 0;
		  }
	  	  for(int j=0;j< trainingData[i].words.length; j++)
		  {
		  	  if(m_map[l].get(trainingData[i].words[j]) != null)
			  {
			  	  int c = m_map[l].get(trainingData[i].words[j]) ;
			  	  m_map[l].replace(trainingData[i].words[j], c+1);
			  }
			  else
			  {
			  	  m_map[l].put(trainingData[i].words[j], 1);
			  }
		  }
	  }
	  documents_per_label_count(trainingData);
	  words_per_label_count(trainingData);

  }

  /*
   * Prints out the number of documents for each label
   */
  public void documents_per_label_count(Instance[] trainingData){
    // TODO : Implement
    m_sports_count = 0;
    m_business_count = 0;
    for(int i=0;i<trainingData.length;i++)
	{
	  	  if( trainingData[i].label == Label.SPORTS )
		  {
		  	  m_sports_count++;
		  }
		  else
		  {
			  m_business_count++;
		  }
	}
  }
  public void print_documents_per_label_count(){
  	  System.out.println("SPORTS=" + m_sports_count);
  	  System.out.println("BUSINESS=" + m_business_count);
  }


  /*
   * Prints out the number of words for each label
   */
  public void words_per_label_count(Instance[] trainingData){
    // TODO : Implement
    m_sports_word_count = 0;
    m_business_word_count = 0;
	for(int i=0;i<trainingData.length;i++)
	{
	  	  if( trainingData[i].label == Label.SPORTS )
		  {
		  	  m_sports_word_count+=trainingData[i].words.length;
		  }
		  else
		  {
		  	  m_business_word_count+=trainingData[i].words.length;
		  }
	}
  }

  public void print_words_per_label_count(){
  	  System.out.println("SPORTS=" + m_sports_word_count);
  	  System.out.println("BUSINESS=" + m_business_word_count);
  }

  /**
   * Returns the prior probability of the label parameter, i.e. P(SPAM) or P(HAM)
   */
  @Override
  public double p_l(Label label) {
    // TODO : Implement
    double ret = 0;
    if(label == Label.SPORTS)
	{
		ret = (double)m_sports_count / (double) (m_sports_count+m_business_count);
	}
	else
	{
		ret = (double)m_business_count / (double) (m_sports_count+m_business_count);
	}
    return ret;
  }

  /**
   * Returns the smoothed conditional probability of the word given the label, i.e. P(word|SPORTS) or
   * P(word|BUSINESS)
   */
  @Override
  public double p_w_given_l(String word, Label label) {
    // TODO : Implement
    double ret = 0;
    m_delta = 0.00001;
    if (label == Label.SPORTS)
	{
		int wc = 0;
		if (m_map[0].get(word) != null)
		{
			wc = m_map[0].get(word);
		}
		ret = (double)(m_delta + wc) / (m_v * m_delta + (double) m_sports_word_count);
	}
	else
	{
		int wc = 0;
		if (m_map[1].get(word) != null)
		{
			wc = m_map[1].get(word);
		}
		ret = (double)(m_delta + wc) / (m_v * m_delta + (double) m_business_word_count);
	}
    return ret;
  }

  /**
   * Classifies an array of words as either SPAM or HAM.
   */
  @Override
  public ClassifyResult classify(String[] words) {
    // TODO : Implement
    ClassifyResult ret = new ClassifyResult();
    ret.label = Label.SPORTS;
    ret.log_prob_sports = Math.log(p_l(Label.SPORTS));
    ret.log_prob_business = Math.log(p_l(Label.BUSINESS));
    for(int i = 0;i<words.length;i++)
	{
		ret.log_prob_sports += Math.log(p_w_given_l(words[i],Label.SPORTS));
	}
    for(int i = 0;i<words.length;i++)
	{
		ret.log_prob_business += Math.log(p_w_given_l(words[i],Label.BUSINESS));
	}
	if(ret.log_prob_sports < ret.log_prob_business)
		ret.label = Label.BUSINESS;
    return ret; 
  }
  
  /*
   * Constructs the confusion matrix
   */
  @Override
  public ConfusionMatrix calculate_confusion_matrix(Instance[] testData){
    // TODO : Implement
    int TP, FP, FN, TN;
    TP = 0;
    FP = 0;
    FN = 0;
    TN = 0;
    for(Instance x:testData)
	{
		if(classify(x.words).label == x.label)
		{
			if(x.label == Label.SPORTS)
				TP++;
			else
				TN++;
		}
		else
		{
			if(x.label == Label.SPORTS)
				FN++;
			else
				FP++;
		}
	}
    return new ConfusionMatrix(TP,FP,FN,TN);
  }
  
}
