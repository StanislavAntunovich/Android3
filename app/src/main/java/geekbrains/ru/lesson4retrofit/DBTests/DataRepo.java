package geekbrains.ru.lesson4retrofit.DBTests;

import java.util.List;

public interface DataRepo<T> {
    List<T> getAll();
    void saveAll(List<T> data);
    void deleteAll();
}
