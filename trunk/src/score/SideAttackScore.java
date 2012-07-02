/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package score;

import java.io.File;

import net.sourceforge.jFuzzyLogic.FIS;

/**
 *
 * @author Renato
 */
public class SideAttackScore extends Score {
    private FIS fis;

    public SideAttackScore() {
        super();
        
                
        //TODO checar directory separator em JAVA
        
        String fileName = "rules/sideAttack.fcl";
        
        this.fis = FIS.load(fileName, true);

        if (this.fis == null) {
            System.err.println("Can't load file: '" + fileName + "'");
        }
        
        //xfis.chart();
    }

    @Override
    public double getScore(double angle, double distance, double direction) {
        this.fis.setVariable("angle", Math.abs(angle));
        this.fis.setVariable("distance", distance/300);
        this.fis.setVariable("direction", (direction>Math.PI?2*Math.PI-direction:direction));
        this.fis.evaluate();
        
        

        double score = fis.getVariable("activate").defuzzify();
        return score;
    }
    
}
