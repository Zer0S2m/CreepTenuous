package CreepTenuous.Api.Main;

public class DataMainPage {
    private final String readyDirectory;
    private final Integer levelDirectory;

    public DataMainPage(String readyDirectory, Integer levelDirectory) {
        this.readyDirectory = readyDirectory;
        this.levelDirectory = levelDirectory;
    }

    public String getReadyDirectory() {
        return readyDirectory;
    }

    public Integer getLevelDirectory() {
        return levelDirectory;
    }
}