module com.cof {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.smartcardio;
    requires java.sql;

    opens com.cof to javafx.fxml;
    exports com.cof;
    opens com.cof.ui to javafx.fxml;
    exports com.cof.ui to javafx.graphics;

}