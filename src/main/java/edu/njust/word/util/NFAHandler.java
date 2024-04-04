package edu.njust.word.util;


import edu.njust.word.domain.NFA;
import edu.njust.word.domain.Grammar;
import edu.njust.word.domain.NFAState;
import edu.njust.word.domain.Rule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 将文法转为 NFA
 */
public class NFAHandler {
    /**
     * 根据文法生成所有类型对应的 NFA
     * @param grammar 文法规则
     * @return
     */
    public static Map<String, NFA> getNFA(Grammar grammar) {
        HashMap<String, NFA> map = new HashMap<>();
        // 1. 获取所有文法规则
        Map<String, Set<Rule>> rules = grammar.getRules();

        // 2. 状态转换
        for (String type : rules.keySet()) {
            // 放入集合
            NFA nfa = new NFA();
            map.put(type, nfa);

            // 3. 填充规则
            for (Rule rule : rules.get(type)) {
                // 3.1 获取规则 左右 代表的状态
                NFAState from = addStates(rule, nfa);
                if (rule.getLeft().equals(type)) {
                    nfa.setStartState(from);
                }
            }
        }
        return map;
    }

    /**
     * @param rule 文法规则
     * @param nfa  所属 NFA
     * @return 初态
     */
    private static NFAState addStates(Rule rule, NFA nfa) {
        Set<NFAState> states = nfa.getStates();
        // 1. 获取左部
        NFAState left = nfa.getStateWithSign(rule.getLeft());
        if (left == null) {
            left = new NFAState(states.size());
            // 添加至状态集合
            nfa.addState(left);
            // 关联状态
            nfa.linkStateWithSign(left, rule.getLeft());
        }

        // 2. 获取右部
        NFAState to = nfa.getStateWithSign(rule.getNext());
        if (to == null || rule.getNext() == null) {
            if (rule.getNext() != null) {
                to = new NFAState(states.size());
                // 添加至状态集合
                nfa.addState(to);
                // 关联状态
                nfa.linkStateWithSign(to, rule.getNext());
            } else {
                String toKey = rule.getSymbol();
                // 该终结符已被添加
                to = nfa.getStateWithSign(toKey);
                if (to == null) {
                    to = new NFAState(states.size());
                    // 添加至状态集合
                    nfa.addState(to);
                    // 关联状态
                    nfa.linkStateWithSign(to, toKey);
                    nfa.addAcceptState(to);
                }
            }
        }

        if (rule.getSymbol() != null) {
            nfa.addTransition(left, rule.getSymbol(), to);
        } else {
            nfa.addEpsilonTransition(left, to);
        }

        return left;
    }


    public static void printNFA(NFA nfa) {
        System.out.println("States:");
        for (NFAState state : nfa.getStates()) {
            System.out.print("State " + state.getStateNum() + ": ");
            if (state == nfa.getStartState()) {
                System.out.print("(Start) ");
            }
            if (nfa.getAcceptStates().contains(state)) {
                System.out.print("(Accept) ");
            }
            System.out.println();
        }

        System.out.println("\nTransitions:");
        for (NFAState state : nfa.getStates()) {
            System.out.println("State " + state.getStateNum() + ":");
            for (String symbol : state.getTransitions().keySet()) {
                System.out.print("  \"" + symbol + "\" -> ");
                for (NFAState nextState : state.getTransitions().get(symbol)) {
                    System.out.print(nextState.getStateNum() + " ");
                }
                System.out.println();
            }
            if (!state.getEpsilonClosures().isEmpty()) {
                System.out.print("  \"ε\" -> ");
                for (NFAState nextState : state.getEpsilonClosures()) {
                    System.out.print(nextState.getStateNum() + " ");
                }
                System.out.println();
            }
        }
    }

}
