package `arithmetic_complex`

// version 1.0.5-2

class Complex(val real: Double, val imag: Double) {
    operator fun plus(other: Complex): Complex {
        return Complex(this.real + other.real, this.imag + other.imag)
    }

    operator fun times(other: Complex): Complex {
        return Complex(this.real * other.real - this.imag * other.imag,
                       this.real * other.imag + this.imag * other.real)
    }

    fun inv(): Complex {
        val denom : Double = this.real * this.real + this.imag * this.imag
        return Complex(this.real / denom, -this.imag / denom)
    }

    operator fun unaryMinus() : Complex {
        return Complex(-this.real, -this.imag)
    }    

    operator fun minus(other: Complex): Complex {
        return this + (-other)
    }

    operator fun div(other: Complex): Complex {
        return this * other.inv()
    }

    fun conj(): Complex {
        return Complex(this.real, -this.imag)
    }

    override fun toString(): String {
        if (this.imag >= 0.0) 
            return "${this.real} + ${this.imag}i" 
        else 
            return "${this.real} - ${-this.imag}i"
    }
}
   
fun main(args: Array<String>) {
    val x = Complex(1.0, 3.0)
    val y = Complex(5.0, 2.0)
    println("x     =  $x")
    println("y     =  $y")
    println("x + y =  ${x + y}")
    println("x - y =  ${x - y}")
    println("x * y =  ${x * y}")
    println("x / y =  ${x / y}")
    println("-x    =  ${-x}")
    println("1 / x =  ${x.inv()}")
    println("x*    =  ${x.conj()}")
}