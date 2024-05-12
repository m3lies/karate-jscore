module ch.sku.karatescore {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires static lombok;

    opens ch.sku.karatescore to javafx.fxml;
    exports ch.sku.karatescore;
    exports ch.sku.karatescore.commons;
    exports ch.sku.karatescore.components;
    exports ch.sku.karatescore.services;
    exports ch.sku.karatescore.model;
    opens ch.sku.karatescore.components to javafx.fxml;
}