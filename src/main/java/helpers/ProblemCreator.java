package helpers;

import br.usp.poli.pcs.lti.jmetalproblems.problems.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.FourBarTruss;
import org.uma.jmetal.problem.multiobjective.Golinski;
import org.uma.jmetal.problem.multiobjective.ebes.Ebes;
import org.uma.jmetal.problem.multiobjective.wfg.*;
import org.uma.jmetal.solution.Solution;
import realproblems.vehiclecrashworthiness.*;
import uk.ac.nottingham.asap.realproblems.*;

/**
 *
 * @author vinicius
 */
public class ProblemCreator {

    protected String problemClass;
    protected final HashMap<String, Integer> params;

    public ProblemCreator(String problemClass) {
        this.problemClass = problemClass;
        params = new HashMap<>();
    }

    public void addParam(String name, int value) {
        params.put(name, value);
    }
    
    public int getParam(String name) {
        return params.get(name);
    }
    
    
    protected Problem getWFG(int problemIndex) {
        int m = params.get("m");
        int k = params.get("k");
        int l = params.get("l");
        switch (problemIndex) {
            case 0:
                return new WFG1(k, l, m);
            case 1:
                return new WFG2(k, l, m);
            case 2:
                return new WFG3(k, l, m);
            case 3:
                return new WFG4(k, l, m);
            case 4:
                return new WFG5(k, l, m);
            case 5:
                return new WFG6(k, l, m);
            case 6:
                return new WFG7(k, l, m);
            case 7:
                return new WFG8(k, l, m);
            case 8:
                return new WFG9(k, l, m);
            default:
                return null;
        }
    }

    protected Problem getVC(int problemIndex) {
        int m = params.get("m");
        int l = params.get("l");
        String[] objectiveList={"W","D", "SSAE", "ENS", "MDV"};
        try {
            switch (problemIndex) {
                case 0:
                    return new VC1();
                case 1:
                    return new VC2();
                case 2:
                    return new VC3();
                case 3:
                    return new VC4();
                case 4:
                    return new WaterReal();
                case 5:
                    return new CarSideImpact();
                case 6:
                    return new Machining();
                case 7:
                    return new FourBarTruss();
                case 8:
                    //http://www.eng.buffalo.edu/Research/MODEL/mdo.test.orig/class2prob4/descr.html
                    return new Golinski();
                case 11:
                    String ebesFileName="Mobile_Bridge_25N_35B_8G_16OrdZXY.ebe";
                    return new Ebes(ebesFileName, objectiveList);
                case 12:
                    String ebs="Displaced_Column_for_Vehicle_Ramp_Acero.ebe";
                    return new Ebes(ebs, objectiveList);
                case 9:
                    return new Quagliarella();
                case 10:
                    return new Poloni();
                default:
                    return null;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ProblemCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
    protected Problem getRealWorld(int problemIndex) {
        switch (problemIndex) {
            case 0:
                return new HeatExchanger();
            case 1:
                return new DiskBrakeDesign();
            case 2:
                return new WeldedBeamDesign();
            case 3:
                return new HydroDynamics();
            case 4:
                return new OpticalFilter();
            case 5:
                return new VibratingPlatformDesign();
            case 6:
                return new AucMaximization();
            case 7:
                return new NeuralNetDoublePoleBalancing();
            case 8:
                return new KernelRidgeRegressionParameterTuning();
            case 9:
                return new FacilityPlacement();
            default:
                return null;
        }
    }


    public Problem getProblemInstance(int problemIndex) {
        switch (problemClass) {
            case "WFG":
                return getWFG(problemIndex);
            case "Real":
                return getRealWorld(problemIndex);
            case "VC":
                return getVC(problemIndex);
            default:
                return null;
        }
    }

    public int getQtdProblem() {
        switch (problemClass) {
            case "WFG":
                return 9;
            case "Real":
                //return 6;
                return 8;
            case "VC":
                return 11;
            default:
                return -1;
        }
    }

    public static List generateInitialPopulation(Problem problem, int popSize) {
        List<Solution> initialPopulation = new ArrayList();
        Solution newSolution;
        for (int i = 0; i < popSize; i++) {
            newSolution = (Solution) problem.createSolution();
            problem.evaluate(newSolution);
            //problemInstances[instanceIndex].evaluateConstraints(newSolution);
            initialPopulation.add(newSolution);
        }
        return initialPopulation;
    }

    public String getProblemClass() {
        return problemClass;
    }

    public void setProblemClass(String problemClass) {
        this.problemClass = problemClass;
    }
}
