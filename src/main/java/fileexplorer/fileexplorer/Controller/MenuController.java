package fileexplorer.fileexplorer.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Objects;

public class MenuController {

    private final String resourcesPath = "/fileexplorer/fileexplorer/";

    private Stage getStage() {
        return (Stage) new Scene(new javafx.scene.layout.Pane()).getWindow();
    }

    @FXML
    private void onDirectoryCompare() throws IOException {
        Stage stage = (Stage) javafx.stage.Window.getWindows().filtered(Window::isShowing).get(0);
        stage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource(resourcesPath + "directory-compare-view.fxml"))), 960, 720));
    }

    @FXML
    private void onDuplicateFinder() throws IOException {
        Stage stage = (Stage) javafx.stage.Window.getWindows().filtered(Window::isShowing).get(0);
        stage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource(resourcesPath + "duplicate-finder-view.fxml"))), 960, 720));
    }
}
