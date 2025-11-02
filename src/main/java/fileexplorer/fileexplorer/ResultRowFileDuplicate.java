package fileexplorer.fileexplorer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ResultRowFileDuplicate {
    private final String fileName;
    private final ObservableList<String> paths;

    public ResultRowFileDuplicate(String fileName, List<?> files) {
        this.fileName = fileName;
        this.paths = FXCollections.observableArrayList(convertToPaths(files));
    }

    private List<String> convertToPaths(List<?> files) {
        if (files == null || files.isEmpty()) return List.of();

        Object first = files.get(0);
        if (first instanceof File) {
            return files.stream()
                    .filter(File.class::isInstance)
                    .map(f -> ((File) f).getAbsolutePath())
                    .collect(Collectors.toList());
        } else if (first instanceof String) {
            return files.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException(
                    "Unsupported list element type: " + first.getClass().getName());
        }
    }

    public String getFileName() { return fileName; }
    public ObservableList<String> getPaths() { return paths; }
}
