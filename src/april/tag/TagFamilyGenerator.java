package april.tag;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;

public class TagFamilyGenerator {
	private final static int nbits = 36;
	private final static int minhamming = 9;
	private final static int mincomplexity = 10;
	private final static int d = (int) Math.sqrt(nbits);
	private static long V0;
	private final static long maxnum = (1L << nbits);
	private final static long maxnumm1 = maxnum - 1;

	private static long starttime;

	private static long[] codelist = new long[0];
	private static int rotlim = -1; // write next entry at index

	private static final long PRIME = 982451653;
	
	static {
		if (d * d != nbits) {
			System.out.println("WARNING: nbits is not a square. This code may do something stupid.\n");
		}
	}

	static final void printBoolean(PrintStream outs, long v, int nbits) {
		for (int b = nbits - 1; b >= 0; b--)
			outs.printf("%d", (v & (1L << b)) > 0 ? 1 : 0);
	}

	static final void printCodes(long codes[], int nbits) {
		for (int i = 0; i < codes.length; i++) {
			long w = codes[i];
			System.out.printf("%5d ", i);
			printBoolean(System.out, w, nbits);
			System.out.printf("    %0" + ((int) Math.ceil(nbits / 4)) + "x\n", w);
		}
	}

	public static void main(String args[]) {
		// if (args.length < 2) {
		// System.out.printf("usage: <nbits> <minhammingdistance>
		// <mincomplexity> <number of rounds>\n");
		// System.out.printf("(For all standard tags, nbits is a square.)\n");
		// return;
		// }

		// int nbits = Integer.parseInt(args[0]);
		// int minhamming = Integer.parseInt(args[1]);

		// default complexity is a function of the nbits. Values
		// before were tuned by hand so they were "reasonable".

		// Size (bits) | Min complexity
		// -------------|-----------------
		// 9 | 3
		// 16 | 5
		// 25 | 8
		// 36 | 10

		// This is approximately: complexity = 0.3 * nbits.
		// int reccomplexity = Math.min(10, nbits / 3);

		// int mincomplexity = args.length > 2 ? Integer.parseInt(args[2]) :
		// reccomplexity;

		int maxruns = args.length > 0 ? Integer.parseInt(args[0]) : 1;

		int bestCountCodes = 0;
		// begin our search at a random position to avoid any bias
		// towards small numbers (which tend to have larger regions of
		// solid black).

		final Random rand = new Random(); // new Random(nbits * 10000 +
											// minhamming * 100 +
											// mincomplexity);

		for (int i = 0; i < maxruns; i++) {
			long V0 = rand.nextLong();
			codelist = new long[0];
			rotlim = -1;
			TagFamily res = TagFamilyGenerator.compute(V0);
			if (res.codes.length > bestCountCodes) {
				TagFamilyGenerator.report("_" + V0);
				bestCountCodes = res.codes.length;
			}
			System.out.format("found family with %d codes, best is now %d !", res.codes.length, bestCountCodes);
		}
		System.out.println("");
		System.out.println("finished.");
	}

	private static long rotate90(final long w) {
		long wr = 0;

		for (int r = d - 1; r >= 0; r--) {
			for (int c = 0; c < d; c++) {
				int b = r + d * c;

				wr = wr << 1;

				if ((w & (1L << b)) != 0)
					wr |= 1;

			}
		}
		return wr;
	}

	private static boolean isCodeOkay(final long v) {
		// The tag must be different from itself when rotated.
		final long rv1 = rotate90(v);
		final long rv2 = rotate90(rv1);
		final long rv3 = rotate90(rv2);

		if (hammingDistanceTooLow(v ^ rv1) || hammingDistanceTooLow(v ^ rv2) || hammingDistanceTooLow(rv1 ^ rv2) || hammingDistanceTooLow(rv1 ^ rv3)
				|| hammingDistanceTooLow(rv2 ^ rv3) || hammingDistanceTooLow(v ^ rv3)) {
			return false;
		}

		// tag (and its rotations) must be different from other tags.
		for (int i = 0; i <= rotlim; i++) {
			if (hammingDistanceTooLow(v ^ codelist[i]) || hammingDistanceTooLow(rv1 ^ codelist[i]) || hammingDistanceTooLow(rv2 ^ codelist[i])
					|| hammingDistanceTooLow(rv3 ^ codelist[i])) {
				return false;
			}
		}

		// tag must be reasonably complex
		if (computeComplexity(v) < mincomplexity) {
			return false;
		}

		return true;
	}

	public static TagFamily compute(final long V0) {
		assert (codelist.length == 0);
		starttime = System.currentTimeMillis();
		TagFamilyGenerator.V0 = V0;

		long lastreporttime = starttime;
		long lastprogresstime = starttime;
		long lastprogressiters = 0;

		System.out.printf("Using only one thread. V0=%d\n", V0);

		long iter = 0;
		long chunksize = 500000;

		// compute v = V0 + PRIME * iter0,
		// being very careful about overflow.
		// (consider the power-of-two expansion of iter0....)
		long v = V0;
		{
			long acc = PRIME;
			long M = iter;
			while (M > 0) {
				if ((M & 1) > 0) {
					v += acc;
					v &= (1L << nbits) - 1;
				}

				acc *= 2;
				acc &= (1L << nbits) - 1;
				M >>= 1;
			}
		}

		while (iter < (1L << nbits)) {
			{
				// print a progress report.
				long now = System.currentTimeMillis();
				if (now - lastprogresstime > 5000) {

					if (now - lastreporttime > 60000) {
						report("_calculating" + V0);
						lastreporttime = now;
					}

					double donepercent = (iter * 100.0) / (1L << nbits);
					double dt = (now - lastprogresstime) / 1000.0;
					long diters = iter - lastprogressiters;
					double rate = diters / dt; // iterations per second
					chunksize = (long) (rate * 5);
					chunksize = Math.min(chunksize, (1L << nbits) - iter);
					double secremaining = ((long) (1L << nbits) - iter) / rate;
					System.out.printf("%8.4f%% , iter: %d,  codes: %-5d (%.0f iters/sec, %.2f minutes = %.2f hours)    chunksize: %d\r", donepercent, iter, rotlim, rate,
							secremaining / (60.0), secremaining / 3600.0, chunksize);

					lastprogresstime = now;
					lastprogressiters = iter;
				}
			}

			for (long iterstop = iter + chunksize; iter < iterstop; iter++) {
				v += PRIME; // big prime.
				v &= maxnumm1;
				if (isCodeOkay(v)) {
					if (rotlim + 1 >= codelist.length) {
						long[] newrot = new long[codelist.length + 1024];
						System.arraycopy(codelist, 0, newrot, 0, codelist.length);
						codelist = newrot;
					}
					// System.out.printf("0x%0" + ((int) Math.ceil(nbits /
					// 4)) +
					// "xL\r\n", v);
					codelist[++rotlim] = v;
					// break;
				}
			}

		}

		long[] codes = new long[rotlim + 1];
		System.arraycopy(codelist, 0, codes, 0, codes.length);
		TagFamily tagFamily = new TagFamily(nbits, minhamming, codes);

		if (false) {
			try {
				tagFamily.writeAllImages("/tmp");
				tagFamily.writeAllImagesMosaic("/tmp/mosaic.png");
			} catch (IOException ex) {
				System.out.println("ex: " + ex);
			}
		}

		return tagFamily;
	}

	static void report(String appendName) {

		String cname = String.format("Tag%dh%dc%d", nbits, minhamming, mincomplexity);
		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream("" + cname + appendName + ".java", false));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		long[] codes = new long[rotlim + 1];
		System.arraycopy(codelist, 0, codes, 0, codes.length);

		int hds[] = new int[nbits + 1];
		// int hdtotal = 0;

		// compute hamming distance table
		for (int i = 0; i < codes.length; i++) {
			long rv0 = codes[i];
			long rv1 = TagFamily.rotate90(rv0, d);
			long rv2 = TagFamily.rotate90(rv1, d);
			long rv3 = TagFamily.rotate90(rv2, d);

			for (int j = i + 1; j < codes.length; j++) {
				int dist = Math.min(Math.min(TagFamily.hammingDistance(rv0, codes[j]), TagFamily.hammingDistance(rv1, codes[j])),
						Math.min(TagFamily.hammingDistance(rv2, codes[j]), TagFamily.hammingDistance(rv3, codes[j])));

				hds[dist]++;
				if (dist < minhamming) {
					System.out.printf("ERROR, dist = %3d: %d %d, val= %d %d\n", dist, i, j, codes[i], codes[j]);
				}
				// hdtotal++;
			}
		}

		out.printf("\n\npackage april.tag.family;\n\n");
		out.printf("\n\nimport april.tag.TagFamily;\n\n");
		out.printf("/** Tag family with %d distinct codes.\n", codes.length);
		out.printf("    V0=%d\n", V0);
		out.printf("    bits: %d,  minimum hamming: %d,  minimum complexity: %d\n\n", nbits, minhamming, mincomplexity);

		// compute some ROC statistics, assuming randomly-visible targets
		// as a function of how many bits we're willing to correct.
		out.printf("    Max bits corrected       False positive rate\n");

		for (int cbits = 0; cbits <= (minhamming - 1) / 2; cbits++) {
			long validCodes = 0; // how many input codes will be mapped to a
									// single valid code?
			// it's the number of input codes that have 0 errors, 1 error, 2
			// errors, ..., cbits errors.
			for (int i = 0; i <= cbits; i++)
				validCodes += choose(nbits, i);

			validCodes *= codes.length; // total number of codes

			out.printf("          %3d             %15.8f %%\n", cbits, (100.0 * validCodes) / (1L << nbits));
		}

		out.printf("\n    Generation time: %f s\n\n", (System.currentTimeMillis() - starttime) / 1000.0);

		out.printf("    Hamming distance between pairs of codes (accounting for rotation):\n\n");
		for (int i = 0; i < hds.length; i++) {
			out.printf("    %4d  %d\n", i, hds[i]);
		}

		out.printf("**/\n");

		out.printf("public class %s extends TagFamily\n", cname);
		out.printf("{\n");
		out.printf("\tpublic %s()\n", cname);
		out.printf("\t{\n");
		out.printf("\t\tsuper(%d, %d, ", nbits, minhamming);
		out.printf("new Long[] { ");
		for (int i = 0; i < codes.length; i++) {
			long w = codes[i];
			out.printf("0x%0" + ((int) Math.ceil(nbits / 4)) + "xL", w);
			if (i + 1 == codes.length)
				out.printf(" });\n");
			else
				out.printf(", ");
			if (i % 10 == 0)
				out.println("");
		}
		out.printf("\t}\n");
		out.printf("}\n");
		out.printf("\n");
	}

	static long choose(int n, int c) {
		long v = 1;
		for (int i = 0; i < c; i++)
			v *= (n - i);
		for (int i = 1; i <= c; i++)
			v /= i;
		return v;
	}

	private static int computeComplexity(long v) {
		final int a[][] = new int[d][d];

		for (int y = 0; y < d; y++) {
			for (int x = 0; x < d; x++) {
				a[y][x] = (v & 1) > 0 ? 1 : 0;
				v = v >> 1;
			}
		}

		return computeComplexity(a);
	}

	/** Compute the hamming distance between two longs. **/
	public static final int hammingDistance(long a, long b) {
		return Long.bitCount(a ^ b);
	}

	private static final boolean hammingDistanceTooLow(final long w) {
		return Long.bitCount(w) < minhamming;
	}

	/**
	 * Given a 2D array of "pixels", what is the minimum number of rectangles
	 * needed to draw that pattern? This is a measure of the complexity of the
	 * pattern.
	 *
	 * The problem itself is NP-hard, but we employ a greedy approximation that,
	 * at each time step, tries *every* rectangle and picks the rectangle that
	 * reduces the number of errors. This is horrifically slow, but for our
	 * purposes, we're only doing very small images...
	 *
	 * @param foo
	 **/
	private static int computeComplexity(final int d[][]) {
		final boolean verbose = false;

		final int width = d[0].length, height = d.length;

		final int out[][] = new int[height][width];
		int numrectangles = 0;

		// initialize output to invalid color.
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				out[y][x] = -1;

		while (true) {

			// What is our error now?
			int error = 0;

			for (int y = 0; y < height; y++)
				for (int x = 0; x < width; x++)
					if (d[y][x] != out[y][x])
						error++;

			// are we done?
			if (error == 0)
				break;

			int bestimprovement = 0;
			int besty0 = -1, bestx0 = -1, besty1 = -1, bestx1 = -1, bestv = -1;

			// search over all rectangles: which one will reduce the
			// error the most?
			for (int y0 = 0; y0 < height; y0++) {
				for (int y1 = y0; y1 < height; y1++) {
					for (int x0 = 0; x0 < width; x0++) {
						for (int x1 = x0; x1 < width; x1++) {

							for (int v = 0; v < 2; v++) {

								int improvement = 0;

								for (int y = y0; y <= y1; y++) {
									for (int x = x0; x <= x1; x++) {
										if (d[y][x] == out[y][x]) {
											if (d[y][x] == v) {
												// no change, still right.
											} else {
												improvement--;
											}
										} else {
											if (d[y][x] == v) {
												improvement++;
											} else {
												// no change, still wrong.
											}
										}
									}
								}

								if (improvement > bestimprovement) {
									besty0 = y0;
									bestx0 = x0;
									besty1 = y1;
									bestx1 = x1;
									bestv = v;
									bestimprovement = improvement;
								}
							}
						}
					}
				}
			}

			// implement change
			for (int y = besty0; y <= besty1; y++) {
				for (int x = bestx0; x <= bestx1; x++) {
					out[y][x] = bestv;
				}
			}

			numrectangles++;

			if (verbose)
				System.out.printf("(%d %d) (%d %d): color %d improvement %d\n", bestx0, besty0, bestx1, besty1, bestv, bestimprovement);
		}

		return numrectangles;
	}
}
