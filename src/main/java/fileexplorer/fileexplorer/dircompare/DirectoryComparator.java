package fileexplorer.fileexplorer.dircompare;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DirectoryComparator {

    public static List<ResultRow> compareDirectories(File dirPath1, File dirPath2) {
        Map<String, DirectoryInfo> map1 = mapDirs(dirPath1);
        Map<String, DirectoryInfo> map2 = mapDirs(dirPath2);

        List<ResultRow> results = new ArrayList<>();

        for (var entry1 : map1.entrySet()) {
            DirectoryInfo d1 = entry1.getValue();
            DirectoryInfo d2 = map2.get(entry1.getKey());

            if (d2 == null) continue;

            String status = d1.equals(d2) ? "Match" : "Different";
            results.add(new ResultRow(d1.getFileName(), d2.getFileName(), status));
        }
        return results;
    }

    private static Map<String, DirectoryInfo> mapDirs(File base) {
        Map<String, DirectoryInfo> directoryMap = new java.util.HashMap<>();
        scan(base, directoryMap);
        return directoryMap;
    }

    private static void scan(File dir, Map<String, DirectoryInfo> directoryMap) {
        if (dir.isDirectory()) {
            directoryMap.put(dir.getPath(), DirectoryInfo.from(dir));
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    scan(file, directoryMap);
                }
            }
        }
    }

    public static class ResultRow {
        private final String dir1;
        private final String dir2;
        private final String status;

        public ResultRow(String dir1, String dir2, String status) {
            this.dir1 = dir1;
            this.dir2 = dir2;
            this.status = status;
        }

        public String getDir1() {
            return dir1;
        }

        public String getDir2() {
            return dir2;
        }

        public String getStatus() {
            return status;
        }
    }
}
