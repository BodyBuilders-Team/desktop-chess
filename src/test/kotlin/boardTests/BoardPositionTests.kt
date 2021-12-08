package boardTests

import domain.board.Board
import kotlin.test.*


class BoardPositionTests {

    @Test
    fun `Position with column outside bounds throws`() {
        assertFailsWith<IllegalArgumentException> {
            Board.Position(col = 'x', row = 2)
        }
    }

    @Test
    fun `Position with row outside bounds throws`() {
        assertFailsWith<IllegalArgumentException> {
            Board.Position(col = 'a', row = 9)
        }
    }

    @Test
    fun `Position with column and row inside bounds works`() {
        Board.Position(col = 'a', row = 5)
    }


    @Test
    fun `Position toString works`() {
        Board.Position(col = 'a', row = 5)
        assertEquals("a5", Board.Position(col = 'a', row = 5).toString())
    }

}
