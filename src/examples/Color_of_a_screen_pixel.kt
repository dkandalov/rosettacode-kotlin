package `color_of_a_screen_pixel`

import java.awt.*

fun getMouseColor(): Color {
    val location = MouseInfo.getPointerInfo().location
    return getColorAt(location.x, location.y)
}

fun getColorAt(x: Int, y: Int): Color {
    return Robot().getPixelColor(x, y)
}