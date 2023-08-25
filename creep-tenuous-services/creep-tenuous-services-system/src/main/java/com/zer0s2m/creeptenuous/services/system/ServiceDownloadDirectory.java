package com.zer0s2m.creeptenuous.services.system;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

/**
 * Service for servicing the download of a catalog in a zip archive
 */
public interface ServiceDownloadDirectory extends ServiceDownloadDirectoryStream {

     /**
      * Download directory archive zip
      * @param systemParents parts of the system path - source
      * @param systemNameDirectory system name directory
      * @return archive zip (source)
      * @throws IOException system error
      */
     Path download(List<String> systemParents, String systemNameDirectory) throws IOException;

     /**
      * Set resource for archiving directory
      * @param map data
      */
     void setMap(HashMap<String, String> map);

}
