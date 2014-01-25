package tool.mapeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import tool.resourcemanager.Resources;

public class ResourceLabel extends Label{
	Drawable drawable;
	private boolean selected;
	private Image buffer;
	private Rectangle rect;
	private boolean drawBuf = true;

	public ResourceLabel(Composite parent, Drawable drawable) {
		super(parent, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
		if(drawable == null)
			throw new IllegalArgumentException("Drawable can't be null!");
		this.drawable = drawable;
		rect = drawable.getBounds();
		buffer = new Image(Display.getCurrent(), rect.width, rect.height);
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				paint(e.gc);
			}
		});
	}

	void paint(GC gc) {
		Rectangle s = getBounds();
//		gc.setBackground(Resources.BLUE);
//		gc.fillRectangle(s);
		if(drawBuf){
			GC bufGC = new GC(buffer);
			bufGC.setBackground(getBackground());
			bufGC.fillRectangle(rect);
			drawable.paint(bufGC, rect.x, rect.y, rect.width, rect.height, 0);
			if(selected){
				bufGC.setAlpha(60);
				bufGC.setBackground(Resources.BLUE);
				bufGC.fillRectangle(buffer.getBounds());
				bufGC.setAlpha(255);
			}
			drawBuf = false;
			bufGC.dispose();
		}
		gc.drawImage(buffer, (s.width - rect.width) / 2, (s.height - rect.height) / 2);
	}
	
	public void setSelected(boolean selection){
		selected = selection;
		drawBuf = true;
		redraw();
	}
	
	public boolean isSelected(){
		return selected;
	}

	@Override
	protected void checkSubclass() {
	}
	
	public void finalize() throws Throwable{
		buffer.dispose();
		super.finalize();
	}

}
