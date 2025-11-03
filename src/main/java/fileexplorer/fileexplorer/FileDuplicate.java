package fileexplorer.fileexplorer;

import java.util.ArrayList;

public class FileDuplicate {

    private final String fileName;
    private ArrayList<String> paths;

    public FileDuplicate(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void addToKnownPaths(String path) {
        paths.add(path);
    }

    public ArrayList<String> getPaths() {
        return paths;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FileDuplicate)) {
            return false;
        }
        FileDuplicate other = (FileDuplicate) obj;
        return fileName.equals(other.fileName);
    }
}
