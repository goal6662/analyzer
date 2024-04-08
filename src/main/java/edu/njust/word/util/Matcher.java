package edu.njust.word.util;

import edu.njust.word.domain.dfa.DFA;
import edu.njust.word.domain.dfa.DFAState;
import edu.njust.word.domain.token.TokenInfo;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * DFA --> Token
 */
public class Matcher {

    private final BufferedReader reader;

    public Matcher(String filePath) throws IOException {
        URL url = this.getClass().getClassLoader().getResource(filePath);

        assert url != null;
        reader = new BufferedReader(new FileReader(url.getFile()));
    }

    public List<TokenInfo> match(DFA dfa) throws IOException {
        List<TokenInfo> infos = new ArrayList<>();
        // 1. 记录行号
        int len = 1;
        String content;

        DFAState curState = dfa.getStartState();
        while ((content = reader.readLine()) != null) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < content.length(); i++) {
                String symbol = String.valueOf(content.charAt(i));

                if (curState == null) {
                    curState = dfa.getStartState();
                }
                if (!dfa.getTransitions().get(curState).containsKey(symbol)) {
                    if (builder.length() > 0) {
                        infos.add(new TokenInfo(len, "type", builder.toString()));
                        builder = new StringBuilder();
                    }
                } else {
                    builder.append(symbol);
                }
                curState = dfa.getTransitions().get(curState).get(symbol);

            }

            if (curState.isAccept()) {
                infos.add(new TokenInfo(len, "type", builder.toString()));
            }
            ++len;
        }


        return infos;
    }


    /**
     * Warning: 写入的文件在target目录下
     * @param outputFile
     * @param infos
     * @throws IOException
     */
    public void writeToFile(String outputFile, List<TokenInfo> infos) throws IOException {
        URL url = this.getClass().getClassLoader().getResource(outputFile);

        assert url != null;
        BufferedWriter writer = new BufferedWriter(new FileWriter(url.getPath()));
        BufferedReader reader1 = new BufferedReader(new FileReader(url.getFile()));

        String temp = reader1.readLine();
        System.out.println();
        for (TokenInfo tokenInfo : infos) {
            writer.write(tokenInfo.getInfo());
            writer.newLine();
        }
        writer.flush();
        writer.close();

    }
}
