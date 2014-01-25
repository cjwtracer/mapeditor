package tool.mapeditor.actions;

/**
 * Interface defining the application's command IDs.
 * Key bindings can be defined for specific commands.
 * To associate an action with a command, use IAction.setActionDefinitionId(commandId).
 *
 * @see org.eclipse.jface.action.IAction#setActionDefinitionId(String)
 */
public interface ICommands {

	public static final String CMD_NEW = "new";
    public static final String CMD_OPEN = "open";
    public static final String CMD_SAVE = "save";
    public static final String CMD_EXIT = "exit";
    public static final String CMD_NEW_RESOURCE = "new.resource";
    public static final String CMD_NEW_LAYER = "new.layer";
    public static final String CMD_REMOVE_LAYER = "remove.layer";
    public static final String CMD_GRID_ALIGN = "grid.align";
    public static final String CMD_SELECT = "select";
    public static final String CMD_CELL_PROP = "cell.property";
    public static final String CMD_RECT_REGION = "rect.region";
    public static final String CMD_VERTEX_ADD = "vertex.add";
    public static final String CMD_VERTEX_DEL = "vertex.del";
    public static final String CMD_SHAPE_REGION = "shape.region";
    public static final String CMD_REGION_ATTRIBUTE = "region.attribute";
    public static final String CMD_MAP_COLLISION = "map.collision";
    public static final String CMD_IMG_SPLITE = "image.splite";
    public static final String CMD_MAP_BACKGROUND = "map.background";
    public static final String CMD_EXPORT_DATA = "export.data";
    public static final String CMD_PLAY_ANIMATION = "play.animation";
	public static final String CMD_EXPORT_NPC = "export.npc";
	public static final String CMD_MERGE_NPC = "merge.npc";
	public static final String CMD_IMPORT_NPC_MODEL = "import.npc.model";
	public static final String CMD_MAP_PROPERTY = "map.property";
	public static final String CMD_EXPORT_MAP = "export.map";
	public static final String CMD_IMPORT_MAP = "import.map";
	public static final String CMD_RESET_ID = "reset.id";
	public static final String CMD_DELETE_SOURCE = "delete.source";
	public static final String CMD_OPEN_EXPORT_FOLDER = "open.export";
	public static final String CMD_PREFERANCE = "metadata";
	public static final String CMD_ADD_RESOURCE = "add.resource";
	public static final String CMD_RES_PROPERTY = "resource.property";
	public static final String CMD_RESOURCEVIEW_RESIZE = "resourceview.resize";
}
