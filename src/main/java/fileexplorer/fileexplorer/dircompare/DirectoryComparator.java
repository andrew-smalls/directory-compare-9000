package fileexplorer.fileexplorer.dircompare;

public class DirectoryComparator {

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
