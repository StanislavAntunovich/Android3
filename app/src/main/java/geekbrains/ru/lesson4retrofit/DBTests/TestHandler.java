package geekbrains.ru.lesson4retrofit.DBTests;

import java.util.Date;
import java.util.List;

public class TestHandler<T> {
    private List<T> data;

    public DBTest makeLoadTest(DataRepo<T> repo) {
        return new DBTest() {
            @Override
            public void before() {
            }

            @Override
            public void test() {
                data = repo.getAll();
            }

            @Override
            public void after() {
            }
        };
    }

    public DBTest makeSaveTest(DataRepo<T> repo, List<T> testData) {
        return new DBTest() {
            @Override
            public void before() {
                repo.deleteAll();
            }

            @Override
            public void test() {
                repo.saveAll(testData);
            }

            @Override
            public void after() {
                data = testData;
            }
        };
    }

    public TestResult run(DBTest test, int testCount) {
        long sum = 0;
        for (int i = 0; i < testCount; i++) {
            test.before();
            Date date1 = new Date();
            test.test();
            Date date2 = new Date();
            test.after();
            sum += date2.getTime() - date1.getTime();
        }
        double result = sum / (double) testCount;
        int count = data.size();

        return new TestResult(result, count);
    }

    public List<T> getData() {
        return data;
    }

}
