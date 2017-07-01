package april.tag.family;

import april.tag.TagFamily;

/** Tag family with 7 distinct codes.
    V0=-2909471679938210924
    bits: 16,  minimum hamming: 7,  minimum complexity: 5

    Max bits corrected       False positive rate
            0                  0,01068115 %
            1                  0,18157959 %
            2                  1,46331787 %
            3                  7,44476318 %

    Generation time: 0,394000 s

    Hamming distance between pairs of codes (accounting for rotation):

       0  0
       1  0
       2  0
       3  0
       4  0
       5  0
       6  0
       7  12
       8  9
       9  0
      10  0
      11  0
      12  0
      13  0
      14  0
      15  0
      16  0
**/
public class Tag16h7c5 extends TagFamily {
	public Tag16h7c5() {
		super(16, 7, new long[] { 0x5959L, 0x7632L, 0x1d83L, 0x075aL, 0x2b2cL, 0x2cb9L, 0xcb23L });
	}
}

