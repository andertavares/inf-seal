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

        String fileName = "C\\Users\\Renato\\Dev\\demolition\\inf-seal\\src\\score\\evade.fcl";
        this.fis = FIS.load(fileName, true);

        if (this.fis == null) {
            System.err.println("Can't load file: '" + fileName + "'");
        }
    }

    @Override
    public double getScore(float angle, float distance, float direction) {
        this.fis.setVariable("angle", angle);
        this.fis.setVariable("distance", distance);
        this.fis.setVariable("direction", direction);
        this.fis.evaluate();

        return this.fis.getVariable("activate").defuzzify();
    }
    
}
