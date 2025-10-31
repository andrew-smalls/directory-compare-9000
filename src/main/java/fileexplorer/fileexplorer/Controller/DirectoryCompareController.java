package fileexplorer.fileexplorer.Controller;

import fileexplorer.fileexplorer.dircompare.DirectoryComparator;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class DirectoryCompareController {

    @FXML private Label dirLabel1;
    @FXML private Label dirLabel2;

    // Results
    @FXML private TableView<DirectoryComparator.ResultRow> resultTable;
    @FXML private TableColumn<DirectoryComparator.ResultRow, String> dir1Column;
    @FXML private TableColumn<DirectoryComparator.ResultRow, String> dir2Column;
    @FXML private TableColumn<DirectoryComparator.ResultRow, String> statusColumn;

    private File dirPath1, dirPath2;
    private final ObservableList<DirectoryComparator.ResultRow> data = FXCollections.observableArrayList();

    private final String resourcesPath = "/fileexplorer/fileexplorer/";

    @FXML
    public void initialize() {
        dir1Column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDir1()));
        dir2Column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDir2()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        resultTable.setItems(data);
    }

    @FXML
    private void onBack() throws IOException {
        Stage stage = (Stage) javafx.stage.Window.getWindows().filtered(Window::isShowing).get(0);
        stage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource(resourcesPath + "menu-view.fxml"))), 960, 720));
    }

    @FXML
    protected void onSelectDir1() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("src"));
        dirPath1 = directoryChooser.showDialog(null);

        if (dirPath1 != null) {
            dirLabel1.setText(dirPath1.getAbsolutePath());
        } else {
            dirLabel1.setText("No directory selected");
        }
    }

    @FXML
    protected void onSelectDir2() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("src"));
        dirPath2 = directoryChooser.showDialog(null);

        if (dirPath2 != null) {
            dirLabel2.setText(dirPath2.getAbsolutePath());
        } else {
            dirLabel2.setText("No directory selected");
        }
    }

    @FXML
    public void onStartCompare() {
        validateDirectories();

        Task <Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                var results = DirectoryComparator.compareDirectories(dirPath1, dirPath2);
                for (var row : results) {
                    updateMessage("Found " + row.getDir1() + " - " + row.getDir2() + ": " + row.getStatus());
                    Platform.runLater(() -> resultTable.getItems().add(row));
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    
    private void validateDirectories() {
        if (dirPath1 == null || dirPath2 == null) {
            new Alert(Alert.AlertType.WARNING, "Please select both directories before comparing.").show();
            return;
        }
    }
}