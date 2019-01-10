package Ex4;

import Geom.Point3D;

public class Fruit {
	private final int id;
	private final Point3D pixelPoint;
	
	public Fruit(int id, Point3D pixelPoint) {
		this.id = id;
		this.pixelPoint = pixelPoint;
	}

	public int getId() {
		return id;
	}

	public Point3D getPixelPoint() {
		return new Point3D(pixelPoint);
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
		Fruit other = (Fruit) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Fruit [id=" + id + ", pixelPoint=" + pixelPoint;
	}
	
	
	
	
}
