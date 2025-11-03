package fileexplorer.fileexplorer;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        // go through map contents and identify duplicates
        start = Instant.now();
        HashMap<String, ArrayList<String>> duplicateFiles = new HashMap<>();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            duplicateFiles = identifyDuplicateFiles(directoryMaps, executor);
        }

        if (!duplicateFiles.isEmpty()) {
            for (Map.Entry<String, ArrayList<String>> entry : duplicateFiles.entrySet()) {
                ResultRowFileDuplicate row = new ResultRowFileDuplicate(entry.getKey(), Arrays.asList(entry.getValue().toArray()));
                results.add(row);
            }
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

    private HashMap<String, ArrayList<String>> identifyDuplicateFiles(ArrayList<DirectoryMap> directoryMaps, ExecutorService executor) {
        ArrayList<DirectoryMap> originalFiles = new ArrayList<>(directoryMaps);
        HashMap<String, ArrayList<String>> duplicates = new HashMap<>();
        for (DirectoryMap directoryMap : directoryMaps) {
            executor.submit(() -> {
                findDuplicatesInDirectory(directoryMap, originalFiles, duplicates);
            });
        }
        return duplicates;
    }

    private HashMap<String, ArrayList<String>> identifyDuplicateFilesSingle(ArrayList<DirectoryMap> directoryMaps) {
        ArrayList<DirectoryMap> originalFiles = new ArrayList<>(directoryMaps);
        HashMap<String, ArrayList<String>> duplicates = new HashMap<>();
        for (DirectoryMap directoryMap : directoryMaps) {
            findDuplicatesInDirectory(directoryMap, originalFiles, duplicates);
        }
        return duplicates;
    }

    private void findDuplicatesInDirectory(DirectoryMap directoryMap, ArrayList<DirectoryMap> originalFiles, HashMap<String, ArrayList<String>> duplicates) {
        try {
            System.out.println("Acquiring concurrency limiter for " + directoryMap.getDirectory());
            concurrencyLimiter.acquire();
            ArrayList<File> duplicateFiles = new ArrayList<>();
            for (File file: directoryMap.getFiles()) {
                if (file.isDirectory() || !isPicture(file.getAbsolutePath())) {
                    continue;
                }
                if (isDuplicate(file, originalFiles)) {
                    duplicateFiles.add(file);
                }
            }
            for (File duplicate: duplicateFiles) {
                ArrayList<String> existingEntries = duplicates.get(duplicate.getName());
                if (existingEntries == null) {
                    ArrayList<String> newEntries = new ArrayList<>();
                    newEntries.add(duplicate.getAbsolutePath());
                    duplicates.put(duplicate.getName(), newEntries);
                } else {
                    existingEntries.add(duplicate.getAbsolutePath());
                    duplicates.put(duplicate.getName(), existingEntries);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to analyse: " + directoryMap.getDirectory());
        } finally {
            concurrencyLimiter.release();
            System.out.println("Releasing concurrency limiter for " + directoryMap.getDirectory());
        }
    }

    private boolean isDuplicate(File file, ArrayList<DirectoryMap> originalFiles) {
        if (file == null || file.isDirectory()) return false;

        return originalFiles.stream()
                .filter(dm -> dm.getFiles() != null)
                .flatMap(dm -> dm.getFiles().stream())      // stream of File
                .anyMatch(f -> !f.getAbsolutePath().equals(file.getAbsolutePath())
                        && f.getName().equals(file.getName()));
    }


    private boolean isPicture(String filePath) {
        String lowerPath = filePath.toLowerCase();
        return lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg") ||
               lowerPath.endsWith(".png") || lowerPath.endsWith(".gif") ||
               lowerPath.endsWith(".bmp") || lowerPath.endsWith(".tiff");
    }
}
