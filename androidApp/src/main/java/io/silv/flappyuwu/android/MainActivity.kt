package io.silv.flappyuwu.android

import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.silv.flappyuwu.GameController
import io.silv.flappyuwu.GameData
import io.silv.flappyuwu.TICK_RATE
import kotlin.math.roundToLong


class MainActivity : ComponentActivity() {

    private val gameController = GameController(
        screenHeight = getScreenHeight(),
        screenWidth = getScreenWidth()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val gameData by gameController.gameData.collectAsState()

                    val playState by gameController.playState.collectAsState()

                    LaunchedEffect(Unit) {
                        gameController.playState.emit(GameController.PlayState.Playing)
                    }

                    when (playState) {
                        GameController.PlayState.Paused -> {
                            Box(Modifier.fillMaxSize()) {
                                Text("paused", Modifier.align(Alignment.Center))
                            }
                        }
                        GameController.PlayState.Playing -> {
                            Game(
                                modifier = Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    gameController.onTapInput()
                                },
                                data = gameData,
                                screenWidth = getScreenWidth().toFloat(),
                                screenHeight = getScreenHeight().toFloat()
                            )
                        }
                        GameController.PlayState.Idle -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Play",
                                    Modifier
                                        .clickable {
                                            gameController.playState.tryEmit(GameController.PlayState.Playing)
                                        }
                                )
                                var value by rememberSaveable { mutableFloatStateOf(TICK_RATE.toFloat()) }
                                Text(text = "Tick rate $value")
                                Slider(
                                    valueRange = 30f..120f,
                                    value = value,
                                    onValueChange = {
                                        value = it
                                        TICK_RATE = it.roundToLong()
                                    },
                                    modifier = Modifier.padding(32.dp)
                                )
                            }
                        }
                        GameController.PlayState.End -> {
                            Box(Modifier.fillMaxSize()) {
                                Text("End", Modifier.align(Alignment.Center))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    private fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }
}

@Composable
fun Game(
    modifier: Modifier = Modifier,
    data: GameData,
    screenHeight: Float,
    screenWidth: Float
) {
    Box(Modifier.fillMaxSize()) {
        Text(
            text = "${data.score}",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(22.dp)
        )
        Canvas(
            modifier = modifier.fillMaxSize(),
        ) {

            data.bird?.let {
                drawRect(
                    color = Color.Yellow,
                    size = Size(100f, 100f),
                    topLeft = Offset(it.x, it.y)
                )
            }

            data.pipes.fastForEach { pipe ->
                drawRect(
                    color = Color(0xff047857),
                    size = Size(pipe.width, screenHeight - pipe.height - pipe.spacing),
                    topLeft = Offset(pipe.x, 0f)
                )

                drawRect(
                    color = Color(0xff047857),
                    size = Size(pipe.width, pipe.height),
                    topLeft = Offset(pipe.x, screenHeight - pipe.height)
                )
            }
        }
    }
}

