package edu.njust.word;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

class NFAState {

    /**
     * 状态标识 0、1、2...
     */
    int stateNum;

    /**
     * 空输入可达状态
     */
    HashSet<NFAState> epsilonClosures;

    /**
     * 输入 char 可达状态
     */
    HashMap<Character, HashSet<NFAState>> transitions;

    NFAState(int stateNum) {
        this.stateNum = stateNum;
        epsilonClosures = new HashSet<>();
        transitions = new HashMap<>();
    }
}

class NFA {

    Map<Character, NFAState> stateUnm;

    /**
     * 状态集合
     */
    Set<NFAState> states;

    /**
     * 起始状态
     */
    NFAState startState;

    /**
     * 结束状态，验证成功
     */
    Set<NFAState> acceptStates;

    String type;

    NFA() {
        states = new LinkedHashSet<>();
        stateUnm = new HashMap<>();
        acceptStates = new HashSet<>();
    }

    void addState(NFAState state) {
        states.add(state);
    }

    void setStartState(NFAState state, String type) {
        startState = state;
        this.type = type;
    }

    void addAcceptState(NFAState state) {
        acceptStates.add(state);
    }

    /**
     * 添加过渡状态
     * @param from 起始状态
     * @param symbol 接收字符
     * @param to 到达状态
     */
    void addTransition(NFAState from, char symbol, NFAState to) {
        if (!from.transitions.containsKey(symbol))
            from.transitions.put(symbol, new HashSet<>());
        from.transitions.get(symbol).add(to);
    }

    void addEpsilonTransition(NFAState from, NFAState to) {
        if (from != null) {
            from.epsilonClosures.add(to);
        }
    }

    NFAState linkStateWithSign(NFAState state, Character sign) {
        this.stateUnm.put(sign, state);
        return state;
    }

    NFAState getStateWithSign(Character sign) {
        return stateUnm.get(sign);
    }
}

public class RegexToNFA {

    private final BufferedReader reader;

    private final NFA nfa;

    public RegexToNFA(String grammarPath) throws FileNotFoundException {
        URL url = this.getClass().getClassLoader().getResource(grammarPath);

        assert url != null;
        this.reader = new BufferedReader(new FileReader(url.getFile()));
        nfa = new NFA();
    }

    private void regexToNFA() throws IOException {

        // 1. 获取起始状态
        String rule;
        if ((rule = reader.readLine()) != null) {
            String[] res = getConAndRes(rule);

            // nfa 类型
            NFAState start = new NFAState(nfa.states.size());
            nfa.setStartState(start, res[0]);
            nfa.addState(start);

            char symbol = res[1].charAt(0);
            NFAState state = getNFAState(res[1].charAt(res[1].length() - 1));
            nfa.addState(state);
            nfa.addTransition(start, symbol, state);
        }

        while ((rule = reader.readLine()) != null) {
            String[] res = getConAndRes(rule);

            NFAState[] states = getStatesWithRule(res);
            NFAState from = states[0];
            NFAState to = states[1];
            char symbol = res[1].charAt(0);
            nfa.addTransition(from, symbol, to);

            // 设置结束状态
            if (res[1].length() == 1) {
                nfa.addAcceptState(to);
            }
        }
    }

    NFAState[] getStatesWithRule(String[] rule) {
        NFAState[] states = new NFAState[2];
        NFAState from;
        NFAState to;
        if (rule[0].length() > 1) {
            from = this.nfa.startState;
        } else {
            char sign = rule[0].charAt(0);
            from = this.nfa.getStateWithSign(sign);
            if (!this.nfa.states.contains(from)) {
                from = getNFAState(sign);
            }
        }

        char sign = rule[1].charAt(rule[1].length() - 1);
        to = this.nfa.getStateWithSign(sign);
        if (!this.nfa.states.contains(to)) {
            to = getNFAState(sign);
        }

        states[0] = from;
        states[1] = to;

        return states;
    }

    /**
     * 根据文法 获取 条件和结果
     * @param rule A -> B
     * @return 条件 A 、结果 B
     */
    private String[] getConAndRes(String rule) {
        // 根据 -> 划分字符串
        return rule.split(" -> ");
    }

    /**
     *
     * @param sign 状态标识（非终结符符号）
     * @return 状态节点
     */
    private NFAState getNFAState(Character sign) {
        NFAState state = new NFAState(this.nfa.states.size());
        // 添加到状态列表
        this.nfa.states.add(state);
        return nfa.linkStateWithSign(state, sign);
    }

    public void printNFA() throws IOException {
        regexToNFA();
        System.out.println("States:");
        for (NFAState state : nfa.states) {
            System.out.print("State " + state.stateNum + ": ");
            if (state == nfa.startState) {
                System.out.print("(Start) ");
            }
            if (nfa.acceptStates.contains(state)) {
                System.out.print("(Accept) ");
            }
            System.out.println();
        }

        System.out.println("\nTransitions:");
        for (NFAState state : nfa.states) {
            System.out.println("State " + state.stateNum + ":");
            for (Character symbol : state.transitions.keySet()) {
                System.out.print("  '" + symbol + "' -> ");
                for (NFAState nextState : state.transitions.get(symbol)) {
                    System.out.print(nextState.stateNum + " ");
                }
                System.out.println();
            }
            if (!state.epsilonClosures.isEmpty()) {
                System.out.print("  epsilon -> ");
                for (NFAState nextState : state.epsilonClosures) {
                    System.out.print(nextState.stateNum + " ");
                }
                System.out.println();
            }
        }
    }

}