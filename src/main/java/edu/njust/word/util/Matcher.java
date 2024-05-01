package edu.njust.word.util;

import edu.njust.common.TokenType;
import edu.njust.word.domain.dfa.DFA;
import edu.njust.word.domain.dfa.DFAState;
import edu.njust.word.domain.token.TokenInfo;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * DFA --> Token
 */
public class Matcher {

    private final List<String> fileData;

    public Matcher(String filePath) throws IOException {
        URL url = this.getClass().getClassLoader().getResource(filePath);

        assert url != null;
        BufferedReader reader = new BufferedReader(new FileReader(url.getFile()));
        fileData = new ArrayList<>();

        String content;
        while ((content = reader.readLine()) != null) {
            fileData.add(content.trim());
        }
        reader.close();
    }

    public List<TokenInfo> defaultMatch(String type, DFA dfa) {
        List<TokenInfo> infos = new ArrayList<>();
        // 1. 记录行号
        int len = 1;

        for (String content : fileData) {
            DFAState curState = dfa.getStartState();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < content.length(); i++) {
                // 1. 获取转移字符
                String symbol = String.valueOf(content.charAt(i));
                if (curState == null) {
                    // TODO: 2024/4/9 开始字符不匹配，接下来的所有字符也不应该进行匹配
                    curState = dfa.getStartState();
                }

                DFAState nextState = dfa.getTransitions().get(curState).get(symbol);
                if (nextState == null) {

                    // 当前字符未能匹配
                    int space = content.indexOf(' ', i);
                    // 存在下一个匹配项
                    if (space != -1) {
                        i = space;
                    } else {
                        break;
                    }
                    if (builder.length() > 0 && curState.isAccept()) {
                        infos.add(new TokenInfo(len, type, builder.toString()));
                        builder = new StringBuilder();
                    }

                } else {
                    builder.append(symbol);
                }

                curState = nextState;

            }

            if (curState != null && curState.isAccept() && builder.length() > 0) {
                infos.add(new TokenInfo(len, type, builder.toString()));
            }
            ++len;
        }

        return infos;
    }

    /**
     * @param dfa
     * @return
     */
    public List<TokenInfo> operatorMatch(DFA dfa) {
        List<TokenInfo> infos = new ArrayList<>();
        // 1. 记录行号
        int len = 1;

        for (String content : fileData) {
            DFAState curState = dfa.getStartState();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < content.length(); i++) {
                // 1. 获取转移字符
                String symbol = String.valueOf(content.charAt(i));
                if (curState == null) {
                    curState = dfa.getStartState();
                }

                if (!dfa.getTransitions().get(curState).containsKey(symbol)) {
                    if (builder.length() > 0) {
                        infos.add(new TokenInfo(len, TokenType.OPERATOR, builder.toString()));
                        builder = new StringBuilder();
                    }
                } else {
                    builder.append(symbol);
                }

                curState = dfa.getTransitions().get(curState).get(symbol);

            }

            if (curState != null && curState.isAccept() && builder.length() > 0) {
                infos.add(new TokenInfo(len, TokenType.OPERATOR, builder.toString()));
            }
            ++len;
        }

        return infos;
    }

    /**
     * 使用所有DFA，匹配文本
     *
     * @param dfa dfa
     * @return
     */
    public List<TokenInfo> match(String type, DFA dfa) {
        List<TokenInfo> infos = new ArrayList<>();
        switch (type) {
            case TokenType.DELIMITER:
                infos.addAll(delimitersMatch(dfa));
                break;
            case TokenType.KEY_WORD:
            case TokenType.TYPE:
                infos.addAll(keywordMatch(dfa, type));
                break;
            case TokenType.CONSTANT:
                infos.addAll(constantMatch(dfa));
                break;
            case TokenType.OPERATOR:
                infos.addAll(operatorMatch(dfa));
                break;
            default:
                infos.addAll(defaultMatch(type, dfa));
                break;
        }
        return infos;
    }

    /**
     * 匹配分隔符
     * @param dfa
     * @return
     */
    private List<TokenInfo> delimitersMatch(DFA dfa) {
        List<TokenInfo> infos = new ArrayList<>();
        // 1. 记录行号
        int len = 1;
        for (String content : fileData) {
            DFAState curState = dfa.getStartState();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < content.length(); i++) {
                // 1. 获取转移字符
                String symbol = String.valueOf(content.charAt(i));

                if (curState == null) {
                    curState = dfa.getStartState();
                }

                DFAState nextState = dfa.getTransitions().get(curState).get(symbol);
                // 当前字符未被包含
                if (nextState == null) {
                    // 存放结果
                    if (builder.length() > 0) {
                        infos.add(new TokenInfo(len, TokenType.DELIMITER, builder.toString()));
                        builder = new StringBuilder();
                    }

                    // 起始状态是否包含当前字符
                    if (dfa.getTransitions().get(dfa.getStartState()).containsKey(symbol)) {
                        builder.append(symbol);
                    }
                } else {
                    // 当前字符可被转移
                    builder.append(symbol);
                }
                curState = nextState;
            }
            // 判断最后一个字符的情况
            if (curState != null && curState.isAccept() && builder.length() > 0) {
                infos.add(new TokenInfo(len, TokenType.DELIMITER, builder.toString()));
            }
            ++len;
        }
        return infos;
    }

    /**
     * 关键字匹配
     *
     * @param dfa
     * @return
     */
    private List<TokenInfo> keywordMatch(DFA dfa, String type) {
        List<TokenInfo> infos = new ArrayList<>();
        // 1. 记录行号
        int len = 1;
        for (String content : fileData) {
            DFAState curState = dfa.getStartState();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < content.length(); i++) {
                // 1. 获取转移字符
                String symbol = String.valueOf(content.charAt(i));

                if (curState == null) {
                    curState = dfa.getStartState();
                    builder = new StringBuilder();
                }

                DFAState nextState = dfa.getTransitions().get(curState).get(symbol);
                // 当前字符未被包含
                if (nextState == null) {
                    // 存放结果
                    if (builder.length() > 0 && curState.isAccept()) {
                        infos.add(new TokenInfo(len, type, builder.toString()));
                        builder = new StringBuilder();
                    }
                } else {
                    // 当前字符可被转移
                    builder.append(symbol);
                }
                curState = nextState;
            }
            // 判断最后一个字符的情况
            if (curState != null && curState.isAccept() && builder.length() > 0) {
                infos.add(new TokenInfo(len, type, builder.toString()));
            }
            ++len;
        }
        return infos;
    }


    private List<TokenInfo> constantMatch(DFA dfa) {
        List<TokenInfo> infos = new ArrayList<>();
        // 1. 记录行号
        int len = 1;

        for (String content : fileData) {
            DFAState curState = dfa.getStartState();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < content.length(); i++) {
                // 1. 获取转移字符
                String symbol = String.valueOf(content.charAt(i));
                if (curState == null) {
                    curState = dfa.getStartState();
                }

                DFAState nextState = dfa.getTransitions().get(curState).get(symbol);
                if (nextState == null) {
                    int temp = i;
                    // 当前字符未能匹配
                    int space = content.indexOf(' ', i);
                    // 存在下一个匹配项
                    if (space != -1) {
                        i = space;
                    } else {
                        break;
                    }

                    if (builder.length() > 0) {
                        if (content.charAt(temp + 1) == ' ') {
                            infos.add(new TokenInfo(len, TokenType.CONSTANT, builder.toString()));
                        }
                        builder = new StringBuilder();
                    }
                } else {
                    builder.append(symbol);
                }

                curState = nextState;

            }
            if (curState != null && curState.isAccept() && builder.length() > 0) {
                infos.add(new TokenInfo(len, TokenType.CONSTANT, builder.toString()));
            }
            ++len;
        }

        return infos;
    }


    /**
     * Warning: 写入的文件在target目录下
     *
     * @param outputFile
     * @param infos
     * @throws IOException
     */
    public void writeToFile(String outputFile, List<TokenInfo> infos) throws IOException {
        URL url = this.getClass().getClassLoader().getResource(outputFile);

        assert url != null;
        BufferedWriter writer = new BufferedWriter(new FileWriter(url.getPath()));

        for (TokenInfo tokenInfo : infos) {
            writer.write(tokenInfo.getInfo());
            writer.newLine();
        }
        writer.flush();
        writer.close();

    }

    public void writeToFile(String outputFile, Map<String, List<TokenInfo>> infos) throws IOException {
        URL url = this.getClass().getClassLoader().getResource(outputFile);

        assert url != null;
        BufferedWriter writer = new BufferedWriter(new FileWriter(url.getPath()));

        for (String type : infos.keySet()) {
            infos.get(type).forEach(info -> {
                try {
                    writer.write(info.getInfo());
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        writer.write("-----------");
        writer.newLine();
        for (String type : infos.keySet()) {
            // 对应符号集合
            Set<String> sets = new HashSet<>();
            infos.get(type).forEach(info -> sets.add(info.getContent()));
            writer.write(type + ": " + sets);
            writer.newLine();
        }

        writer.flush();
        writer.close();

    }
}
