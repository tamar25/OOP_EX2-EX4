package GIS;

import java.util.HashMap;
import java.util.Map;

import Geom.Point3D;

public class ElementMetaData implements Meta_data {
	private Map<String, String> metadata;

	public ElementMetaData(Map<String, String> metadata) {
		this.metadata = new HashMap<String, String>(metadata);
	}

	@Override
	public long getUTC() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Point3D get_Orientation() {
		// TODO Auto-generated method stub
		return null;
	}

}
