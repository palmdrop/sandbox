/*
 * This code is public domain.
 */
package util.noise.type;

/**
 * Interface for three-dimensional sources of coherent noise.
 * 
 * @author saybur
 * 
 */
public interface NoiseSource
{
	/**
	 * Provides coherent noise data for a given coordinate in three-dimensional
	 * space.
	 * 
	 * @param x
	 *            the x coordinate of the point to get noise for.
	 * @param y
	 *            the y coordinate of the point to get noise for.
	 * @param z
	 *            the z coordinate of the point to get noise for.
	 * @return the noise at the provided coordinate.
	 */
	public double coherentNoise(double x, double y, double z);
}
