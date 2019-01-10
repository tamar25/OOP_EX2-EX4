package Ex4;

import Coords.coords_converter_class;
import Geom.Point3D;
import edu.nps.moves.dis.Vector3Double;
import edu.nps.moves.disutil.CoordinateConversions;
import edu.nps.moves.spatial.RangeCoordinates;

public class Map {
	// public final double north = 32.105737;
	// public final double south = 32.101861;
	// public final double east = 35.212613;
	// public final double west = 35.202026;

	public final double north;
	public final double south;
	public final double east;
	public final double west;
	private final double w = 1433;
	private final double h = 642;
	private Point3D xyz_pt1;
	private Point3D xyz_pt2;
	private double pixelPerMeter;
	private RangeCoordinates local;

	public Map(double north, double south, double east, double west) {
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;

		local = new RangeCoordinates(north, west, 0);

		double[] xyz = CoordinateConversions.getXYZfromLatLonDegrees(north, west, 0);
		Vector3Double local_xyz = local.localCoordFromDis(xyz[0], xyz[1], xyz[2]);

		xyz_pt1 = new Point3D(local_xyz.getX(), local_xyz.getY(), local_xyz.getZ());
		xyz = CoordinateConversions.getXYZfromLatLonDegrees(south, east, 0);
		local_xyz = local.localCoordFromDis(xyz[0], xyz[1], xyz[2]);
		xyz_pt2 = new Point3D(local_xyz.getX(), local_xyz.getY(), local_xyz.getZ());

		pixelPerMeter = w / xyz_pt2.x();

	}

	public Point3D gpsToPixel(Point3D gpsPoint) {
		double[] xyz = CoordinateConversions.getXYZfromLatLonDegrees(gpsPoint.x(), gpsPoint.y(), gpsPoint.z());
		Vector3Double local_xyz = local.localCoordFromDis(xyz[0], xyz[1], xyz[2]);
		return new Point3D(local_xyz.getX() * pixelPerMeter, -local_xyz.getY() * pixelPerMeter, local_xyz.getZ());
	}

	public Point3D pixelToGPS(Point3D pixelPoint) {
		double meterX = pixelPoint.x() / pixelPerMeter;
		double meterY = -pixelPoint.y() / pixelPerMeter;
		Point3D meterPoint1 = new Point3D(meterX, meterY, pixelPoint.z());
		Vector3Double localCoordinates = new Vector3Double();
		localCoordinates.setX(meterPoint1.x());
		localCoordinates.setY(meterPoint1.y());
		localCoordinates.setZ(meterPoint1.z());
		local.changeVectorToDisCoordFromLocalFlat(localCoordinates);
		double[] xyz = CoordinateConversions.xyzToLatLonDegrees(
				new double[] { localCoordinates.getX(), localCoordinates.getY(), localCoordinates.getZ() });
		Point3D result_xyz = new Point3D(xyz[0], xyz[1], xyz[2]);
		return result_xyz;
	}

	public double distance(Point3D pixelPoint1, Point3D pixelPoint2) {
		double meterX = pixelPoint1.x() / pixelPerMeter;
		double meterY = -pixelPoint1.y() / pixelPerMeter;

		Point3D meterPoint1 = new Point3D(meterX, meterY, pixelPoint1.z());

		meterX = pixelPoint2.x() / pixelPerMeter;
		meterY = -pixelPoint2.y() / pixelPerMeter;

		Point3D meterPoint2 = new Point3D(meterX, meterY, pixelPoint2.z());

		double distance = meterPoint1.distance3D(meterPoint2);
		return distance;
	}

	public double angle(Point3D pixelPoint1, Point3D pixelPoint2) {
		double meterX = pixelPoint1.x() / pixelPerMeter;
		double meterY = -pixelPoint1.y() / pixelPerMeter;

		Point3D meterPoint1 = new Point3D(meterX, meterY, pixelPoint1.z());

		meterX = pixelPoint2.x() / pixelPerMeter;
		meterY = -pixelPoint2.y() / pixelPerMeter;

		Point3D meterPoint2 = new Point3D(meterX, meterY, pixelPoint2.z());

		double angle = meterPoint1.north_angle(meterPoint2);
		return angle;
	}
}
