package `sort_an_array_of_composite_structures`

// version 1.0.6

data class Employee(val name: String, var category: String) : Comparable<Employee> {
    override fun compareTo(other: Employee) = this.name.compareTo(other.name)
}

fun main(args: Array<String>) {
    val employees = arrayOf(
        Employee("David", "Manager"),
        Employee("Alice", "Sales"),
        Employee("Joanna", "Director"),
        Employee("Henry", "Admin"),
        Employee("Tim", "Sales"),
        Employee("Juan", "Admin")
    )
    employees.sort()
    for (employee in employees) println("${employee.name.padEnd(6)} : ${employee.category}")
}