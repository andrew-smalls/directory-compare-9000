package fileexplorer.fileexplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DuplicateFinder {

    public static List<ResultRowFileDuplicate> findDuplicates(File dirPath1) {
        List<ResultRowFileDuplicate> results = new ArrayList<>();
        ResultRowFileDuplicate row = new ResultRowFileDuplicate("example.txt", List.of(
                dirPath1.getAbsolutePath() + "/example1.txt",
                dirPath1.getAbsolutePath() + "/subdir/example1.txt",
                dirPath1.getAbsolutePath() + "/subdir/example2.txt"
        ));
        results.add(row);
        return results;
    }
}
