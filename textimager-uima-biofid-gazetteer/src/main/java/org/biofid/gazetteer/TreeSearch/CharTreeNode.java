package org.biofid.gazetteer.TreeSearch;

import org.apache.logging.log4j.util.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 07.02.20.
 */
public class CharTreeNode implements ITreeNode {

    public final CharTreeNode parent;
    public final ConcurrentHashMap<Character, CharTreeNode> children;
    public String value;


    /**
     * Create a root node.
     */
    public CharTreeNode() {
        this.parent = null;
        this.children = new ConcurrentHashMap<>();
        this.value = null;
    }

    /**
     * Create a regular node.
     *
     * @param parent
     */
    public CharTreeNode(CharTreeNode parent) {
        this.parent = parent;
        this.children = new ConcurrentHashMap<>();
        this.value = null;
    }

    @Override
    public boolean hasValue() {
        return this.value != null;
    }

    @Override
    public boolean isLeaf() {
        return this.children.isEmpty();
    }

    @Override
    public void insert(String value) {
        this.insert(value, value);
    }

    @Override
    public void insert(String subString, final String value) {
        if (subString.length() == 0) {
            synchronized (this.children) {
                this.value = value;
                return;
            }
        }

        char key = subString.charAt(0);
        synchronized (this.children) {
            if (!this.children.containsKey(key)) {
                this.children.put(key, new CharTreeNode(this));
            }
        }
        this.children.get(key).insert(subString.substring(1), value);
    }
    
    @Override
    public String traverse(@Nonnull String subString) {
        return this.traverse(subString, null);
    }
    
    @Override
    public String traverse(@Nonnull String subString, @Nullable String lastValue) {
        if (subString.length() == 0) {
            return this.value == null ? lastValue : this.value;
        }
        
        char key = subString.charAt(0);
        
        // save value if this node has one
        if (this.value != null) {
            lastValue = this.value;
        }
        
        if (this.children.containsKey(key))
            return this.children.get(key).traverse(subString.substring(1), lastValue);
        else
            return lastValue;
    }
    
    @Override
    public int size() {
        return 1 + this.children.values().stream().mapToInt(CharTreeNode::size).sum();
    }
    
    @Override
    public int leafs() {
        if (this.children.size() == 0) {
            return 1;
        } else {
            return this.children.values().stream().mapToInt(CharTreeNode::leafs).sum();
        }
    }

    @Override
    public int nodesWithValue() {
        int val = this.hasValue() ? 1 : 0;
        return val + this.children.values().stream().mapToInt(CharTreeNode::nodesWithValue).sum();
    }

    @Override
    public String toString() {
        String node = "";
        if (this.hasValue()) {
            node = String.format("\"isLeaf\":\"%b\", \"value\":\"%s\"", this.isLeaf(), value);
        }
        String children = "";
        if (!this.isLeaf()) {
            ArrayList<String> strings = new ArrayList<>();
            for (Map.Entry<Character, CharTreeNode> entry : this.children.entrySet()) {
                strings.add(String.format("\"%s\": {%s}", entry.getKey(), entry.getValue().toString()));
            }
            children = String.join(",\n", strings) + "";
        }
        String s = node + (Strings.isNotBlank(node) && Strings.isNotBlank(children) ? ", " : "") + children;

        if (this.parent == null)
            return "{\"StringTree\": {" + s + "}}";
        else
            return s;
    }
}
