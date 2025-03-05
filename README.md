Clue game for CSCI306 by Nick Silzell and Andrew Grimes
Clash of clans themed

Notes: 
	- Working on C14A1, changed test TestOccupiedCorner, it was failing because the board was calculating targets before certain cells were set to occupied, leading those cells to be included in target.
	- Changed TestBigRoom for same reason as previous
	- Changed TestMaxRoll because it was designed to test a roll of 6, but was only testing a roll of 3
	- Changed TestBigRoom to test room cells correctly. Previously it assumed room cells are not added to targets, updated so that reachable room cells are added.
 	- Changed TestRoomLabels in FileInitTests.java, the tests were failing because three assert statements were comparing different characters to "Clan Castle" rather than their rooms
 	- Changed TestUnusedCorners, it was trying to access board.getCell(NUM_ROWS, NUM_COlUMNS), but they needed to be decremented by 1 to prevent out of bounds error.
 	- Changed TestDoorDirections, the test had column and row numbers swapped for the intended testing cells;
 	- Changed TestTypesOfRooms, the test columns and rows were switched again
 	- Changed ExceptionTests and ExceptionTests306 to include "data/" for files