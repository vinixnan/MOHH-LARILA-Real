package HF_choicefunction;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import jmetal.qualityIndicator.HyperVolumeMinimizationProblem;
import jmetal.qualityIndicator.util.MetricsUtil;
import HF_RandomGenerator.RandomGenerator;
import HF_choicefunction.QualityIndicators;
import HF_choicefunction.choiceFunction;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.interfaces.LLHInterface;
import helpers.AlgorithmCreator;
import helpers.ProblemCreator;
import java.util.Arrays;
import java.util.List;
import javax.management.JMException;
import jmetal.qualityIndicator.util.MetricsUtilPlus;
import org.uma.jmetal.operator.Operator;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;

public class AnyProblemChoiceFunction<S extends Solution<?>> {

    int numberOfObj;// hard code, shouldn't do it like this way, but I am
    // lazy...
    int populationSize;// I am doing the wrong thing again... bite me?
    ProblemCreator problemCreator;
    double[] minimumValues;
    double[] maximumValues;
    double[] reference;

    public AnyProblemChoiceFunction(ProblemCreator problemCreator, int populationSize) {
        this.problemCreator = problemCreator;
        this.populationSize = populationSize;
    }

    public void run(int runIndex, String fullPath) throws ClassNotFoundException, IOException, JMException {
        // TODO Auto-generated method stub
        long seed = (long) 100;//can be randomly seeded.
        RandomGenerator RG = new RandomGenerator(seed);

        MetricsUtilPlus utils_ = new MetricsUtilPlus();
        Properties config = new Properties();

        String configFile = "HF_Config_Benchmark/RealProblemSetting.txt";
        String algorithms[] = {"NSGAII", "SPEA2", "IBEA", "mIBEA", "GDE3"};

        try {
            config.load(new FileInputStream(configFile));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load configuration file!");
            System.exit(-1);
        }

        int totalEval = Integer.parseInt(config.getProperty("MaxEvaluations"));
        int decisionPoints = Integer.parseInt(config.getProperty("DecisionPoints"));//50 iterations

        int arcSize = Integer.parseInt(config.getProperty("ArchiveSize"));
        populationSize = Integer.parseInt(config.getProperty("PopulationSize"));
        
        arcSize=30;
        populationSize=30;
        totalEval=1500;

        //int populationSize = popSize;
        int fixedSolutionEvl = (int) (totalEval / decisionPoints);
        
        System.out.println("CF; decision="+decisionPoints+";fixed="+fixedSolutionEvl+";total="+totalEval);

        MetricsUtil util = new MetricsUtil();

        int maxEvals = Integer.parseInt(config.getProperty("MaxEvaluations"));

        String SolutionType = config.getProperty("SolutionType");
        int popSize = Integer.parseInt(config.getProperty("PopulationSize"));

        Problem[] problemInstances = new Problem[problemCreator.getQtdProblem()];
        int[] numberOfVar = new int[problemCreator.getQtdProblem()];
        double[] mutationProb = new double[problemCreator.getQtdProblem()];
        Operator[] mutations = new Operator[problemCreator.getQtdProblem()];

        //reference point for hypervolume calculation
        /*HyperVolumeMinimizationProblem hyp = new HyperVolumeMinimizationProblem();*/
 /*double[] truePFhypervolume = new double[problemCreator.getQtdProblem()];*/
        for (int problemIndex = 0; problemIndex < problemCreator.getQtdProblem(); problemIndex++) {

            problemInstances[problemIndex] = problemCreator.getProblemInstance(problemIndex);
            numberOfVar[problemIndex] = problemInstances[problemIndex].getNumberOfVariables();
            mutationProb[problemIndex] = (Double) 1.0 / numberOfVar[problemIndex];
            HashMap parameters = new HashMap();
            parameters.put("probability", mutationProb[problemIndex]);
            double mutationDistributionIndex = Double.parseDouble(config.getProperty("MutationDistributionIndex"));
            parameters.put("distributionIndex", mutationDistributionIndex);
            String mutationType = config.getProperty("MutationType");
            mutations[problemIndex] = new PolynomialMutation(mutationProb[problemIndex], mutationDistributionIndex);
        }

        LLHInterface[] algorithm = new LLHInterface[problemCreator.getQtdProblem()];

        System.out.println("Run " + runIndex + "....");

        choiceFunction CF = new choiceFunction();

        for (int instanceIndex = 6; instanceIndex < problemInstances.length; instanceIndex++) {
            numberOfObj=problemInstances[instanceIndex].getNumberOfObjectives();
            reference = new double[numberOfObj];
            Arrays.fill(reference,1.0);
            minimumValues = new double[numberOfObj];
            maximumValues = new double[numberOfObj];

            for (int i = 0; i < numberOfObj; i++) {
                reference[i] = 1.0;
            }

            int remainEval = totalEval;
            List<S> inputPop = null;
            ArrayList<Integer> heuristicList = new ArrayList<Integer>();

            int chosenHeuristic = -1;

            QualityIndicators quality = new QualityIndicators();
            double[][] algorithmEffortArray = new double[algorithms.length][2];
            double[][] ROIArray = new double[algorithms.length][2];
            double[][] hyperVolumeArray = new double[algorithms.length][2];
            double[][] uniformDistributionArray = new double[algorithms.length][2];

            // for the purpose of computing hypervolume.
            initialiseExtremeValues();

            long[] elapsedTime = new long[algorithms.length];
            long[] lastInvokeTime = new long[algorithms.length];
            long[] executionTimeArray = new long[algorithms.length];
            for (int i = 0; i < algorithms.length; i++) {
                elapsedTime[i] = 0;
            }

            //initialise population for each instance which is used for each algorithm
            List<S> initialPopulation = ProblemCreator.generateInitialPopulation(problemInstances[instanceIndex], popSize);
            
            List[] resultSolutions = new List[algorithms.length];
            List[] inputSolutions = new List[algorithms.length];

            for (int i = 0; i < algorithms.length; i++) {
                inputSolutions[i] = new ArrayList(popSize);
                // at the beginning, all inputsolutions are set as the initialPopulation
                inputSolutions[i] = initialPopulation;
                resultSolutions[i] = new ArrayList(popSize);
            }
            LLHInterface eachAlgorithm = null;
            AlgorithmCreator ac = new AlgorithmCreator(problemInstances[instanceIndex]);
            System.out.println(problemInstances[instanceIndex].getName() + " " + problemInstances[instanceIndex].getNumberOfObjectives() + " " + problemInstances[instanceIndex].getNumberOfVariables());
            ac.setMaxEvaluationsAndPopulation(totalEval, populationSize);
            //Initialise the choice function matrix and get the initial algorithm
            for (int algorithmIndex = 0; algorithmIndex < algorithms.length; algorithmIndex++) {
                //initialise last invoke time (the start time of calling this algorithm) of each algorithm;
                heuristicList.add(algorithmIndex);
                lastInvokeTime[algorithmIndex] = System.currentTimeMillis();

                eachAlgorithm=ac.create(algorithmIndex, remainEval);

                long startTime = System.currentTimeMillis();

                try {
                    resultSolutions[algorithmIndex] = eachAlgorithm.execute(/*initialPopulation*/inputSolutions[algorithmIndex], fixedSolutionEvl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //record the execution time of each algorithm
                long executionTime = System.currentTimeMillis() - startTime;
                executionTimeArray[algorithmIndex] = executionTime;

                remainEval -= fixedSolutionEvl;

                //iteration += 2;
                inputSolutions[algorithmIndex] = resultSolutions[algorithmIndex];
                //After executed the algorithm, compute the elapsed time. The end time of called this algorithm

            }// end algorithmIndex, executing each algorithm for initialisation the choice function matrix

            //update elapsed time of since the heuristic was last called.
            for (int i = 0; i < algorithms.length; i++) {
                elapsedTime[i] = System.currentTimeMillis() - lastInvokeTime[i];
            }

            // find worst points and best points for all the three populations
            for (int i = 0; i < algorithms.length; i++) {
                updateExtremeValues(resultSolutions[i]);
            }

            for (int i = 0; i < algorithms.length; i++) {
                algorithmEffortArray[i][0] = i;
                algorithmEffortArray[i][1] = quality.getAlgorithmEffort(executionTimeArray[i], fixedSolutionEvl);

                ROIArray[i][0] = i;
                ROIArray[i][1] = quality.getRatioOfNonDominatedIndividuals(resultSolutions[i]);

                hyperVolumeArray[i][0] = i;

                hyperVolumeArray[i][1] = computeScaledHypervolume(resultSolutions[i]);
                //hyperVolumeArray[i][1] = quality.getHyperVolume(resultSolutions[i],realReference);

                uniformDistributionArray[i][0] = i;
                uniformDistributionArray[i][1] = quality.getUniformDistribution(resultSolutions[i]);

            }

            int numberOfMeasures = 4;
            int alpha = 10;//alpha =100;//if turbine number is 30

            chosenHeuristic = CF.getMaxChoiceFunction(algorithmEffortArray, 0, ROIArray, 1, hyperVolumeArray, 1,
                    uniformDistributionArray, 1, numberOfMeasures, alpha, elapsedTime);
            //start time of the chosen heuristic
            lastInvokeTime[chosenHeuristic] = System.currentTimeMillis();

            //the result population of the chosen heuristic is served as the input population
            inputPop = resultSolutions[chosenHeuristic];

            while (remainEval > 0) {
                algorithm[instanceIndex]=ac.create(chosenHeuristic, remainEval);

                long beginTime = System.currentTimeMillis();
                //execute the chosen algorithm
                heuristicList.add(chosenHeuristic);
                if (fixedSolutionEvl > remainEval) {
                    fixedSolutionEvl = remainEval;
                }
                resultSolutions[chosenHeuristic] = algorithm[instanceIndex].execute(inputPop, fixedSolutionEvl);
                inputPop = resultSolutions[chosenHeuristic];
                
                remainEval -= fixedSolutionEvl;

                // update executionTimeArray
                executionTimeArray[chosenHeuristic] = System.currentTimeMillis() - beginTime;
                //update elapsed time of since the heuristic was last called.
                for (int i = 0; i < algorithms.length; i++) {
                    elapsedTime[i] = System.currentTimeMillis() - lastInvokeTime[i];
                }

                algorithmEffortArray[chosenHeuristic][1] = quality.getAlgorithmEffort(executionTimeArray[chosenHeuristic], fixedSolutionEvl);

                ROIArray[chosenHeuristic][1] = quality.getRatioOfNonDominatedIndividuals(resultSolutions[chosenHeuristic]);

                updateExtremeValues(resultSolutions[chosenHeuristic]);
                hyperVolumeArray[chosenHeuristic][1] = computeScaledHypervolume(resultSolutions[chosenHeuristic]);
                //hyperVolumeArray[chosenHeuristic][1] = quality.getHyperVolume(resultSolutions[chosenHeuristic],realReference);

                uniformDistributionArray[chosenHeuristic][1] = quality.getUniformDistribution(resultSolutions[chosenHeuristic]);

                chosenHeuristic = CF.getMaxChoiceFunction(algorithmEffortArray, 0, ROIArray, 1, hyperVolumeArray, 1,
                        uniformDistributionArray, 1, numberOfMeasures, alpha, elapsedTime);
                //start time of the chosen heuristic
                lastInvokeTime[chosenHeuristic] = System.currentTimeMillis();

                //heuristicList.add(chosenHeuristic);		
            }//end while(remainEvl >0)
            
            String arrayListPath = fullPath+"/"+problemCreator.getProblemClass() + (instanceIndex + 1) + "_ChosenHeuristic.txt";
            utils_.printArrayListInteger(heuristicList, arrayListPath);
            List<S> obtainedSolutionSet = SolutionListUtils.getNondominatedSolutions(inputPop);
            new SolutionListOutput(obtainedSolutionSet)
                    .setSeparator("\t")
                    .setFunFileOutputContext(new DefaultFileOutputContext(fullPath+"/"+problemCreator.getProblemClass() + (instanceIndex + 1) + "CF_FinalParetoFront"
                            + runIndex + ".txt"))
                    .print();
            //print out true front's hypervolume

        }//end each problem instance

        System.out.println("Finished");

    }// End main

    // Initialise the extreme values for the first time
    public void initialiseExtremeValues() {
        for (int i = 0; i < numberOfObj; i++) {
            minimumValues[i] = Double.MAX_VALUE;
            maximumValues[i] = -Double.MAX_VALUE;
        }
    }

    public double computeScaledHypervolume(List<S> pop) {

        /*
		 * if(initialFlag){ //initialiseExtremeValues(pop);
		 * initialiseExtremeValues(); }
         */
        // updateExtremeValues(pop);
        // problem specific extreme value for land area
        // maximumValues[0] = 9.0;
        double[][] objValues = SolutionListUtils.writeObjectivesToMatrix(pop);

        HyperVolumeMinimizationProblem hyp = new HyperVolumeMinimizationProblem();
        jmetal.qualityIndicator.util.MetricsUtil utils_ = new jmetal.qualityIndicator.util.MetricsUtil();

        double[][] normalizedObj = utils_.getNormalizedFront(objValues,
                maximumValues, minimumValues);

        double Hypervolume = hyp.hypervolume(normalizedObj, numberOfObj,
                reference);

        return Hypervolume;
    }

    // update extreme values
    public void updateExtremeValues(List<S> solutions) {
        jmetal.qualityIndicator.util.MetricsUtil util_ = new jmetal.qualityIndicator.util.MetricsUtil();

        double[][] objectiveVectors = SolutionListUtils.writeObjectivesToMatrix(solutions);

        double[] tempMinimumValues = util_.getMinimumValues(objectiveVectors,
                numberOfObj);

        double[] tempMaximumValues = util_.getMaximumValues(objectiveVectors,
                numberOfObj);
        boolean printExtreamValues = false;

        for (int i = 0; i < numberOfObj; i++) {
            if (tempMinimumValues[i] < minimumValues[i]) {
                minimumValues[i] = tempMinimumValues[i];
            }
        }
        for (int j = 0; j < numberOfObj; j++) {
            if (tempMaximumValues[j] > maximumValues[j]) {
                maximumValues[j] = tempMaximumValues[j];
            }
        }

        if (printExtreamValues) {
            System.out.println("Maximum Values:");
            for (double value : maximumValues) {
                System.out.print(value + " ");
            }
            System.out.println();
            System.out.println("Minimum Values:");
            for (double value : minimumValues) {
                System.out.print(value + " ");
            }
        }

    }

}
