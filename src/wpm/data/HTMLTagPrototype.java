package wpm.data;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * This class represents a single element (i.e. tag) in an HTML tree.
 */
public class HTMLTagPrototype implements Comparable<HTMLTagPrototype>, Cloneable {
    // THESE CONSTANTS HELP US SETUP A MINIMAL PAGE
    public static final String TAG_HTML = "html";
    public static final String TAG_HEAD = "head";
    public static final String TAG_TITLE = "title";
    public static final String TAG_LINK = "link";
    public static final String TAG_BODY = "body";
    public static final String TAG_TEXT = "Text";
    public static final String ATT_REL = "rel";
    public static final String REL_STYLESHEET = "stylesheet";
    public static final String ATT_TYPE = "type";
    public static final String TYPE_TEXT_CSS = "text/css";
    public static final String ATT_HREF = "href";
    public static final String HREF_HOME = "./css/home.css";
    
   
    // EACH TAG HAS A NAME,
    String tagName;
    
    // THE SET OF ATTRIBUTES FOR THIS TAG, MEANING NAME, VALUE PAIRS
    HashMap<String, String> attributes;
    
    // THIS IS USEFUL WHEN SAVING THE TREE
    int nodeIndex;
    int parentIndex;
    
    // HELPS KEEP TRACK OF WHERE A TAG CAN BE ADDED TO THE PAGE
    ArrayList<String> legalParents;
    
    // HELPS DURING PAGE GENERATION TO DETERMINE IF THIS PARTICULAR
    // TAG TYPE REQUIRES A CLOSING TAG OR NOT. 
    boolean hasClosingTag;
    
    /**
     * Constructor that initializes the minimal requirements, meaning
     * the name of the tag and whether it has a closing tag.
     */
    public HTMLTagPrototype(String initTagName, boolean initHasClosingTag) {
	// KEEP THE DATA
	tagName = initTagName;
	hasClosingTag = initHasClosingTag;
        
	// AND INIT THE DATA STRUCTURES TO BE FILLED IN LATER
	attributes = new HashMap();
	legalParents = new ArrayList();
        
        
    }
    
    /**
     * Accessor method for testing whether or not this
     * tag has a closing tag.
     */
    public boolean hasClosingTag() {
	return hasClosingTag;
    }

    /**
     * Accessor method for getting the name of this tag.
     */
    public String getTagName() {
	return tagName;
    }

    /**
     * Accessor method for getting the node index for this tag in
     * the HTML page tree.
     */
    public int getNodeIndex() {
	return nodeIndex;
    }

    /**
     * Mutator method for setting the node index for this tag.
     */
    public void setNodeIndex(int initNodeIndex) {
	nodeIndex = initNodeIndex;
    }
    
    /**
     * Accessor method for getting the parent index for this tag in
     * the HTML page tree.

     */
    public int getParentIndex() {
	return parentIndex;
    }

   /**
     * Mutator method for setting the parent index for this tag.

     */
    public void setParentIndex(int initParentIndex) {
	parentIndex = initParentIndex;
    }

    /**
     * Adds the name, value pair to our attributes for this tag.
     */
    public void addAttribute(String name, String value) {
	attributes.put(name, value);
    }
    
    /**
     * Accessor method for getting an attribute for this tag.
     */
    public String getAttribute(String name) {
	return attributes.get(name);
    }
    
    /**
     * Accessor method for getting all of this tag's attributes
     */
    public HashMap<String,String> getAttributes() { 
	return attributes;
    }
    
    /**
     * This method tests to see if the testParent argument is
     * a legal parent tag for this type of tag.
     */
    public boolean isLegalParent(String testParent) {
	return legalParents.contains(testParent);
    }

    /**
     * This method adds the parent argument to the list of
     * legal parent tags for this element.
     */
    public void addLegalParent(String parent) {
	legalParents.add(parent);
    }

    /**
     * Accessor method for getting the full set of legal
     * parents for this element.
     */
    public ArrayList<String> getLegalParents() {
	return legalParents;
    }
    
    /**
     * This method generates and returns a textual representation
     * of this tag, which we'll only use for displaying tags inside
     * the tree.
     */
    @Override
    public String toString() {
	return "<" + tagName + ">";
    }
    
    public String getHTMLDesc() {
        String desc = "<" + tagName;
        if(tagName.equals("link")) {
            desc += " rel="+"\""+getAttribute("rel")+"\" " + 
                    "href="+"\""+getAttribute("href")+"\" " +
                    "type="+"\""+getAttribute("type")+"\">";
        } else if(tagName.equals("img")) {
            desc += " src="+"\""+getAttribute("src")+"\" " + 
                    "alt="+"\""+getAttribute("alt")+"\">";
        } else if(tagName.equals("a")) {
            desc += " href="+"\""+getAttribute("href")+"\">";
        } else if(tagName.equals("p")) {
            desc += " class="+"\""+getAttribute("class")+"\">";
        } else if(tagName.equals("br")||tagName.equals("title")||
                  tagName.equals("body")||tagName.equals("head")||
                  tagName.equals("html")) {
            desc += ">";
        } else if(tagName.equals("Text")) {
            desc = getAttribute("text");
        } else {
            desc += " class="+"\""+getAttribute("class")+"\" " + 
                    " id="+"\""+getAttribute("id")+"\">";
        }
        return desc + "\n";
    }
    /**
     * This method generate and returns clone textual representation 
     * of this tag, which we'll only use for displaying tags inside 
     * the tree
     */
    public String toClosedString(){
        String closingTag = "</" + tagName + ">";
        return closingTag;
    }
    
    /**
     * Used for sorting tags, this method is used for comparing
     * one tag to another for that purpose.
     */
    @Override
    public int compareTo(HTMLTagPrototype otherTag) {
	return tagName.compareTo(otherTag.getTagName());
    }

    /**
     * This class employs the prototype design pattern in that
     * whenever we wish to add a new tag to our tree, we start by
     * cloning a similar one. 
     */
    @Override
    public HTMLTagPrototype clone() {
	// MAKE A NEW OBJECT
	HTMLTagPrototype clonedTag = new HTMLTagPrototype(tagName, hasClosingTag);
	
	// MAKE SURE IT HAS THE SAME LEGAL PARENTS
	clonedTag.legalParents = legalParents;
	
	// WE WANT ALL THE SAME ATTRIBUTS, BUT NO VALUES
	for (String attributeName : attributes.keySet()) {
	    clonedTag.addAttribute(attributeName, "");
	}
	// AND RETURN THE CLONED OBJECT
	return clonedTag;
    }
}