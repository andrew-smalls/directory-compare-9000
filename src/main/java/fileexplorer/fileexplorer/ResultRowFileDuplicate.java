package fileexplorer.fileexplorer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class ResultRowFileDuplicate {
    private final String fileName;
    private final ObservableList<String> paths;

    public ResultRowFileDuplicate(String fileName, List<String> paths) {
        this.fileName = fileName;
        this.paths = FXCollections.observableArrayList(paths);
    }

    public String getFileName() { return fileName; }
    public ObservableList<String> getPaths() { return paths; }
}
