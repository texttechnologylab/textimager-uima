package org.hucompute.services.uima.database.test;

import java.io.IOException;

import org.apache.uima.UIMAException;

public class DBTest {

	public static void main(String[] args) throws UIMAException, IOException {
		String task = args[0];
		if (task.equals("writer")) {
			DBWriterTest.runTest(args[1]);
		} else if (task.equals("reader")) {
			DBWriterTest.runTest(args[1]);
		} else {
			System.out.println("invalid.");
		}
	}

}
