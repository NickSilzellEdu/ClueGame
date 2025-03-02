Clue game for CSCI306 by Nick Silzell and Andrew Grimes
Clash of clans themed

Notes: 
- Working on C14A1, changed test TestOccupiedCorner, it was failing because the board was calculating targets before certain cells were set to occupied, leading those cells to be included in target.
- Changed TestBigRoom for same reason as previous
- Changed TestMaxRoll because it was designed to test a roll of 6, but was only testing a roll of 3
- Deleted TestBigRoom() test, it  tested getting the target of a 2x2 room where one square was in reach of the roll, and the rest were not. We have not covered how we will handle room target selection, so we removed the test.
