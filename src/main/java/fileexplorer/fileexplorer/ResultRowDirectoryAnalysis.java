package fileexplorer.fileexplorer;

public class ResultRowDirectoryAnalysis {
    private final String directoryPath;
    private final Integer numberOfDuplicates;

    public ResultRowDirectoryAnalysis(String directoryPath, Integer numberOfDuplicates) {
        this.directoryPath = directoryPath;
        this.numberOfDuplicates = numberOfDuplicates;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public Integer getNumberOfDuplicates() {
        return numberOfDuplicates;
    }
}
