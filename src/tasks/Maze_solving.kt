package `maze_solving`

// Version 1.2.31

import java.io.File

typealias Maze = List<CharArray>

/**
    * Makes the maze half as wide (i. e. "+---+" becomes "+-+"), so that
    * each cell in the maze is the same size horizontally as vertically.
    * (Versus the expanded version, which looks better visually.)
    * Also, converts each line of the maze from a String to a
    * char[], because we'll want mutability when drawing the solution later.
    */
fun decimateHorizontally(lines: List<String>): Maze {
    val width = (lines[0].length + 1) / 2
    val c = List(lines.size) { CharArray(width) }
    for (i in 0 until lines.size) {
        for (j in 0 until width) c[i][j] = lines[i][j * 2]
    }
    return c
}

/**
    * Given the maze, the x and y coordinates (which must be odd),
    * and the direction we came from, return true if the maze is
    * solvable, and draw the solution if so.
    */
fun solveMazeRecursively(maze: Maze, x: Int, y: Int, d: Int): Boolean {
    var ok = false
    var i = 0
    while (i < 4 && !ok) {
        if (i != d) {
            // 0 = up, 1 = right, 2 = down, 3 = left
            when(i) {
                0 -> if (maze[y - 1][x] == ' ') ok = solveMazeRecursively (maze, x, y - 2, 2)
                1 -> if (maze[y][x + 1] == ' ') ok = solveMazeRecursively (maze, x + 2, y, 3)
                2 -> if (maze[y + 1][x] == ' ') ok = solveMazeRecursively (maze, x, y + 2, 0)
                3 -> if (maze[y][x - 1] == ' ') ok = solveMazeRecursively (maze, x - 2, y, 1)
             else -> {}
            }
        }
        i++
    }

    // check for end condition
    if (x == 1 && y == 1) ok = true

    // once we have found a solution, draw it as we unwind the recursion
    if (ok) {
        maze[y][x] = '*'
        when (d) {
            0 -> maze[y - 1][x] = '*'
            1 -> maze[y][x + 1] = '*'
            2 -> maze[y + 1][x] = '*'
            3 -> maze[y][x - 1] = '*'
         else -> {}
        }
    }
    return ok
}

/**
    * Solve the maze and draw the solution. For simplicity,
    * assumes the starting point is the lower right, and the
    * ending point is the upper left.
    */
fun solveMaze(maze: Maze) =
    solveMazeRecursively(maze, maze[0].size - 2, maze.size - 2, -1)

/**
    * Opposite of decimateHorizontally(). Adds extra characters to make
    * the maze "look right", and converts each line from char[] to
    * String at the same time.
    */
fun expandHorizontally(maze: Maze): Array<String> {
    val tmp = CharArray(3)
    val lines = Array<String>(maze.size) { "" }
    for (i in 0 until maze.size) {
        val sb = StringBuilder(maze[i].size * 2)
        for (j in 0 until maze[i].size) {
            if (j % 2 == 0)
                sb.append(maze[i][j])
            else {
                for (k in 0..2) tmp[k] = maze[i][j]
                if (tmp[1] == '*') {
                    tmp[0] = ' '
                    tmp[2] = ' '
                }
                sb.append(tmp)
            }
        }
        lines[i] = sb.toString()
    }
    return lines
}

/**
    * Accepts a maze as generated by:
    * http://rosettacode.org/wiki/Maze_generation#Kotlin
    * in a file whose name is specified as a command-line argument.
    */
fun main(args: Array<String>) {
    if (args.size != 1) {
        println("The maze file to be read should be passed as a single command line argument.")
        return
    }
    val f = File(args[0])
    if (!f.exists()) {
        println("Sorry ${args[0]} does not exist.")
        return
    }
    val lines = f.readLines(Charsets.US_ASCII)
    val maze = decimateHorizontally(lines)
    solveMaze(maze)
    val solvedLines = expandHorizontally(maze)
    println(solvedLines.joinToString("\n"))
}