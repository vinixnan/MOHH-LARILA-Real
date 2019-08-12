package HF_Main;

import br.usp.poli.pcs.lti.jmetalproblems.problems.Poloni;
import br.usp.poli.pcs.lti.jmetalproblems.problems.Quagliarella;
import helpers.AlgorithmCreator;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.*;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.management.JMException;
import org.uma.jmetal.problem.multiobjective.FourBarTruss;
import org.uma.jmetal.problem.multiobjective.Golinski;
import org.uma.jmetal.problem.multiobjective.ebes.Ebes;
import org.uma.jmetal.solution.Solution;
import uk.ac.nottingham.asap.realproblems.*;

/**
 * Example of experimental study based on solving the problems (configured with
 * 3 objectives) with the algorithms NSGAII, SPEA2, and SMPSO
 * <p>
 * This experiment assumes that the reference Pareto front are known and stored
 * in files whose names are different from the default name expected for every
 * problem. While the default would be "problem_name.pf" (e.g. DTLZ1.pf), the
 * references are stored in files following the nomenclature
 * "problem_name.3D.pf" (e.g. DTLZ1.3D.pf). This is indicated when creating the
 * ExperimentProblem instance of each of the evaluated poblems by using the
 * method changeReferenceFrontTo()
 * <p>
 * Six quality indicators are used for performance assessment.
 * <p>
 * The steps to carry out the experiment are: 1. Configure the experiment 2.
 * Execute the algorithms 3. Compute que quality indicators 4. Generate Latex
 * tables reporting means and medians 5. Generate R scripts to produce latex
 * tables with the result of applying the Wilcoxon Rank Sum Test 6. Generate
 * Latex tables with the ranking obtained by applying the Friedman test 7.
 * Generate R scripts to obtain boxplots
 */
public class RealStudy<S extends Solution<?>> {

    private static final int INDEPENDENT_RUNS = 30;

    public static void main(String[] args) throws IOException, JMException {
        String experimentBaseDirectory;
        if (args.length != 1) {
            experimentBaseDirectory = "output";
        } else {
            experimentBaseDirectory = args[0];
        }

        int core = 3;
        
        List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();

        //problemList.add(new ExperimentProblem<>(new WeldedBeamDesign()));
        //problemList.add(new ExperimentProblem<>(new DiskBrakeDesign()));
        //problems="VibratingPlatformDesign OpticalFilter WeldedBeamDesign DiskBrakeDesign HeatExchanger HydroDynamics"
        //problemList.add(new ExperimentProblem<>(new OpticalFilter()));
        //problemList.add(new ExperimentProblem<>(new VibratingPlatformDesign()));
        //problemList.add(new ExperimentProblem<>(new HeatExchanger()));
        //problemList.add(new ExperimentProblem<>(new HydroDynamics()));
        //problemList.add(new ExperimentProblem<>(new AucMaximization()));
        /*
        problemList.add(new ExperimentProblem<>(new FacilityPlacement()));
        problemList.add(new ExperimentProblem<>(new FourBarTruss()));
        problemList.add(new ExperimentProblem<>(new Golinski()));
        problemList.add(new ExperimentProblem(new Quagliarella()));
        problemList.add(new ExperimentProblem(new Poloni()));
        String ebesFileName="Mobile_Bridge_25N_35B_8G_16OrdZXY.ebe";
        String[] objectiveList={"W","D", "SSAE", "ENS", "MDV"};
        
        problemList.add(new ExperimentProblem(new Ebes(ebesFileName, objectiveList)));
        //problemList.add(new ExperimentProblem<>(new KernelRidgeRegressionParameterTuning()));//pesado
        //problemList.add(new ExperimentProblem<>(new HydroDynamics()));
*/
        //problemList.add(new ExperimentProblem<>(new KernelRidgeRegressionParameterTuning()));
        //problemList.add(new ExperimentProblem<>(new NeuralNetDoublePoleBalancing()));
        problemList.add(new ExperimentProblem<>(new AucMaximization()));
        List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList
                = configureAlgorithmList(problemList);

        Experiment<DoubleSolution, List<DoubleSolution>> experiment
                = new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("REALProblems")
                        .setAlgorithmList(algorithmList)
                        .setProblemList(problemList)
                        .setReferenceFrontDirectory("/pareto_fronts")
                        .setExperimentBaseDirectory(experimentBaseDirectory)
                        .setOutputParetoFrontFileName("FUN")
                        .setOutputParetoSetFileName("VAR")
                        //.setIndicatorList(Arrays.asList(new PISAHypervolume<DoubleSolution>()))
                        .setIndependentRuns(INDEPENDENT_RUNS)
                        .setNumberOfCores(core)
                        .build();

        new ExecuteAlgorithms<>(experiment).run();
        //new ComputeQualityIndicators<>(experiment).run();
        //new GenerateLatexTablesWithStatistics(experiment).run();
        //new GenerateWilcoxonTestTablesWithR<>(experiment).run();
        //new GenerateFriedmanTestTables<>(experiment).run();
        //new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(3).setDisplayNotch().run();
    }

    /**
     * The algorithm list is composed of pairs
     * {@link Algorithm} + {@link Problem} which form part of a
     * {@link ExperimentAlgorithm}, which is a decorator for class
     * {@link Algorithm}.
     */
    static List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> configureAlgorithmList(
            List<ExperimentProblem<DoubleSolution>> problemList) throws JMException {
        List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms = new ArrayList<>();
        for (int run = 0; run < INDEPENDENT_RUNS; run++) {
            for (int i = 0; i < problemList.size(); i++) {
                AlgorithmCreator ac = new AlgorithmCreator(problemList.get(i).getProblem());
                String problemName = problemList.get(i).getProblem().getName();
                int numGenerations = 250;
                int populationSize = 100;
                if (problemName.equals("FacilityPlacement") || problemName.equals("KernelRidgeRegressionParameterTuning")) {
                    populationSize = 30;
                    numGenerations = 50;
                }
                ac.setMaxEvaluationsAndPopulation(numGenerations * populationSize, populationSize);
                for (int j = 0; j < 5; j++) {
                    Algorithm algorithm = (Algorithm) ac.create(j);
                    algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i), run));
                }
            }
        }
        return algorithms;
    }
}
