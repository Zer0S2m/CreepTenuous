package CreepTenuous.api.controllers.files.upload.response;

public class ResponseUploadFile {
    private final Boolean success;

    public ResponseUploadFile(Boolean success) {
        this.success = success;
    }

    public Boolean getSuccess() {
        return this.success;
    }
}
