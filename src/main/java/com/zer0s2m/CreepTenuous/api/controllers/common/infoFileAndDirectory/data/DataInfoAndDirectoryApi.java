package com.zer0s2m.CreepTenuous.api.controllers.common.infoFileAndDirectory.data;

import java.util.Date;

public record DataInfoAndDirectoryApi(boolean isHidden, Date lastModified, long size) {
}
