package tool.mapeditor;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.opengl.renderer.Renderer;

import tool.resourcemanager.Resources;

public abstract class ScrollImageCanvas extends GLCanvas{
	protected static final Color DEFAULT_FG_COLOR = Resources.WHITE;
	
	protected static Graphics graphics;
	
	final Runnable run;
	
	private Color backgroundColor;
	private Color foregroundColor;
	
	protected boolean erroring = false;
	/**
	 * related to the canvas
	 */
	protected final Point mousePos = new Point(0, 0);
	/**
	 * current abslute position of mouse pointer, 
	 * related to the buffer image
	 */
	protected final Point absPos = new Point(0, 0);
	/**
	 * the length mouse moved, related to the origin image
	 */
	protected final Point moveLength = new Point(0, 0);
	/**
	 * The position of the image left-top corner related to
	 * the client area.
	 */
	protected final Point origin = new Point(0, 0);
	/**
	 * Mouse position related to the canvas.
	 */
	protected final Point start = new Point(0, 0);
	/**
	 * initial mouse position related to the origin image 
	 * when press the mouse
	 */
	protected final Point startPoint = new Point(0, 0);
	protected final Rectangle rectZoom = new Rectangle(0, 0, 0, 0);
	/**
	 * move-selection rectangle area
	 */
	protected final Rectangle rectSelect = new Rectangle(0, 0, 0, 0);

	protected Rectangle rectCanvas = getClientArea();
	protected Rectangle rectReal;
	protected int zoom = 1;
	/**
	 * left button down
	 */
	protected boolean mouseDownL;
	/**
	 * right button down
	 */
	protected boolean mouseDownR;
	
	protected final ScrollBar hBar = getHorizontalBar();
	protected final ScrollBar vBar = getVerticalBar();
	
	protected boolean ctrlDown;
	protected boolean shiftDown;
	/**
	 * original scroll bar selection when scrolling canvas
	 */
	protected int selectionX;
	protected int selectionY;
	/**
	 * flag mouse button pressed currently
	 */
	protected int button;
	/**
	 * drag mouse to scroll canvas
	 */
	protected boolean scroll;
	
	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ScrollImageCanvas(Composite parent, GLData data) {
		super(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE, data);
//		setBackground(Resources.BLACK);
		setCurrent();
		try {
			GLContext.useContext(this);
		} catch(LWJGLException e) {
			e.printStackTrace(); 
		}
		int bw = getBounds().width;
		int bh = getBounds().height;
		graphics = new Graphics(bw, bh);
		initGL(bw, bh);
		run = new Runnable(){
			public void run(){
				if(this != null){
					setCurrent();
					try {
						GLContext.useContext(ScrollImageCanvas.this);
					} catch(LWJGLException e) { 
						e.printStackTrace();
					}
					GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
					paint();
					swapBuffers();
				}
			}
		};
		// Scrollbar Events
		hBar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
					int hSelection = hBar.getSelection();
//					int destX = -hSelection - origin.x;
//					scroll(destX, 0, 0, 0, rectZoom.width, rectZoom.height, false);
					origin.x = -hSelection;
					redraw();
			}
		});

		vBar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
					int vSelection = vBar.getSelection();
//					int destY = -vSelection - origin.y;
//					scroll(0, destY, 0, 0, rectZoom.width, rectZoom.height, false);
					origin.y = -vSelection;
					redraw();
			}
		});

		// Resize
		addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				onResize();
				redraw();
			}
		});
		
		// Mouse
		addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				onMouseMove(e);
			}
		});
		
		addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				onMouseDown(e);
			}
			
			public void mouseUp(MouseEvent e) {
				onMouseUp(e);
			}
			
			public void mouseDoubleClick(MouseEvent e) {
				onMouseDoubleClick(e);
			}
		});
		
		// Keyboard
		addKeyListener(new KeyListener() {
			
			public void keyReleased(KeyEvent e) {
				onKeyReleased(e);
			}
			
			public void keyPressed(KeyEvent e) {
				onKeyPressed(e);
			}
		});
		
		// Dispose
		addDisposeListener(new DisposeListener() {			
			public void widgetDisposed(DisposeEvent e) {
				if (backgroundColor != null) {
					backgroundColor.dispose();
				}
				if (foregroundColor != null) {
					foregroundColor.dispose();
				}
			}
		});
		
		addListener(SWT.Paint, new Listener(){
			public void handleEvent(Event e){
				run.run();
			}
		});
		Display.getDefault().asyncExec(run);
	}
	
	/**
	 * Initialize opengl configures.
	 * @param width
	 * @param height
	 */
	protected static void initGL(int width, int height) {
        Renderer.get().initDisplay(width, height);
        graphics.setDimensions(width, height);
        
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glViewport(0, 0, width, height);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glClearDepth(1.0);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, height, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
	}
	
	public Graphics getGraphics(){
		return graphics;
	}

	protected void paint() {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}

	protected void checkSubclass() {
		
	}

	protected void onMouseMove(MouseEvent e) {
		mousePos.x = e.x;
		mousePos.y = e.y;
		absPos.x = (e.x - origin.x) / zoom;
		absPos.y = (e.y - origin.y) / zoom;
		moveLength.x = absPos.x - startPoint.x;
		moveLength.y = absPos.y - startPoint.y;
		
		if (mouseDownL) {
			rectSelect.x = Math.max(0, startPoint.x + Math.min(0, moveLength.x));
			rectSelect.y = Math.max(0, startPoint.y + Math.min(0, moveLength.y));
			rectSelect.width = Math.abs(moveLength.x);
			rectSelect.height = Math.abs(moveLength.y);
		}
	}
	
	protected void onKeyPressed(KeyEvent e) {
		if(e.keyCode == SWT.CONTROL) ctrlDown = true;
		else if(e.keyCode == SWT.SHIFT) shiftDown = true;
	}

	protected void onKeyReleased(KeyEvent e) {
		if(e.keyCode == SWT.CONTROL) ctrlDown = false;
		else if(e.keyCode == SWT.SHIFT) shiftDown = false;
	}
	
	protected void onMouseDown(MouseEvent e) {
		if(e.button == 1){
			mouseDownL = true;
		}else if(e.button == 3){
			mouseDownR = true;
		}
		button = e.button;
		start.x = e.x;
		start.y = e.y;
		moveLength.x = 0;
		moveLength.y = 0;
		startPoint.x = (e.x - origin.x) / zoom;
		startPoint.y = (e.y - origin.y) / zoom;
		rectSelect.x = startPoint.x;
		rectSelect.y = startPoint.y;
		rectSelect.width = 0;
		rectSelect.height = 0;
		selectionX = hBar.getSelection();
		selectionY = vBar.getSelection();
	}
	
	protected void onMouseUp(MouseEvent e) {
		if(e.button == 1){
			mouseDownL = false;
		}else if(e.button == 3){
			mouseDownR = false;
		}
		button = 0;
		scroll = false;
	}
	
	protected void onMouseDoubleClick(MouseEvent e) {

	}

	/**
	 * Resize the client area to enable full display of all the contents.
	 */
	private void resize() {
		int hPage = rectZoom.width - rectCanvas.width;
		int vPage = rectZoom.height - rectCanvas.height;
		hBar.setVisible(hPage > 0);
		vBar.setVisible(vPage > 0);
		hBar.setThumb(Math.min(rectZoom.width, rectCanvas.width));
		vBar.setThumb(Math.min(rectZoom.height, rectCanvas.height));
		int hs = hBar.getSelection(), vs = vBar.getSelection();//get selection first and set maximum later!!
		hBar.setMaximum(rectZoom.width);
		vBar.setMaximum(rectZoom.height);
	}

	/**
	 * Resize client area of the canvas and set the visible
	 * part of the displayed contents.
	 * 
	 * @param zoomFormer
	 * 					The former scaling.
	 */
	private void onResize(int zoomFormer) {
		if(zoomFormer < 1) return;
		
		rectCanvas = getClientArea();
		
		int hPage = rectZoom.width - rectCanvas.width;
		int vPage = rectZoom.height - rectCanvas.height;
		hBar.setVisible(hPage > 0);
		vBar.setVisible(vPage > 0);
		hBar.setThumb(Math.min(rectZoom.width, rectCanvas.width));
		vBar.setThumb(Math.min(rectZoom.height, rectCanvas.height));
		
		float zoomRatio = zoom / (float)zoomFormer;
		int hs = hBar.getSelection(), vs = vBar.getSelection();//get selection first and set maximum later!!
		hBar.setMaximum(rectZoom.width);
		vBar.setMaximum(rectZoom.height);
		int hSelection = (int)((hs + rectCanvas.width / 2) * zoomRatio - rectCanvas.width / 2);
		int vSelection = (int)((vs + rectCanvas.height / 2) * zoomRatio - rectCanvas.height / 2);
		
		if(hSelection < 0) hSelection = 0;
		if (hSelection > hPage) {
			hSelection = Math.max(0, hPage);
		}
		hBar.setSelection(hSelection);
		origin.x = hPage > 0 ? -hSelection : 0;
		if(vSelection < 0) vSelection = 0;
		if (vSelection > vPage) {
			vSelection = Math.max(0, vPage);
		}
		origin.y = vPage > 0 ? -vSelection : 0;
		vBar.setSelection(vSelection);
	}
	
	/**
	 * @deprecated
	 */
	public void zoomIn() {
		if(rectReal == null) return;
		zoom++;
		if (zoom > 10){
			zoom = 10;
			return;
		}
		rectZoom.width = rectReal.width * zoom;
		rectZoom.height = rectReal.height * zoom;
		onResize(zoom - 1);
	}
	
	/**
	 * @deprecated
	 */
	public void zoomOut() {
		if(rectReal == null) return;
		zoom--;
		if (zoom < 1){
			zoom = 1;
			return;
		}
		rectZoom.width = rectReal.width * zoom;
		rectZoom.height = rectReal.height * zoom;
		onResize(zoom + 1);
	}
	
	protected void setBackgroundColor(int color) {
		setBackgroundColor(new RGB(color & 0xff, (color >> 8) & 0xff, (color >> 16) & 0xff));
	}
	
	public void setBackgroundColor(RGB rgb) {
		if (backgroundColor != null) {
			backgroundColor.dispose();
		}
		if(foregroundColor != null){
			foregroundColor.dispose();
		}
		backgroundColor = new Color(getDisplay(), rgb);
		foregroundColor = new Color(getDisplay(), 255 - rgb.red, 255 - rgb.green, 255 - rgb.blue);
		redraw();
	}
	
	/**
	 * Set the client area to the given size.
	 * @param w
	 * @param h
	 */
	protected void setContent(int w, int h){
		rectReal = new Rectangle(0, 0, w, h);
		computeZoomSize();
		resize();
		origin.x = 0;
		origin.y = 0;
		hBar.setSelection(0);
		vBar.setSelection(0);
	}
	
	private void computeZoomSize() {
		if(rectReal == null)
			return;
		rectZoom.width = rectReal.width * zoom;
		rectZoom.height = rectReal.height * zoom;
	}
	
	/**
	 * @deprecated
	 * Set the scaling of displayed contents.
	 * 
	 * @param zoom
	 * 			The multiple of scaling.
	 */
	public void setZoom(int zoom){
		computeZoomSize();
		this.zoom = zoom;
	}
	
	public int getZoom() {
		return zoom;
	}

	/**
	 * Scroll the content of the canvas to the specified location represented
	 * by the left-top point.
	 * 
	 * @param x
	 * @param y
	 */
	protected void scrollCanvas(int x, int y){
		int hSelection = Math.min(Math.max(x, 0), rectZoom.width - rectCanvas.width);
		int vSelection = Math.min(Math.max(y, 0), rectZoom.height - rectCanvas.height);
		origin.x = Math.min(-hSelection, 0);
		origin.y = Math.min(-vSelection, 0);
		hBar.setSelection(hSelection);
		vBar.setSelection(vSelection);
	}
	
	protected void dragScreen(int mouseX, int mouseY){
		scrollCanvas(selectionX + start.x - mouseX, selectionY + start.y - mouseY);
	}
	
	protected void onResize(){
		rectCanvas = getClientArea();
		resize();
		initGL(rectCanvas.width, rectCanvas.height);
	}

}
