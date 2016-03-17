package se.liu.ida.albhe417.tddd78.game;

/**
 * Created by Albin_Hedman on 2016-03-17.
 */
public interface Heightmap {
    public float getHeight(float x, float z);

    /**
     * Get float value from given position
     * @param x
     * @param z
     * @return
     */
    public float getValueF(int x, int z);

    /**
     * Get short value from given position
     * @param x
     * @param z
     * @return
     */
    public float getValueS(int x, int z);

    public void increaseValue(float x, float z, float value);
}
