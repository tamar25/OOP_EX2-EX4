package File_format;


public class Csv2XmlTest {

	public static void main(String[] args) {
		Csv2Xml convert = new Csv2Xml();
		String cwd = System.getProperty("user.dir");
        System.out.println("Current working directory : " + cwd);
		convert.convertFile("csvFile.csv", "xmlFileName.xml, ",",");

	}

}
