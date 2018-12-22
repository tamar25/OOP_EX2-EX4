package Ex3;

import Ex3.ShortestPathAlgo.BestMove;
import Geom.Point3D;

public class Path {

	private Game game;
	private Map map;

	public Path(Game game, Map map) {
		this.game = game;
		this.map = map;
	}

	public double nextStep() {
		BestMove move = ShortestPathAlgo.findBestMove(map, game);
		

		move.bestPackman.setMoved();
		if (move.minTime > 0) {
			Point3D packmanPoint = map.gpsToPixel(move.bestPackman.getGpsLocation());
			Point3D fruitPoint = map.gpsToPixel(move.bestFruit.getGpsLocation());
			double r = move.bestPackman.getRadius();
			double azimuth = map.angle(fruitPoint, packmanPoint);
			double xAngle = 450 - azimuth;
			if (xAngle > 360) {
				xAngle -= 360;
			}
			double theta = Point3D.d2r(xAngle);
			double phi = Point3D.d2r(fruitPoint.up_angle(packmanPoint) + 90);
			double x = fruitPoint.x() + r * Math.cos(theta) * Math.sin(phi);
			double y = fruitPoint.y() - r * Math.sin(theta) * Math.sin(phi);
			double z = fruitPoint.z() - r * Math.cos(phi);
			Point3D newPoint = new Point3D(x, y, z);
			game.movePackman(move.bestPackman, map.pixelToGPS(newPoint));
		}
		game.eatFruit(move.bestFruit);
		
		return move.minTime;
	}

}
