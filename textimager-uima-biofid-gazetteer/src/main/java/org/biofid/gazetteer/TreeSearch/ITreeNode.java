package org.biofid.gazetteer.TreeSearch;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ITreeNode {
    boolean hasValue();

    boolean isLeaf();

    void insert(String value);

    void insert(String subString, String value);

    int size();

    int leafs();

    int nodesWithValue();

    String traverse(@Nonnull String subString);

    String traverse(@Nonnull String subString, @Nullable String lastValue);

    @Override
    String toString();
}
