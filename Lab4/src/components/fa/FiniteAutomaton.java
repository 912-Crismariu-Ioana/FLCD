package components.fa;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FiniteAutomaton {
    private Set<String> alphabet = new HashSet<>();
    private String initialState;
    private Set<String> states = new HashSet<>();
    private Set<String> finalStates = new HashSet<>();
    private Set<Transition> transitions = new HashSet<>();


    public FiniteAutomaton(String filename) {
        readFromFile(filename);
    }

    public Set<String> getAlphabet() {
        return alphabet;
    }

    public String getInitialState() {
        return initialState;
    }

    public Set<String> getStates() {
        return states;
    }

    public Set<String> getFinalStates() {
        return finalStates;
    }

    public Set<Transition> getTransitions() {
        return transitions;
    }

    public boolean isDeterministic(){
        return transitions.stream().noneMatch(transition -> transition.getNextStates().size() > 1);
    }

    public boolean isSequenceAccepted(String sequence){
        String currentState = initialState;
        for(int i = 0; i < sequence.length(); i++){
            String inputSymbol = sequence.substring(i, i+1);
            String current = currentState;
            Transition trans = transitions.stream()
                    .filter(transition ->
                            transition.getCurrentState().equals(current) && transition.getInputSymbol().equals(inputSymbol))
                    .findFirst().orElse(null);
            if(trans == null){
                return false;
            }
            currentState = trans.getNextStates().stream().findFirst().orElse(null);
            if(currentState == null){
                return false;
            }

        }
        return finalStates.contains(currentState);
    }

    private void readAlphabet(Scanner reader){
        String line;
        while(reader.hasNextLine()){
            line = reader.nextLine().strip();

            if(line.length() == 0){
                break;
            }
            alphabet.addAll(Arrays.asList(line.split(" ")));
        }
    }

    private void readStates(Scanner reader){
        while(reader.hasNextLine()){
            String line = reader.nextLine().strip();
            if(line.length() == 0){
                break;
            }

            String[] tokens = line.split(" ");

            if(tokens.length == 0){
                break;
            }
            states.add(tokens[0].strip());

            if(tokens.length == 2){

                if(tokens[1].equals("final")){
                    finalStates.add(tokens[0].strip());
                }
            }
        }
    }

    private void readTransitions(Scanner reader){
        String line;
        while(reader.hasNextLine()){
            line = reader.nextLine().strip();

            if(line.length() == 0){
                break;
            }
            String[] tokens = line.split(" ");
            if(tokens.length >= 3){
                Transition transition = new Transition(tokens[0], tokens[1]);
                for(int i = 2; i < tokens.length; i++){
                    transition.addNextState(tokens[i]);
                }
                transitions.add(transition);
            }

        }
    }

    private void readFromFile(String filename){
        try {
            File file = new File(filename);
            Scanner reader = new Scanner(file);
            while(reader.hasNextLine()){
                String line = reader.nextLine().strip();
                if(line.startsWith("@alphabet")){
                    readAlphabet(reader);
                }

                if(line.startsWith("@initialState")){
                    initialState = reader.nextLine().strip();
                }

                if(line.startsWith("@states")){
                    readStates(reader);
                }

                if(line.startsWith("@transitions")){
                    readTransitions(reader);
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
