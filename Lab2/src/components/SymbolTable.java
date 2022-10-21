package components;

import utils.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class SymbolTable {

    // Wrapper class for the buckets of the hash table

    private static class Bucket{

        // Collision is solved through separate chaining, so each bucket contains
        // a linked list of elements whose keys hash to the same value
        private final List<Pair<String, Integer>> chain;

        public Bucket(){
            this.chain = new LinkedList<>();
        }

        public boolean contains(String key){
            return chain.stream().anyMatch(pair -> pair.getKey().equals(key));
        }

        public boolean append(String key, int pos){
            return chain.add(new Pair<>(key, pos));
        }

        public boolean remove(String key){
            return chain.removeIf(pair->pair.getKey().equals(key));
        }

        public int position(String key){
            Pair<String, Integer> symbol = chain.stream()
                    .filter(pair -> pair.getKey().equals(key))
                    .findFirst().orElse(null);
            return symbol != null ? symbol.getValue() : -1;
        }

        public String toString(){
            return "[" + chain.stream().map(Pair::toString).collect(Collectors.joining(", ")) + "]";
        }

        public  boolean isEmpty(){
            return chain.isEmpty();
        }

    }

    private List<Bucket> table;

    private int size;

    private int position;


    /**
     * Creates a new symbol table with the given capacity
     * @param capacity start capacity of the table
     */
    public SymbolTable(int capacity){
        this.table = new ArrayList<>();
        for(int i = 0; i < capacity; i++){
            this.table.add(new Bucket());
        }
        this.size = 0;
        this.position = 0;
    }

    private int hash(String key){
        int pow = key.length() - 1;
        int hashKey = 0, asciiCode, j;
        for(int i = 0; i < key.length(); i++){
            asciiCode = key.charAt(i);
            for(j = 0; j < pow; j++){
                asciiCode *= 26;
            }
            hashKey += asciiCode;
            pow--;
        }
        return hashKey % this.table.size();
    }

    /** Adds a new entry with the given key to the symbol table
     * @param key symbol
     * @return true if the symbol was successfully added to the table, false if it was already contained
     */
    public boolean add(String key){
        int pos = hash(key);
        if(!table.get(pos).contains(key)){
            // The position of the newly added element is the order of its insertion in the table
            table.get(pos).append(key, position++);
            size++;
            return true;
        }
        return false;
    }

    /**
     * Removes the symbol with the given key from the symbol table
     * @param key symbol
     * @return true if the symbol was successfully removed, false otherwise (e.g. it does not exist in the table)
     */
    public boolean remove(String key){
        int pos = hash(key);
        if(table.get(pos).remove(key)){
            size--;
            return true;
        }
        return false;
    }

    /**
     * Retrieves the position of the symbol with the given key from the symbol table
     * @param key symbol
     * @return a valid position if the key corresponds to a valid entry, -1 otherwise
     */
    public int position(String key){
        int pos = hash(key);
        return table.get(pos).position(key);
    }

    /**
     * Verifies whether a given key corresponds to a valid entry in the symbol table
     * @param key symbol
     * @return true if a corresponding entry is found, false otherwise
     */
    public boolean contains(String key){
        int pos = hash(key);
        return table.get(pos).contains(key);
    }

    /**
     * Returns the number of entries from the symbol table
     * @return current size of the symbol table
     */
    public int size(){
        return size;
    }

    public void printTable(){
        String leftAlignFormat = "| %-15s |%n";

        System.out.format("+-----------------+%n");
        System.out.format(leftAlignFormat, "Symbol Table");
        System.out.format("+-----------------+%n");
        for (Bucket bucket : table) {
            if (!bucket.isEmpty()) {
                System.out.format(leftAlignFormat, bucket);
            }
        }
        System.out.format("+-----------------+%n");
    }

}
