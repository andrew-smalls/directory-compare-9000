package fileexplorer.fileexplorer;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

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

    public Map<String, Double> getDirectorySize() {
        long sizeInBytes = getDirectorySizeBytes();
        HashMap<String, Double> humanReadableSize = new HashMap<>();
        long MB = 1024*1024;
        long GB = MB*1024;
        if (sizeInBytes < 1024) {
            humanReadableSize.put("Bytes", (double) sizeInBytes);
            return humanReadableSize;
        } else if (sizeInBytes < GB) {
            double sizeInMB = (double)sizeInBytes/MB;
            humanReadableSize.put("MB", round(sizeInMB, 2));
            return humanReadableSize;
        } else {
            double sizeInGB = (double)sizeInBytes/GB;
            humanReadableSize.put("GB", round(sizeInGB, 2));
            return humanReadableSize;
        }
    }

    public long getDirectorySizeBytes() {
        Path folder = Paths.get(directoryPath);
        long sizeInBytes = 0L;
        try {
            sizeInBytes = Files.walk(folder)
                    .filter(p -> p.toFile().isFile())
                    .mapToLong(p -> p.toFile().length())
                    .sum();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sizeInBytes;
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
