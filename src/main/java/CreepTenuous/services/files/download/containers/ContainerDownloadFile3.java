package CreepTenuous.services.files.download.containers;

public record ContainerDownloadFile3<ByteContent, StringDataFile>(
        ByteContent byteContent,
        StringDataFile mimeType,
        StringDataFile filename
) {
}
