import domain.GameState
import domain.Session
import domain.SessionState
import domain.gameFromMoves
import domain.pieces.Army
import ui.console.*
import java.io.ByteArrayOutputStream


object ViewsTests {

    data class ViewTest(val view: View, val name: String, val session: Session)

    private val defaultSession = Session(name = "test", SessionState.YOUR_TURN, Army.WHITE, gameFromMoves(), GameState.NO_CHECK)
    
    fun openViewTests(){
        testViews(listOf(
            ViewTest(::openView, "Open game without check",
                defaultSession.copy(state = SessionState.YOUR_TURN, gameState = GameState.NO_CHECK)),

            ViewTest(::openView, "Open game with check",
                defaultSession.copy(state = SessionState.YOUR_TURN, gameState = GameState.CHECK)),

            ViewTest(::openView, "Open game with checkmate",
                defaultSession.copy(state = SessionState.ENDED, gameState = GameState.CHECKMATE)),

            ViewTest(::openView, "Open game with stalemate",
                defaultSession.copy(state = SessionState.ENDED, gameState = GameState.STALEMATE))
        ))
    }

    fun joinViewTests(){
        testViews(listOf(
            ViewTest(::joinView, "Join game without check",
                defaultSession.copy(state = SessionState.YOUR_TURN, gameState = GameState.NO_CHECK)),

            ViewTest(::joinView, "Join game with check",
                defaultSession.copy(state = SessionState.YOUR_TURN, gameState = GameState.CHECK)),

            ViewTest(::joinView, "Join game with checkmate",
                defaultSession.copy(state = SessionState.ENDED, gameState = GameState.CHECKMATE)),

            ViewTest(::joinView, "Join game with stalemate",
                defaultSession.copy(state = SessionState.ENDED, gameState = GameState.STALEMATE))
        ))
    }

    fun playViewTests(){
        testViews(listOf(
            ViewTest(::playView, "Play didn't originate check",
                defaultSession.copy(state = SessionState.WAITING_FOR_OPPONENT, gameState = GameState.NO_CHECK)),

            ViewTest(::playView, "Play originated a check",
                defaultSession.copy(state = SessionState.WAITING_FOR_OPPONENT, gameState = GameState.CHECK)),

            ViewTest(::playView, "Play originated a checkmate",
                defaultSession.copy(state = SessionState.ENDED, gameState = GameState.CHECKMATE)),

            ViewTest(::playView, "Play originated a stalemate",
                defaultSession.copy(state = SessionState.ENDED, gameState = GameState.STALEMATE))
        ))
    }

    fun refreshViewTests(){
        testViews(listOf(
            ViewTest(::refreshView, "Refresh game without check and it's your turn",
                defaultSession.copy(state = SessionState.YOUR_TURN, gameState = GameState.NO_CHECK)),

            ViewTest(::refreshView, "Refresh game with check",
                defaultSession.copy(state = SessionState.YOUR_TURN, gameState = GameState.CHECK)),

            ViewTest(::refreshView, "Refresh game with checkmate",
                defaultSession.copy(state = SessionState.ENDED, gameState = GameState.CHECKMATE)),

            ViewTest(::refreshView, "Refresh game with stalemate",
                defaultSession.copy(state = SessionState.ENDED, gameState = GameState.STALEMATE))
        ))
    }
    
    fun movesViewTests(){
        testViews(listOf(
            ViewTest(::movesView, "Moves",
                defaultSession.copy(game = gameFromMoves("Pe2e4", "Pe7e5", "Nc3")))
        ))
    }

    fun helpViewTests(){
        testViews(listOf(
            ViewTest(::helpView, "Help",
                defaultSession)
        ))
    }

    private fun testViews(views: List<ViewTest>) {
        views.forEach {
            println("-----------------------------\nView start - ${it.name}\n-----------------------------\n")
            it.view(it.session)
            println("\n-----------------------------\nView end - ${it.name}\n-----------------------------")
            println("Press enter to show next view.")
            readLine()!!
            println("\n\n\n".repeat(10))
        }
    }
}

fun main(){
    ViewsTests.openViewTests()
    ViewsTests.joinViewTests()
    ViewsTests.playViewTests()
    ViewsTests.refreshViewTests()
    ViewsTests.movesViewTests()
    ViewsTests.helpViewTests()
}
