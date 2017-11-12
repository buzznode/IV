package iv;

import java.io.PrintWriter;
import java.io.StringWriter;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * @author Bradley W. Duderstadt
 * @version 1.0
 */

public class IVException extends Exception {
    private boolean die;
    private String content;
    private String header;
    private String title;
    
    public IVException() {
        this.die = false;
        this.title = "Exception Dialog";
        this.header = "Caught Exception";
        this.content = null;
    }
    
    public boolean getDie() {
        return this.die;
    }
    
    public String getContent() {
        return this.content;
    }
    
    public String getHeader() {
        return this.header;
    }
    
    private String getSTrace() {
        StringWriter trace = new StringWriter();
        this.printStackTrace(new PrintWriter(trace));
        return trace.toString();
    }

    public String getTitle() {
        return this.title;
    }
    
    public void handler() {
        String content;
        
        content = this.content == null ? "No error content supplied" : this.content;
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(this.title);
        alert.setHeaderText(this.header);
        alert.setContentText(content);
        alert.showAndWait();
        
        if (this.die) {
            Platform.exit();
            System.exit(0);
        }
    }
    
    public void setDie(boolean die) {
        this.die = die;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public void setHeader(String header) {
        this.header = header;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
}
