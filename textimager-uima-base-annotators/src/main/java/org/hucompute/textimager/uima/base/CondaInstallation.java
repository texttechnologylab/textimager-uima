package org.hucompute.textimager.uima.base;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;

public class CondaInstallation {

	public void installMiniconda() throws InvalidExitValueException, IOException, InterruptedException, TimeoutException{
		File minicondaSh = File.createTempFile("miniconda", ".sh");
//		minicondaSh.deleteOnExit();
//		System.out.println(minicondaSh);
		String downloadMiniconda = new ProcessExecutor().command(
				"wget",
				"https://repo.anaconda.com/miniconda/Miniconda3-latest-Linux-x86_64.sh",
				"-O",
				minicondaSh.getAbsolutePath())
                .readOutput(true).execute()
                .outputUTF8();   
		System.out.println(downloadMiniconda);
		
		String installMiniconda = new ProcessExecutor().command(
				"bash",
				minicondaSh.getAbsolutePath(),
				"-b",
				"-p",
				Paths.get(System.getProperty("user.home"),".textimager","miniconda").toAbsolutePath().toString())
                .readOutput(true).execute()
                .outputUTF8();   
		System.out.println(installMiniconda);
	}
	
	public void installEnv(){
		
	}
	
	public static void main(String[] args) throws InvalidExitValueException, IOException, InterruptedException, TimeoutException {

			
	}

}
