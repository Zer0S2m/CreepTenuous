package com.zer0s2m.creeptenuous.redis.repository;

import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.FluentQuery;

import java.util.function.Function;

/**
 * Interface for generic CRUD operations on a repository for {@link RightUserFileSystemObjectRedis}.
 */
public interface RightUserFileSystemObjectRedisRepository
        extends CrudRepository<RightUserFileSystemObjectRedis, String> {

    /**
     * Returns entities matching the given {@link RightUserFileSystemObjectRedis} applying the
     * {@link Function queryFunction} that defines the query and its result type.
     *
     * @param rightUserFileSystemObjectRedis must not be {@literal null}.
     * @param queryFunction the query function defining projection, sorting, and the result type
     * @return all entities matching the given {@link RightUserFileSystemObjectRedis}.
     * @since 2.6
     */
    <S extends RightUserFileSystemObjectRedis, R> R findBy(
            RightUserFileSystemObjectRedis rightUserFileSystemObjectRedis,
            Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction
    );
}
