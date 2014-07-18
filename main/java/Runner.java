import items.BulletCore;
import items.TankCore;
import poor2D.Matrix;
import poor2D.Operations;
import poor2D.Vector;

import java.util.Arrays;

/**
 * Created by alex on 7/3/14.
 */
public class Runner {
    public static void main(String[] args){

        TankCore tank = new TankCore();
        BulletCore first = new BulletCore(tank.getPosition(), tank.getTarget());
        BulletCore.commonBulletStep(2.0f);
        tank.step(1.0f);
        tank.turn((float) (Math.PI / 4));
        BulletCore second = new BulletCore(tank.getPosition(), tank.getTarget());
        BulletCore.commonBulletStep(100000.0f);
        BulletCore.commonBulletStep(0.0f);
        tank.step(1.0f);


        System.out.println(tank);
        System.out.println(first);
        System.out.println(second);
        System.out.print(Arrays.toString(BulletCore.bulletArray));
    }
}
