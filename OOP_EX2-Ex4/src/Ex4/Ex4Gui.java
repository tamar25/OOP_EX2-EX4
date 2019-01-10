package Ex4;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import Geom.Point3D;
import Robot.Play;

public class Ex4Gui extends JFrame implements MouseListener, KeyListener {
	private Container window;
	private JPanel _panel;
	private Graphics _paper;
	private int x = 1;
	private int y = 1;
	private int id = 0;
	private int step = 0;
	private double currtime = 0;

	private Map map;
	private boolean manual = false;
	private boolean waitForKey = true;
	private double rotateAngle;
	private HashMap<Integer, Color> colors = new HashMap<Integer, Color>();
	private String file_name = null;
	private static final String imgMapFilename = "Ariel1.png";

	public Ex4Gui() throws IOException {
		super("Packman Game");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		colors.put(0, Color.BLUE);
		colors.put(1, Color.YELLOW);
		colors.put(2, Color.GREEN);
		colors.put(3, Color.ORANGE);
		colors.put(4, Color.BLACK);
		colors.put(5, Color.CYAN);
		colors.put(6, Color.PINK);
		colors.put(7, Color.MAGENTA);
		colors.put(8, Color.GRAY);

		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(imgMapFilename);
			os = new FileOutputStream("target/classes/Ex3/" + imgMapFilename);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			is.close();
			os.close();
		}

	}

	public void createGui() {
		// A Container is a component that can contain other GUI components
		window = this.getContentPane();
		window.setLayout(new FlowLayout());

		// Add "panel" to be used for drawing
		_panel = new JPanel();
		Dimension d = new Dimension(1433, 642);
		_panel.setPreferredSize(d);
		window.add(_panel);
		_panel.addKeyListener(this);
		_panel.setFocusable(true);
		_panel.requestFocusInWindow();

		// A menu-bar contains menus. A menu contains menu-items (or sub-Menu)
		JMenuBar menuBar; // the menu-bar
		JMenu menu; // each menu in the menu-bar
		JMenuItem menuItem1, menuItem2; // an item in a menu

		menuBar = new JMenuBar();

		// setJMenuBar(menuBar); // "this" JFrame sets its menu-bar

		// A menu-bar contains menus. A menu contains menu-items (or sub-Menu)
		JMenu menu2; // each menu in the menu-bar
		JMenuItem menuItem21, menuItem22; // an item in a menu

		menu2 = new JMenu("Start Game");
		menu2.setMnemonic(KeyEvent.VK_S); // alt short-cut key
		menuBar.add(menu2); // the menu-bar adds this menu

		menuItem22 = new JMenuItem("Load from CSV", KeyEvent.VK_D);
		menu2.add(menuItem22); // the menu adds this item
		menuItem22.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				int returnVal = fileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					// Start the "server"
					// Create a "play" from a file (attached to Ex4)
					file_name = file.getAbsolutePath();

				}
			}

		});

		setJMenuBar(menuBar); // "this" JFrame sets its menu-bar

		ImageIcon imgBck = new ImageIcon(getClass().getResource(imgMapFilename));
		JLabel labelMap = new JLabel();
		labelMap.setIcon(imgBck);
		_panel.add(labelMap);

		// panel (source) fires the MouseEvent.
		// panel adds "this" object as a MouseEvent listener.
		_panel.addMouseListener(this);
	}

	protected void paintElement() {
		// The method getGraphics is called to obtain a Graphics object
		_paper = _panel.getGraphics();
		_paper.setColor(Color.WHITE);
		_paper.fillOval(x, y, 10, 10);
	}

	// public void mouseClicked(MouseEvent event){
	@Override
	public void mousePressed(MouseEvent event) {
		x = event.getX();
		y = event.getY();
		paintElement();
	}

	// Not Used, but need to provide an empty body for compilation
	public void mouseReleased(MouseEvent event) {
	}

	public void mouseClicked(MouseEvent event) {
	}

	public void mouseExited(MouseEvent event) {
	}

	public void mouseEntered(MouseEvent event) {
	}

	public static void main(String[] args) throws IOException {
		Ex4Gui frame = new Ex4Gui();
		frame.setBounds(0, 0, 1433, 642);
		frame.createGui();
		frame.setVisible(true);
		while (frame.file_name == null) {
			try {
				TimeUnit.MILLISECONDS.sleep((long) 500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		frame.playGame();
	}

	public void playGame() {
		Play play1 = new Play(file_name);

		// Set your ID's - of all the group members
		play1.setIDs(318455987);

		// Get the GPS coordinates of the "arena"
		String map_data = play1.getBoundingBox();
		System.out.println("Bounding Box info: " + map_data);

		String[] split = map_data.split(",");

		double south = Double.valueOf(split[2]);
		double west = Double.valueOf(split[3]);
		double north = Double.valueOf(split[5]);
		double east = Double.valueOf(split[6]);

		map = new Map(north, south, east, west);
		// Set the "player" init location - should be a valid location

		// play1.setInitLocation(32.1040, 35.2061);
		Point3D gpsInitPooint = map.pixelToGPS(new Point3D(x, y, 0));
		play1.setInitLocation(gpsInitPooint.x(), gpsInitPooint.y());
		Path path = new Path(map, play1);
		play1.start(); // default max time is 100 seconds (1000*100 ms).

		// "Play" as long as there are "fruits" and time
		// for(int i=0;i<10;i++) {
		int i = 0;
		while (play1.isRuning()) {
			i++;
			try {
				TimeUnit.MILLISECONDS.sleep((long) 1);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			// 7.1) this is the main command to the player (on the server side)
			if (!manual) {
				rotateAngle = path.nextStep();
			} else {
				while (waitForKey) {
					try {
						TimeUnit.MILLISECONDS.sleep((long) 1);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
			if (Double.isNaN(rotateAngle)) {
				System.err.println("No more fruits.");
				// play1.stop();
			}
			play1.rotate(rotateAngle);

			System.out.println("***** " + i + "******");

			// 7.2) get the current score of the game
			String info = play1.getStatistics();
			System.out.println(info);
			// 7.3) get the game-board current state
			ArrayList<String> board_data = play1.getBoard();
			draw_curr_state(board_data, i);
			for (int a = 0; a < board_data.size(); a++) {
				System.out.println(board_data.get(a));
			}
			System.out.println();
			waitForKey = true;
		}
		// stop the server - not needed in the real implementation.
		// play1.stop();
		System.out.println("**** Done Game (user stop) ****");

		// print the data & save to the course DB
		String info = play1.getStatistics();
		System.out.println(info);

	}

	private void draw_curr_state(ArrayList<String> board_data, int i) {
		for (int a = 0; a < board_data.size(); a++) {
			String element = board_data.get(a);
			String[] split = element.split(",");
			String type = split[0];
			int id = Integer.valueOf(split[1]);
			double lat = Double.valueOf(split[2]);
			double lon = Double.valueOf(split[3]);
			double alt = Double.valueOf(split[4]);
			double lat2 = -1;
			double lon2 = -1;
			Point3D gpsLocation2 = null;
			Point3D pixelPoint2 = null;

			if (alt != 0) {
				System.out.println("WARNING: altitude is not 0.");
			}

			Point3D gpsLocation = new Point3D(lat, lon, alt);
			Point3D pixelPoint = map.gpsToPixel(gpsLocation);
			int size = 5;
			Color color = Color.WHITE;
			switch (type) {
			case "M":
				size = 5;
				color = Color.PINK;
				break;
			case "P":
				size = 5;
				color = Color.YELLOW;
				// size *= (i+1)/100.;
				break;
			case "F":
				size = 5;
				color = Color.GREEN;
				break;
			case "G":
				size = 5;
				color = Color.RED;
				break;
			case "B":
				lat2 = Double.valueOf(split[5]);
				lon2 = Double.valueOf(split[6]);
				color = Color.BLACK;
				gpsLocation2 = new Point3D(lat2, lon2, alt);
				pixelPoint2 = map.gpsToPixel(gpsLocation2);
				break;
			default:
				break;
			}

			_paper = _panel.getGraphics();
			if (pixelPoint2 != null) {
				int x1 = Math.min(pixelPoint.ix(), pixelPoint2.ix());
				int y1 = Math.min(pixelPoint.iy(), pixelPoint2.iy());
				int x2 = Math.max(pixelPoint.ix(), pixelPoint2.ix());
				int y2 = Math.max(pixelPoint.iy(), pixelPoint2.iy());
				_paper.setColor(color);
				_paper.fillRect(x1, y1, x2 - x1, y2 - y1);
			} else {
				_paper.setColor(color);
				_paper.fillOval(pixelPoint.ix(), pixelPoint.iy(), size, size);
			}
			/*
			 * if (type.equals("P")) { _paper.setFont(new Font("Monospaced", Font.PLAIN,
			 * 14)); _paper.drawString(String.valueOf(id), pixelPoint.ix(), pixelPoint.iy()
			 * - 10); }
			 */
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!manual) {
			manual = true;
		} else {
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				rotateAngle = 0;
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				rotateAngle = 180;
			} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rotateAngle = 90;
			} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				rotateAngle = -90;
			}
			waitForKey = false;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
}
