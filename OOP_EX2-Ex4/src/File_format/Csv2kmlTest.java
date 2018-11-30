package File_format;


public class Csv2kmlTest {

	public static void main(String[] args) {
		Csv2kml convert = new Csv2kml();
		String filename1 = "WigleWifi_20171201110209";
		String filename2 = "WigleWifi_20171203085618";

		convert.convertFile(filename1 + ".csv", filename1 + ".kml", ",");
		convert.convertFile(filename2 + ".csv", filename2 + ".kml", ",");


	}

}
