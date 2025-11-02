package fileexplorer.fileexplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoryMap {

    private final String directory;
    private ArrayList<File> files;

    public DirectoryMap(String directory) {
        this.directory = directory;
    }

    public String getDirectory() {
        return directory;
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public void addFile(File file) {
        if (files == null) {
            this.files = new ArrayList<>();
        }
        this.files.add(file);
    }

    public void addFiles(File[] filesToAdd) {
        if (files == null) {
            this.files = new ArrayList<>();
        }
        this.files.addAll(List.of(filesToAdd));
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if (files != null) {
            for (File file : files) {
                str.append(file.getAbsolutePath()).append("\n\t");
            }
        }
        return String.format("%s: \n%s", directory, str);
    }
}
