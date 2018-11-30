package GIS;

import Geom.Geom_element;
import Geom.Point3D;
/**
 * This class implements the interface GIS_element with geometric representation and meta data such as:
 * Orientation, color, string, timing...
 * @author Tamar Goldman
 *
 */
public class GIS_element_class implements GIS_element {
	
	private Geom_element geom_element;
	private ElementMetaData metaData;
	
	public GIS_element_class(Geom_element geom_element, ElementMetaData metaData) {
		super();
		this.geom_element = geom_element;
		this.metaData = metaData;
	}
	public Geom_element getGeom() {
		return geom_element;
	}
	public Meta_data getData() {
		return metaData;
		
	}
	public void translate(Point3D vec) {
		((Point3D) geom_element).add(vec);
	}
}
