package fileexplorer.fileexplorer;

import fileexplorer.fileexplorer.dircompare.DirectoryComparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class MainController {

    @FXML private Label dirLabel1;
    @FXML private Label dirLabel2;

    // Results
    @FXML private TableView<DirectoryComparator.ResultRow> resultTable;
    @FXML private TableColumn<DirectoryComparator.ResultRow, String> dir1Column;
    @FXML private TableColumn<DirectoryComparator.ResultRow, String> dir2Column;
    @FXML private TableColumn<DirectoryComparator.ResultRow, String> statusColumn;

    private File dirPath1, dirPath2;
    private final ObservableList<DirectoryComparator.ResultRow> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        dir1Column.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDir1()));
        dir2Column.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDir2()));
        statusColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));
        resultTable.setItems(data);
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
                    javafx.application.Platform.runLater(() -> resultTable.getItems().add(row));
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