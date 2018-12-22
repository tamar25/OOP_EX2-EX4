package Ex3;

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

public class MyFrame extends JFrame implements MouseListener {
	private Container window;
	private JPanel _panel;
	private Graphics _paper;
	private int x, y;
	private boolean isPackman;
	private int id = 0;
	private int step = 0;
	private double currtime = 0;
	private Map map = new Map();
	private Path path = null;
	private Game game = null;
	private List<Packman> packmans = new ArrayList<>();
	private List<Fruit> fruits = new ArrayList<>();
	private HashMap<Integer, Color> colors = new HashMap<Integer, Color>();
	private static final String imgMapFilename = "Ariel1.png";

	public MyFrame() throws IOException {
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

		String path = "lastgame";
		final File folder = new File(path);
		File[] files = folder.listFiles();
		if (files != null) {
			for (File f : files) {
				f.delete();
			}
		}

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

		// A menu-bar contains menus. A menu contains menu-items (or sub-Menu)
		JMenuBar menuBar; // the menu-bar
		JMenu menu; // each menu in the menu-bar
		JMenuItem menuItem1, menuItem2; // an item in a menu

		menuBar = new JMenuBar();

		// First Menu
		menu = new JMenu("Select Action");
		menu.setMnemonic(KeyEvent.VK_A); // alt short-cut key
		menuBar.add(menu); // the menu-bar adds this menu

		menuItem1 = new JMenuItem("Packman", KeyEvent.VK_F);
		menu.add(menuItem1); // the menu adds this item
		menuItem1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isPackman = true;
			}
		});
		menuItem2 = new JMenuItem("Fruit", KeyEvent.VK_S);
		menu.add(menuItem2); // the menu adds this item
		menuItem2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isPackman = false;
			}
		});

		// setJMenuBar(menuBar); // "this" JFrame sets its menu-bar

		// A menu-bar contains menus. A menu contains menu-items (or sub-Menu)
		JMenu menu2; // each menu in the menu-bar
		JMenuItem menuItem21, menuItem22; // an item in a menu

		// Second Menu
		menu2 = new JMenu("Start Game");
		menu2.setMnemonic(KeyEvent.VK_S); // alt short-cut key
		menuBar.add(menu2); // the menu-bar adds this menu

		menuItem21 = new JMenuItem("Save to CSV", KeyEvent.VK_G);
		menu2.add(menuItem21); // the menu adds this item
		menuItem21.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (game == null) {
					game = new Game(packmans, fruits);
					try {
						game.saveToCsv("GameFile.csv");
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		menuItem22 = new JMenuItem("Load from CSV", KeyEvent.VK_D);
		menu2.add(menuItem22); // the menu adds this item
		menuItem22.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				int returnVal = fileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					try {
						game = new Game(file.getPath());
						if (!game.isValid()) {
							System.out.println(" Invalid game! no packmans or fruits.");
						} else {
							path = new Path(game, map);
							do {
								draw_curr_state();
								String stepStr = String.valueOf(step / 10) + String.valueOf(step % 10);
								game.saveToCsv(
										"lastgame/GameFile_" + stepStr + "_" + String.valueOf(currtime) + ".csv");
								System.out.println("step " + String.valueOf(step));

								double time = path.nextStep();
								try {
									TimeUnit.SECONDS.sleep((long) time / 50);
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}
								step++;
								currtime += time;
							} while (!game.over());
							draw_curr_state();
							String stepStr = String.valueOf(step / 10) + String.valueOf(step % 10);
							game.saveToCsv("lastgame/GameFile_" + stepStr + "_" + String.valueOf(currtime) + ".csv");

						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

			}

			private void draw_curr_state() {
				for (Packman packman : game.getPackmans()) {
					Point3D packmanGPS = packman.getGpsLocation();
					Point3D packmanPixels = map.gpsToPixel(packmanGPS);
					_paper = _panel.getGraphics();
					int colorId = packman.getId() % colors.size();
					_paper.setColor(colors.get(colorId));
					_paper.fillOval(packmanPixels.ix(), packmanPixels.iy(), 10, 10);
					if (packman.moved()) {
						_paper.setFont(new Font("Monospaced", Font.PLAIN, 14));
						_paper.drawString(String.valueOf(step), packmanPixels.ix(), packmanPixels.iy() - 10);
					}
				}
				for (Fruit fruit : game.getFruits()) {
					Point3D fruitGPS = fruit.getGpsLocation();
					Point3D fruitPixels = map.gpsToPixel(fruitGPS);
					_paper = _panel.getGraphics();
					_paper.setColor(Color.RED);
					_paper.fillOval(fruitPixels.ix(), fruitPixels.iy(), 5, 5);
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
		if (isPackman) {
			_paper.setColor(Color.YELLOW);
			_paper.fillOval(x, y, 10, 10);
		} else {
			_paper.setColor(Color.RED);
			_paper.fillOval(x, y, 10, 10);
		}
		_paper.setFont(new Font("Monospaced", Font.PLAIN, 14));
		_paper.drawString("(" + Integer.toString(x) + ", " + Integer.toString(y) + ")", x, y - 10);
	}

	// public void mouseClicked(MouseEvent event){
	@Override
	public void mousePressed(MouseEvent event) {
		x = event.getX();
		y = event.getY();

		paintElement();
		Point3D gpsPoint = map.pixelToGPS(new Point3D(x, y, 0));
		new Point3D(x, y, 0);
		if (isPackman) {
			packmans.add(new Packman(id++, gpsPoint, 1, 1));
		} else {
			fruits.add(new Fruit(id++, gpsPoint, 1));
		}

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
		MyFrame frame = new MyFrame();
		frame.setBounds(0, 0, 1433, 642);
		frame.createGui();
		frame.setVisible(true);
	}
}
