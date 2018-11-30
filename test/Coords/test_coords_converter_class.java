package Coords;

import static org.junit.Assert.*;

import org.junit.Test;

import Geom.Point3D;

public class test_coords_converter_class {
	
	private double eps = 1e-10;

	@Test
	public void test_distance3d() {
		coords_converter_class coords = new coords_converter_class();
		Point3D OldTraffordGoalLine1 = new Point3D( 53.462911, -2.292074,41);
		Point3D OldTraffordGoalLine2 = new Point3D( 53.463226, -2.290595,41);

		double distance = coords.distance3d(OldTraffordGoalLine1, OldTraffordGoalLine2);
		double expectedFootballPitchLength = 100;
		assertEquals(expectedFootballPitchLength, distance, 10);
	}


	@Test
	public void test_distance3d_ManaraCliff3D() {
		coords_converter_class coords = new coords_converter_class();
		Point3D manara = new Point3D(  33.195808,  35.543675,860);
		Point3D qiryat8 = new Point3D(  33.208104,  35.570217,130);

		double distance = coords.distance3d(manara, qiryat8);
		assertEquals(2918, distance, 10);
	}
	

	@Test
	public void test_distance3d_ManaraCliff2D() {
		coords_converter_class coords = new coords_converter_class();
		Point3D manara = new Point3D(  33.195808,  35.543675,860);
		Point3D qiryat8 = new Point3D(  33.208104,  35.570217,860);

		double distance = coords.distance3d(manara, qiryat8);
		assertEquals(2824, distance, 10);
	}
	
	@Test
	public void test_vector3d() {
		coords_converter_class coords = new coords_converter_class();
		Point3D pt1 = new Point3D(32.156379, 34.794691, 6);
		Point3D pt2 = new Point3D(32.163698, 34.802812,17);

		Point3D vector = coords.vector3D(pt1, pt2);
		Point3D expected = new Point3D(713, 858, 11);
		assertEquals(expected.x(), vector.x(), 60);
		assertEquals(expected.y(), vector.y(), 50);
		assertEquals(expected.z(), vector.z(), 5);
		assertTrue(expected.close2equals(vector, 80));
	}
	
	@Test
	public void test_azimuth_elevation_dist() {
		coords_converter_class coords = new coords_converter_class();
		Point3D pt1 = new Point3D(32.156379, 34.794691, 6);
		Point3D pt2 = new Point3D(32.163698, 34.802812,17);

		double[] aed = coords.azimuth_elevation_dist(pt1, pt2);
		double azimuth = aed[0];
		double elevation = aed[1];
		double distance = aed[2];
		
		assertEquals(43, azimuth, 2);
		assertEquals(0.5652, elevation, 1);
		assertEquals(1115.6, distance, 1);
	}
	
	@Test
	public void test_add() {
		coords_converter_class coords = new coords_converter_class();
		Point3D pt1 = new Point3D(32.156379, 34.794691, 6);
		Point3D local_vector_in_meter = new Point3D(713, 858, 11);
		Point3D pt2 = coords.add(pt1, local_vector_in_meter);
		
		Point3D expected = new Point3D(32.163698, 34.802812,17);

		assertEquals(expected.x(), pt2.x(), 1);
		assertEquals(expected.y(), pt2.y(), 1);
		assertEquals(expected.z(), pt2.z(), 1);
		assertTrue(expected.close2equals(pt2, 1));
	}
	
	@Test
	public void test_isValid_GPS_Point() {
		coords_converter_class coords = new coords_converter_class();

		Point3D pt = new Point3D(68,99.655, 6);
		assertTrue(coords.isValid_GPS_Point(pt));
	}
	
	@Test
	public void test_isValid_GPS_Point_small_lat() {
		coords_converter_class coords = new coords_converter_class();

		Point3D pt = new Point3D(-532.156379, 45.7961, 10);
		assertFalse(coords.isValid_GPS_Point(pt));
	}
	
	@Test
	public void test_isValid_GPS_Point_big_lat() {
		coords_converter_class coords = new coords_converter_class();

		Point3D pt = new Point3D(146, 118.87, 68);
		assertFalse(coords.isValid_GPS_Point(pt));
	}
	
	@Test
	public void test_isValid_GPS_Point_small_long() {
		coords_converter_class coords = new coords_converter_class();

		Point3D pt = new Point3D(-46, -202, 999);
		assertFalse(coords.isValid_GPS_Point(pt));
	}
	
	@Test
	public void test_isValid_GPS_Point_big_long() {
		coords_converter_class coords = new coords_converter_class();

		Point3D pt = new Point3D(-21.34545, 300, 598);
		assertFalse(coords.isValid_GPS_Point(pt));
	}
	
	@Test
	public void test_isValid_GPS_Point_small_alt() {
		coords_converter_class coords = new coords_converter_class();

		Point3D pt = new Point3D(55, 156, -1000);
		assertFalse(coords.isValid_GPS_Point(pt));
	}
}
