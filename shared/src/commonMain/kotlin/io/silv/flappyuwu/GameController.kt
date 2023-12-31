package io.silv.flappyuwu

import TickerMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ticker
import kotlin.random.Random

class GameController(
    private val applicationScope: ApplicationScope = ApplicationScope(),
    val screenWidth: Int,
    val screenHeight: Int,
) {

    enum class PlayState {
        Paused, Playing, Idle, End
    }

    val playState = MutableStateFlow<PlayState>(PlayState.Idle)

    private val mutableGameData = MutableStateFlow(
        GameState.toGameData(this)
    )
    val gameData = mutableGameData.asStateFlow()

    private val inputsMutex = Mutex()
    private val inputs = ArrayDeque<Input>()

    init {
       // mutableGameData.onEach { Logger.d { it.toString() } }.launchIn(applicationScope)

        applicationScope.launch {
            playState.collectLatest { state ->
                when (state) {
                        PlayState.End -> {
                            playState.emit(PlayState.Idle)
                            GameState.reset()
                            mutableGameData.value = GameData()
                        }
                        PlayState.Playing -> {

                            val interpolation = 60f / TICK_RATE

                            val tickProducer = ticker(
                                delayMillis = (1000L / TICK_RATE),
                                initialDelayMillis = 1000,
                                mode = TickerMode.FIXED_PERIOD
                            )


                            tickProducer.receiveAsFlow()
                                .catch {
                                    playState.emit(PlayState.End)
                                }
                                .collect { tick ->
                                    performTick(
                                        interpolation = interpolation
                                    )
                                        .also { GameState.tick++ }

                                    mutableGameData.emit(
                                        GameState.toGameData(this@GameController)
                                    )

                                    if (GameState.gameOver) {
                                        tickProducer.cancel()
                                        playState.emit(PlayState.End)
                                    }
                                }

                        }
                        else -> Unit
                    }
                }
        }
    }

    private fun performTick(
        interpolation: Float,
    ) = runBlocking {


        val pipes = GameState.gameObjects.filterIsInstance<Pipe>()
        val bird = GameState.bird ?: run { Bird(this@GameController).also { GameState.bird = it } }

        inputsMutex.withLock {
            while (inputs.isNotEmpty()) {
                when (inputs.removeLast()) {
                    Input.Tap -> {
                        bird.applyVelocity()
                    }
                    else -> Unit
                }
            }
        }

        bird.onTick(interpolation)

        if (pipes.all { it.x < screenWidth - it.width - it.spacing }) {
            GameState.gameObjects.add(createPipe())
        }

        val needToRemove = mutableListOf<GameObject>()

        for (obj in GameState.gameObjects) {
            obj.onTick(interpolation)

            if (obj.shouldDestroy()) {
                needToRemove.add(obj)
            }
        }

        GameState.gameObjects.removeAll(
            needToRemove.onEach {
                if(it is Pipe) GameState.score++
            }
        )

        pipes.firstOrNull()?.takeIf { it.x <= bird.x + 100f && it.x + it.width >= bird.x }?.let {
            val topRange = (0f..(screenHeight - it.height - it.spacing))
            val bottomRange = (screenHeight - it.height)..((screenHeight - it.height) + it.height)
            if (bird.y in topRange || bird.y in bottomRange || bird.y + 100f in topRange || bird.y + 100f in bottomRange)
                GameState.gameOver = true
        }

        if (bird.y >= screenHeight.toFloat()) {
            GameState.gameOver = true
        }
    }

    private fun createPipe() = Pipe(
        initX = screenWidth.toFloat(),
        height = Random.nextDouble(screenHeight * 0.1, screenHeight * 0.7).toFloat(),
        this
    )

    fun onTapInput() = applicationScope.launch {
        while (inputsMutex.isLocked)
            continue
        inputs.addLast(Input.Tap)
    }
}