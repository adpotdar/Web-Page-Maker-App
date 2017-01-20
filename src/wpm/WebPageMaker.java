package wpm;

import java.util.Locale;
import static javafx.application.Application.launch;
import saf.components.AppComponentsBuilder;
import saf.components.AppDataComponent;
import saf.components.AppFileComponent;
import saf.AppTemplate;
import saf.components.AppWorkspaceComponent;
import wpm.data.DataManager;
import wpm.file.FileManager;
import wpm.gui.Workspace;

/**
 * This class is the application class for our Web Page Maker program. 
 */
public class WebPageMaker extends AppTemplate {
    /**
     * This builder provides methods for properly setting up all
     * the custom objects needed to run this application. 
     */
    @Override
    public AppComponentsBuilder makeAppBuilderHook() {
	return new AppComponentsBuilder() {
	    /**
	     * Makes the returns the data component for the app.
	     * 
	     * @return The component that will manage all data
	     * updating for this application.
	     * 
	     * @throws Exception An exception may be thrown should
	     * data updating fail, which can then be customly handled.
	     */
	    @Override
	    public AppDataComponent buildDataComponent() throws Exception {
		return new DataManager(WebPageMaker.this);
	    }

	    /**
	     * Makes the returns the file component for the app.
	     * 
	     * @return The component that will manage all file I/O
	     * for this application.
	     * 
	     * @throws Exception An exception may be thrown should
	     * file I/O updating fail, which can then be customly handled.
	     */
	    @Override
	    public AppFileComponent buildFileComponent() throws Exception {
		return new FileManager();
	    }

	    /**
	     * Makes the returns the workspace component for the app.
	     * 
	     * @return The component that serve as the workspace region of
	     * the User Interface, managing all controls therein.
	     * 
	     * @throws Exception An exception may be thrown should
	     * UI updating fail, which can then be customly handled.
	     */
	    @Override
	    public AppWorkspaceComponent buildWorkspaceComponent() throws Exception {
		return new Workspace(WebPageMaker.this);
	    }
	};
    }
    
    /**
     * This is where program execution begins.
     */
    public static void main(String[] args) {
        
	Locale.setDefault(Locale.US);
	launch(args);
        
    }
}
