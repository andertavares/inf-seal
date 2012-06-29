/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package score;

import net.sourceforge.jFuzzyLogic.FIS;

/**
 *
 * @author Renato
 */
public class EvadeScore extends Score {
    private FIS fis;

    public EvadeScore() {
        super();

        String fileName = "bin/score/evade.fcl";
        this.fis = FIS.load(fileName, true);

        if (this.fis == null) {
            System.err.println("Can't load file: '" + fileName + "'");
        }
    }

    @Override
    public double getScore(double angle, double distance, double direction) {
        this.fis.setVariable("angle", Math.abs(angle));
        this.fis.setVariable("distance", distance/300);
        this.fis.setVariable("direction", (direction>Math.PI?2*Math.PI-direction:direction));
        this.fis.evaluate();

        return this.fis.getVariable("activate").defuzzify();
    }
    
}
