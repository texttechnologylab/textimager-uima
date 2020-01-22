package org.hucompute.textimager.uima.spacy;

import jep.Interpreter;
import jep.JepException;
import jep.MainInterpreter;
import jep.PyConfig;
import jep.SharedInterpreter;

public class Simple {

	public static void main(String[] args) throws JepException {
		PyConfig config = new PyConfig();
		config.setPythonHome("/home/ahemati/miniconda3/envs/spacy");				
		MainInterpreter.setInitParams(config);
		try (Interpreter interp = new SharedInterpreter()) {
			interp.exec("import sys");
            interp.exec("sys.argv=[]");
            interp.exec("import spacy");
            interp.exec("from java.lang import System");
            interp.exec("s = 'Hello World'");
            interp.exec("System.out.println(s)");
            interp.exec("print(s)");
            interp.exec("print(s[1:-1])");
		}
	}
}
