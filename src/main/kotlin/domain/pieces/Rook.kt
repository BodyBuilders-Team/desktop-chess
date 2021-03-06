package domain.pieces

import domain.board.Board
import domain.move.Move
import domain.move.isValidStraightMove

/**
 * Rook piece with [type] 'R'.
 *
 * Moves horizontally or vertically
 * @param army piece army
 */
data class Rook(override val army: Army) : Piece {

    override val type = PieceType.ROOK

    override fun toString() = "$army $type"

    override fun isValidMove(board: Board, move: Move) = isValidStraightMove(board, move)
}
