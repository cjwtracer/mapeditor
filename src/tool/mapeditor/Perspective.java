package tool.mapeditor;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		IFolderLayout folderLeft = layout.createFolder("left", IPageLayout.LEFT, 0.75f, editArea);
		folderLeft.addView(MapView.ID);
		layout.getViewLayout(MapView.ID).setCloseable(false);
		IFolderLayout folderRightTop = layout.createFolder("righttoppane", IPageLayout.TOP, 0.25f, editArea);
		IFolderLayout folderRightBottom = layout.createFolder("rightbottompane", IPageLayout.BOTTOM, 0.75f, editArea);
		folderRightTop.addView(LayerView.ID);
		folderRightBottom.addView(ResourceView.ID);
		layout.getViewLayout(LayerView.ID).setCloseable(false);
		layout.getViewLayout(ResourceView.ID).setCloseable(false);
	}

}
