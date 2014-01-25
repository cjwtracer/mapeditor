package tool.mapeditor.actions;

import org.eclipse.jface.action.Action;

import tool.mapeditor.Activator;
import tool.mapeditor.application.MainApplication;
import tool.resourcemanager.ResourceManager;

/**
 * Base action of all action defined in the editor.
 * @author caijw
 *
 */
public class AbstractAction extends Action {
	/**
	 * The hook to the main application.
	 */
	MainApplication mainApp = MainApplication.getInstance();
	
	private AbstractAction(String id){
		super();
		if(id == null)
			throw new IllegalArgumentException("ID can't be null!");
		setId(id);
		setActionDefinitionId(id);
	}
	
	public AbstractAction(String id, String text, int style){
		super(text, style);
		if(id == null)
			throw new IllegalArgumentException("ID can't be null!");
		setId(id);
		setActionDefinitionId(id);
		setText(text);
	}
	
	public AbstractAction(String id, String text){
		this(id);
		setText(text);
	}
	
	public AbstractAction(String id, String text, String image){
		this(id, text, AS_PUSH_BUTTON);
		if(image != null)
			setImageDescriptor(Activator.getImageDescriptor(image));
	}
	
	public AbstractAction(String id, String text, String image, int style){
		this(id, text, style);
		if(image != null)
			setImageDescriptor(ResourceManager.getImageDescriptor(AbstractAction.class, image));
	}

}
