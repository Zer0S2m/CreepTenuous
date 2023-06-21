package com.zer0s2m.creeptenuous.redis.events;

import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * Event deleting a directory object from Redis
 */
class DirectoryRedisDeleteEvent extends ApplicationEvent {

    /**
     * System names of file system objects
     */
    private List<String> namesFileSystemObject;

    DirectoryRedisDeleteEvent(Object source) {
        super(source);
    }

    public String toString(){
        return this.getClass().getName() + "[source=" + this.source + "] " +
                "Removal event of a radish object from the system";
    }

    /**
     * Get system names of file system objects
     * @return system names of file system objects
     */
    public List<String> getNamesFileSystemObject() {
        return namesFileSystemObject;
    }

    /**
     * Set system names of file system objects
     * @param namesFileSystemObject system names of file system objects
     */
    public void setNamesFileSystemObject(List<String> namesFileSystemObject) {
        this.namesFileSystemObject = namesFileSystemObject;
    }

}
