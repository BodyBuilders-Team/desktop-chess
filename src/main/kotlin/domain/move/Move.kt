package domain.move

import domain.*
import domain.board.*
import kotlin.math.abs
import domain.board.Board.Position
import domain.pieces.*


/**
 * Type of the move.
 */
enum class MoveType {
    NORMAL,
    CASTLE,
    EN_PASSANT
}


/**
 * Chess move.
 * @property symbol Piece Moving
 * @property from original piece position
 * @property capture true if the piece will capture enemy piece
 * @property to new piece position
 * @property promotion new PieceType of promotion or null
 * @property type move type
 */
data class Move(
    val symbol: Char,
    val from: Position,
    val capture: Boolean,
    val to: Position,
    val promotion: Char?,
    val type: MoveType
) {
    companion object {

        private val normalMoveRegex = "[PKQNBR]?[a-h]?[1-8]?x?([a-h][1-8])(=[QNBR])?".toRegex().toString()
        private val castleMoveRegex = "(O-O(-O)?)".toRegex().toString()
        private val moveRegex = Regex("^($normalMoveRegex|$castleMoveRegex)\$")
        private const val CAPTURE_CHAR = 'x'
        private const val PROMOTION_CHAR = '='
        private const val MIN_STRING_LEN = 1
        private const val TWO_STRING_LEN = 2
        private const val THREE_STRING_LEN = 3


        /**
         * Move constructor that receives a String.
         * @param moveInString move in string format
         * @return the move extracted from the string, unvalidated in the context of a game
         */
        operator fun invoke(moveInString: String): Move {
            return extractMoveInfo(moveInString).move
        }


        /**
         * Returns a move already validated in the context of a game.
         *
         * Move properties are extracted from [moveInString], it's verified if the move is possible by searching the [game] board.
         * @param moveInString move in string format
         * @param game game where the move will happen
         * @return the validated move
         * @throws IllegalMoveException if move is not possible in [game] or multiple possible moves were found
         */
        fun validated(moveInString: String, game: Game): Move {
            val (move, optionalFromCol, optionalFromRow) = extractMoveInfo(moveInString)
            val validMoves = game.searchMoves(move, optionalFromCol, optionalFromRow, optionalToPos = false)

            if (validMoves.size != 1) throw IllegalMoveException(
                move.toString(optionalFromCol, optionalFromRow),
                if (optionalFromCol || optionalFromRow)
                    "Try with origin column and row." else "Illegal move."
            )

            return validMoves.first()
        }
        

        /**
         * Move extraction
         * @param move unvalidated extracted move
         * @param optionalFromCol if from column is optional
         * @param optionalFromRow if from row is optional
         */
        data class MoveExtraction(val move: Move, val optionalFromCol: Boolean, val optionalFromRow: Boolean)


        /**
         * Extracts move information from a string.
         * This move is still unvalidated in the context of a game.
         * 
         * The returned move will have [FIRST_COL] as the fromCol, and [FIRST_ROW] as the fromRow in case of optional column and/or row, respectively.
         * 
         * @param moveInString move in string
         * @return MoveExtraction containing the unvalidated move, and information on whether the column and/or row is optional
         * @throws IllegalMoveException if move string is not well formatted
         */
        fun extractMoveInfo(moveInString: String): MoveExtraction {
            if (!isCorrectlyFormatted(moveInString))
                throw IllegalMoveException(
                    moveInString, "Unrecognized Play. Use format: [<piece>][<from>][x][<to>][=<piece>], [O-O] or [O-O-O]"
                )

            if(moveInString in listOf("O-O", "O-O-O")){
                return MoveExtraction(
                    Move(
                        'K',
                        Position(INITIAL_KING_COL, 1),
                        capture = false,
                        Position(if (moveInString == "O-O") SHORT_CASTLE_KING_COL else LONG_CASTLE_KING_COL, 1),
                        promotion = null,
                        MoveType.CASTLE
                    ),
                    optionalFromCol = false,
                    optionalFromRow = false
                )
            }
            
            var str = moveInString

            val capture = CAPTURE_CHAR in str

            val promotion = if (PROMOTION_CHAR in str) str.last() else null
            if (promotion != null) str = str.dropLast(2)

            val toPos = Position(str[str.lastIndex - 1], str.last().digitToInt())
            str = str.dropLast(if (capture) 3 else 2)

            var pieceSymbol = 'P'
            var fromRow: Int? = null
            var fromCol: Char? = null


            when (str.length) {
                MIN_STRING_LEN ->
                    when {
                        str.first().isUpperCase() -> pieceSymbol = str.first()
                        str.first().isDigit() -> fromRow = str.first().digitToInt()
                        str.first().isLowerCase() -> fromCol = str.first()
                    }

                TWO_STRING_LEN ->
                    when {
                        str.first().isUpperCase() -> {
                            pieceSymbol = str.first()
                            if (str.last().isLowerCase()) fromCol = str.last()
                            else fromRow = str.last().digitToInt()
                        }

                        str.first().isLowerCase() -> {
                            fromCol = str.first()
                            fromRow = str.last().digitToInt()
                        }
                    }

                THREE_STRING_LEN -> {
                    pieceSymbol = str.first()
                    fromCol = str[1]
                    fromRow = str[2].digitToInt()
                }
            }

            return MoveExtraction(
                Move(
                    pieceSymbol,
                    Position(fromCol ?: FIRST_COL, fromRow ?: FIRST_ROW),
                    capture,
                    toPos,
                    promotion,
                    MoveType.NORMAL
                ),
                optionalFromCol = fromCol == null,
                optionalFromRow = fromRow == null
            )
        }


        /**
         * Checks if a move in String is correctly formatted.
         * @param moveInString piece move
         * @return true if the move in String is correctly formatted
         */
        fun isCorrectlyFormatted(moveInString: String) = moveRegex.containsMatchIn(moveInString)
    }


    /**
     * Return true if the movement is vertical
     * @return true if the movement is vertical
     */
    fun isVertical() = from.col == to.col && rowsAbsoluteDistance() != 0

    /**
     * Return true if the movement is horizontal
     * @return true if the movement is horizontal
     */
    fun isHorizontal() = from.row == to.row && colsAbsoluteDistance() != 0

    /**
     * Return true if the movement is straight (horizontal or vertical)
     * @return true if the movement is straight (horizontal or vertical)
     */
    fun isStraight() = isHorizontal() xor isVertical()

    /**
     * Return true if the movement is diagonal
     * @return true if the movement is diagonal
     */
    fun isDiagonal() = rowsAbsoluteDistance() == colsAbsoluteDistance() && rowsDistance() != 0


    /**
     * Calculates the distance between the rows from the move
     * @return distance between the rows
     */
    fun rowsDistance(): Int = to.row - from.row

    /**
     * Calculates the absolute distance between the rows from the move
     * @return distance between the rows
     */
    fun rowsAbsoluteDistance(): Int = abs(rowsDistance())

    /**
     * Calculates the distance between the columns from the move
     * @return distance between the columns
     */
    fun colsDistance(): Int = to.col - from.col

    /**
     * Calculates the absolute distance between the columns from the move
     * @return distance between the columns
     */
    fun colsAbsoluteDistance(): Int = abs(colsDistance())


    override fun toString() =
        "$symbol$from${if (capture) "x" else ""}$to${if (promotion != null) "=$promotion" else ""}" // ( ͡° ͜ʖ ͡°)

    /**
     * Returns a string representation of the move, with the possibility to omit fromCol and fromRow.
     * @param optionalFromCol if fromCol is to be omitted
     * @param optionalFromRow if romRow is to be omitted
     * @return string representation of the move
     */
    fun toString(optionalFromCol: Boolean, optionalFromRow: Boolean) =
        "$symbol" +
                "${if (!optionalFromCol) from.col else ""}${if (!optionalFromRow) from.row else ""}" +
                (if (capture) "x" else "") +
                "$to" +
                if (promotion != null) "=$promotion" else ""
}


/**
 * Gets a validated move, with information of its move type and capture, or null if the move isn't valid.
 * @param piece piece of the move's from position
 * @param game game where the move will happen
 * @return validated move, with information of its move type and capture, or null if the move isn't valid.
 */
fun Move.getValidatedMove(piece: Piece, game: Game): Move? {
    val validMove = when {
        isValidEnPassant(piece, game) -> copy(type = MoveType.EN_PASSANT)
        isValidCastle(piece, game) -> copy(type = MoveType.CASTLE)
        piece.isValidMove(game.board, this) && isValidCapture(piece, game.board) -> copy(type = MoveType.NORMAL)
        else -> return null
    }

    if (game.board.makeMove(validMove).isKingInCheck(piece.army)) return null

    return validMove.copy(capture = game.board.isPositionOccupied(validMove.to))
}

/**
 * Checks if the capture in the move is valid.
 *
 * Also checking, if the capture is a promotion, if it's a valid promotion.
 * To promote, a piece needs to be a pawn and its next move has to be to the opposite player's first row.
 * @param piece move with the capture
 * @param board board where the move happens
 * @return true if the capture is valid
 */
fun Move.isValidCapture(piece: Piece, board: Board): Boolean {
    val isPromotion = piece is Pawn && (piece.isWhite() && to.row == BLACK_FIRST_ROW ||
            !piece.isWhite() && to.row == WHITE_FIRST_ROW)
    
    val isValidPromotion = isPromotion == (promotion != null)

    val capturedPiece = board.getPiece(to) ?: return !capture && isValidPromotion

    return piece.army != capturedPiece.army && isValidPromotion
}
