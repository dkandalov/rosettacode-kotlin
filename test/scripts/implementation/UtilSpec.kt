package scripts.implementation

import io.kotlintest.specs.StringSpec

class UtilSpec : StringSpec() {
    init {
        "retry throws exception when exceeded amount of retries" {
            var attempts = 0
            val failingFunction = {
                attempts += 1
                throw MyException()
            }

            shouldThrow<IllegalStateException> {
                retry(Exception::class, retries = 3, f = failingFunction)
            }

            attempts shouldEqual 3
        }
    }

    private class MyException: Exception()
}