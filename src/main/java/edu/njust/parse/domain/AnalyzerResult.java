package edu.njust.parse.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class AnalyzerResult {
    private boolean isSuccess = true;

    private String message;

    @Getter
    private List<Point> analyzeProcess;

    public AnalyzerResult(String message, List<Point> analyzeProcess) {
        this.message = message;
        this.analyzeProcess = analyzeProcess;
        this.isSuccess = false;
    }

    @Override
    public String toString() {
        if (isSuccess) {
            return "匹配成功";
        } else {
            return "存在错误：" + message + "\n";
        }
    }
}
