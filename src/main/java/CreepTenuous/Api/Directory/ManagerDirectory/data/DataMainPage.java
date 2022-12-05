package CreepTenuous.Api.Directory.ManagerDirectory.data;

public class DataMainPage {
    private final String readyDirectory;
    private final Integer levelDirectory;
    private final String[] partsDirectory;

    public DataMainPage(String[] partsDirectory, String readyDirectory, Integer levelDirectory) {
        this.partsDirectory = partsDirectory;
        this.readyDirectory = readyDirectory;
        this.levelDirectory = levelDirectory;
    }

    public String getReadyDirectory() {
        return readyDirectory;
    }

    public Integer getLevelDirectory() {
        return levelDirectory;
    }

    public String[] getPartsDirectory() {
        return partsDirectory;
    }
}