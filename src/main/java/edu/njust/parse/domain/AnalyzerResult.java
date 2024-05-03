package edu.njust.parse.domain;

public class AnalyzerResult {
    private boolean isSuccess;

    private String message;

    @Override
    public String toString() {
        if (isSuccess) {
            return "匹配成功";
        } else {
            return "存在错误：" + message + "\n";
        }
    }
}
