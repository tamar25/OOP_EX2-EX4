package Coords;

import Geom.Point3D;
import edu.nps.moves.dis.Vector3Double;
import edu.nps.moves.disutil.CoordinateConversions;
import edu.nps.moves.spatial.RangeCoordinates;

/**
 * This class implements the @{coords_converter} interface, which represents a
 * basic coordinate system converter, including: 1. The 3D vector between two
 * lat,lon, alt points 2. Adding a 3D vector in meters to a global point. 3.
 * convert a 3D vector from meters to polar coordinates, using open-source
 * library open-dis https://github.com/open-dis/open-dis (version 4.16)
 * 
 * @author Boaz Ben-Moshe
 *
 */

public class coords_converter_class implements coords_converter {
	/**
	 * computes a new point which is the gps point transformed by a 3D vector (in
	 * meters)
	 */
	public Point3D add(Point3D gps, Point3D local_vector_in_meter) {

		RangeCoordinates local = new RangeCoordinates(gps.x(), gps.y(), gps.z());

		double[] xyz = CoordinateConversions.getXYZfromLatLonDegrees(gps.x(), gps.y(), gps.z());
		Vector3Double local_xyz = local.localCoordFromDis(xyz[0], xyz[1], xyz[2]);
		Point3D xyz_pt1 = new Point3D(local_xyz.getX(), local_xyz.getY(), local_xyz.getZ());
		xyz_pt1.add(local_vector_in_meter);
		Vector3Double localCoordinates = new Vector3Double();
		localCoordinates.setX(xyz_pt1.x());
		localCoordinates.setY(xyz_pt1.y());
		localCoordinates.setZ(xyz_pt1.z());
		local.changeVectorToDisCoordFromLocalFlat(localCoordinates);
		xyz = CoordinateConversions.xyzToLatLonDegrees(
				new double[] { localCoordinates.getX(), localCoordinates.getY(), localCoordinates.getZ() });
		Point3D result_xyz = new Point3D(xyz[0], xyz[1], xyz[2]);
		return result_xyz;
	}

	public double distance3d(Point3D gps0, Point3D gps1) {// todo calculate

		double[] xyz = CoordinateConversions.getXYZfromLatLonDegrees(gps0.x(), gps0.y(), gps0.z());
		Point3D xyz_pt1 = new Point3D(xyz[0], xyz[1], xyz[2]);
		xyz = CoordinateConversions.getXYZfromLatLonDegrees(gps1.x(), gps1.y(), gps1.z());
		Point3D xyz_pt2 = new Point3D(xyz[0], xyz[1], xyz[2]);
		return xyz_pt1.distance3D(xyz_pt2);
	}

	/** computes the 3D vector (in meters) between two gps like points */
	public Point3D vector3D(Point3D gps0, Point3D gps1) {

		RangeCoordinates local = new RangeCoordinates(gps0.x(), gps0.y(), gps0.z());

		double[] xyz = CoordinateConversions.getXYZfromLatLonDegrees(gps0.x(), gps0.y(), gps0.z());
		Vector3Double local_xyz = local.localCoordFromDis(xyz[0], xyz[1], xyz[2]);

		Point3D xyz_pt1 = new Point3D(local_xyz.getX(), local_xyz.getY(), local_xyz.getZ());
		xyz = CoordinateConversions.getXYZfromLatLonDegrees(gps1.x(), gps1.y(), gps1.z());
		local_xyz = local.localCoordFromDis(xyz[0], xyz[1], xyz[2]);
		Point3D xyz_pt2 = new Point3D(local_xyz.getX(), local_xyz.getY(), local_xyz.getZ());

		double delta_X = xyz_pt2.x() - xyz_pt1.x();
		double delta_Y = xyz_pt2.y() - xyz_pt1.y();
		double delta_Z = xyz_pt2.z() - xyz_pt1.z();

		Point3D p = new Point3D(delta_X, delta_Y, delta_Z);

		return p;
	}

	/**
	 * computes the polar representation of the 3D vector be gps0-->gps1 Note: this
	 * method should return an azimuth (aka yaw), elevation (pitch), and distance
	 */
	public double[] azimuth_elevation_dist(Point3D gps0, Point3D gps1) {
		RangeCoordinates local = new RangeCoordinates(gps0.x(), gps0.y(), gps0.z());

		double[] xyz = CoordinateConversions.getXYZfromLatLonDegrees(gps0.x(), gps0.y(), gps0.z());
		Vector3Double local_xyz = local.localCoordFromDis(xyz[0], xyz[1], xyz[2]);

		Point3D xyz_pt1 = new Point3D(local_xyz.getX(), local_xyz.getY(), local_xyz.getZ());
		xyz = CoordinateConversions.getXYZfromLatLonDegrees(gps1.x(), gps1.y(), gps1.z());
		local_xyz = local.localCoordFromDis(xyz[0], xyz[1], xyz[2]);
		Point3D xyz_pt2 = new Point3D(local_xyz.getX(), local_xyz.getY(), local_xyz.getZ());
		
		
		double azimuth = xyz_pt1.north_angle(xyz_pt2);
		double elevation = xyz_pt1.up_angle(xyz_pt2);
		double distance = this.distance3d(gps0, gps1);
		return new double[] {azimuth, elevation, distance};

	}

	/**
	 * return true iff this point is a valid lat, lon , lat coordinate:
	 * [-180,+180],[-90,+90],[-450, +inf]
	 * 
	 * @param p
	 * @return
	 */
	public boolean isValid_GPS_Point(Point3D p) { // to do +inf
		if (p.x() > 90 || p.x() < -90) {// lat
			return false;
		}
		if (p.y() > 180 || p.y() < -180) {// lon
			return false;
		}
		if (p.z() < -450) {// alt
			return false;
		}
		return true;
	}

}
