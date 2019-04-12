/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HF_Main;

import java.io.IOException;
import javax.management.JMException;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 *
 * @author psavd
 */
public class Runner {

    public static void main(String[] args) throws ClassNotFoundException, IOException, JMException, ConfigurationException {
        //int[] fixedGenerations = {10, 20};
        int[] fixedGenerations = {20};

        for (int i = 0; i < fixedGenerations.length; i++) {
            int fixedGeneration = fixedGenerations[i];
            for (int j = 0; j < 2-1; j++) {
                String[] argv = new String[6];
                argv[0] = "40";
                argv[1] = String.valueOf(fixedGeneration);//20 fixedGeneration
                argv[2] = "-1";
                argv[3] = String.valueOf(j);//0 runningAlgorithmIndex
                argv[4] = "WFG";
                argv[5] = "5";
                RealProblemsLearningAutomataMain.main(argv);
            }
        }

    }
}
