package geekbrains.ru.lesson4retrofit.DBTests;

public class TestResult {
    private Double time;
    private Integer count;

    public TestResult(double time, int count) {
        this.time = time;
        this.count = count;
    }

    public TestResult() {
    }

    public Double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
