package april.tag.family;

import april.tag.TagFamily;

/** Tag family with 21 distinct codes.
    V0=5183873981058224185
    bits: 16,  minimum hamming: 6,  minimum complexity: 5

    Max bits corrected       False positive rate
            0                  0,03204346 %
            1                  0,54473877 %
            2                  4,38995361 %

    Generation time: 0,519000 s

    Hamming distance between pairs of codes (accounting for rotation):

       0  0
       1  0
       2  0
       3  0
       4  0
       5  0
       6  167
       7  0
       8  41
       9  0
      10  2
      11  0
      12  0
      13  0
      14  0
      15  0
      16  0
**/
public class Tag16h6c5 extends TagFamily {
	public Tag16h6c5() {
		super(16, 6, new long[] { 0x95feL, 0xa188L, 0xa74dL, 0xad12L, 0x0962L, 0x8e15L, 0x9f64L, 0x523fL, 0x0adfL, 0xe1d5L, 0xd7c8L, 0x7c05L, 0xb420L, 0x4f6fL, 0x50ecL, 0x9139L,
				0x7be6L, 0x7161L, 0x4bf8L, 0xb9dbL, 0x6ac1L });

	}
}
