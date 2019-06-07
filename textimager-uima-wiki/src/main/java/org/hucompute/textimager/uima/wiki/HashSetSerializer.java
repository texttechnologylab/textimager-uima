package org.hucompute.textimager.uima.wiki;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;

import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

public class HashSetSerializer implements Serializable,Serializer<HashSet<WikidataHyponymObject>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7297871012667258562L;

	@Override
	public HashSet<WikidataHyponymObject> deserialize(DataInput2 arg0, int arg1) throws IOException {
		HashSet<WikidataHyponymObject>output = new HashSet<>();
		while(arg0.getPos()!= arg1){
			WikidataHyponymObject out = new WikidataHyponymObject();
			out.linkTo = arg0.readLine();
			out.depth = arg0.readInt();
			out.isInstanceOf = arg0.readBoolean();
			output.add(out);
		}
		return output;
	}

	@Override
	public void serialize(DataOutput2 arg0, HashSet<WikidataHyponymObject> arg1) throws IOException {
		for (WikidataHyponymObject wikidataHyponymObject : arg1) {
			if(wikidataHyponymObject.linkTo != null){
				arg0.writeChars(wikidataHyponymObject.linkTo);
				arg0.writeInt(wikidataHyponymObject.depth);
				arg0.writeBoolean(wikidataHyponymObject.isInstanceOf);
			}
		}
	}
}