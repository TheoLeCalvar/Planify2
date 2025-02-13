package com.planify.server.solver;

import java.util.HashMap;
import java.util.Set;

public class BijectiveHashMap<Key, Value> {
	private HashMap<Key, Value> keyToValue;
	private HashMap<Value, Key> valueToKey;
	
	public BijectiveHashMap(){
		this.keyToValue = new HashMap<Key, Value>();
		this.valueToKey = new HashMap<Value, Key>();
	}
	
	public boolean containKey(Key key) {
		return this.keyToValue.containsKey(key);
	}
	
	public boolean containValue(Value value) {
		return this.valueToKey.containsKey(value);
	}
	
	public Value getValue(Key key) {
		return keyToValue.get(key);
	}
	
	public Key getKey(Value value) {
		return valueToKey.get(value);
	}
	
	public boolean deleteKey(Key key) {
		if (!this.containKey(key)) return false;
		this.valueToKey.remove(this.keyToValue.remove(key));
		return true;
	}
	
	public boolean deleteValue(Value value) {
		if (!this.containValue(value)) return false;
		this.keyToValue.remove(this.valueToKey.remove(value));
		return true;
	}
	
	public void put(Key key, Value value) {
		if (this.containKey(key)) this.deleteKey(key);
		if (this.containValue(value)) this.deleteValue(value);
		this.keyToValue.put(key, value);
		this.valueToKey.put(value, key);
	}
	
	public Set<Key> keySet() {
		return keyToValue.keySet();
	}
	
	public Set<Value> valueSet(){
		return valueToKey.keySet();
	}
}
