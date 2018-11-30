package File_format;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

//https://stackoverflow.com/questions/12120055/conversion-of-csv-to-xml-with-java

public class Csv2kml {
	// Protected Properties
	protected DocumentBuilderFactory domFactory = null;
	protected DocumentBuilder domBuilder = null;

	public Csv2kml() {
		try {
			domFactory = DocumentBuilderFactory.newInstance();
			domBuilder = domFactory.newDocumentBuilder();
		} 
		catch (FactoryConfigurationError exp) {
			System.err.println(exp.toString());
		} 
		catch (ParserConfigurationException exp) {
			System.err.println(exp.toString());
		} 
		catch (Exception exp) {
			System.err.println(exp.toString());
		}
	}
	public int convertFile(String csvFileName, String xmlFileName, String delimiter) {
		int rowsCount = -1;
		try {
			Document newDoc = domBuilder.newDocument();
			// Root element
			Element rootElement = newDoc.createElement("Folder");
			newDoc.appendChild(rootElement);
			Element curElem = newDoc.createElement("name");
			curElem.appendChild(newDoc.createTextNode(csvFileName.substring(0, csvFileName.length() - 4)));
			rootElement.appendChild(curElem);
			// Read csv file
			BufferedReader csvReader;
			csvReader = new BufferedReader(new FileReader(csvFileName));
			int fieldCount = 0;
			String[] csvFields = null;
			StringTokenizer stringTokenizer = null;
			String firstLine = csvReader.readLine();//TODO save as layer metadata

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
					int i = 0;
					String lat = null;
					String lon = null;
					Map<String, String> desc = new HashMap<String, String>();

					while (stringTokenizer.hasMoreElements()) {
						try {

							String curValue = String.valueOf(stringTokenizer.nextElement());
							String fieldName = csvFields[i++];
							if (fieldName.equals("SSID")) {
								fieldName = "name";
								Element curElement = newDoc.createElement(fieldName);
								CDATASection cdata = newDoc.createCDATASection(curValue);
								curElement.appendChild(cdata);
								rowElement.appendChild(curElement);

							}
							else if (fieldName.equals("CurrentLatitude")) {
								lat = curValue;
							}
							else if (fieldName.equals("CurrentLongitude")) {
								lon = curValue;
							}
							else {
								desc.put(fieldName, curValue);
//								Element curElement = newDoc.createElement(fieldName);
//								curElement.appendChild(newDoc.createTextNode(curValue));
//								rowElement.appendChild(curElement);
							}
							
							if (lat != null && lon != null) {
								Element curElement = newDoc.createElement("Point");
								Element coords = newDoc.createElement("coordinates");
								curValue = lon + "," + lat;
								coords.appendChild(newDoc.createTextNode(curValue));
								curElement.appendChild(coords);
								rowElement.appendChild(curElement);
								lat = null;
								lon = null;
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
			Result result = new StreamResult(new File(xmlFileName));
			aTransformer.transform(src, result);
			rowsCount++;

			// Output to console for testing
			// Result result = new StreamResult(System.out);

		} catch (IOException exp) {
			System.err.println(exp.toString());
		} catch (Exception exp) {
			System.err.println(exp.toString());
		}
		return rowsCount;
		// "XLM Document has been created" + rowsCount;
	}
}
