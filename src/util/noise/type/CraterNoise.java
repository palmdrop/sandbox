/*
 * This code is public domain.
 */
package util.noise.type;

import util.Xorshift;
import util.noise.Noise;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleBinaryOperator;

/**
 * Implements a random placement crater system for fractal surfaces. This is
 * useful when creating planets that have been subject to meteor bombardment
 * that is still visible as the &quot;ring of rock&quot; element. This code is
 * derived from my port of Matt Phar's {@link CellularNoise}, which itself
 * implements Steve Worley's cellular noise. This version, instead of reading
 * cellular minimum distance, instead treats voxel coordinates as an impact
 * crater and tries to determine the value based on the distance from the
 * center.
 * <p>
 * To provide additional flexibility, this allows the value function that
 * generates the cratering effect to be replaced by the user. The function
 * accepts two <code>double</code> values: the first is the squared distance to
 * a given voxel, and the second is a random value from 0 to 1. Return values
 * are up to the user.
 * 
 * @author saybur
 * 
 */
public final class CraterNoise implements Noise
{
	/**
	 * The default value function.
	 */
	public static final DoubleBinaryOperator DEFAULT_VALUE_FUNCTION = (d, x) ->
	{
		final double distance = d * 4 * (x + 1);

		if(distance > 0.5)
		{
			return Math.max(1 - (distance - 0.5) * 1.5, 0.0);
		}
		else
		{
			return 1 - (distance - 0.5) * -6;
		}
	};

	private static double distanceSquared(double xa, double ya, double za,
			double xb, double yb, double zb)
	{
		double x2 = xa - xb;
		double y2 = ya - yb;
		double z2 = za - zb;
		return x2 * x2 + y2 * y2 + z2 * z2;
	}
	
	private static int floor(double n)
	{
		return n > 0 ? (int) n : (int) n - 1;
	}
	
	/**
	 * Creates a new noise instance with a random seed value.
	 * 
	 * @return the new noise instance.
	 */
	public static CraterNoise getInstance()
	{
		return new CraterNoise(ThreadLocalRandom.current().nextLong(),
				DEFAULT_VALUE_FUNCTION);
	}

	/**
	 * Creates a new noise instance with a random seed value, and a given value
	 * function.
	 * 
	 * @param function
	 *            the value function.
	 * @return the new noise instance.
	 */
	public static CraterNoise getInstance(DoubleBinaryOperator function)
	{
		return new CraterNoise(ThreadLocalRandom.current().nextLong(),
				function);
	}
	
	/**
	 * Creates a new noise instance with the specified seed value.
	 * 
	 * @param seed
	 *            the seed value.
	 * @return the new noise instance.
	 */
	public static CraterNoise getInstance(long seed)
	{
		return new CraterNoise(seed, DEFAULT_VALUE_FUNCTION);
	}

	/**
	 * Creates a new noise instance with the specified seed value, and a given
	 * value function.
	 * 
	 * @param seed
	 *            the seed.
	 * @param function
	 *            the value function.
	 * @return the new noise instance.
	 */
	public static CraterNoise getInstance(long seed, DoubleBinaryOperator function)
	{
		return new CraterNoise(seed, function);
	}

	private final Xorshift randomFactory;
	private final DoubleBinaryOperator valueFunction;

	private CraterNoise(long seed, DoubleBinaryOperator function)
	{
		randomFactory = Xorshift.create(seed);
		valueFunction = function;
	}
	
	private double calculate(Xorshift.Instance r, double xf, double yf,
			double zf)
	{
		// hack, but easier than handling points that are exactly at negative
		// integer latice-points correctly.
		xf = xf + 1e-7f;
		yf = yf + 1e-7f;
		zf = zf + 1e-7f;
		// get the coordinate that this point resides at
		int x = floor(xf);
		int y = floor(yf);
		int z = floor(zf);
		// create storage of value
		double s = 0.0;
		// determine the value based on all neighbors
		for(int i = -1; i <= 1; ++i)
		{
			for(int j = -1; j <= 1; ++j)
			{
				for(int k = -1; k <= 1; ++k)
				{
					s += processVoxel(r, xf, yf, zf, x + i, y + j, z + k);
				}
			}
		}
		return s;
	}

	/**
	 * Provides a noise value at a given coordinate.
	 * 
	 * @param x
	 *            the x coordinate of the point to get noise at.
	 * @param y
	 *            the y coordinate of the point to get noise at.
	 * @param z
	 *            the z coordinate of the point to get noise at.
	 * @return the noise value at the given point.
	 */
	private double noise(double x, double y, double z)
	{
		return calculate(randomFactory.getInstance(), x, y, z);
	}

	/**
	 * Processes a given voxel by determining the distance to all points within
	 * the voxel and adding the points to the internal value tracker.
	 * 
	 * @param r
	 *            the source of randomness to use.
	 * @param xf
	 *            the x coordinate of what we're checking against.
	 * @param yf
	 *            the y coordinate of what we're checking against.
	 * @param zf
	 *            the z coordinate of what we're checking against.
	 * @param x
	 *            the x coordinate of the voxel.
	 * @param y
	 *            the y coordinate of the voxel.
	 * @param z
	 *            the z coordinate of the voxel.
	 * @return the sum of the distances to the voxel's points, plus the value
	 *         provided for storage.
	 */
	private double processVoxel(Xorshift.Instance r,
			double xf, double yf, double zf,
			int x, int y, int z)
	{
		// reset random number generator for the voxel
		r.setSeed(x, y, z);
		// find value for this voxel
		double nx = x + r.nextDouble();
		double ny = y + r.nextDouble();
		double nz = z + r.nextDouble();
		double magnitude = r.nextDouble() + 1;
		// determine the distance between the generated point
		// and the source point we're checking. then get the value
		double distance = distanceSquared(xf, yf, zf, nx, ny, nz);
		double value = valueFunction.applyAsDouble(distance, magnitude);
		return value;
	}

	@Override
	public Double get(double x) {
		return noise(x, 0, 0);
	}

	@Override
	public Double get(double x, double y) {
		return noise(x, y, 0);
	}

	@Override
	public Double get(double x, double y, double z) {
		return noise(x, y, z);
	}
}
