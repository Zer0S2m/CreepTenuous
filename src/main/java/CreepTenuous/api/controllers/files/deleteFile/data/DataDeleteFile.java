package CreepTenuous.api.controllers.files.deleteFile.data;

public class DataDeleteFile {
    private final Integer typeFile;
    private final String titleFile;

    public DataDeleteFile(Integer typeFile, String titleFile) {
        this.typeFile = typeFile;
        this.titleFile = titleFile;
    }
}
