package poor2D;

/**
 * Created by alex on 7/13/14.
 */

import static poor2D.Constants.DIMENSION;

public class Matrix {
    private float[][] array;

    public Matrix(){
        array = new float[DIMENSION][DIMENSION];
    }

    public Matrix(float[][] array){
        this.array = array;
    }

    /**
     * Diagonal initializer
     */
    public Matrix(float... diagonalElements){
        this();
        for (int i = 0; i < DIMENSION; ++i){
            array[i][i] = diagonalElements[i];
        }
    }

    public static Matrix identity2f(){
        return new Matrix(1.0f, 1.0f);
    }

    public float get(int i, int j){
        return array[i][j];
    }

    public void set(int i, int j, float element){
        array[i][j] = element;
    }

    /**
     * The degree is measured in radians. Rotation
     * is performed counterclockwise
     */
    public static Matrix initRotationTransformation(float degree){
        Matrix result = new Matrix();
        result.set(0, 0, (float) Math.cos(degree));
        //////////////////////
        result.set(0, 1, (float) Math.sin(-degree));
        result.set(1, 0, (float) Math.sin(degree));
        result.set(1, 1, (float) Math.cos(degree));

        return result;
    }
}
