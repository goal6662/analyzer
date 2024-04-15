package edu.njust.word.util;

import edu.njust.word.domain.dfa.DFA;
import edu.njust.word.domain.dfa.DFAState;
import edu.njust.word.domain.token.TokenInfo;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public List<TokenInfo> match(String type, DFA dfa) {
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

                if (!dfa.getTransitions().get(curState).containsKey(symbol)) {

                    // 当前字符未能匹配
                    int space = content.indexOf(' ', i);
                    // 存在下一个匹配项
                    if (space != -1) {
                        i = space;
                        symbol = String.valueOf(content.charAt(i + 1));
                    } else {
                        break;
                    }

                    if (builder.length() > 0) {
                        infos.add(new TokenInfo(len, type, builder.toString()));
                        builder = new StringBuilder();
                    }
                } else {
                    builder.append(symbol);
                }

                curState = dfa.getTransitions().get(curState).get(symbol);

            }

            if (curState != null && curState.isAccept() && builder.length() > 0) {
                infos.add(new TokenInfo(len, type, builder.toString()));
            }
            ++len;
        }

        return infos;
    }

    /**
     * 使用所有DFA，匹配文本
     *
     * @param dfaMap dfas
     * @return
     */
    public List<TokenInfo> match(Map<String, DFA> dfaMap) {
        List<TokenInfo> infos = new ArrayList<>();
        for (String type : dfaMap.keySet()) {
            infos.addAll(match(type, dfaMap.get(type)));
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
}
