package se.mrbob.flowplayground

val threadName: String
    get() {
        val name = Thread.currentThread().name
        return when(val rangeStart = name.indexOf("-worker")) {
            -1 -> name
            else -> name.removeRange(rangeStart..rangeStart+6)
        }
    }