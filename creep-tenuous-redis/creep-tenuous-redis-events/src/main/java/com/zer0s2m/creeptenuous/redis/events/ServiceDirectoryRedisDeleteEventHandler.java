package com.zer0s2m.creeptenuous.redis.events;

import com.zer0s2m.creeptenuous.repository.user.CategoryFileSystemObjectRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
class ServiceDirectoryRedisDeleteEventHandler {

    private final CategoryFileSystemObjectRepository categoryFileSystemObjectRepository;

    public ServiceDirectoryRedisDeleteEventHandler(
            CategoryFileSystemObjectRepository categoryFileSystemObjectRepository
    ) {
        this.categoryFileSystemObjectRepository = categoryFileSystemObjectRepository;
    }

    /**
     * Deleting file objects associated witt custom categories
     *
     * @param nameFileSystemObject system name of the file system object
     */
    @Transactional
    public void deleteFileObjectCustomCategories(final @NotNull List<String> nameFileSystemObject) {
        categoryFileSystemObjectRepository.deleteAllByFileSystemObjectIn(
                nameFileSystemObject
                        .stream()
                        .filter(name -> name != null && name.length() == 36)
                        .map(UUID::fromString)
                        .toList()
        );
    }

}
