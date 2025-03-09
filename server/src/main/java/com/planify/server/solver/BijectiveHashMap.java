package com.planify.server.solver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Class representing a BijectiveHashMap i.e. a one-to-one relation between objects of type Key and objects of type Value.
 * @author Nathan RABIER
 *
 * @param <Key> The first Type.
 * @param <Value> The second Type.
 */
public class BijectiveHashMap<Key, Value> {
	private HashMap<Key, Value> keyToValue;
	private HashMap<Value, Key> valueToKey;
	
	public BijectiveHashMap(){
		this.keyToValue = new HashMap<Key, Value>();
		this.valueToKey = new HashMap<Value, Key>();
	}
	
	/**
	 * Returns true if this map contains a mapping for the specified key.
	 * @param key The key whose presence in this map is to be tested
	 * @return true if this map contains a mapping for the specified key.
	 */
	public boolean containKey(Key key) {
		return this.keyToValue.containsKey(key);
	}
	
	/**
	 * Returns true if this map contains a mapping for the specified value.
	 * @param value The value whose presence in this map is to be tested
	 * @return true if this map contains a mapping for the specified value.
	 */
	public boolean containValue(Value value) {
		return this.valueToKey.containsKey(value);
	}
	
	/**
	 * Get the value if exist, and null otherwise.
	 * @param key The key.
	 * @return The value mapped, or null if no value is mapped to the key.
	 */
	public Value getValue(Key key) {
		return keyToValue.get(key);
	}
	
	/**
	 * Get the key if exist, and null otherwise.
	 * @param value The value.
	 * @return The key mapped, or null if no key is mapped to the value.
	 */
	public Key getKey(Value value) {
		return valueToKey.get(value);
	}
	
	/**
	 * Delete the mapping for the specified key from this map if present.
	 * @param key The key.
	 * @return true if there was a mapping with this key, false if no mapping where present.
	 */
	public boolean deleteKey(Key key) {
		if (!this.containKey(key)) return false;
		this.valueToKey.remove(this.keyToValue.remove(key));
		return true;
	}
	
	/**
	 * Delete the mapping for the specified value from this map if present.
	 * @param value The value.
	 * @return true if there was a mapping with this value, false if no mapping where present.
	 */
	public boolean deleteValue(Value value) {
		if (!this.containValue(value)) return false;
		this.keyToValue.remove(this.valueToKey.remove(value));
		return true;
	}
	
	/**
	 * Associates the specified value with the specified key in this map.
	 * If the map previously contained a mapping for the key and or the value, the oldvalue are replaced.
	 * @param key The key.
	 * @param value The value.
	 */
	public void put(Key key, Value value) {
		if (this.containKey(key)) this.deleteKey(key);
		if (this.containValue(value)) this.deleteValue(value);
		this.keyToValue.put(key, value);
		this.valueToKey.put(value, key);
	}
	
	/**
	 * Returns a Set view of the keys contained in this map.
	 * @return a Set view of the keys contained in this map
	 */
	public Set<Key> keySet() {
		return new HashSet<Key>(keyToValue.keySet());
	}
	
	/**
	 * Returns a Set view of the values contained in this map.
	 * @return a Set view of the values contained in this map
	 */
	public Set<Value> valueSet(){
		return new HashSet<Value>(valueToKey.keySet());
	}
}
