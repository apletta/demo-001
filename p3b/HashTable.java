//////////////////// ALL ASSIGNMENTS INCLUDE THIS SECTION /////////////////////
//
// Title:           P3
// Files:           HashTable.java, HashTableTest.java
// Due: 			3/14/2019
//
// Author:          Alex Pletta
// Email:          	apletta@wisc.edu
// Lecturer's Name: Deb Deppeler
///////////////////////////// CREDIT OUTSIDE HELP /////////////////////////////
//
// Students who get help from sources other than their partner must fully 
// acknowledge and credit those sources of help here.  Instructors and TAs do 
// not need to be credited here, but tutors, friends, relatives, room mates, 
// strangers, and others do.  If you received no outside help from either type
//  of source, then please explicitly indicate NONE.
//
// Persons:         none
// Online Sources:  Piazza, StackOverflow.com
/////////////////////////////// 80 COLUMNS WIDE ///////////////////////////////



// TODO: comment and complete your HashTableADT implementation
// DO ADD UNIMPLEMENTED PUBLIC METHODS FROM HashTableADT and DataStructureADT TO YOUR CLASS
// DO IMPLEMENT THE PUBLIC CONSTRUCTORS STARTED
// DO NOT ADD OTHER PUBLIC MEMBERS (fields or methods) TO YOUR CLASS
//
// TODO: implement all required methods
//
// TODO: describe the collision resolution scheme you have chosen
// identify your scheme as open addressing or bucket
// --> I will be using open addressing with linear probing for collision handling.
//
// TODO: explain your hashing algorithm here 
// --> I will be taking the Java hashCode() of the key and then taking the modulus of it using the 
//     capacity (table size) to get my initial index for the key. If an item already exists at that
//     index I will be using wrap-around linear probing to find the next null or removed space.
//
// NOTE: you are not required to design your own algorithm for hashing,
//       since you do not know the type for K,
//       you must use the hashCode provided by the <K key> object
//       and one of the techniques presented in lecture

/**
 * @class HashTable defines hash table data structure
 * @field capacity = total number of items hash table can hold
 * @field loadFactorThreshold = max load factor allowed for table before resizing
 * @field numKeys = number of keys in hash table
 * @field table = array of stored hash nodes 
 *  
 */
public class HashTable<K extends Comparable<K>, V> implements HashTableADT<K, V> {

	// TODO: ADD and comment DATA FIELD MEMBERS needed for your implementation
	int capacity;
	double loadFactorThreshold;
	int numKeys;
	HashNode[] table;

	// TODO: comment and complete a default no-arg constructor
	/**
	 * Default construction of hash table with no specified capacity, or load factor.
	 * @field capacity = number of HashNode objects hash table can hold
	 * @field loadFactorThreshold = max load factor threshold that when reached or surpassed causes table resizing
	 * @field table = array to store HashNode objects
	 */
	public HashTable() {
		this.capacity = 100;
		this.loadFactorThreshold = 0.7;
		this.table = new HashNode[this.capacity];
	}

	// TODO: comment and complete a constructor that accepts
	// initial capacity and load factor threshold
	// threshold is the load factor that causes a resize and rehash
	/**
	 * Constructor that allows for specified initial capacity and load factor threshold.
	 * If the table is resized, the capacity will change but the load factor threshold will not.
	 * @param initialCapacity = capacity to set for hash table
	 * @param loadFactorThreshold = load factor threshold to set for table
	 */
	public HashTable(int initialCapacity, double loadFactorThreshold) {
		this.capacity = initialCapacity;
		this.loadFactorThreshold = loadFactorThreshold;
		this.table = new HashNode[this.capacity];
	}

	// TODO: add all unimplemented methods so that the class can compile

	// Operations
	/**
	 * Inserts a given key and value into the hash table. 
	 * Resizes table if incoming insert will cause load factor to surpass or equal load factor threshold.
	 * Rehashes new table so previous entries are not lost
	 * Initial index based on Java hashCode() of key, then searches table for first null or sentinel location 
	 * @param key = Comparable object of type K that will be used as a lookup reference
	 * @param value = Object of type V that will be the data stored in a HashNode at the appropriate key location
	 */
	@Override
	public void insert(K key, V value) throws IllegalNullKeyException, DuplicateKeyException {

		// make sure key not null
		if (key == null) {
			throw new IllegalNullKeyException();
		}

		// check if key already in table, throw duplicate exception if so
		if (contains(key)) {
			throw new DuplicateKeyException();
		}

		this.numKeys++;

		// check load factor, resize table if necessary
		if (getLoadFactor() >= this.loadFactorThreshold) { // if over or at load factor, resize table
			table = rehashTable(table); // resize table
			this.numKeys++;
		}

		// insert node
		HashNode node = new HashNode(key, value); // node to insert
		
		int index = hashCode((K) node.key); // index to start insert at
		if (table[index] == null || table[index].isRemoved) { // if index spot available, insert
			table[index] = node;
		} else {// if index not available, use open addressing to find next available index
			int i = index;
			while (table[i % this.capacity] != null && !table[i % this.capacity].isRemoved) {
				i++;
			}
			table[i % this.capacity] = node; // insert node
		}
	}

	/**
	 * Helper method to resize table when load factor threshold is reached and/or surpassed.
	 * @param oldTable = current table that needs to be resized and have variables passed on from.
	 * @return table = updated table with newly rehashed items.
	 * @throws IllegalNullKeyException = needed in case of error with insert for rehashing, theoretically should never get thrown.
	 * @throws DuplicateKeyException = needed in case of error with insert for rehashing, theoretically should never get thrown.
	 */
	public HashNode[] rehashTable(HashNode[] oldTable) throws IllegalNullKeyException, DuplicateKeyException {
		this.table = new HashNode[this.capacity * 2 + 1]; // resize table
		this.capacity = table.length;
		this.numKeys = 0;
		for (int i = 0; i < oldTable.length; i++) { // for all previous nodes
			if (oldTable[i] != null) {
				insert((K) oldTable[i].key, (V) oldTable[i].value); // rehash and insert to new table
				if(oldTable[i].isRemoved) {
					remove((K) oldTable[i].key);
				}
			}
		}
		return table;
	}

	/**
	 * Searches for existing key in hash table and sets the associated HashNode to removed to preserve it as a sentinel value.
	 * @param key = key to remove from hash table
	 * @return boolean = true for successful remove, false for key not existing or key already being removed
	 * @throws IllegalNullKeyException = thrown for attempt to pass in a null key
	 */
	@Override
	public boolean remove(K key) throws IllegalNullKeyException {
		// check if key is null
		if (key == null) {
			throw new IllegalNullKeyException();
		}

		// check that key exists
		if (!contains(key)) {
			return false;
		}

		int index = hashCode(key); // index to start search at
	
		if (table[index].key.equals(key)) { // if indexed spot matches, delete key by setting isRemoved to true
			if (table[index].isRemoved == false) { // if key is not yet removed
				table[index].isRemoved = true;
				numKeys--;
				return true;
			}
		}

		// if index not available, use open addressing to find next available index
		int i = index;
		while (!table[i].key.equals(key)) { // iterate until key found
			i++;
		}
		if (table[index].isRemoved == false) { // if key is not yet removed
			table[i].isRemoved = true; // remove node
			numKeys--;
			return true;
		}
		return false;
	}

	/**
	 * Searches hash table for a specified, existing and non-null key and returns the data value stored in that HashNode
	 * @param key = key to be searched for
	 * @return value = value associated with HashNode that has a given key
	 * @throws IllegalNullKeyException = thrown for attempt to get value from a null key
	 * @throws KeyNotFoundException = thrown for attempt to get value from a key that does not exist in hash table
	 */
	@Override
	public V get(K key) throws IllegalNullKeyException, KeyNotFoundException {
		// check key not null
		if (key == null) {
			throw new IllegalNullKeyException();
		}

		// check key exists
		if (!contains(key)) {
			throw new KeyNotFoundException();
		}

		// get key
		int i = hashCode(key); // index to start insert at

		while (!table[i % this.capacity].key.equals(key)) { // iterate until key found
			i++;
		}
		V value = (V) table[i % this.capacity].value;
		return value;

	}

	// Accessor methods //
	
	/**
	 * Helper method to give out load factor threshold of the hash table.
	 * This stays the same after the hash table is initialized.
	 * @return --> the current load factor threshold
	 */
	public double getLoadFactorThreshold() {
		return this.loadFactorThreshold;
	}

	/**
	 * Helper method to give out load factor of the hash table.
	 * This can change with inserting and deleting items.
	 * @return --> the current load factor 
	 */
	public double getLoadFactor() {
		return (double) numKeys / (double) this.capacity;
	}

	/**
	 * Helper method to give out current capacity of the hash table. 
	 * This can change with resizing.
	 * @return --> the current capacity of the hash table
	 */
	public int getCapacity() {
		return this.capacity;
	}
	
	/**
	 * Helper method to give out collision resolution scheme of the hash table. 
	 * This stays the same for the hash table
	 * @return --> the code for the type of collision resolution
	 */
	public int getCollisionResolution() {
		return 1; // 1 OPEN ADDRESSING: linear probe
	}
	
	/**
	 * Helper method to send out number of keys in hash table.
	 * The number of keys changes with inserting and deleting HashNodes, but not with resizing.
	 * @return = number of keys in hash table. 
	 */
	@Override
	public int numKeys() {
		// TODO Auto-generated method stub
		return this.numKeys;
	}

	// Helper methods //
	
	
	/**
	 * Method for finding Java hashCode() of a given key.
	 * @param key = key to find the hashCode() for
	 * @return code = Java hashCode() for given key, used for initial index in hash table
	 */
	public int hashCode(K key) {
		int hashCode = Math.abs(key.hashCode()); // use absolute value to prevent indexing error
		int code = hashCode % this.capacity;
		return code;
	}

	/**
	 * Searches hash table to see if a specified key exists and is not removed(aka a sentinel value)
	 * @param key = key to search for
	 * @return boolean = true for key existing in and not removed from hash table, false otherwise
	 */
	public boolean contains(K key) {

		int index = hashCode(key); // index to start insert at

		if (table[index] == null || table[index].isRemoved) { // if index spot available, return false because item not
																// there
			return false;
		}

		// if index not available, use open addressing to search table for item
		int i = index;

		while (i < this.capacity * 2) { // loop through twice the size of table to make sure full coverage
			if (table[i % this.capacity] == null || table[i % this.capacity].isRemoved) {
				return false;
			} else if (table[i % this.capacity].key.equals(key)) {
				return true;
			}
			i++;
		}
		return false;
	}

	/**
	 * Prints out table in a single line. 
	 * Used to visualize entries in table for debugging.
	 */
	public void printTable() {
		for (int i = 0; i < this.capacity; i++) {
			if (table[i] == null) {
				System.out.print("[], ");
			} else {
				System.out.print(table[i].key + ", ");
			}
		}
		System.out.println("");
	}

	/**
	 * @class HashNode defines nodes to be put in hash table
	 * @field key = key associated with HashNode, used for indexing HashNode in hash table
	 * @field value = data value stored in HashNode
	 * @field isRemoved = flag to indicate if HashNode is removed or not, true if now a sentinel node, false otherwise
	 * @type <K> = Generic comparable object type for key field
	 * @type <V> = Generic object type for value field
	 */
	public class HashNode<K extends Comparable<K>, V> {
		K key;
		V value;
		boolean isRemoved; // = false when node exists in table, = true when node is removed. Needed for
							// open addressing with linear probing to work.

		/**
		 * Constructor for HashNode that allows for setting key and value fields
		 * @param key = key to be associated with HashNode
		 * @param value = value to be stored in HashNode
		 * @param isRemoved = flag to indicate if HashNode is removed or not, true if now a sentinel node, false otherwise
		 */
		public HashNode(K key, V value) {
			this.key = key;
			this.value = value;
			this.isRemoved = false;
		}
	}

}
