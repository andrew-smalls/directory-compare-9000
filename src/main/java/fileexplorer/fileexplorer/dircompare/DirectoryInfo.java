package fileexplorer.fileexplorer.dircompare;

import java.io.File;
import java.util.Objects;

public class DirectoryInfo {
    private final String fileName;
    private final long fileSize;
    private final long fileCount;

    public DirectoryInfo(String fileName, long fileSize, long fileCount) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileCount = fileCount;
    }

    public static DirectoryInfo from(File dir) {
        long[] stats = getStats(dir);
        return new DirectoryInfo(dir.getName(), stats[0], stats[1]);
    }

    private static long[] getStats(File dir) {
        long totalSize = 0;
        long totalCount = 0;

        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    long[] subStats = getStats(file);
                    totalSize += subStats[0];
                    totalCount += subStats[1];
                } else {
                    totalSize += file.length();
                    totalCount += 1;
                }
            }
        }

        return new long[] { totalSize, totalCount };
    }

    public String getFileName() {
        return fileName;
    }
    public long getFileSize() {
        return fileSize;
    }
    public long getFileCount() {
        return fileCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DirectoryInfo)) return false;
        DirectoryInfo that = (DirectoryInfo) o;
        return fileSize == that.fileSize && fileCount == that.fileCount && Objects.equals(fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, fileSize, fileCount);
    }
}
