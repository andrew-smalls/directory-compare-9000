package fileexplorer.fileexplorer.Controller;

import fileexplorer.fileexplorer.ResultRowFileDuplicate;
import fileexplorer.fileexplorer.dircompare.DirectoryComparator;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class DuplicateFinderController {

    @FXML private Label dirLabel1;

    // Results
    @FXML private TableView<ResultRowFileDuplicate> resultTable;
    @FXML private TableColumn<ResultRowFileDuplicate, String> fileColumn;
    @FXML private TableColumn<ResultRowFileDuplicate, ObservableList<String>> pathsColumn;

    private File dirPath1, dirPath2;
    private final ObservableList<ResultRowFileDuplicate> data = FXCollections.observableArrayList();

    private final String resourcesPath = "/fileexplorer/fileexplorer/";

    @FXML
    public void initialize() {
        fileColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFileName()));
        pathsColumn.setCellValueFactory(cellData -> new SimpleListProperty<>(cellData.getValue().getPaths()));
        resultTable.setItems(data);
    }

    @FXML
    private void onBack() throws IOException {
        Stage stage = (Stage) javafx.stage.Window.getWindows().filtered(Window::isShowing).get(0);
        stage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource(resourcesPath + "menu-view.fxml"))), 960, 720));
    }
}
