module com.cof {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;
    requires java.smartcardio;

    opens com.cof to javafx.fxml;
    exports com.cof;
    opens com.cof.ui to javafx.fxml;
    exports com.cof.ui to javafx.graphics;

}