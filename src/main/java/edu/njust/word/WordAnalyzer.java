package edu.njust.word;

import edu.njust.word.domain.Grammar;
import edu.njust.word.domain.NFA;
import edu.njust.word.util.NFAHandler;

import java.io.IOException;
import java.util.Map;

public class WordAnalyzer {

    public static void main(String[] args) throws IOException {
        Grammar grammar = new Grammar("word/gra.txt");
        Map<String, NFA> nfa = NFAHandler.getNFA(grammar);

        for (String key : nfa.keySet()) {
            System.out.print(key + ": ");
            NFAHandler.printNFA(nfa.get(key));

            System.out.println("\n");
        }

    }

}
