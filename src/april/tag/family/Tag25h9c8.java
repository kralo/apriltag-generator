package april.tag.family;

import april.tag.TagFamily;

/** Tag family with 38 distinct codes.
    V0=5800576261780540266
    bits: 25,  minimum hamming: 9,  minimum complexity: 8

    Max bits corrected       False positive rate
            0                  0,00011325 %
            1                  0,00294447 %
            2                  0,03691912 %
            3                  0,29739141 %
            4                  1,72998905 %

    Generation time: 4,328000 s

    Hamming distance between pairs of codes (accounting for rotation):

       0  0
       1  0
       2  0
       3  0
       4  0
       5  0
       6  0
       7  0
       8  0
       9  200
      10  269
      11  127
      12  67
      13  32
      14  6
      15  2
      16  0
      17  0
      18  0
      19  0
      20  0
      21  0
      22  0
      23  0
      24  0
      25  0
**/
public class Tag25h9c8 extends TagFamily {
	public Tag25h9c8() {
		super(25, 9,
				new long[] { 0x11c16f4L, 0x3a227eL, 0x1582e08L, 0x16695e2L, 0x1a2acf6L, 0x18bd559L, 0x8a9983L, 0x14d4699L, 0x26cb4cL, 0x16a164dL, 0x1d164efL, 0x27d40bL, 0x363be5L,
						0xd9a19bL, 0xbd0e4aL, 0x153b776L, 0xe1fa2bL, 0x1251215L, 0x1d7e864L, 0x7a1b90L, 0x11e8a05L, 0x39eae2L, 0x10b3c35L, 0x555e41L, 0xfbb469L, 0xd26df0L,
						0x020ed9L, 0xc3c12dL, 0x185946eL, 0x3cfd49L, 0x1daa2c6L, 0x17702d2L, 0x2a61d1L, 0xed2b0dL, 0x88fcecL, 0x144624eL, 0x18af337L, 0x1cd375aL });
	}
}
