package domain.move

import domain.Game
import domain.board.*
import domain.board.Board.*
import domain.pieces.*

const val INITIAL_KING_COL = 'e'
const val INITIAL_ROOK_COL_FURTHER_FROM_KING = FIRST_COL
const val INITIAL_ROOK_COL_CLOSER_TO_KING = LAST_COL
const val LONG_CASTLE_ROOK_COL = 'd'
const val SHORT_CASTLE_ROOK_COL = 'f'
const val LONG_CASTLE_KING_COL = 'c'
const val SHORT_CASTLE_KING_COL = 'g'


/**
 * Checks if the move is a valid en passant.
 * @param piece piece to check if the move is a valid en passant
 * @param game game where the move will happen
 * @return true if the move is a valid en passant
 */
fun Move.isValidEnPassant(piece: Piece, game: Game) =
    piece is Pawn && piece.isValidEnPassant(game.board, this) &&
            isEnPassantPossible(piece, game.moves)


/**
 * Checks if the move is a valid castle.
 * @param piece piece to check if the move is a valid castle
 * @param game game where the move will happen
 * @return true if the move is a valid castle
 */
fun Move.isValidCastle(piece: Piece, game: Game) =
    piece is King && !game.board.isKingInCheck(piece.army) &&
            piece.isValidCastle(game.board, this) && isCastlePossible(piece, game.moves)


/**
 * Checks if the last move is valid to do en passant move immediately next.
 * @param piece piece that makes en passant
 * @param previousMoves previous game moves
 * @return true if the last move is valid
 */
fun Move.isEnPassantPossible(piece: Piece, previousMoves: List<Move>) =
    previousMoves.isNotEmpty() && previousMoves.last().toString() in listOf(
        "P${from.col - 1}${from.row + 2 * if (piece.isWhite()) 1 else -1}${from.col - 1}${from.row}",
        "P${from.col + 1}${from.row + 2 * if (piece.isWhite()) 1 else -1}${from.col + 1}${from.row}"
    )


/**
 * Checks if a castle move is possible.
 * @param piece piece that makes castle move
 * @param previousMoves previous game moves
 * @return true if the castle move is possible
 */
fun Move.isCastlePossible(piece: Piece, previousMoves: List<Move>) =
    previousMoves.none { move ->
        move.from in listOf(
            Position(INITIAL_KING_COL, if (piece.isWhite()) WHITE_FIRST_ROW else BLACK_FIRST_ROW),
            Position(
                if (to.col == LONG_CASTLE_KING_COL)
                    INITIAL_ROOK_COL_FURTHER_FROM_KING
                else
                    INITIAL_ROOK_COL_CLOSER_TO_KING,
                if (piece.isWhite()) WHITE_FIRST_ROW else BLACK_FIRST_ROW
            )
        )
    }


object Castle {
    /**
     * Gets the from position of the rook in a castle move.
     * @param kingToPos king position after the castle move
     * @return position of the rook
     */
    fun getRookPosition(kingToPos: Position) =
        Position(
            col = if (kingToPos.col == SHORT_CASTLE_KING_COL) INITIAL_ROOK_COL_CLOSER_TO_KING else INITIAL_ROOK_COL_FURTHER_FROM_KING,
            row = kingToPos.row
        )

    /**
     * Gets the to position of the rook in a castle move.
     * @param kingToPos king position after the castle move
     * @return position of the rook
     */
    fun getRookToPosition(kingToPos: Position) =
        Position(
            col = if (kingToPos.col == SHORT_CASTLE_KING_COL) SHORT_CASTLE_ROOK_COL else LONG_CASTLE_ROOK_COL,
            row = kingToPos.row
        )
}


/**
 * Gets the position of the captured pawn from the en passant move.
 * @param attackerToPos position of the attacker after the move
 * @param attackingPiece attacker
 * @return position of the captured pawn
 */
fun getEnPassantCapturedPawnPosition(attackerToPos: Position, attackingPiece: Piece) =
    Position(
        col = attackerToPos.col,
        row = attackerToPos.row + if (attackingPiece.isWhite()) -1 else 1
    )


