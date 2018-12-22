package Ex3;

import Geom.Point3D;

public class ShortestPathAlgo {
	
	public static class BestMove {

		protected Fruit bestFruit;
		protected Packman bestPackman;
		protected double minTime;
		
		public BestMove(Fruit bestFruit, Packman bestPackman, double minTime) {
			this.bestFruit = bestFruit;
			this.bestPackman = bestPackman;
			this.minTime = minTime;
		}
		
		
	}
	
	private static class NearestPackman{
		private double minTime;
		private Packman bestPackman;
		
		public NearestPackman(double minTime, Packman bestPackman) {
			this.minTime = minTime;
			this.bestPackman = bestPackman;
		}

		
	}
	
	public static BestMove findBestMove(Map map, Game game) {
		double minTime = Double.POSITIVE_INFINITY;
		Fruit bestFruit = null;
		Packman bestPackman = null;

		for (Fruit fruit : game.getFruits()) {
			NearestPackman nearestPackman = getNearestPackman(fruit, map, game);
			double time = nearestPackman.minTime;
			Packman packman = nearestPackman.bestPackman;
			if (time < minTime) {
				bestFruit = fruit;
				bestPackman = packman;
				minTime = time;
			}
			if (minTime == 0) {
				break;
			}
		}
		
		return new BestMove(bestFruit, bestPackman, minTime);
	}
	
	private static NearestPackman getNearestPackman(Fruit fruit, Map map, Game game) {
		Point3D fruitPoint = fruit.getGpsLocation();
		fruitPoint = map.gpsToPixel(fruitPoint);
		double minTime = Double.POSITIVE_INFINITY;
		Packman bestPackman = null;
		for (Packman packman : game.getPackmans()) {
			packman.resetMoved();
			Point3D packmanPoint = packman.getGpsLocation();
			packmanPoint = map.gpsToPixel(packmanPoint);
			double distance = Math.max(0, map.distance(fruitPoint, packmanPoint) - packman.getRadius());
			double time = distance / packman.getSpeed();
			if (time < minTime) {
				bestPackman = packman;
				minTime = time;
			}
			if (minTime == 0) {
				break;
			}
		}
		return new NearestPackman(minTime, bestPackman);
	}

}
