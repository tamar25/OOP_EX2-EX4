package Ex3;

import Geom.Point3D;

public class Packman {
	private final int id;
	private Point3D gpsLocation;
	private final double speed;
	private final double radius;
	private boolean moved;

	public Packman(int id, Point3D gpsLocation, double speed, double radius) {
		this.id = id;
		this.gpsLocation = gpsLocation;
		this.speed = speed;
		this.radius = radius;
	}

	public Point3D getGpsLocation() {
		return new Point3D(gpsLocation);
	}

	public void setGpsLocation(Point3D gpsLocation) {
		this.gpsLocation = gpsLocation;
	}

	public int getId() {
		return id;
	}

	public double getSpeed() {
		return speed;
	}

	public double getRadius() {
		return radius;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Packman other = (Packman) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Packman [id=" + id + ", gpsLocation=" + gpsLocation + ", speed=" + speed + ", radius=" + radius + "]";
	}

	public boolean moved() {
		return moved;
	}

	public void resetMoved() {
		moved = false;
	}

	public void setMoved() {
		moved = true;
	}
}
