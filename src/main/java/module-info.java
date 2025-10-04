module br.com.so.gerenciamentoprocessos {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;


    opens br.com.so.gerenciamentoprocessos to javafx.fxml;
    exports br.com.so.gerenciamentoprocessos;
}