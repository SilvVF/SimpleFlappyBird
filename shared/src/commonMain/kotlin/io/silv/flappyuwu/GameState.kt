package io.silv.flappyuwu

import kotlin.native.concurrent.ThreadLocal


@ThreadLocal
object GameState {

    var tick: Int = 0

    var score: Int = 0

    var gameOver: Boolean = false

    var bird: Bird? = null

    val gameObjects = mutableListOf<GameObject>()

    fun reset() {
        tick = 0
        score = 0
        gameOver = false
        bird = null
        gameObjects.clear()
    }
}

fun GameState.toGameData(gameController: GameController): GameData {
    return GameData(
        tick = tick,
        pipes = gameObjects.filterIsInstance<Pipe>(),
        bird = bird ?: Bird(gameController),
        score = score
    )
}

data class GameData(
    val tick: Int = 0,
    val pipes: List<Pipe> = emptyList(),
    val bird: Bird? = GameState.bird,
    val score: Int = 0
)