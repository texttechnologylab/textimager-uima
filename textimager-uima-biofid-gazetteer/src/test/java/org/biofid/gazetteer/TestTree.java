package org.biofid.gazetteer;

import com.google.common.base.Strings;
import org.apache.commons.io.IOUtils;
import org.biofid.gazetteer.TreeSearch.CharTreeNode;
import org.biofid.gazetteer.TreeSearch.StringTreeNode;
import org.junit.Test;

import java.io.*;

public class TestTree {

    @Test
    public void TestCharTree() throws IOException {
        CharTreeNode tree = new CharTreeNode();
        tree.insert("test file");
        tree.insert("test file text");
        System.out.println(tree);

        try (FileReader reader = new FileReader(new File("src/test/resources/text.txt"))) {
            String query = String.join(" ", IOUtils.readLines(reader));

            int offset = -1;
            do {
                offset = offset + 1;
                String substring = query.substring(offset);
                String match = tree.traverse(substring);
                if (!Strings.isNullOrEmpty(match)) {
                    System.out.println(String.format("%d, %d, %s", offset, offset + match.length(), match));
                    offset += match.length();
                }
                offset = query.indexOf(" ", offset + 1);
            } while (offset < query.length() && offset > -1);
        }
    }

    @Test
    public void TestStringTree() throws IOException {
        StringTreeNode tree = new StringTreeNode();
        tree.insert("test file");
        tree.insert("test file text");
        System.out.println(tree);

        try (FileReader reader = new FileReader(new File("src/test/resources/text.txt"))) {
            String query = String.join(" ", IOUtils.readLines(reader));

            int offset = -1;
            do {
                offset = offset + 1;
                String substring = query.substring(offset);
                String match = tree.traverse(substring);

                if (!Strings.isNullOrEmpty(match)) {
                    System.out.println(String.format("%d, %d, %s", offset, offset + match.length(), match));
                    offset += match.length();
                }

                offset = query.indexOf(" ", offset + 1);
            } while (offset < query.length() && offset > -1);
        }
    }
}
