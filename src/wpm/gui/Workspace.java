package wpm.gui;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import saf.ui.AppYesNoCancelDialogSingleton;
import saf.ui.AppMessageDialogSingleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import properties_manager.PropertiesManager;
import saf.ui.AppGUI;
import wpm.data.HTMLTagPrototype;
import saf.AppTemplate;
import saf.components.AppWorkspaceComponent;
import wpm.PropertyType;
import static wpm.PropertyType.TEMP_PAGE_LOAD_ERROR_MESSAGE;
import static wpm.PropertyType.TEMP_PAGE_LOAD_ERROR_TITLE;
import static wpm.PropertyType.UPDATE_ERROR_MESSAGE;
import static wpm.PropertyType.UPDATE_ERROR_TITLE;
import wpm.WebPageMaker;
import wpm.controller.PageEditController;
import wpm.data.DataManager;
import static wpm.data.HTMLTagPrototype.TAG_HTML;
import wpm.file.FileManager;
import static wpm.file.FileManager.TEMP_PAGE;

public class Workspace extends AppWorkspaceComponent {

    // THESE CONSTANTS ARE FOR TYING THE PRESENTATION STYLE OF
    // THIS Workspace'S COMPONENTS TO A STYLE SHEET THAT IT USES
    static final String CLASS_MAX_PANE = "max_pane";
    static final String CLASS_TAG_BUTTON = "tag_button";
    static final String EMPTY_TEXT = "";
    static final int BUTTON_TAG_WIDTH = 75;

    // THE APP
    AppTemplate app;

    // IT KNOWS THE GUI IT IS PLACED INSIDE
    AppGUI gui;

    // THIS HANDLES INTERACTIONS WITH PAGE EDITING CONTROLS
    PageEditController pageEditController;

    SplitPane workspaceSplitPane;

    // THESE ARE THE BUTTONS FOR ADDING AND REMOVING COMPONENTS
    BorderPane leftPane;
    Pane tagToolbar;
    ScrollPane tagToolbarScrollPane;
    ArrayList<Button> tagButtons;
    HashMap<String, HTMLTagPrototype> tags;

    // THIS IS THE TREE REPRESENTING THE DOM
    TreeView htmlTree;
    TreeItem<HTMLTagPrototype> htmlRoot;
    ScrollPane treeScrollPane;

    // AND FOR EDITING A TAG
    GridPane tagEditorPane;
    ScrollPane tagEditorScrollPane;
    Label tagEditorLabel;
    ArrayList<Label> tagPropertyLabels;
    ArrayList<TextField> tagPropertyTextFields;

    // THIS WILL CONTAIN BOTH THE TREE AND THE TREE EDITOR
    VBox editVBox;

    // THIS IS WHERE WE CAN VIEW THE WEB PAGE OR DIRECTLY EDIT THE CSS
    TabPane rightPane;
    WebView htmlView;
    WebEngine htmlEngine;
    TextArea cssEditor;

    // HERE ARE OUR DIALOGS
    AppMessageDialogSingleton messageDialog;
    AppYesNoCancelDialogSingleton yesNoCancelDialog;
    
    //THIS IS THE REMOVE BUTTON IN TAG TOOLBAR
    Button removeButton;

    /**
     * Constructor for initializing the workspace, note that this constructor
     * will fully setup the workspace user interface for use.
     */
    public Workspace(AppTemplate initApp) throws IOException {
	// KEEP THIS FOR LATER
	app = initApp;

	// KEEP THE GUI FOR LATER
	gui = app.getGUI();

	// THIS WILL PROVIDE US WITH OUR CUSTOM UI SETTINGS AND TEXT
	PropertiesManager propsSingleton = PropertiesManager.getPropertiesManager();

	// WE'LL ORGANIZE OUR WORKSPACE COMPONENTS USING A BORDER PANE
	workspace = new BorderPane();

	// FIRST THE LEFT HALF OF THE SPLIT PANE
	leftPane = new BorderPane();

	// THIS WILL MANAGE ALL EDITING EVENTS
	pageEditController = new PageEditController((WebPageMaker) app);
        
	// THIS IS THE TOP TOOLBAR
	tagToolbar = new FlowPane(Orientation.VERTICAL);
        tagToolbar.setPrefSize(100,100);
	tagToolbarScrollPane = new ScrollPane(tagToolbar);
	tagToolbarScrollPane.setFitToHeight(true);
	tagButtons = new ArrayList();
	tags = new HashMap();
        
	// LOAD ALL THE HTML TAG TYPES
	FileManager fileManager = (FileManager) app.getFileComponent();
	DataManager dataManager = (DataManager) app.getDataComponent();

	// AND NOW MAKE THE TREE
	htmlTree = new TreeView();
	treeScrollPane = new ScrollPane(htmlTree);
	htmlTree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	htmlTree.getSelectionModel().selectedItemProperty().addListener(e -> {
	    
            for(Button button: tagButtons){
                TreeItem selectedItem = (TreeItem) htmlTree.getSelectionModel().getSelectedItem();
                HTMLTagPrototype selectedTag = (HTMLTagPrototype) selectedItem.getValue();
                HTMLTagPrototype clickedTag = dataManager.getTag(button.getText());
                if(clickedTag.isLegalParent(selectedTag.getTagName())){
                    button.setDisable(false);
                }else{
                    button.setDisable(true);
                }
            }
            
            reloadWorkspace();
	});

	// NOW RESET THE TREE
	HTMLTagPrototype htmlTag = new HTMLTagPrototype(TAG_HTML, true);
	htmlRoot = new TreeItem(htmlTag);
	htmlTree.setRoot(htmlRoot);
	dataManager.setHTMLRoot(htmlRoot);
	dataManager.reset();
        
        // ADD THE REMOVE TAG BUTTON
        removeButton = new Button("X");
        removeButton.setMaxWidth(BUTTON_TAG_WIDTH);
	removeButton.setMinWidth(BUTTON_TAG_WIDTH);
        removeButton.setPrefWidth(BUTTON_TAG_WIDTH);
        tagToolbar.getChildren().add(removeButton);
        // INIT ITS EVENT HANDLER
	    removeButton.setOnAction(e -> {
		String removeButtonName = removeButton.getText();
		HTMLTagPrototype clickedTag = dataManager.getTag(removeButtonName);
		pageEditController.handleRemoveElementRequest();
	    });

        // USE THE LOADED TAG TYPES TO ADD BUTTONS
	for (HTMLTagPrototype tag : dataManager.getTags()) {
	    // MAKE THE BUTTON
	    Button tagButton = new Button(tag.getTagName());
	    tagButtons.add(tagButton);
	    tagButton.setMaxWidth(BUTTON_TAG_WIDTH);
	    tagButton.setMinWidth(BUTTON_TAG_WIDTH);
	    tagButton.setPrefWidth(BUTTON_TAG_WIDTH);
              tagButton.setLayoutX(250);
             tagButton.setLayoutY(220); 
	    tagToolbar.getChildren().add(tagButton);
                  
	    // INIT ITS EVENT HANDLER
	    tagButton.setOnAction(e -> {
		String tagName = tagButton.getText();
		HTMLTagPrototype clickedTag = dataManager.getTag(tagName);
		pageEditController.handleAddElementRequest(clickedTag);
	    });
	}

	// THE REGION FOR EDITING TAG PROPERTIES
	tagEditorPane = new GridPane();
	tagEditorScrollPane = new ScrollPane(tagEditorPane);
	tagEditorLabel = new Label("Tag Editor");
	tagPropertyLabels = new ArrayList();
	tagPropertyTextFields = new ArrayList();

	// PUT THEM IN THE LEFT
	leftPane.setLeft(tagToolbarScrollPane);
	leftPane.setCenter(treeScrollPane);
	leftPane.setBottom(tagEditorScrollPane);

	// NOW FOR THE RIGHT
	rightPane = new TabPane();
	htmlView = new WebView();
	htmlEngine = htmlView.getEngine();
	cssEditor = new TextArea();

	// PUT BOTH ITEMS IN THE TAB PANE
	Tab htmlTab = new Tab();
	htmlTab.setText("HTML");
	htmlTab.setContent(htmlView);

	// NOW FOR THE CSS
	Tab cssTab = new Tab();
	cssTab.setText("CSS");
	cssTab.setContent(cssEditor);
	rightPane.getTabs().add(htmlTab);
	rightPane.getTabs().add(cssTab);

	// SETUP THE RESPONSE TO CSS EDITING
	cssEditor.textProperty().addListener(e -> {
	    pageEditController.handleCSSEditing(cssEditor.getText());
	});

	// AND NOW PUT IT IN THE WORKSPACE
	workspaceSplitPane = new SplitPane();
	workspaceSplitPane.getItems().add(leftPane);
	workspaceSplitPane.getItems().add(rightPane);

	// AND FINALLY, LET'S MAKE THE SPLIT PANE THE WORKSPACE
	workspace = new Pane();
	workspace.getChildren().add(workspaceSplitPane);

        // NOTE THAT WE HAVE NOT PUT THE WORKSPACE INTO THE WINDOW,
	// THAT WILL BE DONE WHEN THE USER EITHER CREATES A NEW
	// COURSE OR LOADS AN EXISTING ONE FOR EDITING
	workspaceActivated = false;

	// MAKE SURE THE FILE MANAGER HAS THE ROOT AND THEN
	// EXPORT THE SITE TO THE temp DIRECTORY. THEN, LOAD
	// IT INTO THE WEB ENGINE
	dataManager.setHTMLRoot(htmlRoot);
	fileManager.exportData(dataManager, TEMP_PAGE);
	loadTempPage();
    }

    /**
     * Accessor method for getting the html engine, which is tied to the page
     * display.
     */
    public WebEngine getHTMLEngine() {
	return htmlEngine;
    }

    /**
     * Accessor method for getting the html tree, which contains all the tags
     * for the page being edited.
     */
    public TreeView getHTMLTree() {
	return htmlTree;
    }

    /**
     * Accessor method for getting the root node of the html tree. Through that
     * node one can access the full DOM.
    */
    public TreeItem getHTMLRoot() {
	return htmlRoot;
    }

    /**
     * Mutator method for setting the root node for the html tree.
    */
    public void setHTMLRoot(TreeItem initRoot) {
	htmlTree.setRoot(initRoot);
	htmlRoot = initRoot;
    }

    /**
     * This function specifies the CSS style classes for all the UI components
     * known at the time the workspace is initially constructed. 
     */
    @Override
    public void initStyle() {
	
	tagToolbar.getStyleClass().add(CLASS_BORDERED_PANE);
        removeButton.getStyleClass().add(CLASS_TAG_BUTTON);
	for (Button b : tagButtons) {
	    b.getStyleClass().add(CLASS_TAG_BUTTON);
	}
	leftPane.getStyleClass().add(CLASS_MAX_PANE);
	treeScrollPane.getStyleClass().add(CLASS_MAX_PANE);
	tagEditorLabel.getStyleClass().add(CLASS_HEADING_LABEL);
    }

    /**
     * This function reloads all the controls for editing tag attributes into
     * the workspace.
     */
    @Override
    public void reloadWorkspace() {
	try {
	    // WE DON'T WANT TO RESPOND TO EVENTS FORCED BY
	    // OUR INITIALIZATION SELECTIONS
	    pageEditController.enable(false);

	    // FIRST CLEAR OUT THE OLD STUFF
	    tagPropertyLabels.clear();
	    tagPropertyTextFields.clear();
	    tagEditorPane.getChildren().clear();

	    // FIRST ADD THE LABEL
	    tagEditorPane.add(tagEditorLabel, 0, 0, 2, 1);

	    // THEN LOAD IN ALL THE NEW STUFF
	    TreeItem selectedItem = (TreeItem) htmlTree.getSelectionModel().getSelectedItem();
	    if (selectedItem != null) {
		HTMLTagPrototype selectedTag = (HTMLTagPrototype) selectedItem.getValue();
		HashMap<String, String> attributes = selectedTag.getAttributes();
		Collection<String> keys = attributes.keySet();
		int row = 1;
		for (String attributeName : keys) {
		    String attributeValue = selectedTag.getAttribute(attributeName);
		    Label attributeLabel = new Label(attributeName + ": ");
		    attributeLabel.getStyleClass().add(CLASS_PROMPT_LABEL);
		    TextField attributeTextField = new TextField(attributeValue);
		    attributeTextField.getStyleClass().add(CLASS_PROMPT_TEXT_FIELD);
		    tagEditorPane.add(attributeLabel, 0, row);
		    tagEditorPane.add(attributeTextField, 1, row);
		    attributeTextField.textProperty().addListener(e -> {
			// UPDATE THE TEMP SITE AS WE TYPE ATTRIBUTE VALUES
			pageEditController.handleAttributeUpdate(selectedTag, attributeName, attributeTextField.getText());
		    });
		    row++;
		}
	    }

	    // LOAD THE CSS
	    DataManager dataManager = (DataManager) app.getDataComponent();
	    cssEditor.setText(dataManager.getCSSText());

	    // THEN FORCE THE CHANGES TO THE TEMP HTML PAGE
	    FileManager fileManager = (FileManager) app.getFileComponent();
	    fileManager.exportData(dataManager, TEMP_PAGE);

	    pageEditController.enable(true);
	} catch (Exception e) {
	    AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
	    PropertiesManager props = PropertiesManager.getPropertiesManager();
	    dialog.show(props.getProperty(UPDATE_ERROR_TITLE), props.getProperty(UPDATE_ERROR_MESSAGE));
	}
    }

    /**
     * This function loads the temp page into the web view.
     */
    public void loadTempPage() {
	String urlPath = TEMP_PAGE;
	File webPageFile = new File(urlPath);
	try {
	    URL pageURL = webPageFile.toURI().toURL();
	    String pagePath = pageURL.toString();
	    htmlEngine.load(pagePath);
	} catch (MalformedURLException murle) {
	    PropertiesManager props = PropertiesManager.getPropertiesManager();
	    AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
	    dialog.show(props.getProperty(TEMP_PAGE_LOAD_ERROR_TITLE), props.getProperty(TEMP_PAGE_LOAD_ERROR_MESSAGE));
	}
    }
}
