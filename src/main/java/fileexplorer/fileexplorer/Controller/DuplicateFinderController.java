package fileexplorer.fileexplorer.Controller;

import fileexplorer.fileexplorer.DuplicateFinder;
import fileexplorer.fileexplorer.ResultRowFileDuplicate;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class DuplicateFinderController {

    @FXML private Label dirLabel1;
    private File dirPath1;

    // Results
    @FXML private TableView<ResultRowFileDuplicate> resultTable;
    @FXML private TableColumn<ResultRowFileDuplicate, String> fileColumn;
    @FXML private TableColumn<ResultRowFileDuplicate, ObservableList<String>> pathsColumn;

    private final ObservableList<ResultRowFileDuplicate> data = FXCollections.observableArrayList();

    private final String resourcesPath = "/fileexplorer/fileexplorer/";

    @FXML
    public void initialize() {
        fileColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFileName()));
        pathsColumn.setCellValueFactory(cellData -> new SimpleListProperty<>(cellData.getValue().getPaths()));

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

        resultTable.setItems(data);
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
        data.clear();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                List<ResultRowFileDuplicate> duplicates = DuplicateFinder.findDuplicates(dirPath1);
                for (ResultRowFileDuplicate duplicate : duplicates) {
                    updateMessage("Found " + duplicate.getFileName() + " - " + duplicate.getPaths());
                    Platform.runLater(() -> resultTable.getItems().add(duplicate));
                }
                return null;
            }
        };
        new Thread(task).start();
    }
}
