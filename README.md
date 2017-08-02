# apriltag-generator

This is a sped-up version of the generator published by the [original authors](https://april.eecs.umich.edu/software/apriltag.html).

I have partially modified to make use of faster java operations and also converted it back to single-threading, to get determinism back.

**A word of Warning**: you should always make sure, that the set of codes you printed does exactly match the set of codes you have programmed in your recognition tool - or otherwise it might not work!

## Number of codes

| Bits | Min. Complexity | Hamming Distances |    |    |     |          |         |         |     |
|------|-----------------|-------------------|----|----|-----|----------|---------|---------|-----|
|      |                 | 4                 | 5  | 6  | 7   | 8        | 9       | 10      | 11  |
| 16   | 5               | 201               | 34 | 21 | 7   |          |         |         |     |
| 25   | 8               |                   |    |    | 254 | 152      | 38      |         |     |
| 36   | 10              |                   |    |    |     | 31812    | 5375    | 2793    | 593 |


## Usage

compile the source with

  ant

and then launch the generation. If you wanted to generate 36h11c10 tags, and let it do 2 rounds, you would do

  java -cp april.jar april.tag.TagFamilyGenerator 2


To generate the images from source,

  java -cp april.jar april.tag.TagFamily april.tag.family.Tag36h11c10 /tmp/tag36h11c10

Useful for running in ec2:

  screen -S april36h9c10 -d -m java -cp april36h9c10.jar april.tag.TagFamilyGenerator 100

## License

The original work states BSD License. I hereby license my modifications under the same license.
