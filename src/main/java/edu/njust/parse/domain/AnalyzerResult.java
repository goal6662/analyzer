package edu.njust.parse.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.*;
import java.net.URL;
import java.util.List;

public class AnalyzerResult {
    @Getter
    private boolean isSuccess = true;

    private String message;

    @Getter
    private List<Point> analyzeProcess;

    public AnalyzerResult(String message, List<Point> analyzeProcess) {
        this.message = message;
        this.analyzeProcess = analyzeProcess;
        this.isSuccess = false;
    }

    public AnalyzerResult(List<Point> analyzeProcess) {
        this.analyzeProcess = analyzeProcess;
    }

    @Override
    public String toString() {
        if (isSuccess) {
            return "匹配成功";
        } else {
            return "存在错误-" + message + "\n";
//                    + "可以尝试在token前添加Except内的字符\n";
        }
    }

    public void writeProcessToFile(String filePath) throws IOException {
        URL url = this.getClass().getClassLoader().getResource(filePath);

        assert url != null;
        BufferedWriter writer = new BufferedWriter(new FileWriter(url.getFile()));

        writer.write("步骤, 符号栈, 当前输入, 符号串, 所用规则");
        writer.newLine();
        for (Point process : analyzeProcess) {
            writer.write(process.toString());
            writer.newLine();
        }

        writer.flush();
        writer.close();
    }


    public void writeProcessToFile(String filePath, String errorInfo) throws IOException {
        URL url = this.getClass().getClassLoader().getResource(filePath);

        assert url != null;
        BufferedWriter writer = new BufferedWriter(new FileWriter(url.getFile()));

        writer.write("步骤, 符号栈, 当前输入, 符号串, 所用规则");
        writer.newLine();
        for (Point process : analyzeProcess) {
            writer.write(process.toString());
            writer.newLine();
        }
        writer.write(errorInfo);
        writer.flush();
        writer.close();
    }

}
