package io.silv.flappyuwu

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform