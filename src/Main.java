import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Main extends JPanel implements MouseListener, MouseWheelListener, KeyListener, WindowFocusListener {
	
	static double guideScale = 100;
	
	static double fieldOfView = 70;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	static JFrame frame = new JFrame("3D Test");
	
	
	static Image icon = null;
	
	static Canvas mainCanvas;
	static BufferedImage bufferImage = null;
	static BufferedImage shaderBufferImage = null;
	
	
	static double aspectRatio = 1;
	static Orientation3d cameraOrientation = new Orientation3d();
	static boolean didStart = false;
	static boolean freeLook = false;
	static double halfPi = Math.PI/2;
	static double drawLimit = halfPi;
	static double fovPercent = fieldOfView/180;
	static double frameTime = 0;
	static long frameCt;
	static boolean hasStarted = false;
	static Runtime runtime = Runtime.getRuntime();
	static int black = Color.black.getRGB();
	static boolean showFps = true;
    static String storagePath = System.getenv("APPDATA") + File.separator + "3dTestGame" + File.separator;
	
	
	static List<Button> buttonList = new ArrayList<Button>(); // Name : (x1, y1 ; x2, y2)
	static Image pauseBufferImage;
	static boolean isPaused = false;
	static enum MenuType {
    	Main,
    	Pause,
    	Options,
    	ShipSelect
    }
	static MenuType currentMenuType = MenuType.Main;
	static MenuType previousMenuType = MenuType.Main;
	
	
	static long frameStartTime = 1;
	static double fps = 60;
	
	static Map<Integer, Boolean> keyInput = new HashMap<Integer, Boolean>();
	static Map<Vector3d, Color> skybox = new HashMap<Vector3d, Color>();
	
	static Object3d camera = new Object3d();
	static Object3d testShip = new Object3d(new Model3d("model/basicShip", "obj", Color.black), "ship");
	static Object3d testCockpit = new Object3d(new Model3d("model/cockpitInterior", "obj", Color.black), "playerShip");
	
	public static void main(String[] args) {
	        //Schedule a job for the event dispatch thread:
	        //creating and showing this application's GUI.
	        javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                createAndShowGUI();
	            }
	        });
	        Main test = new Main();
	        

		    // Use relative path for Unix systems
		    File f = new File(storagePath + "readme.txt");
		    
		    f.getParentFile().mkdirs(); 
		    try {
				f.createNewFile();
				
				FileWriter readMe = new FileWriter(f);
		    	readMe.write("Hello and welcome!\nThis is where any screenshots and other assorted test files for this game will go.");
		    	readMe.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		    
	        
	        while (!didStart) {
	        	int i = 0;
	        	try {
	        		i++;
	        		test.Startup(true);
	        		System.out.println("Attempt start (" + i + ")");
	        	} catch (Exception e) {
	        		e.printStackTrace();
	        	}
	        }
	    }
	     
	    /*
	     * Create the GUI and show it.  For thread safety,
	     * this method should be invoked from the
	     * event dispatch thread.
	     */
	    private static void createAndShowGUI() {
	        //Create and set up the window.
	    	try {
				icon = ImageIO.read(new File("img/icon.png").getAbsoluteFile());
			} catch (IOException e) {
				//System.out.println(e);
			}
	    	
	        
	        frame.setIconImage(icon);
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setMinimumSize(new Dimension(500, 500));
	        //Create and set up the content pane.
	        JComponent newContentPane = new Main();
	        newContentPane.setOpaque(true); //content panes must be opaque
	        frame.setContentPane(newContentPane);
	        //Display the window.
	        frame.setUndecorated(true);
	        
	        frame.pack();
	        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	        frame.setVisible(true);
	        
	    }
	     
		public Main() {
	        super(new GridLayout(0, 1));
	    	
	        mainCanvas = new Canvas() {
	        	public void paint(Graphics g) {
	        	}
	        };
	        mainCanvas.setBackground(Color.black);
	        
	        add(mainCanvas, BorderLayout.CENTER);
	        
	        //Register for mouse events on mainCanvas and the panel.
	        mainCanvas.addMouseListener(this);
	        mainCanvas.addKeyListener(this);
	        mainCanvas.addMouseWheelListener(this);
	        addMouseListener(this);
	        //setBorder(BorderFactory.createLineBorder(Color.black, 2));
	    }
		
		
		public static void createMenu(int cX, int cY, MenuType type) {
			System.out.println("Drawing menu of type " + type);
			
			if (type != currentMenuType) previousMenuType = currentMenuType;
			currentMenuType = type;
			
			Graphics g = bufferImage.getGraphics();
			
			if (type == MenuType.Main) g.clearRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight()); else g.drawImage(pauseBufferImage, 0, 0, null);
			g.setColor(Color.white);
			
			buttonList.clear();
			
			if (type == MenuType.Main || type == MenuType.Pause) {
				// Title
				g.setFont(new Font("Courier", Font.PLAIN, 80));
				CPoint titleCenter = centerString("3D TEST", g, new CPoint(cX, (int)(cY*0.4)));
				g.drawString("3D TEST", (int)titleCenter.x, (int)titleCenter.y);
				
				// Subtitle
				g.setFont(new Font("Dialog", Font.PLAIN, 30));
				titleCenter = centerString("by Aristeas", g, new CPoint(cX, (int)(cY*0.55)));
				g.drawString("by Aristeas", (int)titleCenter.x, (int)titleCenter.y);
				
				buttonList.add(new Button("OPTIONS",
						new Rectangle((int)(cX*0.85), (int)(cY*1.025), (int)(cX*0.3), (int)(cY*0.2)),
						() -> {
							createMenu(mainCanvas.getWidth()/2, mainCanvas.getHeight()/2, MenuType.Options);
						}));
				
				buttonList.add(new Button("EXIT (OS)",
						new Rectangle((int)(cX*0.85), (type == MenuType.Pause ? (int)(cY*1.475) : (int)(cY*1.25)), (int)(cX*0.3), (int)(cY*0.2)),
						() -> System.exit(0)
						));
				
				
				if (type == MenuType.Main) {
					buttonList.add(new Button("START",
							new Rectangle((int)(cX*0.85), (int) (cY*0.8), (int)(cX*0.3), (int)(cY*0.2)),
							() -> {
								buttonClock.stop();
					    		startupGame(); // even more brain damage
								//createMenu(cX, cY, MenuType.ShipSelect);
							}));
				}
				else if (type == MenuType.Pause) {
					buttonList.add(new Button("RESUME",
							new Rectangle((int)(cX*0.85), (int) (cY*0.8), (int)(cX*0.3), (int)(cY*0.2)),
							() -> {
								drawPrevMenu();
							}));
					
					buttonList.add(new Button("EXIT (MENU)",
							new Rectangle((int)(cX*0.85), (int)(cY*1.25), (int)(cX*0.3), (int)(cY*0.2)),
							() -> {
								reset();
								startup();
								createMenu(cX, cY, MenuType.Main);
							}));
				}
			}
			
			else if (type == MenuType.Options) {

				buttonList.add(new Button("RETURN",
						new Rectangle((int)(cX*0.85), (int) (cY*0.8), (int)(cX*0.3), (int)(cY*0.2)),
						() -> {
							System.out.println("OPTIONS tried to draw " + (isPaused ? MenuType.Pause : MenuType.Main));
							createMenu(mainCanvas.getWidth()/2, mainCanvas.getHeight()/2, isPaused ? MenuType.Pause : MenuType.Main);
						}));
				
				buttonList.add(new Button((showFps ? "Hide" : "Show") + " FPS",
						new Rectangle((int)(cX*0.85), (int) (int)(cY*1.025), (int)(cX*0.3), (int)(cY*0.2)),
						() -> {
							showFps = !showFps;
							createMenu(cX, cY, MenuType.Options);
						}));
				
				/*buttonList.add(new Button((ignoreRadarSweep ? "Enable" : "Disable") + " RADAR",
						new Rectangle((int)(cX*0.85), (int) (cY+cY*0.25), (int)(cX*0.5), (int)(cY*0.2)),
						() -> {
							ignoreRadarSweep = !ignoreRadarSweep;
							createMenu(cX, cY, MenuType.Options);
						}));
				buttonList.add(new Button((ignoreInterdictors ? "Enable" : "Disable") + " INTERDICTORS",
						new Rectangle((int)(cX*0.85), (int) (cY*1.475), (int)(cX*0.5), (int)(cY*0.2)),
						() -> {
							ignoreInterdictors = !ignoreInterdictors;
							createMenu(cX, cY, MenuType.Options);
						}));
				buttonList.add(new Button((ignoreInterdictors ? "Enable" : "Disable") + " ADV FLIGHT",
						new Rectangle((int)(cX*0.85), (int) (cY*1.475), (int)(cX*0.5), (int)(cY*0.2)),
						() -> {
							advancedControls = !advancedControls;
							createMenu(cX, cY, MenuType.Options);
						}));*/
			}
			/*else if (type == MenuType.ShipSelect) {
				for (int i = 0; i < shipModel.values().length; i++) {
					shipModel model = shipModel.values()[i];
					int x = cX*2/(shipModel.values().length + 1)*(i+1)-64;
					g.drawImage(model.preview, x, cY-128, null); // TODO fix
					
					buttonList.add(new Button("Select",
							new Rectangle(x-8, cY, 144, (int)(cY*0.15)),
							() -> {

								buttonClock.stop();
					    		startupGame();
							}));
				}
			}*/
			
			g.setColor(Color.black);
			for (Button button : buttonList) {
				Rectangle rect = button.rectangle;
				//System.out.println("Filled button background [" + button.name + "]");
				g.fillRect(rect.x, rect.y, rect.width, rect.height);
			}
			
			g.setFont(new Font(null));
			g.drawString(currentMenuType.name(), 10, 10);
			drawButtons(buttonList, bufferImage.getGraphics());
			mainCanvas.getGraphics().drawImage(bufferImage, 0, 0, null);
		}
		
		public void menuButtonPressed(MouseEvent e) {
			String debugButtonName = "";
			System.out.println("Button press!");
			 try {
				 for (Button button : buttonList) {
					 debugButtonName = button.name;
					 if (button.isClicked(e.getLocationOnScreen())) button.action.run();
				 }
			}
			catch (Exception ex) {
				System.out.println("Failed to draw buttons - returning to menu [" + ex + "] on [" + currentMenuType + ":" + debugButtonName + "]");
				//ex.printStackTrace();
				createMenu(mainCanvas.getWidth()/2, mainCanvas.getHeight()/2, currentMenuType);
			}
		}
		
		public static void drawButtons(List<Button> buttonList2, Graphics g) {
			buttonClock.start();
			g.setColor(Color.white);
			g.setFont(new Font("Courier", Font.PLAIN, 30));
			buttonList2.forEach((button) -> {
						String name = button.name;
						Rectangle pos = button.rectangle;
						/*System.out.println("Drawing button " + name + " at " + pos);
						System.out.println("Drawing string " + name + " at " + new CPoint((pos.x+pos.width)/2, (pos.y+pos.height)/2));*/
						g.drawRect(pos.x, pos.y, pos.width, pos.height);
						CPoint centeredString = centerString(name, g, new CPoint((pos.x*2+pos.width)/2, (pos.y*2+pos.height)/2));
						g.drawString(name, (int)centeredString.x, (int)centeredString.y);
			});
		}
		
		public static CPoint centerString(String string, Graphics g, CPoint pos) {
			FontMetrics metrics = g.getFontMetrics();
			
			return new CPoint(pos.x - metrics.stringWidth(string)/2, pos.y + (int) metrics.getStringBounds(string, g).getHeight()/4);
		}
		
		public static void drawPrevMenu() {
			if (drawClock.isRunning()) {
				mainCanvas.setCursor(Cursor.getDefaultCursor());
				drawClock.stop();
				buttonClock.start();
				pauseBufferImage.getGraphics().drawImage(bufferImage, 0, 0, null);
				isPaused = true;
				createMenu(mainCanvas.getWidth()/2, mainCanvas.getHeight()/2, MenuType.Pause);
			}
			else {
				if (currentMenuType == MenuType.Pause) {
					isPaused = false;
					buttonClock.stop();
					drawClock.start();
					mainCanvas.setCursor( mainCanvas.getToolkit().createCustomCursor(
							new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB ),
							new Point(),
							null ) );
				}
				else if (currentMenuType != MenuType.Main) {
					System.out.println("ESC pressed - returning to " + previousMenuType);
					createMenu(mainCanvas.getWidth()/2, mainCanvas.getHeight()/2, previousMenuType);
				}
			}
		}
		
		
		static ActionListener clock40 = new ActionListener() {
	    	public void actionPerformed (ActionEvent ae) {
	    		//Boolean didDraw = false;
	    		if (drawClock.isRunning()) buttonClock.stop();
				Graphics g = bufferImage.getGraphics();
				
				for (Button button : buttonList) {
					Rectangle rect = button.rectangle;
					Point mousePos = MouseInfo.getPointerInfo().getLocation();
					SwingUtilities.convertPointFromScreen(mousePos, mainCanvas);
					
					if (rect.contains(mousePos)) {
						g.setColor(Color.gray);
						//didDraw = true;
					}
					else g.setColor(Color.black);
					
					g.fillRect(rect.x, rect.y, rect.width, rect.height);
				}
				
				//if (!didDraw) g.drawImage(bufferImage, 0, 0, null);
				//else {
				//	drawButtons(buttonList, g);
				//}
				drawButtons(buttonList, g);
				mainCanvas.getGraphics().drawImage(bufferImage, 0, 0, null);
	    	}
	    };
	    
	    static Timer buttonClock = new Timer(40, clock40);
		
	    public static void reset() {
	    	drawClock.stop();
	    	hasStarted = false;
	    	isPaused = false;
	    	camera.pos = new Vector3d();
	    	camera.angle = new Vector3d();
	    }
		
	    public static void startup() {
			System.out.println("STARTUP RUN");
	    	int rX = mainCanvas.getWidth();
			int rY = mainCanvas.getHeight();
			
			bufferImage = (BufferedImage) mainCanvas.createImage(rX, rY);
			pauseBufferImage = (BufferedImage) mainCanvas.createImage(rX, rY);
	    	
		    createMenu(rX/2, rY/2, currentMenuType);
	    	
	    	
	    	//startupGame();
	    }
	    
	    public static void startupGame() {
			System.out.println("Starting game...");
	    	
			isPaused = false;
			buttonClock.stop();
			
			
			drawClock.setDelay(10);
			drawClock.start();
			hasStarted = true;
			
			mainCanvas.setCursor( mainCanvas.getToolkit().createCustomCursor(
					new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB ),
					new Point(),
					null ) );
		}
	    
	    
	    
		
		
		
		
		
		
		public void Startup(boolean doWait) {
			if (doWait) {
				try {
					Thread.sleep(500);
					bufferImage = (BufferedImage) mainCanvas.createImage(mainCanvas.getWidth(), mainCanvas.getHeight());
					shaderBufferImage = (BufferedImage) mainCanvas.createImage(mainCanvas.getWidth(), mainCanvas.getHeight());
					
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			for (int i = 0; i < 100; i++) {
				keyInput.put(i, false);
			}
			//cube = cube.scale(2).offset(new Vector3d(25, 0, 0));
			
			aspectRatio = (double)mainCanvas.getWidth()/mainCanvas.getHeight();
			drawLimit = Math.toRadians(fieldOfView);
			
			mainCanvas.getGraphics().drawString("Loading... (this may take a few seconds)", mainCanvas.getWidth()/2, mainCanvas.getHeight()/2);
			for (int i = 0; i < 1000; i++) {
				skybox.put(Vector3d.random(3.14).subtract(new Vector3d(1,1,1)).normalize(), new Color((int)(Math.random()*200), (int)(Math.random()*150), (int)(Math.random()*255)));
			}
			
			//drawClock.start();
			didStart = true;
			startup();
		}

		public static void drawScene(Graphics g) {
			camera.update();

			drawFromObject(testShip, g, false);
			
			
			//camera.pos = testCockpit.pos.add(cameraOrientation.forward.multiply(1).add(cameraOrientation.up.multiply(0.7)));
			camera.pos = testCockpit.pos.subtract(cameraOrientation.forward.multiply(1).add(cameraOrientation.up.multiply(0.7)));
			/*if (freeLook) testCockpit.rotCenter = cameraOrientation.forward.multiply(2.75);
			else {
				testCockpit.rotCenter = new Vector3d();
				testCockpit.angle = new Vector3d(-camera.angle.y, -camera.angle.x, camera.angle.z);
			}*/
			if (freeLook) {
				//testCockpit.position = testCockpit.position.subtract(cameraOrientation.forward.multiply(0.9));
			}
			else {
				testCockpit.angle = new Vector3d(-camera.angle.y, -camera.angle.x, 0);
			}
			
			//drawFromObject(testCockpit, g, true);
		}
		
		public static void drawHud(Graphics g, CPoint c) {
			g.drawString("Camera Angle: (" + Math.round(Math.toDegrees(camera.angle.x)) + ", " + Math.round(Math.toDegrees(camera.angle.y)) + ", " + Math.round(Math.toDegrees(camera.angle.z)) + ")", 10, 10);
			g.drawString("Camera Position: " + camera.pos.round(1), 10, 25);
			g.drawString("Ship Angle: (" + Math.round(Math.toDegrees(testShip.angle.x)) + ", " + Math.round(Math.toDegrees(testShip.angle.y)) + ", " + Math.round(Math.toDegrees(testShip.angle.z)) + ")", 10, 55);
			
			
			g.drawLine((int)c.x - 5, (int)c.y, (int)c.x - 15, (int)c.y);
			g.drawLine((int)c.x + 5, (int)c.y, (int)c.x + 15, (int)c.y);
			g.drawLine((int)c.x, (int)c.y - 5, (int)c.x, (int)c.y - 15);
			g.drawLine((int)c.x, (int)c.y + 5, (int)c.x, (int)c.y + 15);
			
			if (showFps) {
				g.drawString("FPS: " + fps + " (" + frameTime + "ms)", 10, 40);
				g.drawString("Memory Util: " + runtime.totalMemory()/1048576d + "mb (" + (int)((runtime.totalMemory()/(double)(runtime.totalMemory()-runtime.freeMemory()))*10)/10d + "%)", 10, 70);
			}
			
			CPoint hudDraw = project3d(testCockpit.model.getSubobject("Screen.001", new Vector3d(-0.25, 0.15, 0), testCockpit.pos, testCockpit.oldAngle, testCockpit.angle), camera.angle, camera.pos, false).multiply(c.multiply(2));
			g.drawString(camera.pos.round(1).toString(), (int)(hudDraw.x + c.x), (int)(-hudDraw.y + c.y));
			
			hudDraw = project3d(testCockpit.model.getSubobject("Screen.002", new Vector3d(0, 0.25, 0), testCockpit.pos, testCockpit.oldAngle, testCockpit.angle), camera.angle, camera.pos, false).multiply(c.multiply(2));
			g.drawString(String.valueOf(testCockpit.throttle), (int)(hudDraw.x + c.x), (int)(-hudDraw.y + c.y));
			
			hudDraw = project3d(testCockpit.model.getSubobject("Screen.003", new Vector3d(0, 0.25, 0), testCockpit.pos, testCockpit.oldAngle, testCockpit.angle), camera.angle, camera.pos, false).multiply(c.multiply(2));

			g.setColor(Color.red);
			g.drawLine((int)hudDraw.x, (int)hudDraw.y, (int)hudDraw.x, (int)hudDraw.y + (int)(testCockpit.throttle*2));
		}
		
		static ActionListener render = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fps = 1000/(frameStartTime + drawClock.getDelay());
				if (frameCt%2 == 0) frameTime = frameStartTime;
				frameStartTime = System.nanoTime();
				
				// Move mouse to center of screen, translate to rotation
				try {
					CPoint relMousePos = new CPoint(mainCanvas.getMousePosition());
					relMousePos.x -= mainCanvas.getWidth()/2;
					relMousePos.y -= mainCanvas.getHeight()/2;
					relMousePos.x /= mainCanvas.getWidth();
					relMousePos.y /= mainCanvas.getHeight();
					if (relMousePos.distanceSquared(new CPoint()) > 0.0000001) {
						relMousePos = CPoint.rotatePoint(relMousePos, camera.angle.z, new CPoint());
						
						//camera.angle.y += camera.angle.x > 90 && camera.angle.x < 270 ? -relMousePos.x : relMousePos.x;
						camera.angle.y += relMousePos.x;
						camera.angle.x += relMousePos.y;
					}
					
					Robot robot;
					try {
						robot = new Robot();
						
						
						robot.mouseMove(mainCanvas.getWidth()/2, mainCanvas.getHeight()/2);
					} catch (AWTException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} catch (Exception e) {}
				

				handleKeyInputs();
				Graphics g;
				try {
					g = bufferImage.getGraphics();
				}
				catch (Exception e) {
					return;
				}
				
				CPoint c = new CPoint(mainCanvas.getWidth()/2, mainCanvas.getHeight()/2);
				
				g.clearRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());
				
				camera.angle = camera.angle.add(new Vector3d(Math.PI*2, Math.PI*2, Math.PI*2));
				
				camera.angle.x %= Math.PI*2;
				camera.angle.y %= Math.PI*2;
				camera.angle.z %= Math.PI*2;
				
				cameraOrientation.forward = new Vector3d(0, 0, -1).rotate(new Vector3d(1, 0, 0), -camera.angle.x).rotate(new Vector3d(0, 1, 0), -camera.angle.y).normalize();
				cameraOrientation.right = cameraOrientation.forward.cross(new Vector3d(0, camera.angle.x >= halfPi && camera.angle.x <= halfPi*3 ? -1 : 1, 0)).normalize().rotate(cameraOrientation.forward, camera.angle.z);
				cameraOrientation.up = cameraOrientation.forward.cross(cameraOrientation.right).normalize();
				
				drawBackground();
				
				g.setColor(Color.white);
				drawScene(g);
				
				drawLine(testShip.orient.up.multiply(10), testShip.pos, Color.red, g, false);
				
				g.setColor(Color.white);
				drawHud(g, c);
				
				mainCanvas.getGraphics().drawImage(bufferImage, 0, 0, null);
				
				frameStartTime = (long)(Math.abs(frameStartTime - System.nanoTime())/1000000d);
				drawClock.setDelay((int) Math.abs(16.67 - frameStartTime));
				frameCt++;
			}
		};
		
		static Timer drawClock = new Timer(16, render);
		
		
		
		@SuppressWarnings("unused")
		public static void drawBackground() {
			// Draws skybox
			for (Vector3d key : skybox.keySet()) {
				if (key.angleBetween(camera.angle) < drawLimit || true) {
					CPoint a = project3d(key, new Vector3d(-camera.angle.x, camera.angle.y, 0), new Vector3d(), true).multiply(new CPoint(mainCanvas.getWidth(), mainCanvas.getHeight()));
					try {
						CPoint pos = new CPoint(a.x + mainCanvas.getWidth()/2, a.y + mainCanvas.getHeight()/2);
						
						bufferImage.setRGB((int)pos.x, (int)pos.y, skybox.get(key).getRGB());
						for (int i = 1; i < key.w; i++) {
							int iColor = new Color(skybox.get(key).getRed()/(5-i), skybox.get(key).getGreen()/(5-i), skybox.get(key).getBlue()/(5-i)).getRGB();
							bufferImage.setRGB((int)pos.x+1*i, (int)pos.y, iColor);
							bufferImage.setRGB((int)pos.x-1*i, (int)pos.y, iColor);
							bufferImage.setRGB((int)pos.x, (int)pos.y+1*i, iColor);
							bufferImage.setRGB((int)pos.x, (int)pos.y-1*i, iColor);
						}
					} 
					catch (Exception e) 
					{
						
					}
				}
			}
			
			shaderBufferImage.getGraphics().drawImage(bufferImage, 0, 0, null);
			
			// Primitive antialiasing. Awful performance.
			if (false) {
				for (int h = 0; h < bufferImage.getHeight(); h++) {
					for (int w = 0; w < bufferImage.getWidth(); w++) {
						int aRGB = bufferImage.getRGB(w, h);
						Color pixel = new Color(
								(aRGB >> 16) & 0xFF,
								(aRGB >> 8) & 0xFF,
								(aRGB     ) & 0xFF
								);
						
						
						if (pixel.getRGB() == black) continue;
						
						aRGB = new Color(pixel.getRed()/4, pixel.getGreen()/4, pixel.getBlue()/4, 10).getRGB();
						try {
							if (bufferImage.getRGB(w+1, h) == black) shaderBufferImage.setRGB(w+1, h, aRGB);
						} catch (Exception e) {}
						try {
							if (bufferImage.getRGB(w-1, h) == black) shaderBufferImage.setRGB(w-1, h, aRGB);
						} catch (Exception e) {}
						try {
							if (bufferImage.getRGB(w, h+1) == black) shaderBufferImage.setRGB(w, h+1, aRGB);
						} catch (Exception e) {}
						try {
							if (bufferImage.getRGB(w, h-1) == black) shaderBufferImage.setRGB(w, h-1, aRGB);
						} catch (Exception e) {}
					}
				}
				
				bufferImage.getGraphics().drawImage(shaderBufferImage, 0, 0, null);
			}
		}
		
		
		
		public static void drawLine(Vector3d origin, Vector3d end, Color color, Graphics g, boolean fixedToPlayer) {
			int cX = mainCanvas.getWidth()/2;
			int cY = mainCanvas.getHeight()/2;
			CPoint s = new CPoint(mainCanvas.getWidth(),mainCanvas.getHeight());
			
			
			CPoint drawPos = new CPoint();
			CPoint endPos = new CPoint();
			
			g.setColor(color);
			
			drawPos = project3d(origin, camera.angle, camera.pos, fixedToPlayer);
			endPos = project3d(end, camera.angle, camera.pos, fixedToPlayer);
			
			if (drawPos.equals(new CPoint())) return;
			g.drawLine((int)(drawPos.x*s.x + cX), (int)(-drawPos.y*s.y + cY), (int)(endPos.x*s.x + cX), (int)(-endPos.y*s.y + cY));
		}
		
		public static CPoint testVec(Vector3d v1, Object3d object, boolean fixedToPlayer, CPoint s) {
			
			//if (drawLimit <= v1.subtract(camera.pos).angleBetween(cameraOrientation.forward) && !sizeInvariant) {
			//	return new CPoint();
			//}
			v1 = v1.add(object.pos);
			
			if (cameraOrientation.forward.dot(v1.subtract(camera.pos).normalize()) < 0) {
				return new CPoint();
			}
			
			CPoint c1 = project3d(v1, camera.angle, camera.pos, fixedToPlayer);

			if (Double.isInfinite(c1.x) || Double.isInfinite(c1.y) || Double.isNaN(c1.x)) {
				return new CPoint();
			}
			
			c1 = c1.multiply(s);
			

			return c1;
		}
		
		public static void drawFromObject(Object3d obj, Graphics g, boolean fixedToPlayer) {
			CPoint c = new CPoint(mainCanvas.getWidth()/2, mainCanvas.getHeight()/2);
			
			obj.update();
			
			
			try {
				Arrays.sort(obj.model.triangles, (o1, o2) -> (int)(o2.center().add(obj.pos).distanceSq(camera.pos)-o1.center().add(obj.pos).distanceSq(camera.pos)));
			} catch (Exception e) {
			}
			for (Poly3d poly : obj.model.triangles) {
				if (cameraOrientation.forward.dot(poly.normal)-fovPercent > 0) continue;
				
				CPoint s = new CPoint(mainCanvas.getWidth(), -mainCanvas.getHeight());
				CPoint[] drawPoints = new CPoint[poly.points.length];
				int[] xDrawPoints = new int[poly.points.length];
				int[] yDrawPoints = new int[poly.points.length];
				
				
				for (int i = 0; i < poly.points.length; i++) {
					drawPoints[i] = testVec(poly.points[i], obj, fixedToPlayer, s);
					if (drawPoints[i].equals(new CPoint())) continue;

					drawPoints[i] = drawPoints[i].add(c);
					xDrawPoints[i] = (int)drawPoints[i].x;
					yDrawPoints[i] = (int)drawPoints[i].y;
					
					//if (new Rectangle(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight()).contains(drawPoints[i].toPoint2D())) {
					//	isAllOffScreen = false;
					//}
				}
				
				
				
				
				boolean isAllOffScreen = true;
				g.setColor(obj.model.color);
				for (int i = 0; i < drawPoints.length; i++) {
					
					int j = i < drawPoints.length-1 ? i+1 : 0;
					if (xDrawPoints[i] == 0 || xDrawPoints[j] == 0) continue;
					
					g.drawLine(xDrawPoints[i], yDrawPoints[i], xDrawPoints[j], yDrawPoints[j]);
					
					if (new Line2D.Double(xDrawPoints[i], yDrawPoints[i], xDrawPoints[j], yDrawPoints[j]).intersects(new Rectangle(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight()))) {
						isAllOffScreen = false;
					}
				}
				
				g.setColor(poly.color);
				if (!isAllOffScreen) g.fillPolygon(xDrawPoints, yDrawPoints, poly.points.length);
			}
		}
		
		public static CPoint project3d(Vector3d point, Vector3d cameraAngle, Vector3d cameraPosit, boolean fixedToPlayer) {
			CPoint drawPos = new CPoint();
			Vector3d cameraPosition = cameraPosit.copy();
			
			point = point.subtract(cameraPosition)
					//.rotate(new Vector3d(0, 0, 1), -cameraAngle.z) // Cam Roll
					.rotate(new Vector3d(0, 1, 0), cameraAngle.y) // Cam Pitch
					.rotate(new Vector3d(1, 0, 0), cameraAngle.x) // Cam Yaw
					.add(cameraPosition);
			
			point = point.add(cameraPosition.multiply(-1));
			double z = (point.z == 0 ? 1 : -point.z);
		
			
		    drawPos.x = point.x / z;

		    drawPos.y = point.y / z;
		    
		    //CPoint.rotatePoint(drawPos, cameraAngle.z, new CPoint());
		    if (!fixedToPlayer) CPoint.rotatePoint(drawPos, cameraAngle.z, new CPoint());

		    
		    drawPos = drawPos.add(0.0000000000000000000001).multiply(Math.tan(fieldOfView / 2));
		    drawPos.y *= aspectRatio;
		    //if (Double.isNaN(drawPos.x) || Double.isInfinite(drawPos.x) || drawPos.x == 0) System.out.println("Issue @ " + point);

		    
		    //drawPos.clamp(1.5);
			return drawPos;
		}
		
		
		public boolean testIntersect(CPoint l1, CPoint l2, Rectangle rect) {
	    	return new Line2D.Double(l1.toPoint2D(), l2.toPoint2D()).intersects(rect.getBounds());
	    }
		
		
		
		public static void handleKeyInputs() {
			if (keyInput.get(87)) { // W
				if (testCockpit.throttle < 1) testCockpit.throttle += 0.025;
				else testCockpit.throttle = 1;
			}
			if (keyInput.get(65)) { // A
				testCockpit.pos = testCockpit.pos.subtract(testCockpit.orient.right);
			}
			if (keyInput.get(83)) { // S
				testCockpit.throttle = 0;
			}
			if (keyInput.get(68)) { // D
				testCockpit.pos = testCockpit.pos.add(testCockpit.orient.right);
			}
			if (keyInput.get(81)) { // Q
				camera.angle.z -= Math.toRadians(2);
			}
			if (keyInput.get(69)) { // E
				camera.angle.z += Math.toRadians(2);
			}
			if (keyInput.get(32)) { // space
				testCockpit.pos = testCockpit.pos.add(testCockpit.orient.up);
			}
			if (keyInput.get(67)) { // C
				testCockpit.pos = testCockpit.pos.subtract(testCockpit.orient.up);
			}
			
			// @ pi pitch, yaw0.5 roll0.5. at 0 pitch, yaw1 roll0. at 2pi pitch, yaw0 roll1
			if (keyInput.get(38)) { // Up
				testShip.rotateY(0.01);
			}
			if (keyInput.get(40)) { // Down
				testShip.rotateY(-0.01);
			}
			if (keyInput.get(37)) { // Left
				testShip.rotateX(-0.01);
			}
			if (keyInput.get(39)) { // Right
				testShip.rotateX(0.01);
			}
			if (keyInput.get(27)) { //esc
				drawPrevMenu();
			}
		}
		
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			System.out.println("Key pressed '" + e.getKeyChar() + "' : " + e.getKeyCode());
			switch (e.getKeyCode()) {				
				case 82: // R
					camera.angle = new Vector3d();
					testCockpit.pos = new Vector3d();
					break;
				case 123: //F12
					File f = new File(storagePath + System.currentTimeMillis() + ".png");
					try {
						f.createNewFile();

						RenderedImage out = (RenderedImage) bufferImage;
						
						ImageIO.write(out, "png", f);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					System.out.println("Attempted Screenshot");
					break;
			}
			keyInput.put(e.getKeyCode(), true);
		}

		@Override
		public void windowGainedFocus(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowLostFocus(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void keyReleased(KeyEvent e) {
			keyInput.put(e.getKeyCode(), false);
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			/*double newScale = cube.scale + e.getPreciseWheelRotation()/10;
			if (newScale > 0.1 && newScale < 1.5) cube = cube.scale(newScale);
			System.out.println(cube.scale);
			Startup(false);
			*/
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == 2) freeLook = true;
			if (!hasStarted || isPaused) menuButtonPressed(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.getButton() == 2) {
				freeLook = false;
				camera.angle = new Vector3d(-testCockpit.angle.y, -testCockpit.angle.x, testCockpit.angle.z);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if (hasStarted)
				mainCanvas.setCursor( mainCanvas.getToolkit().createCustomCursor(
						new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB ),
						new Point(),
						null ) );
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
}