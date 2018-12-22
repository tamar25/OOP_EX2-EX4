package Ex3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Path2KML {

	List<String> filenames = new LinkedList<String>();
	protected DocumentBuilderFactory domFactory = null;
	protected DocumentBuilder domBuilder = null;

	public Path2KML() {
		try {
			domFactory = DocumentBuilderFactory.newInstance();
			domBuilder = domFactory.newDocumentBuilder();
		} catch (FactoryConfigurationError exp) {
			System.err.println(exp.toString());
		} catch (ParserConfigurationException exp) {
			System.err.println(exp.toString());
		} catch (Exception exp) {
			System.err.println(exp.toString());
		}
	}

	public void listFilesForFolder(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				if (fileEntry.getName().contains(".csv"))
					filenames.add(fileEntry.getPath());
			}
		}
	}

	public static void main(String[] args) throws IOException {
		String path = "lastgame";
		final File folder = new File(path);
		Path2KML path2kml = new Path2KML();
		path2kml.listFilesForFolder(folder);
		System.out.println(path2kml.filenames);
		String delimiter = ",";
		int rowsCount = -1;
		try {
			Document newDoc = path2kml.domBuilder.newDocument();
			// Root element
			Element rootElement = newDoc.createElement("Folder");
			newDoc.appendChild(rootElement);
			Element curElem = newDoc.createElement("name");
			curElem.appendChild(newDoc.createTextNode("Game"));
			rootElement.appendChild(curElem);
			Element packmanStyle = newDoc.createElement("Style");
			packmanStyle.setAttribute("id", "packman");
			Element iconStyle = newDoc.createElement("IconStyle");
			Element icon = newDoc.createElement("Icon");
			Element href = newDoc.createElement("href");
			href.appendChild(newDoc.createTextNode("pacman.png"));
			icon.appendChild(href);
			iconStyle.appendChild(icon);
			packmanStyle.appendChild(iconStyle);
			rootElement.appendChild(packmanStyle);

			Element fruitStyle = newDoc.createElement("Style");
			fruitStyle.setAttribute("id", "fruit");
			Element iconStyle2 = newDoc.createElement("IconStyle");
			Element icon2 = newDoc.createElement("Icon");
			Element href2 = newDoc.createElement("href");
			href2.appendChild(newDoc.createTextNode("fruit.png"));
			icon2.appendChild(href2);
			iconStyle2.appendChild(icon2);
			fruitStyle.appendChild(iconStyle2);
			rootElement.appendChild(fruitStyle);
			for (int ii = 0; ii < path2kml.filenames.size(); ii++) {
				String csvFileName = path2kml.filenames.get(ii);

				// Read csv file
				BufferedReader csvReader;
				String[] split = csvFileName.split("_");
				int last = split.length - 1;
				double startTime = Double.valueOf(split[last].substring(0, split[last].length() - 4));
				double endTime = startTime;
				if (ii < path2kml.filenames.size() - 1) {
					String csvFileNameNext = path2kml.filenames.get(ii + 1);
					split = csvFileNameNext.split("_");
					last = split.length - 1;
					endTime = Double.valueOf(split[last].substring(0, split[last].length() - 4));
				} else {
					endTime = startTime + 10;
				}
				csvReader = new BufferedReader(new FileReader(csvFileName));
				int fieldCount = 0;
				String[] csvFields = null;
				StringTokenizer stringTokenizer = null;
				// Assumes the second line in CSV file is column/field names
				// The column names are used to name the elements in the XML file,
				// avoid the use of Space or other characters not suitable for XML element
				// naming

				String curLine = csvReader.readLine();
				if (curLine != null) {
					// how about other form of csv files?
					stringTokenizer = new StringTokenizer(curLine, delimiter);
					fieldCount = stringTokenizer.countTokens();
					if (fieldCount > 0) {
						csvFields = new String[fieldCount];
						int i = 0;
						while (stringTokenizer.hasMoreElements())
							csvFields[i++] = String.valueOf(stringTokenizer.nextElement());
					}
				}

				// At this point the coulmns are known, now read data by lines
				while ((curLine = csvReader.readLine()) != null) {
					stringTokenizer = new StringTokenizer(curLine, delimiter);
					fieldCount = stringTokenizer.countTokens();
					if (fieldCount > 0) {
						Element rowElement = newDoc.createElement("Placemark");

						Element TimeSpan = newDoc.createElement("TimeSpan");
						Element begin = newDoc.createElement("begin");
						Element end = newDoc.createElement("end");
						Calendar c = Calendar.getInstance();
						int minute = (int) (startTime / 60);
						int second = (int) (startTime % 60);

						c.set(2019, 1, 1, 0, minute, second);
						SimpleDateFormat yyyyMMddTHHmmssSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
						begin.appendChild(newDoc.createTextNode(yyyyMMddTHHmmssSDF.format(c.getTime())));
						minute = (int) (endTime / 60);
						second = (int) (endTime % 60);
						c.set(2019, 1, 1, 0, minute, second);
						end.appendChild(newDoc.createTextNode(yyyyMMddTHHmmssSDF.format(c.getTime())));

						TimeSpan.appendChild(begin);
						TimeSpan.appendChild(end);

						rowElement.appendChild(TimeSpan);
						int i = 0;
						String lat = null;
						String lon = null;
						String alt = null;

						Map<String, String> desc = new HashMap<String, String>();

						while (stringTokenizer.hasMoreElements()) {
							try {

								String curValue = String.valueOf(stringTokenizer.nextElement());
								String fieldName = csvFields[i++];
								if (fieldName.equals("id")) {
									fieldName = "name";
									Element curElement = newDoc.createElement(fieldName);
									CDATASection cdata = newDoc.createCDATASection(curValue);
									curElement.appendChild(cdata);
									rowElement.appendChild(curElement);
								} else if (fieldName.equals("Type")) {
									if (curValue.equals("P")) {
										Element styleUrl = newDoc.createElement("styleUrl");
										styleUrl.appendChild(newDoc.createTextNode("#packman"));
										rowElement.appendChild(styleUrl);
									} else {
										Element styleUrl = newDoc.createElement("styleUrl");
										styleUrl.appendChild(newDoc.createTextNode("#fruit"));
										rowElement.appendChild(styleUrl);
									}
								} else if (fieldName.equals("Lat")) {
									lat = curValue;
								} else if (fieldName.equals("Lon")) {
									lon = curValue;
								} else if (fieldName.equals("Alt")) {
									alt = curValue;
								} else {
									desc.put(fieldName, curValue);
									// Element curElement = newDoc.createElement(fieldName);
									// curElement.appendChild(newDoc.createTextNode(curValue));
									// rowElement.appendChild(curElement);
								}

								if (lat != null && lon != null && alt != null) {
									Element curElement = newDoc.createElement("Point");

									if (Double.valueOf(alt) > 0) {
										Element extrude = newDoc.createElement("extrude");
										extrude.appendChild(newDoc.createTextNode("1"));
										curElement.appendChild(extrude);

										Element altitudeMode = newDoc.createElement("altitudeMode");
										altitudeMode.appendChild(newDoc.createTextNode("relativeToGround"));
										curElement.appendChild(altitudeMode);
									}
									Element coords = newDoc.createElement("coordinates");
									curValue = lon + "," + lat + "," + alt;
									coords.appendChild(newDoc.createTextNode(curValue));
									curElement.appendChild(coords);

									rowElement.appendChild(curElement);
									lat = null;
									lon = null;
									alt = null;

								}

							} catch (Exception exp) {
							}
						}

						Element curElement = newDoc.createElement("description");
						String desc_str = "";
						boolean first = true;
						for (String key : desc.keySet()) {
							if (!first) {
								desc_str += "<br/>";
							}
							String value = desc.get(key);
							desc_str += key;
							desc_str += ": <b>" + value + "</b>";
							first = false;

						}
						CDATASection cdata = newDoc.createCDATASection(desc_str);
						curElement.appendChild(cdata);
						rowElement.appendChild(curElement);

						rootElement.appendChild(rowElement);
						rowsCount++;
					}
				}
				csvReader.close();

				// Save the document to the disk file
				TransformerFactory tranFactory = TransformerFactory.newInstance();
				Transformer aTransformer = tranFactory.newTransformer();
				aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
				aTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
				aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
				Source src = new DOMSource(newDoc);
				Result result = new StreamResult(new File("test.kml"));
				aTransformer.transform(src, result);
				rowsCount++;

			}

		} catch (IOException exp) {
			System.err.println(exp.toString());
		} catch (Exception exp) {
			System.err.println(exp.toString());
		}

	}
}
