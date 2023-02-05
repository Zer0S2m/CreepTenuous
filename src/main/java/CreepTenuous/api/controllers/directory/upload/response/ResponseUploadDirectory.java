package CreepTenuous.api.controllers.directory.upload.response;

public class ResponseUploadDirectory {
    private final Boolean success;

    public ResponseUploadDirectory(Boolean success) {
        this.success = success;
    }

    public Boolean getSuccess() {
        return this.success;
    }
}
