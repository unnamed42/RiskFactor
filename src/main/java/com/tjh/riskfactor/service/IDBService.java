package com.tjh.riskfactor.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.server.ResponseStatusException;

import static com.tjh.riskfactor.error.ResponseErrors.notFound;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

interface IDBService<T> {

    JpaRepository<T, Integer> getRepo();

    String getEntityName();

    /**
     * 删除全部数据，以便重新读取
     */
    default void drop() {
        getRepo().deleteAllInBatch();
    }

    /**
     * 存储一系列数据。意在读取预设数据之后全部存入
     * @param items 数据实体的Stream
     * @return 存储后的数据实体集合
     */
    default List<T> saveAll(Stream<T> items) {
        return saveAll(items::iterator);
    }

    default List<T> saveAll(Iterable<T> items) {
        return getRepo().saveAll(items);
    }

    default T save(T t) {
        return getRepo().save(t);
    }

    default Optional<T> find(Integer id) {
        return getRepo().findById(id);
    }

    /**
     * 根据id查找实体，与上面的不同，当不存在时抛出异常
     * @param id 实体id
     * @return 数据库实体
     */
    default T checkedFind(Integer id) throws ResponseStatusException {
        return getRepo().findById(id).orElseThrow(() -> notFound(getEntityName(), id.toString()));
    }

}
