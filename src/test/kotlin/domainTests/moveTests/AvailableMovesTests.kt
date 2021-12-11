package domainTests.moveTests

import domain.Game
import domain.board.*
import domain.move.*
import domain.pieces.Army
import kotlin.test.*


class AvailableMovesTests {

    // getAvailableMoves

    @Test
    fun `getAvailableMoves returns emptyList when there are no available moves for queen`() {
        val sut = Board()
        val position = Board.Position('d', 1)
        val piece = sut.getPiece(position)

        assertNotNull(piece)
        assertEquals(emptyList(), piece.getAvailableMoves(Game(sut, emptyList()), position))
    }

    @Test
    fun `getAvailableMoves returns available moves for queen`() {
        val sut = Board(
            getMatrix2DFromString(
                "rnbqkbnr" +
                "pppppppp" +
                "        " +
                "        " +
                "        " +
                " P P    " +
                "PP  PPPP" +
                "RN QKBNR"
            )
        )
        val position = Board.Position('d', 1)
        val piece = sut.getPiece(position)

        assertNotNull(piece)
        assertEquals(
            setOf("Qd1c1", "Qd1c2", "Qd1d2"),
            piece.getAvailableMoves(Game(sut, emptyList()), position).map { it.toString() }.toSet()
        )
    }

    @Test
    fun `getAvailableMoves returns available moves, including castle move, for king`() {
        val sut = Board(
            getMatrix2DFromString(
                "rnbqkbnr" +
                "pppppppp" +
                "        " +
                "        " +
                "        " +
                "        " +
                "PPPPPPPP" +
                "R   KBNR"
            )
        )
        val position = Board.Position('e', 1)
        val piece = sut.getPiece(position)

        assertNotNull(piece)
        assertEquals(
            setOf("Ke1c1", "Ke1d1"),
            piece.getAvailableMoves(Game(sut, emptyList()), position).map { it.toString() }.toSet()
        )
    }

    @Test
    fun `getAvailableMoves returns available moves, including en passant move, for pawn`() {
        val sut = Board(
            getMatrix2DFromString(
                "rnbqkbnr" +
                "ppp pppp" +
                "        " +
                "   pP   " +
                "        " +
                "        " +
                "PPPP PPP" +
                "R   KBNR"
            )
        )
        val position = Board.Position('e', 5)
        val previousMoves = listOf(Move("Pd7d5"))

        val piece = sut.getPiece(position)

        assertNotNull(piece)
        assertEquals(
            setOf("Pe5e6", "Pe5d6"),
            piece.getAvailableMoves(Game(sut, previousMoves), position).map { it.toString() }.toSet()
        )
    }

    // hasAvailableMoves

    @Test
    fun `hasAvailableMoves returns true with default board`() {
        val sut = Board()
        assertTrue(Game(sut, emptyList()).hasAvailableMoves(Army.WHITE))
        assertTrue(Game(sut, emptyList()).hasAvailableMoves(Army.BLACK))
    }

    @Test
    fun `hasAvailableMoves returns false with empty board`() {
        val sut = Board(getMatrix2DFromString("        ".repeat(BOARD_SIDE_LENGTH)))
        assertFalse(Game(sut, emptyList()).hasAvailableMoves(Army.WHITE))
        assertFalse(Game(sut, emptyList()).hasAvailableMoves(Army.BLACK))
    }

    // currentTurnArmy

    @Test
    fun `currentTurnArmy returns White when number of moves is even`() {
        assertEquals(Army.WHITE, currentTurnArmy(emptyList()))
    }

    @Test
    fun `currentTurnArmy returns Black when number of moves is odd`() {
        assertEquals(Army.BLACK, currentTurnArmy(listOf(Move("Pe2e4"))))
    }

    // isWhiteTurn

    @Test
    fun `isWhiteTurn returns true when number of moves is even`() {
        assertTrue(isWhiteTurn(emptyList()))
    }

    @Test
    fun `isWhiteTurn returns false when number of moves is odd`() {
        assertFalse(isWhiteTurn(listOf(Move("Pe2e4"))))
    }
}
