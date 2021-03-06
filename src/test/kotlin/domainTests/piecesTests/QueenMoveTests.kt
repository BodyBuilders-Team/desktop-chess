package domainTests.piecesTests

import domain.board.Board
import isValidMove
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class QueenMoveTests { // [✔]
    private val sut = Board(
        "        " +
            "        " +
            "        " +
            " p      " +
            "        " +
            "        " +
            " Q      " +
            "       K"
    )

    @Test
    fun `Queen vertical(up) move is valid`() {
        assertTrue(sut.isValidMove("Qb2b4"))
    }

    @Test
    fun `Queen vertical(down) move is valid`() {
        assertTrue(sut.isValidMove("Qb2b1"))
    }

    @Test
    fun `Queen horizontal(right) move is valid`() {
        assertTrue(sut.isValidMove("Qb2f2"))
    }

    @Test
    fun `Queen horizontal(left) move is valid`() {
        assertTrue(sut.isValidMove("Qb2a2"))
    }

    @Test
    fun `Queen move with capture is valid`() {
        assertTrue(sut.isValidMove("Qb2b5"))
    }

    @Test
    fun `Queen move(up,right) with capture is valid`() {
        assertTrue(sut.isValidMove("Qb2c3"))
    }

    @Test
    fun `Queen move(up,left) is valid`() {
        assertTrue(sut.isValidMove("Qb2a3"))
    }

    @Test
    fun `Queen move(down,right) is valid`() {
        assertTrue(sut.isValidMove("Qb2c1"))
    }

    @Test
    fun `Queen move(down,left) is valid`() {
        assertTrue(sut.isValidMove("Qb2a1"))
    }

    @Test
    fun `Queen move to same place is not valid`() {
        assertFalse(sut.isValidMove("Qb2b2"))
    }
}
