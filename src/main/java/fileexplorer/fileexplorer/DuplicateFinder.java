package fileexplorer.fileexplorer;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class DuplicateFinder {

    private final Semaphore concurrencyLimiter;

    public DuplicateFinder(int maxParallelism) {
        this.concurrencyLimiter = new Semaphore(maxParallelism);
    }

    public List<ResultRowFileDuplicate> findDuplicates(File dirPath1) {
        List<ResultRowFileDuplicate> results = new ArrayList<>();

        ArrayList<DirectoryMap> directoryMaps = new ArrayList<>();
        Instant start = Instant.now();
        directoryMaps = scanFiles(dirPath1, directoryMaps);
        Instant end = Instant.now();
        long duration = Duration.between(start, end).toMillis();
        System.out.println("Scanned " + directoryMaps.size() + " directories in " + duration + " miliseconds");
        for (DirectoryMap directoryMap : directoryMaps) {
            // System.out.println(directoryMap.toString());
            ResultRowFileDuplicate row = new ResultRowFileDuplicate(directoryMap.getDirectory(), directoryMap.getFiles());
            results.add(row);
        }

        // go through map contents and identify duplicates
        start = Instant.now();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            identifyDuplicateFiles(directoryMaps, executor);
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toSeconds();
        System.out.println("Total time elapsed: " + timeElapsed + " seconds");

        return results;
    }

    private ArrayList<DirectoryMap> scanFiles(File file, ArrayList<DirectoryMap> directoryMaps) {
        if (file.isDirectory()) {
            DirectoryMap dm = new DirectoryMap(file.getAbsolutePath());
            directoryMaps.add(dm); // add the current directory

            File[] files = file.listFiles();
            if (files == null) return directoryMaps;

            dm.addFiles(files);

            for (File f : files) {
                scanFiles(f, directoryMaps); // just recurse
            }
        }
        return directoryMaps;
    }

    private ArrayList<DirectoryMap> identifyDuplicateFiles(ArrayList<DirectoryMap> directoryMaps, ExecutorService executor) {
        ArrayList<DirectoryMap> duplicates = new ArrayList<>();
        for (DirectoryMap directoryMap : directoryMaps) {
            executor.submit(() -> {
                try {
                    System.out.println("Acquiring concurrency limiter for " + directoryMap.getDirectory());
                    concurrencyLimiter.acquire();
                } catch (Exception e) {
                    System.err.println("Failed to analyse: " + directoryMap.getDirectory());
                } finally {
                concurrencyLimiter.release();
                System.out.println("Releasing concurrency limiter for " + directoryMap.getDirectory());
            }
            });
        }
        return duplicates;
    }


    private boolean isPicture(String filePath) {
        String lowerPath = filePath.toLowerCase();
        return lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg") ||
               lowerPath.endsWith(".png") || lowerPath.endsWith(".gif") ||
               lowerPath.endsWith(".bmp") || lowerPath.endsWith(".tiff");
    }
}
