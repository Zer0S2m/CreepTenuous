package com.zer0s2m.creeptenuous.services.system;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

/**
 * Service for servicing the download of a catalog in a zip archive
 */
public interface ServiceDownloadDirectory {

     /**
      * Download directory archive zip
      * @param systemParents parts of the system path - source
      * @param systemNameDirectory system name directory
      * @return archive zip
      * @throws IOException system error
      */
     ResponseEntity<Resource> download(List<String> systemParents, String systemNameDirectory) throws IOException;

     /**
      * Collect headers for download directory
      * @param path source archive zip
      * @param data byre data
      * @return headers
      */
     HttpHeaders collectHeaders(Path path, ByteArrayResource data);

     /**
      * Set resource for archiving directory
      * @param map data
      */
     void setMap(HashMap<String, String> map);

}
