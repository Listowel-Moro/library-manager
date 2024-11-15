module listo.librarymanager {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires jbcrypt;

    opens listo.librarymanager to javafx.fxml;
    opens listo.librarymanager.controllers to javafx.fxml;
    opens listo.librarymanager.models to javafx.base;
    exports listo.librarymanager;
}