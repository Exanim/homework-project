package puzzle.model;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;

import java.util.*;

/**
 * Represents the state of the puzzle.
 */
public class PuzzleState implements Cloneable {

    /**
     * The height of the board.
     */
    public static final int BOARD_HEIGHT = 4;

    /**
     * The width of the board.
     */
    public static final int BOARD_WIDTH = 6;

    /**
     * The index of the square.
     */
    public static final int SQUARE = 0;

    /**
     * The index of the top left corner-tile.
     */
    public static final int TOPLEFT = 1;

    /**
     * The index of the top right corner-tile.
     */
    public static final int TOPRIGHT = 2;

    /**
     * The index of the bottom left corner-tile.
     */
    public static final int BOTTOMLEFT = 3;

    /**
     * The index of the bottom right corner-tile.
     */
    public static final int BOTTOMRIGHT = 4;

    private ReadOnlyObjectWrapper<Position>[] positions = new ReadOnlyObjectWrapper[5];

    private ReadOnlyBooleanWrapper goal = new ReadOnlyBooleanWrapper();

    /**
     * Creates a {@code PuzzleState} object that corresponds to the original
     * initial state of the puzzle.
     */
    public PuzzleState() {
        this(new Position(0, 4),
                new Position(0, 0),
                new Position(0, 2),
                new Position(2, 0),
                new Position(2, 2)
        );
    }

    /**
     * Creates a {@code PuzzleState} object initializing the positions of the
     * pieces with the positions specified. The constructor expects an array of
     * five {@code Position} objects or five {@code Position} objects.
     *
     * @param positions the initial positions of the pieces
     */
    public PuzzleState(Position... positions) {
        for (var i = 0; i < positions.length; i++) {
            this.positions[i] = new ReadOnlyObjectWrapper<>(positions[i]);
        }
        checkPositions(positions);
        goal.bind(setGoal());
    }

    private void checkPositions(Position[] positions) {
        if (positions.length != 5) {
            throw new IllegalArgumentException();
        }
        for (var position : positions) {
            if (!isOnBoard(position)) {
                throw new IllegalArgumentException();
            }
        }
        checkForOverlaps(positions);
    }

    private void checkForOverlaps(Position[] positions) {
        for (int tileNumber = 0; tileNumber < 5; tileNumber++) {
            for (int otherTileNumber = tileNumber + 1; otherTileNumber < 5; otherTileNumber++) {
                for (var position : overlapOf(tileNumber)) {
                    if (overlapOf(otherTileNumber).contains(position)) {
                        throw new IllegalArgumentException();
                    }
                }
            }
        }
    }



    /**
     * {@return a copy of the position of the piece specified}
     *
     * @param n the number of a piece
     */
    public Position getPosition(int n) {
        return positions[n].get();
    }

    public ReadOnlyObjectProperty<Position> positionProperty(int n) {
        return positions[n].getReadOnlyProperty();
    }

    /**
     * {@return whether the puzzle is solved}
     */
    public boolean isGoal() {
        return goal.get();
    }

    public ObservableValue<? extends Boolean> setGoal() {
        BooleanBinding condition = Bindings.createBooleanBinding(() ->
                getPosition(SQUARE).equals(getPosition(TOPLEFT).getRight().getDown()) &&
                        getPosition(SQUARE).equals(getPosition(TOPRIGHT).getLeft().getDown()) &&
                        getPosition(SQUARE).equals(getPosition(BOTTOMLEFT).getRight().getUp()) &&
                        getPosition(SQUARE).equals(getPosition(BOTTOMRIGHT).getLeft().getUp())
        );
        return condition;
    }

    public ReadOnlyBooleanProperty goalProperty() {
        return goal.getReadOnlyProperty();
    }

    /**
     * {@return whether the tile can be moved to the direction specified}
     *
     * @param tile the tile which is intended to be moved
     * @param direction a direction to which the tile is intended to be moved
     */
    public boolean canMove(int tile, Direction direction) {
        return switch (direction) {
            case UP -> canMoveUp(tile);
            case RIGHT -> canMoveRight(tile);
            case DOWN -> canMoveDown(tile);
            case LEFT -> canMoveLeft(tile);
        };
    }

    private boolean canMoveUp(int tile) {
        if (getPosition(tile).row() == 0) {
            return false;
        }
        if (tile == BOTTOMLEFT) {
            return isOverlapped(getPosition(tile).getUp()) &&
                    isOverlapped(getPosition(tile).getRight());
        }
        if (tile == BOTTOMRIGHT) {
            return isOverlapped(getPosition(tile)) &&
                    isOverlapped(getPosition(tile).getUp().getRight());
        }
        return isOverlapped(getPosition(tile).getUp()) &&
                isOverlapped(getPosition(tile).getUp().getRight());
    }

    private boolean canMoveRight(int tile) {
        if (getPosition(tile).col() + 1 == BOARD_WIDTH - 1) {
            return false;
        }
        if (tile == TOPLEFT) {
            return isOverlapped(getPosition(tile).getRight().getRight()) &&
                    isOverlapped(getPosition(tile).getRight().getDown());
        }
        if (tile == BOTTOMLEFT) {
            return isOverlapped(getPosition(tile).getRight()) &&
                    isOverlapped(getPosition(tile).getRight().getRight().getDown());
        }
        return isOverlapped(getPosition(tile).getRight().getRight()) &&
                isOverlapped(getPosition(tile).getRight().getRight().getDown());
    }

    private boolean canMoveDown(int tile) {
        if (getPosition(tile).row() + 1 == BOARD_HEIGHT - 1) {
            return false;
        }
        if (tile == TOPLEFT) {
            return isOverlapped(getPosition(tile).getDown().getDown()) &&
                    isOverlapped(getPosition(tile).getRight().getDown());
        }
        if (tile == TOPRIGHT) {
            return isOverlapped(getPosition(tile).getDown()) &&
                    isOverlapped(getPosition(tile).getRight().getDown().getDown());
        }
        return isOverlapped(getPosition(tile).getDown().getDown()) &&
                isOverlapped(getPosition(tile).getDown().getDown().getRight());
    }

    private boolean canMoveLeft(int tile) {
        if (getPosition(tile).col() == 0) {
            return false;
        }
        if (tile == TOPRIGHT) {
            return isOverlapped(getPosition(tile).getRight()) &&
                    isOverlapped(getPosition(tile).getDown());
        }
        if (tile == BOTTOMRIGHT) {
            return isOverlapped(getPosition(tile)) &&
                    isOverlapped(getPosition(tile).getDown().getLeft());
        }
        return isOverlapped(getPosition(tile).getLeft()) &&
                isOverlapped(getPosition(tile).getLeft().getDown());
    }

    /**
     * Moves the tile to the direction specified.
     *
     * @param tile the tile that is being moved
     * @param direction the direction to which the tile is moved
     */
    public void move(int tile, Direction direction) {
        switch (direction) {
            case UP -> movePiece(tile, Direction.UP);
            case RIGHT -> movePiece(tile, Direction.RIGHT);
            case DOWN -> movePiece(tile, Direction.DOWN);
            case LEFT -> movePiece(tile, Direction.LEFT);
        }
    }

    private void movePiece(int n, Direction direction) {
        var newPosition = getPosition(n).getPosition(direction);
        positions[n].set(newPosition);
    }

    /**
     * {@return all the directions each tile can be moved}
     */
    // TODO: modify legalMoves to TreeMap<Integer, ArrayList<Direction>> because each tile could move different directions
    public TreeMap<Integer, Direction> getLegalMoves() {
        TreeMap<Integer, Direction> legalMoves = new TreeMap<>();
        for (var direction : Direction.values()) {
            for (int tileNumber = 0; tileNumber < 5; tileNumber++) {
                if (canMove(tileNumber, direction)) {
                    legalMoves.put(tileNumber, direction);
                }
            }
        }
        return legalMoves;
    }

    private boolean isOnBoard(Position position) {
        return position.row() >= 0 && position.row() < BOARD_HEIGHT &&
                position.col() >= 0 && position.col() < BOARD_WIDTH;
    }

    public boolean isOverlapped(Position position) {
        if (!isOnBoard(position)) {
            return true;
        }
        // handle the case where bottom right's position is empty because of the nature of it's shape
        if (position.equals(getPosition(BOTTOMRIGHT))) {
            return true;
        }
        for (int tileNumber = 0; tileNumber < 5; tileNumber++) {
            if (overlapOf(tileNumber).contains(position)) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<Position> overlapOf(int tile) {
        switch (tile) {
            case SQUARE -> {
                return getAsList(getPosition(SQUARE), getPosition(SQUARE).getRight(), getPosition(SQUARE).getDown(),
                        getPosition(SQUARE).getRight().getDown());
            }
            case TOPLEFT -> {
                return getAsList(getPosition(TOPLEFT), getPosition(TOPLEFT).getRight(), getPosition(TOPLEFT).getDown());
            }
            case TOPRIGHT -> {
                return getAsList(getPosition(TOPRIGHT), getPosition(TOPRIGHT).getRight(),
                        getPosition(TOPRIGHT).getRight().getDown());

            }
            case BOTTOMLEFT -> {
                return getAsList(getPosition(BOTTOMLEFT), getPosition(BOTTOMLEFT).getDown(),
                        getPosition(BOTTOMLEFT).getDown().getRight());

            }
            case BOTTOMRIGHT -> {
                return getAsList(getPosition(BOTTOMRIGHT).getDown(), getPosition(BOTTOMRIGHT).getDown().getRight(),
                        getPosition(BOTTOMRIGHT).getRight());

            }
            default -> throw new IllegalArgumentException();
        }
    }

    public ArrayList<Position> getAsList(Position... positions) {
        ArrayList<Position> list = new ArrayList<>(Arrays.asList(positions));
        return list;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        return (o instanceof PuzzleState other) && equals(positions, other.positions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(positions[0].get(), positions[1].get(), positions[2].get(), positions[3].get(), positions[4].get());
    }

    @Override
    public PuzzleState clone() {
        return new PuzzleState(getPosition(0), getPosition(1), getPosition(2), getPosition(3), getPosition(4));
    }

    @Override
    public String toString() {
        var sj = new StringJoiner(",", "[", "]");
        for (var position : positions) {
            sj.add(position.get().toString());
        }
        return sj.toString();
    }

    private boolean equals(ObservableValue[] a1, ObservableValue[] a2) {
        if (a1.length != a2.length) {
            return false;
        }
        for (var i = 0; i < a1.length; i++) {
            if (!a1[i].getValue().equals(a2[i].getValue())) {
                return false;
            }
        }
        return true;
    }

}
