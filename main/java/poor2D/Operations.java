package poor2D;

/**
 * Created by alex on 7/13/14.
 */

import static poor2D.Constants.DIMENSION;

public class Operations {

    public static Vector multiply(Matrix matrix, Vector vector){
        Vector result = new Vector();
        for (int i = 0; i < DIMENSION; ++i) {
            float sum = 0.0f;
            for (int j = 0; j < DIMENSION; ++j) {
                sum += matrix.get(i, j) * vector.get(j);
            }
            result.set(i, sum);
        }

        return result;
    }

    public static Vector multiply(float multiplier, Vector vector){
        Vector result = new Vector();
        for (int i = 0; i < DIMENSION; ++i) {
            result.set(i, vector.get(i) * multiplier);
        }
        return result;
    }

    public static Vector add(Vector a, Vector b){
        Vector result = new Vector();
        for (int i = 0; i < DIMENSION; ++i){
            result.set(i, a.get(i) + b.get(i));
        }
        return result;
    }

    public static double scalarProduct(Vector a, Vector b) {
        double result = 0;
        for (int i = 0; i < DIMENSION; ++i) {
            result += a.get(i) * b.get(i);
        }
        return result;
    }
}
