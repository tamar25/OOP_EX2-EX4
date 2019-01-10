package Ex4;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import Geom.Point3D;
import Robot.Play;
import graph.Graph;
import graph.Graph_Algo;
import graph.Node;

public class Path {
	private static final int pixelBuffer=5;
	private final Map map;
	private final Play play;
	private int nextFruitId = -1;
	private List<Fruit> fruits;
	private final List<Point3D> blocksPoints;

	private Point3D playerLocation;
	private ArrayList<Point3D> blocksTLOrig;
	private ArrayList<Point3D> blocksBROrig;
	private ArrayList<Point3D> blocksTL;
	private ArrayList<Point3D> blocksBR;
	private ArrayList<Point3D> blocksTR;
	private ArrayList<Point3D> blocksBL;
	private ArrayList<String> currPath;
	private Point3D[] pp;
	private int nextNode;
	private double lastAngle;

	public Path(Map map, Play play) {
		this.map = map;
		this.play = play;
		blocksPoints = new ArrayList<Point3D>();
		blocksTL = new ArrayList<Point3D>();
		blocksBR = new ArrayList<Point3D>();
		blocksTLOrig = new ArrayList<Point3D>();
		blocksBROrig = new ArrayList<Point3D>();
		blocksTR = new ArrayList<Point3D>();
		blocksBL = new ArrayList<Point3D>();

		ArrayList<String> board_data = play.getBoard();
		for (int a = 0; a < board_data.size(); a++) {
			String element = board_data.get(a);
			String[] split = element.split(",");
			String type = split[0];
			int id = Integer.valueOf(split[1]);
			double lat = Double.valueOf(split[2]);
			double lon = Double.valueOf(split[3]);
			double lat2 = -1;
			double lon2 = -1;
			double alt = 0;
			Point3D gpsLocation2 = null;
			Point3D pixelPoint2 = null;

			Point3D gpsLocation = new Point3D(lat, lon, alt);
			Point3D pixelPoint = map.gpsToPixel(gpsLocation);
			if (type.equals("B")) {
				lat2 = Double.valueOf(split[5]);
				lon2 = Double.valueOf(split[6]);
				gpsLocation2 = new Point3D(lat2, lon2, alt);
				pixelPoint2 = map.gpsToPixel(gpsLocation2);
				int x1 = Math.min(pixelPoint.ix(), pixelPoint2.ix());
				int y1 = Math.min(pixelPoint.iy(), pixelPoint2.iy());
				int x2 = Math.max(pixelPoint.ix(), pixelPoint2.ix());
				int y2 = Math.max(pixelPoint.iy(), pixelPoint2.iy());

				blocksTL.add(new Point3D(x1 - pixelBuffer, y1 - pixelBuffer, alt));
				blocksBR.add(new Point3D(x2 + pixelBuffer, y2 + pixelBuffer, alt));
				blocksTR.add(new Point3D(x2 + pixelBuffer, y1 - pixelBuffer, alt));
				blocksBL.add(new Point3D(x1 - pixelBuffer, y2 + pixelBuffer, alt));
				
				blocksTLOrig.add(new Point3D(x1, y1, alt));
				blocksBROrig.add(new Point3D(x2, y2, alt));


			}
		}
		HashSet<Integer> removeTL = new HashSet<>();
		HashSet<Integer> removeBR = new HashSet<>();
		HashSet<Integer> removeTR = new HashSet<>();
		HashSet<Integer> removeBL = new HashSet<>();

		int blockNum = blocksTL.size();

		for (int i = 0; i < blockNum; i++) {
			Point3D topLeft = blocksTL.get(i);
			Point3D bottomRight = blocksBR.get(i);
			Point3D topRight = blocksTR.get(i);
			Point3D bottomLeft = blocksBL.get(i);
			for (int j = 0; j < blockNum; j++) {
				Point3D topLeftJ = blocksTL.get(j);
				Point3D bottomRightJ = blocksBR.get(j);
				if (i != j) {
					if (topLeft.ix() >= topLeftJ.ix() && topLeft.ix() <= bottomRightJ.ix()) {
						if (topLeft.iy() >= topLeftJ.iy() && topLeft.iy() <= bottomRightJ.iy()) {
							removeTL.add(i);
						}
					}
					if (bottomRight.ix() >= topLeftJ.ix() && bottomRight.ix() <= bottomRightJ.ix()) {
						if (bottomRight.iy() >= topLeftJ.iy() && bottomRight.iy() <= bottomRightJ.iy()) {
							removeBR.add(i);
						}
					}
					if (topRight.ix() >= topLeftJ.ix() && topRight.ix() <= bottomRightJ.ix()) {
						if (topRight.iy() >= topLeftJ.iy() && topRight.iy() <= bottomRightJ.iy()) {
							removeTR.add(i);
						}
					}
					if (bottomLeft.ix() >= topLeftJ.ix() && bottomLeft.ix() <= bottomRightJ.ix()) {
						if (bottomLeft.iy() >= topLeftJ.iy() && bottomLeft.iy() <= bottomRightJ.iy()) {
							removeBL.add(i);
						}
					}
				}
			}
		}

		for (int i = 0; i < blockNum; i++) {
			if (!removeTL.contains(i)) {
				blocksPoints.add(blocksTL.get(i));
			}
			if (!removeBR.contains(i)) {
				blocksPoints.add(blocksBR.get(i));
			}
			if (!removeTR.contains(i)) {
				blocksPoints.add(blocksTR.get(i));
			}
			if (!removeBL.contains(i)) {
				blocksPoints.add(blocksBL.get(i));
			}
		}
	}

	private void updateData() {
		fruits = new ArrayList<Fruit>();

		ArrayList<String> board_data = play.getBoard();
		for (int a = 0; a < board_data.size(); a++) {
			String element = board_data.get(a);
			String[] split = element.split(",");
			String type = split[0];
			int id = Integer.valueOf(split[1]);
			double lat = Double.valueOf(split[2]);
			double lon = Double.valueOf(split[3]);
			double lat2 = -1;
			double lon2 = -1;
			double alt = 0;
			Point3D gpsLocation2 = null;
			Point3D pixelPoint2 = null;

			Point3D gpsLocation = new Point3D(lat, lon, alt);
			Point3D pixelPoint = map.gpsToPixel(gpsLocation);

			if (type.equals("F")) {
				fruits.add(new Fruit(id, pixelPoint));
			} else if (type.equals("M")) {
				playerLocation = pixelPoint;
			}
		}
	}

	public double nextStep() {
		updateData();
		if (!fruitExists()) {
			Fruit nearestFruit = findNearestFruit();
			if (nearestFruit == null) {
				return Double.NaN;
			}
			this.nextFruitId = nearestFruit.getId();
			return findPath(nearestFruit.getPixelPoint());
		} else {
			String nextNodeName = currPath.get(nextNode);

			Point3D nextNodePoint;
			if (nextNodeName.equals("b")) {
				nextNodePoint = pp[pp.length - 1];
			} else {
				nextNodePoint = pp[Integer.valueOf(nextNodeName)];
			}

			if (playerLocation.close2equals(nextNodePoint,pixelBuffer)) {
				if (nextNodeName.equals("b")) {
					System.err.println("fruit was supposed to be eaten");
					return lastAngle;
				}
				nextNode++;
				return getAngle();
			}
		}
		return lastAngle;

	}

	private double findPath(Point3D fruitLocation) {
		int size = blocksPoints.size() + 2;
		pp = new Point3D[size];

		Graph g = new Graph();
		String source = "a";
		String target = "b";
		g.add(new Node(source));
		pp[0] = playerLocation;
		for (int i = 1; i < size - 1; i++) {
			Node d = new Node("" + i);
			g.add(d);
			pp[i] = blocksPoints.get(i-1);
		}
		pp[size - 1] = fruitLocation;
		g.add(new Node(target));

		for (int i = 0; i < pp.length - 1; i++) {
			String nameI = String.valueOf(i);
			if (i == 0) {
				nameI = "a";
			}
			for (int j = i + 1; j < pp.length; j++) {
				String nameJ = String.valueOf(j);
				if (j == pp.length - 1) {
					nameJ = "b";
				}
				if (!crossesBlocks(pp[i], pp[j])) {
					g.addEdge(nameI, nameJ, pp[i].distance2D(pp[j]));
				}
			}
		}
		Graph_Algo.dijkstra(g, source);
		Node b = g.getNodeByName(target);

		currPath = b.getPath();
		currPath.add(target);
		nextNode = 1;

		return getAngle();

	}

	private double getAngle() {
		String nextNodeName = currPath.get(nextNode);
		Point3D nextNodePoint;
		if (nextNodeName.equals("b")) {
			nextNodePoint = pp[pp.length - 1];
		} else {
			nextNodePoint = pp[Integer.valueOf(nextNodeName)];
		}
		double azimuth = map.angle(nextNodePoint, playerLocation);
		azimuth -= 180;
		double xAngle = 450 - azimuth;
		if (xAngle > 360) {
			xAngle -= 360;
		}
		lastAngle = azimuth;
		return azimuth;
	}

	private boolean crossesBlocks(Point3D p1, Point3D p2) {
		int blockNum = blocksTLOrig.size();
		for (int i = 0; i < blockNum; i++) {
			Point3D topLeft = blocksTLOrig.get(i);
			Point3D bottomRight = blocksBROrig.get(i);
			int width = bottomRight.ix() - topLeft.ix();
			int height = bottomRight.iy() - topLeft.iy();

			Rectangle rect = new Rectangle(topLeft.ix(), topLeft.iy(), width, height);
			Line2D line = new Line2D.Float(p1.ix(), p1.iy(), p2.ix(), p2.iy());
			if (line.intersects(rect)) {
				return true;
			}
		}
		return false;
	}

	private boolean fruitExists() {
		for (Fruit fruit : this.fruits) {
			if (fruit.getId() == nextFruitId) {
				return true;
			}
		}
		return false;
	}

	private Fruit findNearestFruit() {
		double minDistance = Double.POSITIVE_INFINITY;
		Fruit nearestFruit = null;

		for (Fruit fruit : this.fruits) {
			Point3D fruitPoint = fruit.getPixelPoint();
			double distance = Math.max(0, map.distance(fruitPoint, playerLocation));
			if (distance < minDistance) {
				nearestFruit = fruit;
				minDistance = distance;
			}
		}

		return nearestFruit;
	}

}
