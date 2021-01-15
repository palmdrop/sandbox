/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015-2016 saybur
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package util;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Random number generator for voxel-based fractals based on the Xorshift
 * pseudo-random number generator described by George Marsaglia in his paper
 * <i>Xorshift RNGs</i> (<a href="http://www.jstatsoft.org/v08/i14/paper"
 * >http://www.jstatsoft.org/v08/i14/paper</a>).
 * <p>
 * This particular implementation has a period of <code>2 ^ 96 - 1</code> and
 * uses Marsaglia's triple of [13, 19, 3]. For use in cellular noise functions,
 * it has a method for resetting the generator state via
 * {@link Instance#setSeed(long, long, long)} for <code>x, y, z</code> voxel
 * coordinates, which will wrap on [0, 1024) for each of <code>x, y, z</code>.
 * <p>
 * Objects of <code>Xorshift.Instance</code> <i>are not</i> threadsafe.
 * Concurrent access should be externally synchronized or use separate
 * instances. <code>Xorshift</code> itself is threadsafe.
 * 
 * @author saybur
 *
 */
public final class Xorshift
{
	/**
	 * Random number generator instance for <code>Xorshift</code>. See the class
	 * documentation for details.
	 * <p>
	 * A reminder: this is <i>not</i> threadsafe.
	 * 
	 * @author saybur
	 *
	 */
	public final class Instance
	{
		private long x, y, z;
		
		private Instance()
		{
			setSeed(0, 0, 0);
		}
		
		/**
		 * Provides a pseudo-random <code>double</code> value on [0.0, 1.0).
		 * 
		 * @return the next double value.
		 */
		public double nextDouble()
		{
			// get out of signed range, then divide in the remaining space
			// of the shifted long value.  i hope this works...
			return (nextLong() >>> 2) * (1.0 / (1L << 62));
		}
		
		/**
		 * Provides a pseudo-random <code>long</code> value.
		 * 
		 * @return the next <code>long</code> value.
		 */
		public long nextLong()
		{
			long t;
			t = (x ^ (x << 13));
			x = y;
			y = z;
			z = (z ^ (z >>> 3)) ^ (t ^ t >>> 19);
			return z;
		}
		
		/**
		 * Sets the seed for this number generator to the given values.
		 * <p>
		 * This is for the cellular noise functions and may be ignored by other
		 * users, as the constructor will initialize the generator with random
		 * starting values.
		 * 
		 * @param x
		 *            the X component.
		 * @param y
		 *            the Y component.
		 * @param z
		 *            the Z component.
		 */
		public void setSeed(long x, long y, long z)
		{
			this.x = seeds[(int) (Math.abs(x) % SEED_TABLE_SPACE)];
			this.y = seeds[(int) (Math.abs(y) % SEED_TABLE_SPACE)
					+ SEED_TABLE_SPACE];
			this.z = seeds[(int) (Math.abs(z) % SEED_TABLE_SPACE)
					+ SEED_TABLE_SPACE + SEED_TABLE_SPACE];
			
			// decreases spherical artifacts, but obviously is slow
			// TODO replace by fixing underlying issue
			for(int i = 0; i < 5; i++)
				nextLong();
		}
	}

	/**
	 * The space for each of x, y, and z in the seeds table.
	 */
	private static final int SEED_TABLE_SPACE = 1024;
	/**
	 * The final size of the seeds table.
	 */
	private static final int SEED_TABLE_SIZE = SEED_TABLE_SPACE * 3;
	
	/**
	 * Creates a new random number generator factory with a random seed value.
	 * 
	 * @return the random number generator factory.
	 */
	public static Xorshift create()
	{
		return new Xorshift(ThreadLocalRandom.current().nextLong());
	}
	
	/**
	 * Creates a new random number generator factory with the given seed value.
	 * 
	 * @param seed
	 *            the seed value.
	 * @return the random number generator factory.
	 */
	public static Xorshift create(long seed)
	{
		return new Xorshift(seed);
	}
	
	private final long[] seeds;
	
	private Xorshift(long seed)
	{
		Random random = new Random(seed);
		seeds = new long[SEED_TABLE_SIZE];
		for(int i = 0; i < SEED_TABLE_SIZE; i++)
		{
			seeds[i] = random.nextLong();
		}
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof Xorshift)
		{
			Xorshift o = (Xorshift) obj;
			return Objects.equals(seeds, o.seeds);
		}
		else
		{
			return false;
		}
	}

	/**
	 * Creates a new random number generator instance.
	 * <p>
	 * Each instance should be used by a single thread only. See the class
	 * documentation for information about the generator itself.
	 * 
	 * @return a new random number generator instance.
	 */
	public Xorshift.Instance getInstance()
	{
		return new Xorshift.Instance();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(seeds);
	}
	
}
