package fileexplorer.fileexplorer;

import fileexplorer.fileexplorer.dircompare.DirectoryComparator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

    public void onStartCompare(ActionEvent actionEvent) {

    }
}