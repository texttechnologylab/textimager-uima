package org.hucompute.textimager.uima.wiki;

import java.io.Serializable;
import java.util.Objects;

import com.google.gson.Gson;

public class WikidataHyponymObject implements Serializable{
	private static final long serialVersionUID = 3444885324460109932L;

	public String linkTo;
	public int depth;
	public boolean isInstanceOf;
	
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	@Override
	public int hashCode() {
		return Objects.hash(linkTo,depth,isInstanceOf);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WikidataHyponymObject other = (WikidataHyponymObject) obj;
		if (depth != other.depth)
			return false;
		if (linkTo == null) {
			if (other.linkTo != null)
				return false;
		} else if (!linkTo.equals(other.linkTo))
			return false;
		return true;
	}
}