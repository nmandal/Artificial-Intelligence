
import java.util.*;
import java.io.*;

public class Chatbot{
    private static String filename = "./WARC201709_wid.txt";
    private static ArrayList<Integer> readCorpus(){
        ArrayList<Integer> corpus = new ArrayList<Integer>();
        try{
            File f = new File(filename);
            Scanner sc = new Scanner(f);
            while(sc.hasNext()){
                if(sc.hasNextInt()){
                    int i = sc.nextInt();
                    corpus.add(i);
                }
                else{
                    sc.next();
                }
            }
            sc.close();
        }
        catch(FileNotFoundException ex){
            System.out.println("File Not Found.");
        }
        return corpus;
    }
    
    private static ArrayList<String> getVocab() {
        ArrayList<String> vocab = new ArrayList<String>();
		try {
			Scanner s = new Scanner(new File("./vocabulary.txt"));
            vocab.add("OOV");
            while (s.hasNext()) {
            	vocab.add(s.next());
            }
            s.close();
		} catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
		}
		return vocab;
    }
    
    private static double[] getUniProbDist() {
    	ArrayList<Integer> corpus = readCorpus();
    	ArrayList<String>  vocab  = getVocab();
		double[] probDist = new double[vocab.size()];
		
        for (int i = 0; i < corpus.size(); i++) {
        	probDist[corpus.get(i)]++;
        }
        for (int j = 0; j < probDist.length; j++) {
        	probDist[j] = (probDist[j]/(double)corpus.size());
        }     
    	return probDist;
    }
    
    private static double[] getProbDist(ArrayList<Integer> words) {
    	ArrayList<Integer> corpus = readCorpus();
        ArrayList<String> vocab = getVocab();
		double[] probDist = new double[vocab.size()];
		
		for (int i = 0; i < words.size(); i++) {
			for (int j = 0; j < corpus.size(); j++) {
				if (words.get(i) == j)
					probDist[corpus.get(j)]++;
			}
		}
		
		for (int j = 0; j < probDist.length; j++) {
			probDist[j] = (probDist[j]/(double)words.size());
		}
		
        return probDist;
    }
        
    static public void main(String[] args){
        ArrayList<Integer> corpus = readCorpus();
		int flag = Integer.valueOf(args[0]);
        
        if(flag == 100){
			int w = Integer.valueOf(args[1]);
            int count = 0;
            //TODO count occurence of w			
            for (int i = 0; i < corpus.size(); i++) {
            	if (corpus.get(i) == w) count++;
            }

            System.out.println(count);
            System.out.println(String.format("%.7f",count/(double)corpus.size()));
        }
        else if(flag == 200){
            int n1 = Integer.valueOf(args[1]);
            int n2 = Integer.valueOf(args[2]);
            //TODO generate
            double[] probDist = getUniProbDist();
            double r = (double) n1 /n2;
            int wi = 0;
            double lwi = 0;
            double rwi = 0;
                        
            ArrayList<double[]> segments = new ArrayList<double[]>();
            if (probDist[0] != 0) segments.add(segment(segments, 0, probDist));
        
            for (int i = 1; i < probDist.length; i++) {
            	if (probDist[i] != 0) segments.add(segment(segments, i, probDist));
            }
                                
            for (int j = 1; j < segments.size(); j++) { 
            	if (r > segments.get(j-1)[1] && r < segments.get(j)[1]) {
            		wi  = (int) segments.get(j-1)[0];
            		lwi = segments.get(j-1)[1];
            		rwi = segments.get(j)[1];
            	}
            	else if (r > segments.get(j)[1]) {
            		wi = (int) segments.get(j)[0];
            		lwi = segments.get(j)[1];
            		rwi = 1;
            	}
            }

            System.out.println(wi);
            System.out.println(String.format("%.7f", lwi));
            System.out.println(String.format("%.7f", rwi));
        }
        else if(flag == 300){
            int h = Integer.valueOf(args[1]);
            int w = Integer.valueOf(args[2]);
            int count = 0;
            ArrayList<Integer> words_after_h = new ArrayList<Integer>();
        	for (int i = 0; i < corpus.size() - 1; i++) {
            	if (corpus.get(i) == h) {
            		words_after_h.add(i+1);
            	}
        	}
            //TODO
            for (int i = 0; i < corpus.size() - 1; i++) {
            	if (corpus.get(i) == h) {
            		if (corpus.get(i+1) == w) {
            			count++;
            		}
            	}
            }            

            //output 
            System.out.println(count); // c(h,w)
            System.out.println(words_after_h.size());
            System.out.println(String.format("%.7f",count/(double)words_after_h.size())); // p(w | h)
        }
        else if(flag == 400){
            int n1 = Integer.valueOf(args[1]);
            int n2 = Integer.valueOf(args[2]);
            int h = Integer.valueOf(args[3]);
            //TODO
            double r = (double) n1 /n2;
            int word = 0;
            double leftWord  = 0;
            double rightWord = 0;
            
            ArrayList<Integer> words_after_h = new ArrayList<Integer>();
        	for (int i = 0; i < corpus.size() - 1; i++) {
            	if (corpus.get(i) == h) {
            		words_after_h.add(i+1);
            	}
        	}
        	        	
        	double[] probDist = getProbDist(words_after_h);
        	ArrayList<double[]> segments = new ArrayList<double[]>();
            if (probDist[0] != 0) segments.add(segment(segments, 0, probDist));
                        
            for (int i = 1; i < probDist.length; i++) {
            	if (probDist[i] != 0) segments.add(segment(segments, i, probDist));
            }
            
            for (int j = 0; j < segments.size(); j++) {
            	if (r <= segments.get(j)[1]) {
            		word = (int) segments.get(0)[0];
            		leftWord = segments.get(0)[1];
            		rightWord = segments.get(0)[2];
            		break;
            	}
            	else if (r > segments.get(j)[1] && r < segments.get(j)[2]) {
            		word = (int) segments.get(j)[0];
            		leftWord = segments.get(j)[1];
            		rightWord = segments.get(j)[2];
            		break;
            	}
            	else {
            		word = (int) segments.get(j+1)[0];
            		leftWord = segments.get(j+1)[1];
            		rightWord = segments.get(j+1)[2];
            		break;
            	}
            }
        	
            System.out.println(word);
            System.out.println(String.format("%.7f", leftWord));
            System.out.println(String.format("%.7f", rightWord));
        }
        else if(flag == 500){
            int h1 = Integer.valueOf(args[1]);
            int h2 = Integer.valueOf(args[2]);
            int w = Integer.valueOf(args[3]);
            int count = 0;
            ArrayList<Integer> words_after_h1h2 = new ArrayList<Integer>();
            //TODO
        	for (int i = 0; i < corpus.size() - 2; i++) {
            	if (corpus.get(i) == h1) {
            		if (corpus.get(i+1) == h2) {
            				words_after_h1h2.add(i+2);
            		}
            	}
        	} 
        	
        	for (int i = 0; i < corpus.size() - 2; i++) {
            	if (corpus.get(i) == h1) {
            		if (corpus.get(i+1) == h2) {
            			if (corpus.get(i+2) == w) {
            				count++;
            			}
            		}
            	}
        	}   
            //output 
            System.out.println(count);
            System.out.println(words_after_h1h2.size());
            if(words_after_h1h2.size() == 0)
                System.out.println("undefined");
            else
                System.out.println(String.format("%.7f",count/(double)words_after_h1h2.size()));
        }
        else if(flag == 600){
            int n1 = Integer.valueOf(args[1]);
            int n2 = Integer.valueOf(args[2]);
            int h1 = Integer.valueOf(args[3]);
            int h2 = Integer.valueOf(args[4]);
            //TODO
            double r = (double) n1 /n2;
            int word = 0;
            double leftWord  = 0;
            double rightWord = 0;
            ArrayList<Integer> words_after_h1h2 = new ArrayList<Integer>();
            
        	for (int i = 0; i < corpus.size() - 1; i++) {
            	if (corpus.get(i) == h1) {
            		if (corpus.get(i+1) == h2) {
            			words_after_h1h2.add(i+2);
            		}
            	}
        	}
        	        	
        	double[] probDist = getProbDist(words_after_h1h2);
        	ArrayList<double[]> segments = new ArrayList<double[]>();
            if (probDist[0] != 0) segments.add(segment(segments, 0, probDist));
            
            for (int i = 1; i < probDist.length; i++) {
            	if (probDist[i] != 0) segments.add(segment(segments, i, probDist));
            }
            
            for (int j = 0; j < segments.size(); j++) {
            	if (r <= segments.get(j)[1]) {
            		word = (int) segments.get(0)[0];
            		leftWord = segments.get(0)[1];
            		rightWord = segments.get(0)[2];
            	}
            	else if (r > segments.get(j)[1] && r < segments.get(j)[2]) {
            		word = (int) segments.get(j)[0];
            		leftWord = segments.get(j)[1];
            		rightWord = segments.get(j)[2];
            	}
            	else {
            		word = (int) segments.get(j+1)[0];
            		leftWord = segments.get(j+1)[1];
            		rightWord = segments.get(j+1)[2];
            	}
            }
            
            if (words_after_h1h2.size() > 0) {
            	System.out.println(word);
                System.out.println(String.format("%.7f", leftWord));
                System.out.println(String.format("%.7f", rightWord));
            } else System.out.println("undefined");
            
        }
        else if(flag == 700){
            int seed = Integer.valueOf(args[1]);
            int t = Integer.valueOf(args[2]);
            int h1=0,h2=0;

            Random rng = new Random();
            if (seed != -1) rng.setSeed(seed);

            if(t == 0){
                // TODO Generate first word using r
                double r = rng.nextDouble();
                System.out.println(h1);
                if(h1 == 9 || h1 == 10 || h1 == 12){
                    return;
                }

                // TODO Generate second word using r
                r = rng.nextDouble();
                System.out.println(h2);
            }
            else if(t == 1){
                h1 = Integer.valueOf(args[3]);
                // TODO Generate second word using r
                double r = rng.nextDouble();
                System.out.println(h2);
            }
            else if(t == 2){
                h1 = Integer.valueOf(args[3]);
                h2 = Integer.valueOf(args[4]);
            }

            while(h2 != 9 && h2 != 10 && h2 != 12){
                double r = rng.nextDouble();
                int w  = 0;
                // TODO Generate new word using h1,h2
                System.out.println(w);
                h1 = h2;
                h2 = w;
            }
        }
        return;
    }
	private static double[] segment(ArrayList<double[]> segments, int i, double[] probDist) {
		double[] interval = new double[3];
		int j = segments.size();
		if (j == 0) {
			interval[0] = i;
			interval[1] = 0;
			interval[2] = probDist[i];
			return interval;
		}
		interval[0] = i;
		interval[1] = segments.get(j-1)[2];
		interval[2] = interval[1] + probDist[i];
		return interval;
	}
}