package fileexplorer.fileexplorer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ResultRowFileDuplicate {
    private final String fileName;
    private final ObservableList<String> paths;
    private final HashSet<String> directories;

    public ResultRowFileDuplicate(String fileName, List<?> files) {
        this.fileName = fileName;
        this.paths = FXCollections.observableArrayList(convertToPaths(files));
        this.directories = addUniquePaths();
    }

    private HashSet<String> addUniquePaths() {
        HashSet<String> uniquePaths = new HashSet<>();
        for (String path : paths) {
            String dirName = path.substring(0, path.lastIndexOf(File.separator));
            uniquePaths.add(dirName);
        }
        return uniquePaths;
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

    public Optional<String> getFirstPath() {
        return paths.isEmpty() ? Optional.empty() : Optional.of(paths.getFirst());
    }

    public Optional<String> getSecondPath() {
        return paths.size() < 2 ? Optional.empty() : Optional.of(paths.get(1));
    }

    public HashSet<String> getDirectories() {
        return directories;
    }
}
