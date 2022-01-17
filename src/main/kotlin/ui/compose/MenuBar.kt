package ui.compose

import androidx.compose.runtime.*
import androidx.compose.ui.window.*
import domain.*


/**
 * Window Menu Bar.
 * @param session app session
 * @param appOptions app options
 * @param onRefreshGameRequest callback to be executed when occurs a refresh game request
 * @param onCloseGameRequest callback to be executed when occurs a close game request
 * @param onShowTargetsChange callback to be executed when occurs a change in the show-targets checkbox
 * @param onSinglePlayerChange callback to be executed when occurs a change in the single-player checkbox
 */
@Composable
fun FrameWindowScope.MenuBar(
    state: SessionState,
    appOptions: AppOptions,
    onRefreshGameRequest: () -> Unit,
    onCloseGameRequest: () -> Unit,
    onShowTargetsChange: (Boolean) -> Unit,
    onSinglePlayerChange: (Boolean) -> Unit
) {
    MenuBar {
        Menu("File") {
            Item(
                text = "Refresh game",
                onClick = onRefreshGameRequest,
                enabled = state.isWaiting()
            )
            Item(
                text = "Close game",
                onClick = onCloseGameRequest,
                enabled = state.isNotLogging()
            )
        }
        Menu("Options") {
            CheckboxItem(
                text = "Show targets",
                checked = appOptions.targetsOn.value,
                onCheckedChange = onShowTargetsChange
            )

            CheckboxItem(
                text = "Single player",
                checked = appOptions.singlePlayer.value,
                onCheckedChange = onSinglePlayerChange,
                enabled = state.isLogging()
            )
        }
    }
}
