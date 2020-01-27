package org.hucompute.textimager.uima.io.mediawiki;

import java.lang.Iterable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import org.hucompute.services.type.CategoryCoveredTagged;

public class SortedDDCSet implements Iterable<CategoryCoveredTagged> {

	/** Rank CategoryCoveredTagged by their plausibility. */
	private class CategoryCoveredTaggedPlausibilityComparator implements Comparator<CategoryCoveredTagged> {
		/** This will order objects from high to low. */
		public int compare(CategoryCoveredTagged o1, CategoryCoveredTagged o2) {
			if (o1 != null && o2 != null) {
				return (int)((o2.getScore() * 100) - (o1.getScore() * 100));
			} else if (o1 == null) {
				return -1;
			} else {
				return +1;
			}
		}

		public boolean equals(Object obj) {
			return obj == this;
		}
	}
		
	TreeSet<CategoryCoveredTagged> ddcs = new TreeSet<CategoryCoveredTagged>(new CategoryCoveredTaggedPlausibilityComparator());

	public void add(CategoryCoveredTagged cct) {
		for (CategoryCoveredTagged old : ddcs) {
			if (old.getValue().replaceAll("__label_ddc__", "").equals(cct.getValue().replaceAll("__label_ddc__", ""))) {
				if (old.getScore() < cct.getScore()) {
					ddcs.remove(old);
					ddcs.add(cct);
					return;
				}
			}
		}
		ddcs.add(cct);
	}

	public void addAll(Iterable<CategoryCoveredTagged> iterable) {
		for (CategoryCoveredTagged cct : iterable) {
			add(cct);
		}
	}

	public CategoryCoveredTagged get(int index) {
		int i = 0;
		for (CategoryCoveredTagged cct : ddcs) {
			if (i == index) {
				return cct;
			}
			i++;
		}
		return null;
	}

	public boolean isEmpty() {
		return ddcs.isEmpty();
	}

	public Iterator<CategoryCoveredTagged> iterator() {
		return ddcs.iterator();
	}
}
