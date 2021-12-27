package ui.compose

import WINDOW_PADDING
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.*
import domain.game.Game


// Constants
val MOVES_WIDTH = 260.dp
val MOVES_HEIGHT = BOARD_HEIGHT
val FONT_FAMILY = FontFamily.Monospace
val FONT_SIZE = 20.sp


/**
 * Composable used to display a column with the moves already made in a chess game.
 * @param game chess game
 */
@Composable
fun MovesView(game: Game) {
    Column(
        modifier = Modifier.padding(start = WINDOW_PADDING)
            .width(MOVES_WIDTH)
            .height(MOVES_HEIGHT)
            .background(Color(WHITE))
    ) {
        game.moves.forEachIndexed { idx, move ->
            if (idx % 2 == 0) {
                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    Text(
                        text = "${idx / 2}. $move",
                        fontFamily = FONT_FAMILY,
                        fontSize = FONT_SIZE
                    )

                    if (idx != game.moves.size - 1)
                        Text(
                            text = "${game.moves[idx + 1]}",
                            modifier = Modifier.padding(start = WINDOW_PADDING),
                            fontFamily = FONT_FAMILY,
                            fontSize = FONT_SIZE
                        )
                }
            }
        }
    }
}