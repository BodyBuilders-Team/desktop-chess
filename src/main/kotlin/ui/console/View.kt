package ui.console

import domain.Session
import domain.SessionState
import domain.board.BOARD_SIDE_LENGTH
import domain.board.Board
import domain.game.Game
import domain.game.GameState
import domain.game.armyToPlay
import domain.game.makeMove
import domain.game.state
import domain.move.Move

/**
 * Representation of view
 */
typealias View = (Session) -> Unit

/**
 * Shows view after opening game.
 * @param session opened session
 */
fun openView(session: Session) {
    openingGameView(session, whiteArmy = true)
}

/**
 * Shows view after joining game.
 * @param session joined session
 */
fun joinView(session: Session) {
    openingGameView(session, whiteArmy = false)
}

/**
 * Shows view after playing a move.
 * @param session session after play
 */
fun playView(session: Session) {
    afterMoveView(session, playerTurn = false)
}

/**
 * Shows view after refreshing.
 * @param session refreshed session
 */
fun refreshView(session: Session) {
    afterMoveView(session, playerTurn = true)
}

/**
 * Shows view for an opening game command.
 * @param session session after opening game
 * @param whiteArmy if the current army playing is white
 */
private fun openingGameView(session: Session, whiteArmy: Boolean) {
    printBoard(session.game.board)
    println("${if (whiteArmy) "Opened" else "Joined"} game ${session.name}. Play with ${if (whiteArmy) "white" else "black"} pieces.")
    if (session.game.state != GameState.NO_CHECK) {
        println(
            when (session.game.state) {
                GameState.CHECK -> "${if (session.state == SessionState.YOUR_TURN) "Your" else "Enemy"} King is in check."
                GameState.CHECKMATE ->
                    "Game ended in checkmate, ${
                    session.game.armyToPlay.other().toString().lowercase()
                    } won!"
                GameState.STALEMATE -> "Game ended in draw by stalemate!"
                GameState.FIFTY_MOVE_RULE -> "Game ended in draw by fifty move rule!"
                GameState.THREE_FOLD -> "Game ended in draw by repetition!"
                GameState.DEAD_POSITION -> "Game ended in draw by insufficient material!"
                GameState.NO_CHECK -> ""
            }
        )
    }
}

/**
 * Shows view for a command that makes a move in the board (Play or Refresh).
 * @param session session after making a move in the board
 * @param playerTurn if it's the player's turn to play
 */
private fun afterMoveView(session: Session, playerTurn: Boolean) {
    printBoard(session.game.board)
    if (session.game.state != GameState.NO_CHECK) {
        println(
            when (session.game.state) {
                GameState.CHECK -> "${if (playerTurn) "Your" else "Enemy"} King is in check."
                GameState.CHECKMATE -> "${if (playerTurn) "Your" else "Enemy"} King is in checkmate. Game ended, you ${if (playerTurn) "lose" else "win"}!"
                GameState.STALEMATE -> "${if (playerTurn) "Your" else "Enemy"} King is in stalemate. Game ended, it's a draw!"
                GameState.FIFTY_MOVE_RULE -> "Game ended in draw by fifty move rule!"
                GameState.THREE_FOLD -> "Game ended in draw by repetition!"
                GameState.DEAD_POSITION -> "Game ended in draw by insufficient material!"
                GameState.NO_CHECK -> ""
            }
        )
    }
}

/**
 * Prints all the moves made in the game
 * @param session current session
 */
fun movesView(session: Session) {
    session.game.moves.forEachIndexed { index, move -> println("${index + 1}. $move") }
}

/**
 * Prints all the possible commands.
 * @param session current session
 */
fun helpView(session: Session) {
    println(
        "${session.name} -> These are the application commands you can use:\n" +
            "open <game> - Opens or joins the game named <game> to play with the White pieces\n" +
            "join <game> - Joins the game named <game> to play with the Black pieces\n" +
            "play <move> - Makes the <move> play\n" +
            "refresh     - Refreshes the game\n" +
            "moves       - Prints all moves made\n" +
            "exit        - Ends the application"
    )
}

/**
 * Prints the board, one row in each line.
 * @param board board to print
 */
fun printBoard(board: Board) {
    println("     a b c d e f g h  ")
    println("    ----------------- ")
    board.toString().chunked(BOARD_SIDE_LENGTH).forEachIndexed { idx, cols ->
        print(" ${(BOARD_SIDE_LENGTH - idx)} | ")
        cols.forEach { print("$it ") }
        println("|")
    }
    println("    ----------------- ")
}

/**
 * Receives multiple moves to be made in a new game, and shows the step-by-step execution of the game on the console.
 *
 * To go to the next move, press ENTER.
 * @param movesInString moves to make in string
 */
fun viewGameExecution(vararg movesInString: String) {
    var newGame = Game(Board(), emptyList())

    printBoard(newGame.board)

    movesInString.forEach { moveInString ->
        println("Next move: $moveInString")

        readLine()!!
        println("\n\n".repeat(30))

        newGame = newGame.makeMove(Move.validated(moveInString, newGame))

        printBoard(newGame.board)
    }
}

fun main() {
    viewGameExecution("f3", "e5", "g4", "Qh4")
}
