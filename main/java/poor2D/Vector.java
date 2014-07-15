package poor2D;

/**
 * Created by alex on 7/13/14.
 */

import java.util.*;

import static poor2D.Constants.*;

public class Vector implements Cloneable{
    private float[] array;

    public Vector(){
        this.array = new float[DIMENSION];
    }

    public Vector(float... args){
        this.array = args;
    }

    @Override
    public Object clone(){
        Vector copy = null;
        try {
            copy = (Vector) super.clone();
        }catch (CloneNotSupportedException e){
            e.printStackTrace();
        }
        copy.array = array.clone();
        return copy;
    }

    @Override
    public String toString(){
        return this == null ? null : Arrays.toString(array);
    }

    public void set(int i, float element){
        array[i] = element;
    }

    public float get(int i){
        return array[i];
    }
}
