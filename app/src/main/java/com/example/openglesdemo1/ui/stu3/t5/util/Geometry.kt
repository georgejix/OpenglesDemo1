/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/
package com.example.openglesdemo1.ui.stu3.t5.util

object Geometry {
    class Point(val x: Float, val y: Float, val z: Float) {
        fun translateY(distance: Float): Point {
            return Point(x, y + distance, z)
        }

        fun translate(vector: Vector): Point {
            return Point(
                x + vector.x,
                y + vector.y,
                z + vector.z
            )
        }
    }

    class Vector(val x: Float, val y: Float, val z: Float) {
        fun length(): Float {
            return Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        }

        // http://en.wikipedia.org/wiki/Cross_product        
        fun crossProduct(other: Vector): Vector {
            return Vector(
                (y * other.z) - (z * other.y),
                (z * other.x) - (x * other.z),
                (x * other.y) - (y * other.x)
            )
        }

        // http://en.wikipedia.org/wiki/Dot_product
        fun dotProduct(other: Vector): Float {
            return x * other.x
            +y * other.y
            +z * other.z
        }

        fun scale(f: Float): Vector {
            return Vector(
                x * f,
                y * f,
                z * f
            )
        }
    }
}
