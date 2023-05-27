package puzzle.model;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class PuzzleStateTest {

    PuzzleState state1 = new PuzzleState(); // the original initial state

    PuzzleState state2 = new PuzzleState(new Position(1, 1),
            new Position(0, 0),
            new Position(0, 2),
            new Position(2, 0),
            new Position(2, 2)); // a goal state

    PuzzleState state3 = new PuzzleState(new Position(2, 4),
            new Position(0, 0),
            new Position(0, 2),
            new Position(2, 0),
            new Position(0, 4)); // a non-goal state

    PuzzleState state4 = new PuzzleState(new Position(0, 0),
            new Position(0, 2),
            new Position(0, 4),
            new Position(2, 0),
            new Position(2, 3)); // another non-goal state (this puzzle always has a legal move meaning there is no dead end state)

    @Test
    void constructor() {
        var positions = new Position[] {
                new Position(0, 4),
                new Position(0, 0),
                new Position(0, 2),
                new Position(2, 0),
                new Position(2, 2)
        };
        PuzzleState state = new PuzzleState(positions);
        for (var i = 0; i < 4; i++) {
            assertEquals(positions[i], state.getPosition(i));
        }
    }

    @Test
    void constructor_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new PuzzleState(new Position(0, 0)));
        assertThrows(IllegalArgumentException.class, () -> new PuzzleState(new Position(0, 0),
                new Position(1, 1),
                new Position(2, 2),
                new Position(3, 3)));
        assertThrows(IllegalArgumentException.class, () -> new PuzzleState(new Position(1, 1),
                new Position(1, 1),
                new Position(1, 1),
                new Position(1, 1),
                new Position(1, 1)));
        assertThrows(IllegalArgumentException.class, () -> new PuzzleState(new Position(0, 1),
                new Position(0, 2),
                new Position(0, 3),
                new Position(0, 4),
                new Position(0, 5)));
        assertThrows(IllegalArgumentException.class, () -> new PuzzleState(new Position(0, 4),
                new Position(0, 0),
                new Position(0, 1),
                new Position(2, 0),
                new Position(2, 2)));
    }

    @Test
    void isGoal() {
        assertFalse(state1.isGoal());
        assertTrue(state2.isGoal());
        assertFalse(state3.isGoal());
        assertFalse(state4.isGoal());
    }

    @Test
    void canMove_state1() {
        assertFalse(state1.canMove(PuzzleState.SQUARE, Direction.UP));
        assertFalse(state1.canMove(PuzzleState.SQUARE, Direction.RIGHT));
        assertTrue(state1.canMove(PuzzleState.SQUARE, Direction.DOWN)); // #1 possible move
        assertFalse(state1.canMove(PuzzleState.SQUARE, Direction.LEFT));

        assertFalse(state1.canMove(PuzzleState.TOPLEFT, Direction.UP));
        assertFalse(state1.canMove(PuzzleState.TOPLEFT, Direction.RIGHT));
        assertFalse(state1.canMove(PuzzleState.TOPLEFT, Direction.DOWN));
        assertFalse(state1.canMove(PuzzleState.TOPLEFT, Direction.LEFT));

        assertFalse(state1.canMove(PuzzleState.TOPRIGHT, Direction.UP));
        assertFalse(state1.canMove(PuzzleState.TOPRIGHT, Direction.RIGHT));
        assertFalse(state1.canMove(PuzzleState.TOPRIGHT, Direction.DOWN));
        assertFalse(state1.canMove(PuzzleState.TOPRIGHT, Direction.LEFT));

        assertFalse(state1.canMove(PuzzleState.BOTTOMLEFT, Direction.UP));
        assertFalse(state1.canMove(PuzzleState.BOTTOMLEFT, Direction.RIGHT));
        assertFalse(state1.canMove(PuzzleState.BOTTOMLEFT, Direction.DOWN));
        assertFalse(state1.canMove(PuzzleState.BOTTOMLEFT, Direction.LEFT));

        assertFalse(state1.canMove(PuzzleState.BOTTOMRIGHT, Direction.UP));
        assertTrue(state1.canMove(PuzzleState.BOTTOMRIGHT, Direction.RIGHT)); // #2 possible move
        assertFalse(state1.canMove(PuzzleState.BOTTOMRIGHT, Direction.DOWN));
        assertFalse(state1.canMove(PuzzleState.BOTTOMRIGHT, Direction.LEFT));
    }

    // TODO: fix canMove
    @Test
    void canMove_state2() {
        assertFalse(state2.canMove(PuzzleState.SQUARE, Direction.UP));
        assertFalse(state2.canMove(PuzzleState.SQUARE, Direction.RIGHT));
        assertFalse(state2.canMove(PuzzleState.SQUARE, Direction.DOWN));
        assertFalse(state2.canMove(PuzzleState.SQUARE, Direction.LEFT));

        assertTrue(state1.canMove(PuzzleState.TOPRIGHT, Direction.RIGHT));
        assertTrue(state1.canMove(PuzzleState.BOTTOMRIGHT, Direction.RIGHT));
    }

    @Test
    void canMove_state3() {
        assertFalse(state3.canMove(PuzzleState.SQUARE, Direction.UP));
        assertFalse(state3.canMove(PuzzleState.SQUARE, Direction.RIGHT));
        assertFalse(state3.canMove(PuzzleState.SQUARE, Direction.DOWN));
        assertFalse(state3.canMove(PuzzleState.SQUARE, Direction.LEFT));

        assertTrue(state3.canMove(PuzzleState.TOPRIGHT, Direction.RIGHT));
        assertFalse(state3.canMove(PuzzleState.BOTTOMRIGHT, Direction.RIGHT));

        assertFalse(state3.canMove(PuzzleState.TOPLEFT, Direction.RIGHT));
        assertFalse(state3.canMove(PuzzleState.BOTTOMLEFT, Direction.RIGHT));
    }

    @Test
    void canMove_state4() {
        assertFalse(state4.canMove(PuzzleState.TOPLEFT, Direction.RIGHT));

        assertFalse(state4.canMove(PuzzleState.TOPRIGHT, Direction.RIGHT));

        assertTrue(state4.canMove(PuzzleState.BOTTOMRIGHT, Direction.RIGHT));
        assertTrue(state4.canMove(PuzzleState.BOTTOMRIGHT, Direction.LEFT));
    }

    // TODO: add tests for overlapOf(), isOverlapped

    @Test
    void getLegalMoves() {
        assertEquals(new TreeMap<>(Map.of(0, Direction.DOWN, 4, Direction.RIGHT)), state1.getLegalMoves());
        assertEquals(new TreeMap<>(Map.of(3, Direction.RIGHT, 4, Direction.RIGHT)), state2.getLegalMoves());
        assertEquals(new TreeMap<>(Map.of(0, Direction.LEFT, 2, Direction.DOWN, 3, Direction.RIGHT)), state3.getLegalMoves());
        assertEquals(new TreeMap<>(Map.of(1, Direction.DOWN, 2, Direction.DOWN, 3, Direction.RIGHT, 4, Direction.RIGHT)), state4.getLegalMoves());
    }


    @Test
    void testEquals() {
        assertTrue(state1.equals(state1));

        var clone = state1.clone();
        clone.move(PuzzleState.SQUARE, Direction.DOWN);
        assertFalse(clone.equals(state1));

        assertFalse(state1.equals(null));
        assertFalse(state1.equals("Hello, World!")); // :)
        assertFalse(state1.equals(state2));
    }

    @Test
    void testHashCode() {
        assertTrue(state1.hashCode() == state1.hashCode());
        assertTrue(state1.hashCode() == state1.clone().hashCode());
    }

    @Test
    void testClone() {
        var clone = state1.clone();
        assertTrue(clone.equals(state1));
        assertNotSame(clone, state1);
    }

//    @Test
//    void testToString() {
//        assertEquals("[(0,0),(2,0),(1,1),(0,2)]", state1.toString());
//        assertEquals("[(1,1),(1,1),(1,1),(1,2)]", state2.toString());
//        assertEquals("[(1,1),(2,0),(1,1),(0,2)]", state3.toString());
//        assertEquals("[(0,0),(1,0),(0,1),(0,0)]", state4.toString());
//    }

}
