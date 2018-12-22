package Ex3;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import Ex3.Map;
import Geom.Point3D;

class test_map {
	
	private double eps = 1e-10;

	@Test
	public void test_gpsToPixel() throws IOException {
		Map map = new Map();
		double center_lat = map.north - (map.north - map.south) / 2.;
		double center_lon = map.east - (map.east - map.west) / 2.;

		Point3D centerPointGPS = new Point3D(center_lat, center_lon, 0);

		Point3D centerPointPixelsExpected = new Point3D(716.5, 321, 0);
		Point3D centerPointPixelsActual = map.gpsToPixel(centerPointGPS);
		assertTrue(centerPointPixelsExpected.close2equals(centerPointPixelsActual, 1));
	}
	
	@Test
	public void test_pixelToGPS() throws IOException {
		Map map = new Map();
		Point3D centerPointPixel = new Point3D(716.5, 321, 0);
		
		
		double center_lat = map.north - (map.north - map.south) / 2.;
		double center_lon = map.east - (map.east - map.west) / 2.;

		Point3D centerPointGPSExpected = new Point3D(center_lat, center_lon, 0);

		Point3D centerPointGPSsActual = map.pixelToGPS(centerPointPixel);
		assertTrue(centerPointGPSExpected.close2equals(centerPointGPSsActual, 1));
	}

	@Test
	public void test_distance() throws IOException {
		Map map = new Map();
		Point3D pixelPoint1 = new Point3D(778, 39, 0);
		Point3D pixelPoint2 = new Point3D(850, 130, 0);
		double dis = map.distance(pixelPoint1, pixelPoint2);
		assertEquals( 74, dis, 5);
	}

	@Test
	public void test_angle() throws IOException {
		Map map = new Map();
		Point3D pixelPoint1 = new Point3D(780, 41, 0);
		Point3D pixelPoint2 = new Point3D(846, 130, 0);
		double angle = map.angle(pixelPoint1, pixelPoint2);
		assertEquals(142.4, angle, 3);
	}
}