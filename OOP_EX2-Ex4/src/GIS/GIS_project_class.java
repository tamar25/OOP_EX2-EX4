package GIS;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GIS_project_class implements GIS_project {

	private Set<GIS_layer> layers = new HashSet<GIS_layer>();
	private ProjectMetaData projectMetaData;
	
	public boolean add(GIS_layer e) {
		return layers.add(e);
	}

	public boolean addAll(Collection<? extends GIS_layer> c) {
		return layers.addAll(c);
	}

	public void clear() {
		layers.clear();
	}

	public boolean contains(Object o) {
		return layers.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return layers.containsAll(c);
	}

	public boolean equals(Object o) {
		return layers.equals(o);
	}

	public int hashCode() {
		return layers.hashCode();
	}

	public boolean isEmpty() {
		return layers.isEmpty();
	}

	public Iterator<GIS_layer> iterator() {
		return layers.iterator();
	}


	public boolean remove(Object o) {
		return layers.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		return layers.removeAll(c);
	}


	public boolean retainAll(Collection<?> c) {
		return layers.retainAll(c);
	}

	public int size() {
		return layers.size();
	}

	public Object[] toArray() {
		return layers.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return layers.toArray(a);
	}

	@Override
	public Meta_data get_Meta_data() {
		return projectMetaData;
	}
	
	public void toKML() {
		for (GIS_layer gis_layer : layers) {
			gis_layer.toKML();
		}
	}
}
