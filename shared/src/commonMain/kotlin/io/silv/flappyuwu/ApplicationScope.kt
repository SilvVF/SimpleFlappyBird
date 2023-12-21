@file:OptIn(DelicateCoroutinesApi::class)

package io.silv.flappyuwu

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope

data class ApplicationScope(val scope: CoroutineScope = GlobalScope): CoroutineScope by scope