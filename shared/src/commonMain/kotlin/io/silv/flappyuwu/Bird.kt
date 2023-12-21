package io.silv.flappyuwu

class Bird(
    private val gameController: GameController
): GameObject {

    var x: Float = 100f
    var y: Float = gameController.screenHeight / 2f

    var velocity: Float = 0f
    var speed: Float = 1f

    private val jumpHeight = -15
    private val gravity = 5

    fun applyVelocity() {
        velocity = 2f
    }

    override fun onTick(interp: Float) {
        val gravityApplied = (y + (gravity * interp))
        val jumpVelocityApplied = (gravityApplied + (jumpHeight * velocity * interp))

        y = (jumpVelocityApplied).coerceAtLeast(0f)

        velocity -= (.1f * interp).coerceAtLeast(0f)
    }

    override fun shouldDestroy(): Boolean {
        return false
    }
}