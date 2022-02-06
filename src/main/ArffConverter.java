package main;

import java.io.File;
import java.io.IOException;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;


public class ArffConverter {
	
	private static String[] proj = {"Bookkeeper","Storm"};
	private static String path = "C:\\Users\\Ilenia\\Desktop\\Releases3";
	
	public static String arffCreation(String path) throws IOException {
		File projectClasses = new File(path);
		String newPath = new String();
		if (projectClasses.exists()) {
			CSVLoader loader = new CSVLoader();
			loader.setFieldSeparator(",");
		    loader.setSource(projectClasses);
		    Instances data = loader.getDataSet();
		    
		    // save ARFF
		    ArffSaver saver = new ArffSaver();

			//set the dataset we want to convert
		    saver.setInstances(data);

		    //and save as ARFF
		    String path2 = path.substring(0, path.length()-4);
		    newPath = path2+"_Dataset.arff";
		    saver.setFile(new File(newPath));
		    saver.writeBatch();
		}
		
		return newPath;
	}

}