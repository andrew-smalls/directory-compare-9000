package fileexplorer.fileexplorer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FileExplorerApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("File explorer (Teemu version)!");

        FXMLLoader fxmlLoader = new FXMLLoader(FileExplorerApp.class.getResource("menu-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}