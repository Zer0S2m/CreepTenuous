package CreepTenuous.services.files.downloadFile.service.containers;

public class ContainerDownloadFile3<ByteContent, StringDataFile> {
    private final ByteContent byteContent;
    private final StringDataFile mimeType;
    private final StringDataFile filename;

    public ContainerDownloadFile3(ByteContent byteContent, StringDataFile mimeType, StringDataFile filename) {
        this.byteContent = byteContent;
        this.mimeType = mimeType;
        this.filename = filename;
    }

    public ByteContent getByteContent() {
        return byteContent;
    }

    public StringDataFile getMimeType() {
        return mimeType;
    }

    public StringDataFile getFilename() {
        return filename;
    }
}
