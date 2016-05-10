package se.liu.ida.albhe417.tddd78.math;

/**
 * Created by Albin_Hedman on 2016-05-06.
 */
public class FloatMapper {
    float inMin;
    float inMax;
    float outMin;
    float outMax;

    float deltaIn;
    float deltaOut;
    float scale;

    public FloatMapper(float inMin, float inMax, float outMin, float outMax){
        this.inMin = inMin;
        this.inMax = inMax;
        this.outMin = outMin;
        this.outMax = outMax;

        this.deltaIn = inMax - inMin;
        this.deltaOut = outMax - outMin;

        this.scale = deltaOut / deltaIn;
    }

    public float map(float value){

        final float displacedValue = value - inMin;

        return displacedValue * scale + outMin;
    }
}
