package org.y_lab.adapter.out.repository.interfaces;

import org.y_lab.application.exceptions.NotFoundException;

/**
 * Cache interface for more fast access to entities w/o request to DB
 * @param <K> key Type
 * @param <T> value Type
 */
public interface Cache<K, T> {
    /**
     * Save to cache
     * @param t object
     * @return key
     */
    K cache(T t);

    /**
     * read from cache
     * @param id key
     * @return object
     * @throws NotFoundException if key wrong or value not presented
     */
    T getFromCache(K id) throws NotFoundException;

    /**
     * delete from cache
     * @param id key to delete record
     */
    void delete(K id);
}
