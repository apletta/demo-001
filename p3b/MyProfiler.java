/**
 * Filename:   MyProfiler.java
 * Project:    p3b-201901     
 * Authors:    Alex Pletta, LEC001
 *
 * Semester:   Spring 2019
 * Course:     CS400
 * 
 * Due Date:   P3b - due 3/28 at 10pm
 * Version:    1.0
 * 
 * Credits:    none
 * 
 * Bugs:       TODO: add any known bugs, or unsolved problems here
 */

// Used as the data structure to test our hash table against
import java.util.TreeMap;

public class MyProfiler<K extends Comparable<K>, V> {

    HashTableADT<K, V> hashtable;
    TreeMap<K, V> treemap;
    
    public MyProfiler() {
        // TODO: complete the Profile constructor
        // Instantiate your HashTable and Java's TreeMap
    	hashtable = new HashTable<K, V>();
    	treemap = new TreeMap<K, V>();
    }
    
    public void insert(K key, V value) throws IllegalNullKeyException, DuplicateKeyException {
        // TODO: complete insert method
        // Insert K, V into both data structures
    	hashtable.insert(key, value);
    	treemap.put(key,value);
    	
    }
    
    public void retrieve(K key) throws IllegalNullKeyException, KeyNotFoundException {
        // TODO: complete the retrieve method
        // get value V for key K from data structures
		hashtable.get(key);	
    	treemap.get(key);
    }
    
    public static void main(String[] args) {
        try {
            int numElements = Integer.parseInt(args[0]);

            
            // TODO: complete the main method. 
            // Create a profile object. 
            // For example, Profile<Integer, Integer> profile = new Profile<Integer, Integer>();
            MyProfiler<Integer, Integer> profile = new MyProfiler<Integer, Integer>();
            
            // execute the insert method of profile as many times as numElements
            for(int i=0;i<numElements;i++) {
            	profile.insert(i, i);
            }
            // execute the retrieve method of profile as many times as numElements
            for(int i=0;i<numElements;i++) {
            	profile.retrieve(i);
            }
            // See, ProfileSample.java for example.
            
        
            String msg = String.format("Inserted and retreived %d (key,value) pairs", numElements);
            System.out.println(msg);
        }
        catch (Exception e) {
        	e.printStackTrace();
            System.out.println("Usage: java MyProfiler <number_of_elements>");
            System.exit(1);
        }
    }
}
