package Ex3;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import Ex3.Fruit;
import Ex3.Game;
import Ex3.Map;
import Ex3.Packman;
import Geom.Point3D;

class test_game {


	@Test
	public void testCtor() throws IOException {
		
		Game game= new Game("gameTest.csv");
		List<Packman> packmans = game.getPackmans();
		List<Fruit> fruits = game.getFruits();
		assertNotNull(packmans);
		assertNotNull(fruits);
		assertEquals(1, packmans.size());
		assertEquals(1, fruits.size());
		Packman packman = packmans.get(0);
		Fruit fruit = fruits.get(0);
		Point3D gpsLocationPackman= new Point3D(1,2,0);
		Point3D gpsLocationFruit= new Point3D(3,4,0);
		assertTrue(gpsLocationPackman.equals(packman.getGpsLocation()));
		assertTrue(gpsLocationFruit.equals(fruit.getGpsLocation()));
		assertEquals(1, packman.getId());
		assertEquals(2, fruit.getId());
		assertEquals(10, packman.getSpeed());
		assertEquals(1, fruit.getWeight());
		assertEquals(1, packman.getRadius());

		
	}
	
	@Test
	public void test_saveToCsv() throws IOException {
		
		String csvFileName= "gameWriteTest.csv";
		
		(new File(csvFileName)).delete();
		
		
		List<Packman> packmans = new ArrayList<Packman>();
		List<Fruit> fruits = new ArrayList<Fruit>();
		packmans.add(new Packman(0 , new Point3D(1,2,3), 1, 1));
		packmans.add(new Packman(1, new Point3D(4, 5, 6) ,10, 11));
		packmans.add(new Packman(2, new Point3D(7, 8,9), 100, 111));
		fruits.add(new Fruit(4,new Point3D(10,11,12), 22));
		fruits.add(new Fruit(5, new Point3D(13,14,15), 33));
		fruits.add(new Fruit(6, new Point3D(16,17,18), 44));
		(new Game(packmans, fruits)).saveToCsv(csvFileName);
		
		
		Game game= new Game(csvFileName);
		packmans = game.getPackmans();
		fruits = game.getFruits();
		assertNotNull(packmans);
		assertNotNull(fruits);
		assertEquals(3, packmans.size());
		assertEquals(3, fruits.size());
		Packman packman = packmans.get(0);
		Fruit fruit = fruits.get(0);
		Point3D gpsLocationPackman= new Point3D(1,2,3);
		Point3D gpsLocationFruit= new Point3D(10,11,12);
		assertTrue(gpsLocationPackman.equals(packman.getGpsLocation()));
		assertTrue(gpsLocationFruit.equals(fruit.getGpsLocation()));
		assertEquals(0, packman.getId());
		assertEquals(4, fruit.getId());
		assertEquals(1, packman.getSpeed());
		assertEquals(22, fruit.getWeight());
		assertEquals(1, packman.getRadius());
		
		
	} 
	
}
