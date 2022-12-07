package CreepTenuous.Api.common.InfoFileAndDirectory.data;

import java.util.Date;

public class DataInfoAndDirectoryApi {
    private final boolean isHidden;
    private final Date lastModified;
    private final long size;

    public DataInfoAndDirectoryApi(boolean isHidden, Date lastModified, long size) {
        this.isHidden = isHidden;
        this.lastModified = lastModified;
        this.size = size;
    }

    public boolean getIsHidden() {
        return this.isHidden;
    }

    public Date getLastModified() {
        return this.lastModified;
    }

    public long getSize() {
        return this.size;
    }
}
