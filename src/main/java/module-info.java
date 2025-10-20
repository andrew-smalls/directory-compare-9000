module fileexplorer.fileexplorer {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens fileexplorer.fileexplorer to javafx.fxml;
    exports fileexplorer.fileexplorer;
}