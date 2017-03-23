package `bitmap_bresenhams_line_algorithm`

// version 1.1.1

import java.awt.*
import javax.swing.*

class Bresenham(w: Int, h: Int) : JPanel() {
    private val centerX = w / 2
    private val centerY = h / 2

    init {
        preferredSize = Dimension(w, h)
        background = Color.blue
    }

    override protected fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        drawLine(g, 0, 0, 8, 19)   // NNE
        drawLine(g, 0, 0, 19, 8)   // ENE
        drawLine(g, 0, 0, 19, -8)  // ESE
        drawLine(g, 0, 0, 8, -19)  // SSE
        drawLine(g, 0, 0, -8, -19) // SSW
        drawLine(g, 0, 0, -19, -8) // WSW
        drawLine(g, 0, 0, -19, 8)  // WNW
        drawLine(g, 0, 0, -8, 19)  // NNW
    }

    private fun plot(g: Graphics, x: Int, y: Int) {
        g.color = Color.white
        g.drawOval(centerX + x * 10, centerY -y * 10, 10, 10)
    }

    private fun drawLine(g: Graphics, x1: Int, y1: Int, x2: Int, y2: Int) {
        var d = 0
        val dy = Math.abs(y2 - y1)
        val dx = Math.abs(x2 - x1)
        val dy2 = dy shl 1
        val dx2 = dx shl 1
        val ix = if (x1 < x2)  1 else -1
        val iy = if (y1 < y2)  1 else -1
        var xx = x1
        var yy = y1

        if (dy <= dx) {
            while (true) {
                plot(g, xx, yy)
                if (xx == x2) break
                xx += ix
                d  += dy2
                if (d > dx) {
                    yy += iy
                    d  -= dx2
                }
            }
        }
        else {
            while (true) {
                plot(g, xx, yy)
                if (yy == y2) break
                yy += iy
                d  += dx2
                if (d > dy) {
                    xx += ix
                    d  -= dy2
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    SwingUtilities.invokeLater {
        val f = JFrame()
        f.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        f.isVisible = true
        f.add(Bresenham(600, 500), BorderLayout.CENTER)
        f.title = "Bresenham"
        f.isResizable = false
        f.pack()
        f.setLocationRelativeTo(null)
    }
}