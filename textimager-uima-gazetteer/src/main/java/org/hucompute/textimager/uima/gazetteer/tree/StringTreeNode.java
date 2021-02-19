package org.hucompute.textimager.uima.gazetteer.tree;

import org.apache.commons.collections4.iterators.ListIteratorWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

/**
 * Created on 07.02.20.
 */
public class StringTreeNode implements ITreeNode {
	
	public final StringTreeNode parent;
	public final ConcurrentMap<String, StringTreeNode> children;
	
	private String value;
	private final Pattern tokenBoundaryRegex;
	private boolean toLowerCase;
	
	
	/**
	 * Create a root node.
	 */
	public StringTreeNode(String tokenBoundaryRegex, boolean toLowerCase) {
		this.toLowerCase = toLowerCase;
		this.tokenBoundaryRegex = Pattern.compile(tokenBoundaryRegex, Pattern.UNICODE_CHARACTER_CLASS);
		this.parent = null;
		this.children = new ConcurrentHashMap<>(1, 1);
		this.value = null;
	}
	
	/**
	 * Create a regular node.
	 *
	 * @param parent
	 */
	public StringTreeNode(StringTreeNode parent, Pattern tokenBoundaryRegex) {
		this.parent = parent;
		this.children = new ConcurrentHashMap<>(1, 1);
		this.value = null;
		this.tokenBoundaryRegex = tokenBoundaryRegex;
	}
	
	public boolean hasValue() {
		return this.value != null;
	}
	
	public boolean isLeaf() {
		return this.children.isEmpty();
	}
	
	public void insert(String value) {
		if (toLowerCase)
			value = value.toLowerCase();
		ArrayDeque<String> arrayDeque = new ArrayDeque<>();
		Collections.addAll(arrayDeque, tokenBoundaryRegex.split(value.trim()));
		this.insert(arrayDeque, value);
	}
	
	private void insert(ArrayDeque<String> stringDeque, final String value) {
		if (stringDeque.isEmpty()) {
			this.value = value;
			return;
		}
		
		String key = stringDeque.pop();
		synchronized (this.children) {
			if (!this.children.containsKey(key)) {
				this.children.put(key, new StringTreeNode(this, this.tokenBoundaryRegex));
			}
		}
		this.children.get(key).insert(stringDeque, value);
	}
	
	public ImmutablePair<String, Integer> traverse(@Nonnull List<String> fullString) {
		return this.traverse(new ListIteratorWrapper<>(fullString.iterator()), null);
	}
	
	private ImmutablePair<String, Integer> traverse(@Nonnull ListIteratorWrapper<String> listIterator, @Nullable String lastValue) {
		// if there are further tokens
		if (listIterator.hasNext()) {
			// save value if this node has one
			if (this.value != null) {
				lastValue = this.value;
			}
			
			// traverse the tree recursively
			String key = listIterator.next();
			if (this.children.containsKey(key)) {
				return this.children.get(key).traverse(listIterator, lastValue);
			}
			listIterator.previous();
		}
		
		int previousIndex = listIterator.hasPrevious() ? listIterator.previousIndex() : -1;
		if (this.value == null) {
			return ImmutablePair.of(lastValue, previousIndex);
		} else {
			return ImmutablePair.of(this.value, previousIndex);
		}
	}
	
	public int size() {
		return 1 + this.children.values().stream().mapToInt(StringTreeNode::size).sum();
	}
	
	public int leafs() {
		if (this.children.size() == 0) {
			return 1;
		} else {
			return this.children.values().stream().mapToInt(StringTreeNode::leafs).sum();
		}
	}
	
	public int nodesWithValue() {
		int val = this.hasValue() ? 1 : 0;
		return val + this.children.values().stream().mapToInt(StringTreeNode::nodesWithValue).sum();
	}
	
	@Override
	public String toString() {
		String node = "";
		if (this.hasValue()) {
			node = String.format("\"isLeaf\":\"%b\", \"value\":\"%s\"", this.isLeaf(), value);
		}
		String sChildren = "";
		if (!this.isLeaf()) {
			ArrayList<String> strings = new ArrayList<>();
			for (Map.Entry<String, StringTreeNode> entry : this.children.entrySet()) {
				strings.add(String.format("\"%s\": {%s}", entry.getKey(), entry.getValue().toString()));
			}
			sChildren = String.join(",\n", strings) + "";
		}
		String s = node + (StringUtils.isNotBlank(node) && StringUtils.isNotBlank(sChildren) ? ", " : "") + sChildren;
		
		if (this.parent == null)
			return "{\"StringTree\": {" + s + "}}";
		else
			return s;
	}
	
	@Override
	public int depth() {
		return 1 + this.children.values().stream().map(StringTreeNode::depth).max(Integer::compareTo).orElse(0);
	}
	
	@Override
	public String getValue() {
		return value;
	}
}
