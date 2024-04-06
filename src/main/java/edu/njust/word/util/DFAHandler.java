package edu.njust.word.util;

import edu.njust.word.domain.dfa.DFA;
import edu.njust.word.domain.dfa.DFAState;
import edu.njust.word.domain.nfa.NFA;
import edu.njust.word.domain.nfa.NFAState;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * NFA --> DFA
 */
public class DFAHandler {

    public static DFA nfaToNFA(NFA nfa) {
        DFA dfa = new DFA();

        // 1. 构造起始状态集
        Set<NFAState> startNFAStates = new HashSet<>();
        // 1.1 添加起始状态
        startNFAStates.add(nfa.getStartState());
        // 1.2 空状态可达状态
        startNFAStates.addAll(nfa.getStartState().getEpsilonClosures());

        // 2. DFA
        //
        DFAState startDFAState = new DFAState(startNFAStates, nfa.getAcceptStates());
        dfa.setStartState(startDFAState);
        dfa.addState(startDFAState);

        Queue<DFAState> dfaStateQueue = new LinkedList<>();
        // 添加至队列
        dfaStateQueue.offer(startDFAState);

        // 3. 求闭包
        while (!dfaStateQueue.isEmpty()) {
            // 获取并移除队首元素
            DFAState curState = dfaStateQueue.poll();

            for (String symbol : nfa.getSymbols()) {

                Set<NFAState> next = new HashSet<>();
                // 获取当前 dfaState 包含的 nfaState
                for (NFAState nfaState : curState.getNfaStates()) {
                    // 当前 symbol 可以进行转移
                    if (nfaState.getTransitions().containsKey(symbol)) {
                        HashSet<NFAState> states = nfaState.getTransitions().get(symbol);
                        next.addAll(states);
                    }
                    // 空状态
                    next.addAll(nfaState.getEpsilonClosures());
                }

                if (!next.isEmpty()) {
                    DFAState nextDFAState = new DFAState(next, nfa.getAcceptStates());
                    if (!dfa.getTransitions().containsKey(nextDFAState)) {
                        dfa.addState(nextDFAState);
                        dfaStateQueue.offer(nextDFAState);
                    }
                    dfa.addTransition(curState, symbol, nextDFAState);
                }
            }
        }

        // 4. 设置终态集
        for (DFAState state : dfa.getTransitions().keySet()) {
            if (state.isAccept()) {
                dfa.addAcceptState(state);
            }
        }

        return dfa;
    }

    public static void printDFA(DFA dfa) {
        System.out.println("DFA:");
        System.out.println("Start state: " + NFAState.printStatesInfo(dfa.getStartState().getNfaStates()));
        System.out.print("Accept state: [");
        for (DFAState dfaState : dfa.getAcceptState()) {
            System.out.print(NFAState.printStatesInfo(dfaState.getNfaStates()));
        }
        System.out.println("]");
        System.out.println("Transitions:");
        for (DFAState state : dfa.getTransitions().keySet()) {
            System.out.println("State " + NFAState.printStatesInfo(state.getNfaStates()) + ":");
            for (String symbol : dfa.getTransitions().get(state).keySet()) {
                System.out.println("  '" + symbol + "' -> " +
                        NFAState.printStatesInfo(dfa.getTransitions().get(state).get(symbol).getNfaStates()));
            }
        }
    }


}
