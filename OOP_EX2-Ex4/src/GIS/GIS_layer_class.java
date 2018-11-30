package GIS;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import File_format.Csv2kml;
import Geom.Geom_element;
import Geom.Point3D;

public class GIS_layer_class implements GIS_layer {

	private Set<GIS_element> elements = new HashSet<GIS_element>();
	private LayerMetaData layerMetaData;
	private String filename;
	
	public GIS_layer_class(LayerMetaData layerMetaData, String filename) {
		super();
		this.layerMetaData = layerMetaData;
		this.filename = filename;
	}

	public boolean add(GIS_element e) {
		return elements.add(e);
	}

	public boolean addAll(Collection<? extends GIS_element> c) {
		return elements.addAll(c);
	}

	public void clear() {
		elements.clear();
	}

	public boolean contains(Object o) {
		return elements.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return elements.containsAll(c);
	}

	public boolean equals(Object o) {
		return elements.equals(o);
	}

	public int hashCode() {
		return elements.hashCode();
	}

	public boolean isEmpty() {
		return elements.isEmpty();
	}

	public Iterator<GIS_element> iterator() {
		return elements.iterator();
	}


	public boolean remove(Object o) {
		return elements.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		return elements.removeAll(c);
	}


	public boolean retainAll(Collection<?> c) {
		return elements.retainAll(c);
	}

	public int size() {
		return elements.size();
	}

	public Object[] toArray() {
		return elements.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return elements.toArray(a);
	}

	@Override
	public Meta_data get_Meta_data() {
		return layerMetaData;
	}

	@Override
	public void toKML() {
		Csv2kml convert = new Csv2kml();

		convert.convertFile(filename + ".csv", filename + ".kml", ",");		
	}
}
