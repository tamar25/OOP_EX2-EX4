package Algorithms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import GIS.ElementMetaData;
import GIS.GIS_element_class;
import GIS.GIS_layer;
import GIS.GIS_layer_class;
import GIS.GIS_project;
import GIS.GIS_project_class;
import GIS.LayerMetaData;
import Geom.Point3D;

public class MultiCSV {
	List<String> filenames = new LinkedList<String>();

	public void listFilesForFolder(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				if (fileEntry.getName().contains(".csv"))
					filenames.add(fileEntry.getName());
			}
		}
	}

	public GIS_project createGisDataStructue(String rootDir) throws IOException {
		File folder = new File(rootDir);
		MultiCSV multicsv = new MultiCSV();
		multicsv.listFilesForFolder(folder);
		List<String> csvList = multicsv.filenames;
		GIS_project project = new GIS_project_class();
		for (String csvFile : csvList) {
			GIS_layer layer = createGisLayer(csvFile);
			project.add(layer);
		}
		return project;
	}

	private GIS_layer createGisLayer(String csvFileName) throws IOException {
		String delimiter = ",";
		BufferedReader csvReader;
		csvReader = new BufferedReader(new FileReader(csvFileName));
		int fieldCount = 0;
		String[] csvFields = null;
		StringTokenizer stringTokenizer = null;
		String firstLine = csvReader.readLine();
		LayerMetaData layerMetaData = new LayerMetaData(firstLine);
		String filename = csvFileName.substring(0, csvFileName.length() - 4);

		GIS_layer_class gis_layer_class = new GIS_layer_class(layerMetaData, filename);

		// Assumes the second line in CSV file is column/field names
		// The column names are used to name the elements in the XML file,
		// avoid the use of Space or other characters not suitable for XML element
		// naming

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

				Map<String, String> desc = new HashMap<String, String>();

				while (stringTokenizer.hasMoreElements()) {
					try {

						String curValue = String.valueOf(stringTokenizer.nextElement());
						String fieldName = csvFields[i++];
						if (fieldName.equals("CurrentLatitude")) {
							lat = Double.parseDouble(curValue);
						} else if (fieldName.equals("CurrentLongitude")) {
							lon = Double.parseDouble(curValue);
						} else if (fieldName.equals("AltitudeMeters")) {
							alt = Double.parseDouble(curValue);
						}

						else {
							desc.put(fieldName, curValue);
						}
						ElementMetaData metaData = new ElementMetaData(desc);
						gis_layer_class.add(new GIS_element_class(new Point3D(lat, lon, alt), metaData));
					} catch (Exception exp) {
					}
				}

			}
		}
		csvReader.close();
		return gis_layer_class;
	}

	public static void main(String[] args) {
		String path = System.getProperty("user.dir");
		final File folder = new File(path);
		MultiCSV multicsv = new MultiCSV();
		multicsv.listFilesForFolder(folder);
		System.out.println(multicsv.filenames);
	}
}