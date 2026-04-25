module edu.okcu.tablefx {
    requires javafx.controls;
    requires javafx.fxml;
    opens edu.okcu.tablefx to javafx.fxml;
    exports edu.okcu.tablefx;
}

