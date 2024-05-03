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

    public List<TokenInfo> match(Map<String, DFA> dfaMap) {
        List<TokenInfo> infos = new ArrayList<>();
        Map<String, List<TokenInfo>> infoMap = new HashMap<>();
        // 记录行号
        int len = 1;
        for (String content : fileData) {

            for (String type : dfaMap.keySet()) {
                List<TokenInfo> tokenInfoList = matchLen(content + " ", dfaMap.get(type));
                int curLen = len;
                tokenInfoList.forEach((info) -> {
                    info.setLen(curLen);

                    if (info.getType() == null) {
                        info.setType(type);
                    }
                });
                List<TokenInfo> cur = infoMap.getOrDefault(type, new ArrayList<>());
                cur.addAll(tokenInfoList);
                infoMap.put(type, cur);

                infos.addAll(tokenInfoList);
            }
            len += 1;
        }

        // 去重
        Set<String> kt = new HashSet<>();
        infoMap.get(TokenType.TYPE).forEach((info) -> kt.add(info.getContent()));
        infoMap.get(TokenType.KEY_WORD).forEach((info) -> kt.add(info.getContent()));

        List<TokenInfo> list = new ArrayList<>();
        for (TokenInfo info : infos) {
            if (info.getType().equals(TokenType.IDENTIFIER) && kt.contains(info.getContent())) {
                continue;
            }
            list.add(info);
        }
        return list;
    }

    private List<TokenInfo> matchLen(String content, DFA dfa) {
        List<TokenInfo> infos = new ArrayList<>();

        DFAState curState = dfa.getStartState();
        StringBuilder builder = new StringBuilder();

        // 当前字符是否有误
        boolean isError = false;

        for (int i = 0; i < content.length(); ) {
            // 逐个字符判断
            String curStr = String.valueOf(content.charAt(i));

            // 不是则不匹配
            if (dfa.getStartState() == curState && !dfa.getTransitions().get(curState).containsKey(curStr)) {

                // 跳过界符
                if (isDelimiter(curStr)) {
                    ++i;
                }

                while (!isDelimiter(curStr)) {
                    ++i;
                    curStr = String.valueOf(content.charAt(i));
                }
                continue;
            }

            if (isError) {
                if (isDelimiter(curStr)) {
                    // 清零，重新开始匹配
                    curState = dfa.getStartState();
//                    infos.add(new TokenInfo(0, TokenType.ERROR, builder.toString()));
                    builder = new StringBuilder();
                    isError = false;

                } else {
                    builder.append(curStr);
                    ++i;
                }
                continue;
            }

            if (dfa.getTransitions().get(curState).containsKey(curStr)) {
                builder.append(curStr);
                ++i;
                // 转至下一个状态
                curState = dfa.getTransitions().get(curState).get(curStr);
            } else {
                // 正常完结
                if (isDelimiter(curStr) && curState.isAccept()) {
                    if (builder.length() > 0) {
                        infos.add(new TokenInfo(builder.toString()));
                    }
                    builder = new StringBuilder();
                    curState = dfa.getStartState();
                } else {
                    // 这是一个错误的字符
                    isError = true;
                    builder.append(curStr);
                    ++i;
                }
            }

        }
        return infos;
    }

    private boolean isDelimiter(String s) {
        Set<String> set = new HashSet<>(Arrays.asList("(", ")", "{", "}", "[", "]", ",", ";", " "));
        return set.contains(s);
    }

    public void writeToFile(String outputFile, List<TokenInfo> infos) throws IOException {
        URL url = this.getClass().getClassLoader().getResource(outputFile);

        Map<String, Set<String>> map = new HashMap<>();
        assert url != null;
        BufferedWriter writer = new BufferedWriter(new FileWriter(url.getPath()));

        for (TokenInfo info : infos) {
            writer.write(info.getInfo());
            writer.newLine();

            Set<String> cur = map.getOrDefault(info.getType(), new HashSet<>());
            cur.add(info.getContent());
            map.put(info.getType(), cur);
        }

        writer.write("-----------");
        writer.newLine();
        for (String type : map.keySet()) {
            writer.write(type + ": " + map.get(type));
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

        Set<String> kt = new HashSet<>();
        infos.get(TokenType.KEY_WORD).forEach((item) -> kt.add(item.getContent()));
        infos.get(TokenType.TYPE).forEach((item) -> kt.add(item.getContent()));

        writer.write("-----------");
        writer.newLine();
        for (String type : infos.keySet()) {
            // 对应符号集合
            Set<String> sets = new HashSet<>();
            if (type.equals(TokenType.IDENTIFIER)) {
                infos.get(type).forEach(info -> {
                    if (!kt.contains(info.getContent())) {
                        sets.add(info.getContent());
                    }
                });
            } else {
                infos.get(type).forEach((info) -> sets.add(info.getContent()));
            }
            writer.write(type + ": " + sets);
            writer.newLine();
        }

        writer.flush();
        writer.close();

    }
}
