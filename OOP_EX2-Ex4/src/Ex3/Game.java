package Ex3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import Geom.Point3D;

public class Game {
	private final List<Packman> packmans;
	private final List<Fruit> fruits;

	public Game(List<Packman> packmans, List<Fruit> fruits) {
		this.packmans = new ArrayList<>(packmans);
		this.fruits = new ArrayList<>(fruits);
	}

	public Game(String csvFileName) throws IOException {
		packmans = new ArrayList<Packman>();
		fruits = new ArrayList<Fruit>();

		String delimiter = ",";
		BufferedReader csvReader;
		csvReader = new BufferedReader(new FileReader(csvFileName));
		int fieldCount = 0;
		String[] csvFields = null;
		StringTokenizer stringTokenizer = null;
		String filename = csvFileName.substring(0, csvFileName.length() - 4);
		// Assumes the first line in CSV file is column/field names

		String curLine = csvReader.readLine();
		if (curLine != null) {
			stringTokenizer = new StringTokenizer(curLine, delimiter);
			fieldCount = stringTokenizer.countTokens();
			if (fieldCount > 0) {
				csvFields = new String[fieldCount];
				int i = 0;
				while (stringTokenizer.hasMoreElements())
					csvFields[i++] = String.valueOf(stringTokenizer.nextElement());
			}
		}

		while ((curLine = csvReader.readLine()) != null) {
			stringTokenizer = new StringTokenizer(curLine, delimiter);
			fieldCount = stringTokenizer.countTokens();
			if (fieldCount > 0) {
				int i = 0;
				double lat = 0;
				double lon = 0;
				double alt = 0;
				double speedOrWeight = 0;
				double radius = 0;
				String type = null;
				int id = 0;

				while (stringTokenizer.hasMoreElements()) {
					try {

						String curValue = String.valueOf(stringTokenizer.nextElement());
						String fieldName = csvFields[i++];
						if (fieldName.equals("Lat")) {
							lat = Double.parseDouble(curValue);
						} else if (fieldName.equals("Lon")) {
							lon = Double.parseDouble(curValue);
						} else if (fieldName.equals("Alt")) {
							alt = Double.parseDouble(curValue);
						} else if (fieldName.equals("Speed/Weight")) {
							speedOrWeight = Double.parseDouble(curValue);
						} else if (fieldName.equals("Radius")) {
							radius = Double.parseDouble(curValue);
						}

						else if (fieldName.equals("Type")) {
							type = curValue;
						} else if (fieldName.equals("id")) {
							id = Integer.parseInt(curValue);
						}
					} catch (Exception exp) {
					}
				}
				Point3D gpsLocation = new Point3D(lat, lon, alt);
				if (type != null && type.equals("P")) {
					Packman packman = new Packman(id, gpsLocation, speedOrWeight, radius);
					packmans.add(packman);
				} else if (type != null && type.equals("F")) {
					Fruit fruit = new Fruit(id, gpsLocation, speedOrWeight);
					fruits.add(fruit);
				}

			}
		}
		csvReader.close();
	}

	public void saveToCsv(String csvFileName) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File(csvFileName));
		StringBuilder sb = new StringBuilder();

		sb.append("Type");
		sb.append(',');
		sb.append("id");
		sb.append(',');
		sb.append("Lat");
		sb.append(',');
		sb.append("Lon");
		sb.append(',');
		sb.append("Alt");
		sb.append(',');
		sb.append("Speed/Weight");
		sb.append(',');
		sb.append("Radius");
		sb.append('\n');
		
		for (Packman packman : packmans) {
			sb.append("P");
			sb.append(',');
			sb.append(packman.getId());
			sb.append(',');
			Point3D gpsPoint = packman.getGpsLocation();
			sb.append(gpsPoint.x());
			sb.append(',');
			sb.append(gpsPoint.y());
			sb.append(',');
			sb.append(gpsPoint.z());
			sb.append(',');
			sb.append(packman.getSpeed());
			sb.append(',');
			sb.append(packman.getRadius());
			sb.append('\n');
		}
		for (Fruit fruit : fruits) {
			sb.append("F");
			sb.append(',');
			sb.append(fruit.getId());
			sb.append(',');
			Point3D gpsPoint = fruit.getGpsLocation();
			sb.append(gpsPoint.x());
			sb.append(',');
			sb.append(gpsPoint.y());
			sb.append(',');
			sb.append(gpsPoint.z());
			sb.append(',');
			sb.append(fruit.getWeight());
			sb.append('\n');
		}
		pw.write(sb.toString());
		pw.close();
	}

	public List<Packman> getPackmans() {
		return new ArrayList<>(packmans);
	}

	public List<Fruit> getFruits() {
		return new ArrayList<>(fruits);
	}

	public boolean over() {
		return fruits.isEmpty();
	}

	public boolean isValid() {
		return !fruits.isEmpty() && !packmans.isEmpty();	
	}

	public void eatFruit(Fruit fruit) {
		fruits.remove(fruit);
	}

	public void movePackman(Packman packman, Point3D gpsDestination) {
		for (Packman currPackman : packmans) {
			if (currPackman.equals(packman)){
				packman.setGpsLocation(gpsDestination);
				return;
			}
		}
	}
}
