package items;

import android.util.Log;

import poor2D.Matrix;
import poor2D.Operations;
import poor2D.Vector;

import java.util.List;

import static items.Constants.*;

/**
 * Created by alex on 7/13/14.
 */

public class TankCore {
    //// EPSILON ~ step
    private float EPSILON = 0.005f;
    private Vector position = new Vector(0.0f, 0.0f);
    private Vector target = new Vector(1.0f, 0.0f);

    private Vector horizontal = new Vector(1.0f, 0.0f);


    public TankCore() {
    }

    public TankCore(Vector position, Vector target) {
        this.position = position;
        this.target = target;
    }

    public void setPosition(Vector position) {
        this.position = (Vector) position.clone();
    }

    public void setTarget(Vector target) {
        this.target = (Vector) target.clone();
    }

    public Vector getPosition() {
        return (Vector) position.clone();
    }

    public Vector getTarget() {
        return (Vector) target.clone();
    }

    /**
     * The angle is measured in radians
     */
    public void turn(float degree) {

        target = Operations.multiply(Matrix.initRotationTransformation(degree), horizontal);
    }

    public void setStartX(float startX) {
        Constants.startX = startX;
    }

    public void setStratY(float stratY) {
        Constants.startY = stratY;
    }

    public void step(float factorX, float factorY, float commonFactor) {

        Vector result = Operations.add(position, Operations.multiply(commonFactor, target));

        boolean temp1 = position.get(0) < 0.0f + EPSILON;
        boolean temp2 = position.get(1) < 0.0f + EPSILON;
        boolean temp3 = target.get(0) < 0.0f;
        boolean temp4 = target.get(1) < 0.0f;
        boolean temp5 = target.get(0) > 0.0f;
        boolean temp6 = target.get(1) > 0.0f;
        boolean temp7 = position.get(0) < SCREEN_SIZE - EPSILON;
        boolean temp8 = position.get(0) > SCREEN_SIZE - EPSILON;
        boolean temp9 = position.get(1) < SCREEN_SIZE - EPSILON;
        boolean temp10 = position.get(1) > SCREEN_SIZE - EPSILON;
        boolean temp11 = position.get(0) > 0.0f + EPSILON;
        boolean temp12 = position.get(1) > 0.0f + EPSILON;

        if (temp1 &&  temp2 && temp3 && temp4) {
            return;
        }

        if (temp2 && temp4) {
            if ( temp7 || temp3)
                position = new Vector(result.get(0), 0.0f);
            return;
        }

        if ( temp8 && temp5) {
            if (temp9 || temp4)
                position = new Vector(SCREEN_SIZE, result.get(1));
            return;
        }

        if (temp10 && temp6) {
            if (temp11 || temp5)
                position = new Vector(result.get(0), SCREEN_SIZE);
            return;
        }

        if (temp1 && temp3) {
            if (temp12 || temp6)
                position = new Vector(0.0f, result.get(1));
            return;
        }

        position = result;
    }

    @Override
    public String toString() {
        return "Tank: Position: " + position + "; Target: " + target + ";";
    }
}
