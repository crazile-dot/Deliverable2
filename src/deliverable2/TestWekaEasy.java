package deliverable2;
/*
 *  How to use WEKA API in Java 
 *  Copyright (C) 2014 
 *  @author Dr Noureddin M. Sadawi (noureddin.sadawi@gmail.com)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it as you wish ... 
 *  I ask you only, as a professional courtesy, to cite my name, web page 
 *  and my YouTube Channel!
 *  
 */

//import required classes
import weka.core.Instances;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.filters.supervised.instance.Resample;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.evaluation.*;




public class TestWekaEasy{
	
	public static String csvForWekaTraining(List <Release> releases, int counter) throws IOException {
		String name = "C:\\Users\\crazile\\Desktop\\Releases" + counter + "_Training_" + ".csv";
		try (BufferedWriter br = new BufferedWriter(new FileWriter(name))) {
			StringBuilder sb = new StringBuilder();
			sb.append("LOC");
			sb.append(",");
			sb.append("Age");
			sb.append(",");
			sb.append("CHG");
			sb.append(",");
			sb.append("MAX_CHG");
			sb.append(",");
			sb.append("AVG_CHG");
			sb.append(",");
			sb.append("Bug Fixed");
			sb.append(",");
			sb.append("NAuth");
			sb.append(",");
			sb.append("Number of Commit");
			sb.append(",");
			sb.append("Buggy");
			sb.append("\n");
			br.write(sb.toString());
			int size = releases.size();
			for (int i = 0 ; i < size; i++) {
				for (Class c : releases.get(i).getReleaseClasses()) {
					StringBuilder sb2 = new StringBuilder();
					sb2.append(c.getLoc());
					sb2.append(",");
					sb2.append(TempMetrics.classAge(c));
					sb2.append(",");
					sb2.append(c.getChg());
					sb2.append(",");
					sb2.append(c.getMaxChg());
					sb2.append(",");
					sb2.append(TempMetrics.getAVGChg(c));
					sb2.append(",");
					sb2.append(TempMetrics.numberOfBugFixedForRelease(releases.get(i), c));
					sb2.append(",");
					sb2.append(c.getAuthors());
					sb2.append(",");
					sb2.append(c.getNRevisions());
					sb2.append(",");
					sb2.append(c.getBugginess());
					sb2.append("\n");
					br.write(sb2.toString());
				}
			}	
		}
		return name;
	}
	
	
	public static String csvForWekaTesting(List <Release> releases, int counter) throws IOException {
		String name = "C:\\Users\\crazile\\Desktop\\Releases" + counter + "_Testing_" + ".csv";
		try (BufferedWriter br = new BufferedWriter(new FileWriter(name))) {
			StringBuilder sb = new StringBuilder();
			sb.append("LOC");
			sb.append(",");
			sb.append("Age");
			sb.append(",");
			sb.append("CHG");
			sb.append(",");
			sb.append("MAX_CHG");
			sb.append(",");
			sb.append("AVG_CHG");
			sb.append(",");
			sb.append("Bug Fixed");
			sb.append(",");
			sb.append("NAuth");
			sb.append(",");
			sb.append("Number of Commit");
			sb.append(",");
			sb.append("Buggy");
			sb.append("\n");
			br.write(sb.toString());
			int size = releases.size();
			for (int i = 0 ; i < size; i++) {
				for (Class c : releases.get(i).getReleaseClasses()) {
					StringBuilder sb2 = new StringBuilder();
					sb2.append(c.getLoc());
					sb2.append(",");
					sb2.append(TempMetrics.classAge(c));
					sb2.append(",");
					sb2.append(c.getChg());
					sb2.append(",");
					sb2.append(c.getMaxChg());
					sb2.append(",");
					sb2.append(TempMetrics.getAVGChg(c));
					sb2.append(",");
					sb2.append(TempMetrics.numberOfBugFixedForRelease(releases.get(i), c));
					sb2.append(",");
					sb2.append(c.getAuthors());
					sb2.append(",");
					sb2.append(c.getNRevisions());
					sb2.append(",");
					sb2.append(c.getBugginess());
					sb2.append("\n");
					br.write(sb2.toString());
				}
			}	
		}
		return name;
	}
	
	
	public static List<String> makeTrainingSet(List<Release> releases) throws IOException {
		List<String> trainingSet = new ArrayList<>();
		for (int i = 1; i <releases.size(); i++) {
			String s = new String();
			//expanding windows
			s = csvForWekaTraining(releases.subList(0,i),i);
			trainingSet.add(s);
		}
		return trainingSet;
	}
	
	
	public static List<String> makeTestingSet(List<Release> releases) throws IOException {
		List<String> testingSet = new ArrayList<>();
		for(int i = 1; i<releases.size(); i++) {
			String s = new String ();
			s = csvForWekaTesting(releases.subList(i, i+1),i+10);
			testingSet.add(s);
		}
		return testingSet;
	}
	
	public static void walkForward(List <Release> releases) throws Exception {
		List<String> trainingSet = makeTrainingSet(releases);
		List<String> testingSet= makeTestingSet(releases);
		for(int z = 0; z< testingSet.size(); z ++) {
			//prima di fare questo passaggio devo convertire in arff
			TestWekaEasy.wekaAction(testingSet.get(z), trainingSet.get(z), z);
		}
	}
	
	
	public static void wekaAction(String trainingSet, String testingSet, int z) throws Exception{
		//load datasets
				DataSource source1 = new DataSource(trainingSet);
				Instances training = source1.getDataSet();
				DataSource source2 = new DataSource(testingSet);
				Instances testing = source2.getDataSet();
	
				int numAttr = training.numAttributes();
				training.setClassIndex(numAttr - 1);
				testing.setClassIndex(numAttr - 1);

				NaiveBayes classifier = new NaiveBayes();

				classifier.buildClassifier(training);

				Evaluation eval = new Evaluation(testing);	

				eval.evaluateModel(classifier, testing); 
				
				System.out.println("precision = "+eval.precision(1));
				System.out.println("recall = "+eval.recall(1));
				System.out.println("AUC = "+eval.areaUnderROC(1));
				System.out.println("kappa = "+eval.kappa());
			
				
	}
}