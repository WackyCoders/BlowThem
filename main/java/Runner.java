import items.TankCore;
import poor2D.Matrix;
import poor2D.Operations;
import poor2D.Vector;

/**
 * Created by alex on 7/3/14.
 */
public class Runner {
    public static void main(String[] args){

        TankCore tank = new TankCore();
        // Default values: Position: [0.0f, 0.0f]; Target: [1.0f, 1.0f];

        tank.step(1.0f);
        // A step along current target vector; Position: [1.0f, 0.0f]; Target: [1.0f, 1.0f];

        tank.turn((float) (Math.PI / 4));
        // Target rotation (the angle of 45 degrees); Position: [1.0f, 0.0f]; Target: [~0.7f, ~0.7f];

        tank.step(1.0f);
        // A step along new target vector; Position: [~1.7f, ~0.7f]; Target: [~0.7f, ~0.7f];

        System.out.println(tank);
    }
}
