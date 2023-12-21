package io.silv.flappyuwu


class Pipe(
    initX: Float,
    val height: Float,
    private val gameController: GameController,
    private var speed: Float = 1f,
): GameObject {

    val width = 300f

    val spacing by lazy {
        gameController.screenHeight / 3.5f
    }

    var x: Float = initX
        private set


    override val type: Int = PIPE_TYPE_ID

    private val onTickOffset = 10

    override fun onTick(interp: Float) {
        x -= (speed * onTickOffset * interp)
    }

    override fun shouldDestroy(): Boolean = x < 0 - width


    companion object {
        const val PIPE_TYPE_ID = 1
    }
}

