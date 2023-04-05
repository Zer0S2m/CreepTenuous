package CreepTenuous.api.controllers.common.infoFileAndDirectory.data;

import java.util.Date;

public record DataInfoAndDirectoryApi(boolean isHidden, Date lastModified, long size) {
}
