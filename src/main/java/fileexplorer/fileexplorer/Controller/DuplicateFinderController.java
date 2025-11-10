package fileexplorer.fileexplorer.Controller;

import fileexplorer.fileexplorer.DuplicateFinder;
import fileexplorer.fileexplorer.ResultRowDirectoryAnalysis;
import fileexplorer.fileexplorer.ResultRowFileDuplicate;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DuplicateFinderController {

    @FXML private Label dirLabel1;
    private File dirPath1;

    // File Duplicates
    @FXML private TableView<ResultRowFileDuplicate> fileDuplicatesTable;
    @FXML private TableColumn<ResultRowFileDuplicate, String> fileColumn;
    @FXML private TableColumn<ResultRowFileDuplicate, ObservableList<String>> pathsColumn;
    @FXML private TableColumn<ResultRowFileDuplicate, Void> imagePreview;
    private final ObservableList<ResultRowFileDuplicate> dataFileDuplicates = FXCollections.observableArrayList();

    // Directory analysis
    @FXML private TableView<ResultRowDirectoryAnalysis> directoryAnalysisTable;
    @FXML private TableColumn<ResultRowDirectoryAnalysis, String> directoryColumn;
    @FXML private TableColumn<ResultRowDirectoryAnalysis, Integer> nrDuplicateFilesColumn;
    @FXML private TableColumn<ResultRowDirectoryAnalysis, Double> directorySize;
    private final ObservableList<ResultRowDirectoryAnalysis> dataDirectoryAnalysis = FXCollections.observableArrayList();

    private final String resourcesPath = "/fileexplorer/fileexplorer/";

    @FXML
    public void initialize() {
        setDataForFileDuplicates();
        setDataForDirectoryAnalysis();
    }

    private void setDataForFileDuplicates() {
        fileColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFileName()));
        pathsColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(ObservableList<String> item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String joined = String.join("\n", item);
                    setText(joined + "\n");
                }
            }
        });

        imagePreview.setCellFactory(column -> new TableCell<>() {
            private final HBox container = new HBox(10);
            private final ImageView imageView1 = new ImageView();
            private final ImageView imageView2 = new ImageView();
            private final Button keepFirst = new Button("Keep First");
            private final Button keepSecond = new Button("Keep Second");

            {
                imageView1.setFitWidth(80);
                imageView1.setFitHeight(80);
                imageView1.setPreserveRatio(true);

                imageView2.setFitWidth(80);
                imageView2.setFitHeight(80);
                imageView2.setPreserveRatio(true);

                container.setAlignment(Pos.CENTER_LEFT);
                container.getChildren().addAll(imageView1, imageView2, keepFirst, keepSecond);

                // Wire up button events
                keepFirst.setOnAction(e -> {
                    ResultRowFileDuplicate item = getTableView().getItems().get(getIndex());
                    item.getSecondPath().ifPresent(path -> {
                        File f = new File(path);
                        if (f.exists()) f.delete();
                    });
                    getTableView().getItems().remove(getIndex());
                });

                keepSecond.setOnAction(e -> {
                    ResultRowFileDuplicate item = getTableView().getItems().get(getIndex());
                    item.getFirstPath().ifPresent(path -> {
                        File f = new File(path);
                        if (f.exists()) f.delete();
                    });
                    getTableView().getItems().remove(getIndex());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                ResultRowFileDuplicate row = getTableView().getItems().get(getIndex());

                // Load images (only if they’re valid image files)
                imageView1.setImage(null);
                imageView2.setImage(null);

                row.getFirstPath().ifPresent(path -> {
                    if (path.matches(".*\\.(jpg|jpeg|png|bmp|gif)$")) {
                        try {
                            imageView1.setImage(new Image(new File(path).toURI().toString(), 80, 80, true, true));
                        } catch (Exception ignored) {}
                    }
                });
                row.getSecondPath().ifPresent(path -> {
                    if (path.matches(".*\\.(jpg|jpeg|png|bmp|gif)$")) {
                        try {
                            imageView2.setImage(new Image(new File(path).toURI().toString(), 80, 80, true, true));
                        } catch (Exception ignored) {}
                    }
                });

                setGraphic(container);
            }
        });

        fileDuplicatesTable.setItems(dataFileDuplicates);
    }

    private void setDataForDirectoryAnalysis() {
        directoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDirectoryPath()));
        nrDuplicateFilesColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getNumberOfDuplicates()));
        directorySize.setCellValueFactory(cellData -> {
          Map.Entry<String, Double> entry = cellData.getValue().getDirectorySize().entrySet().iterator().next();
            return new SimpleObjectProperty(String.format("%.2f (%s)", entry.getValue(), entry.getKey()));
        });
        directoryAnalysisTable.setItems(dataDirectoryAnalysis);
    }

    @FXML
    private void onBack() throws IOException {
        Stage stage = (Stage) javafx.stage.Window.getWindows().filtered(Window::isShowing).get(0);
        stage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource(resourcesPath + "menu-view.fxml"))), 960, 720));
    }

    @FXML
    public void onSelectDir1() {
        DirectoryChooser chooser = new DirectoryChooser();
        dirPath1 = chooser.showDialog(null);
        if (dirPath1 != null) dirLabel1.setText(dirPath1.getAbsolutePath());
    }

    @FXML
    public void onFindDuplicates() {
        if (dirPath1 == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a directory").show();
            return;
        }
        dataFileDuplicates.clear();
        dataDirectoryAnalysis.clear();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                int cores = Runtime.getRuntime().availableProcessors();
                int maxParallelism = (int) (cores * 0.8);
                DuplicateFinder duplicateFinder = new DuplicateFinder(maxParallelism);
                List<ResultRowFileDuplicate> duplicates = duplicateFinder.findDuplicates(dirPath1);
                HashMap<String, ArrayList<String>> uniqueDirectoriesContainingDuplicates = new HashMap<>();
                for (ResultRowFileDuplicate duplicate : duplicates) {
                    String duplicateFilename = duplicate.getFileName();
                    updateMessage("Found " + duplicateFilename + " - " + duplicate.getPaths());
                    Platform.runLater(() -> fileDuplicatesTable.getItems().add(duplicate));

                    HashSet<String> uniqueDirectoriesForFile = duplicate.getDirectories();
                    for (String uniqueDirectory : uniqueDirectoriesForFile) {
                        if (uniqueDirectoriesContainingDuplicates.containsKey(uniqueDirectory)) {
                            ArrayList<String> existingFiles = uniqueDirectoriesContainingDuplicates.get(uniqueDirectory);
                            existingFiles.add(duplicateFilename);
                            uniqueDirectoriesContainingDuplicates.put(uniqueDirectory, existingFiles);
                        } else {
                            ArrayList<String> filesInDirectory = new ArrayList<>();
                            filesInDirectory.add(duplicateFilename);
                            uniqueDirectoriesContainingDuplicates.put(uniqueDirectory, filesInDirectory);
                        }
                    }
                }

                for (Map.Entry<String, ArrayList<String>> entry : uniqueDirectoriesContainingDuplicates.entrySet()) {
                    ResultRowDirectoryAnalysis resultRowDirectoryAnalysis = new ResultRowDirectoryAnalysis(entry.getKey(), entry.getValue().size());
                    Platform.runLater(() -> directoryAnalysisTable.getItems().add(resultRowDirectoryAnalysis));
                }

                return null;
            }
        };
        new Thread(task).start();
    }
}
