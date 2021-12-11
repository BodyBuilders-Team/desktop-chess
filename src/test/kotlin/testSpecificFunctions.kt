import domain.Game
import domain.board.Board
import domain.move.*


/**
 * Makes a move in the board.
 *
 * ONLY USED IN TESTS. Has no knowledge of previous moves in the board.
 * @param moveInString move to make in string
 * @return new board with the move made
 */
fun Board.makeMove(moveInString: String): Board {
    return makeMove(Move.validated(moveInString, Game(this, emptyList())))
}


/**
 * Checks if a move [moveInString] is valid.
 * Returns false when an [IllegalMoveException] is thrown.
 *
 * ONLY USED IN TESTS. Has no knowledge of previous moves in the board.
 *
 * @param moveInString piece move in string
 * @return true if the move is valid
 */
fun Board.isValidMove(moveInString: String): Boolean {
    try {
        Move.validated(moveInString, Game(this, emptyList()))
    } catch (err: IllegalMoveException) {
        return false
    }
    return true
}