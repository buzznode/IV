package iv;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Bradley W. Duderstadt
 * @version 1.0
 */

public class IV extends Application {
    private MainController mainCtrl;
    
    /**
     * Standard main method.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This method starts the application by initializing the first
     * screen, setting the controller and showing the initial Stage.
     * @param stage
     * @throws Exception 
     */
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("IVApp");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
        mainCtrl = new MainController();
        loader.setController(mainCtrl);
        stage.setScene(new Scene(loader.load()));
        mainCtrl.setPrimaryStage(stage);
        stage.show();
    }
}
