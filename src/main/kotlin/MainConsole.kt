import domain.INITIAL_SESSION // ktlint-disable filename
import domain.commands.CommandException
import domain.move.IllegalMoveException
import storage.DbMode
import storage.ENV_DB_NAME
import storage.GameStorageAccessException
import storage.MongoDBGameStorage
import storage.getDBConnectionInfo
import storage.mongodb.createMongoClient
import storage.tryDataBaseAccess
import ui.console.getPrompt
import ui.console.readCommand

/**
 * The application entry point.
 *
 * The application supports the following commands:
 * - open <game> - Opens or joins the game named <game> to play with the White pieces
 * - join <game> - Joins the game named <game> to play with the Black pieces
 * - play <move> - Makes the <move> play
 * - refresh - Refreshes the game
 * - moves - Prints all moves made
 * - exit - Ends the application
 * - help - Prints all the commands above.
 *
 * Execution is parameterized through the following environment variables:
 * - MONGO_DB_NAME, bearing the name of the database to be used
 * - MONGO_DB_CONNECTION, bearing the connection string to the database server. If absent, the application
 * uses a local server instance (it must be already running)
 */
suspend fun main() {
    val dbInfo = getDBConnectionInfo()
    val driver = createMongoClient(if (dbInfo.mode == DbMode.REMOTE) dbInfo.connectionString else null)

    driver.use {
        try {
            var session = INITIAL_SESSION
            val gameStorage = MongoDBGameStorage(tryDataBaseAccess { driver.getDatabase(System.getenv(ENV_DB_NAME)) })

            while (true) {
                try {
                    val dispatcher = buildCommandsHandler(session, gameStorage)
                    val (command, parameter) = readCommand(getPrompt(session))

                    val handler = dispatcher[command]
                    if (handler == null) {
                        println("Invalid command")
                    } else {
                        val result = handler.action(parameter)
                        if (result.isSuccess) {
                            session = result.getOrThrow()
                            handler.display(session)
                        } else break
                    }
                } catch (err: Exception) {
                    println(
                        when (err) {
                            is IllegalMoveException -> "Illegal move \"${err.move}\". ${err.message}"
                            is CommandException -> "ERROR: ${err.message}"
                            else -> throw err
                        }
                    )
                }
            }
        } catch (err: GameStorageAccessException) {
            println(
                "An unknown error occurred while trying to reach the database. " +
                    if (dbInfo.mode == DbMode.REMOTE) "Check your network connection."
                    else "Is your local database started?"
            )
        } finally {
            println("BYE.")
        }
    }
}
