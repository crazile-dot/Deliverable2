package main;

import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.evaluation.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestWekaProbabilities {

    private static Logger logger;

    public static void main(String[] args) throws Exception{

        // load datasets
        Instances training = DataSource.read("C:/Program Files/Weka-3-8-5/data/breast-cancerKnown.arff");
        Instances testing = DataSource.read("C:/Program Files/Weka-3-8-5/data/breast-cancerNOTK.arff");
        int numAttr = training.numAttributes();
        training.setClassIndex(numAttr - 1);
        testing.setClassIndex(numAttr - 1);

        // make sure they're compatible
        String msg = training.equalHeadersMsg(testing);
        if (msg != null)
          throw new IOException(msg);

        int numtesting = testing.numInstances();
        logger.log(Level.INFO, "There are " + numtesting + " test instances\n");

        RandomForest classifier = new RandomForest();
        classifier.buildClassifier(training);

        // Loop over each test instance.
        for (int i = 0; i < numtesting; i++) {
            // Get the true class label from the instance's own classIndex.
            String trueClassLabel =
                testing.instance(i).toString(testing.classIndex());

            // Make the prediction here.
            double predictionIndex =
                classifier.classifyInstance(testing.instance(i));

            // Get the predicted class label from the predictionIndex.
            String predictedClassLabel =
                testing.classAttribute().value((int) predictionIndex);

            // Get the prediction probability distribution.
            double[] predictionDistribution =
                classifier.distributionForInstance(testing.instance(i));

            // Print out the true label, predicted label, and the distribution.
            logger.log(Level.INFO, i + ": true=" + trueClassLabel + " predicted=" + predictedClassLabel + " distribution=");

            // Loop over all the prediction labels in the distribution.
                for (int predictionDistributionIndex = 0;
                     predictionDistributionIndex < predictionDistribution.length;
                     predictionDistributionIndex++) {

                    // Get this distribution index's class label.
                    String predictionDistributionIndexAsClassLabel =
                        testing.classAttribute().value(
                            predictionDistributionIndex);

                    // Get the probability.
                    double predictionProbability =
                        predictionDistribution[predictionDistributionIndex];

                    logger.log(Level.INFO, predictionDistributionIndexAsClassLabel + "% : " + predictionProbability + "%");
                }
                System.out.printf("\n");
            }
        Evaluation eval = new Evaluation(testing);
        eval.evaluateModel(classifier, testing);

        logger.log(Level.INFO, "AUC = "+eval.areaUnderROC(1));
        logger.log(Level.INFO, "Precision = "+eval.precision(1));
        logger.log(Level.INFO, "Recall = "+eval.recall(1));
    }
}