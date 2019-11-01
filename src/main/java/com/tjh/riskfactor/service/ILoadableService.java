package com.tjh.riskfactor.service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 标记这个的Service，其数据应当是预设在resources当中
 */
interface ILoadableService<T> {

    JpaRepository<T, Integer> getRepo();

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
        return getRepo().saveAll(items::iterator);
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

}
