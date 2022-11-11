package components.fa;

import java.util.Scanner;

public class FAMenu {
    private static FiniteAutomaton fa = null;

    public static void printMenu(){
        Scanner scanner = new Scanner(System.in);
        boolean stop = false;
        while (!stop){
            System.out.println("Enter an option:");
            System.out.println("0.Stop program");
            System.out.println("1.Read FA from file");
            System.out.println("2.Display alphabet");
            System.out.println("3.Display initial state");
            System.out.println("4.Display all states");
            System.out.println("5.Display final states");
            System.out.println("6.Display transitions");
            System.out.println("7.Check if sequence is accepted by the FA");
            String option = scanner.next().strip();
            switch (option){
                case "0":
                    stop = true;
                    break;
                case "1":
                    System.out.println("Enter filename:");
                    String filename = scanner.next().strip();
                    fa = new FiniteAutomaton(filename);
                    break;
                case "2":
                    if(fa != null){
                        fa.getAlphabet().forEach(System.out::println);
                    }
                    break;
                case "3":
                    if(fa != null){
                        System.out.println(fa.getInitialState());
                    }
                    break;
                case "4":
                    if(fa != null){
                        fa.getStates().forEach(System.out::println);
                    }
                    break;
                case "5":
                    if(fa != null){
                        fa.getFinalStates().forEach(System.out::println);
                    }
                    break;
                case "6":
                    if(fa != null){
                        fa.getTransitions().forEach(System.out::println);
                    }
                    break;
                case "7":
                    if(fa != null){
                        System.out.println("Enter sequence:");
                        String sequence = scanner.next().strip();
                        if(!fa.isDeterministic()) {
                            System.out.println("FA is not deterministic :(");
                        } else {
                            if(fa.isSequenceAccepted(sequence)){
                                System.out.println("Sequence is accepted by the FA :)");
                            } else {
                                System.out.println("Sequence is not accepted by the FA :(");
                            }
                        }
                    }
                    break;
                default:
                    System.out.println("Not a valid option");
            }
        }
    }

    public static void main(String[] args) {
        printMenu();
    }
}
