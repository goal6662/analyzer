package edu.njust.word;

import edu.njust.word.domain.dfa.DFA;
import edu.njust.word.domain.grammar.Grammar;
import edu.njust.word.domain.nfa.NFA;
import edu.njust.word.domain.token.TokenInfo;
import edu.njust.word.util.DFAHandler;
import edu.njust.word.util.Matcher;
import edu.njust.word.util.NFAHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordAnalyzer {

    public static void main(String[] args) throws IOException {
        Grammar grammar = new Grammar("word/gra.txt");
        Map<String, NFA> nfa = NFAHandler.getNFA(grammar);

//        printNFA(nfa);

        Map<String, DFA> dfa = new HashMap<>();
        for (String key : nfa.keySet()) {
            System.out.println(key);
            NFA nfaT = nfa.get(key);
            DFA dfaT = DFAHandler.nfaToNFA(nfaT);
            dfa.put(key, dfaT);

            DFAHandler.printDFA(dfaT);

            System.out.println("\n -------------------------------------- \n");
        }

        Matcher matcher = new Matcher("word/lex.txt");

        List<TokenInfo> match =
                matcher.match(dfa.get("constant"));

        matcher.writeToFile("word/out10.txt", match);

        System.out.println(match);
    }

    public static void printNFA(Map<String, NFA> nfa) {
        for (String key : nfa.keySet()) {
            System.out.print(key + ": ");
            NFAHandler.printNFA(nfa.get(key));

            System.out.println("\n");
        }
    }
}
