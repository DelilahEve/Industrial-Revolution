package me.steven.indrev.api.machines

enum class TransferMode(val rgb: Long, val input: Boolean, val output: Boolean) {
    INPUT(0x997e75ff, true, false),
    INPUT_FIRST(0x9975ff8e, true, false),
    INPUT_SECOND(0x9975ff8e, true, false),
    OUTPUT_FIRST(0x9975ff8e, false, true),
    OUTPUT_SECOND(0x9975ff8e, false, true),
    OUTPUT(0x99ffb175, false, true),
    INPUT_OUTPUT(0x99d875ff, true, true),
    NONE(-1, false, false);

    fun next(): TransferMode = when (this) {
        INPUT -> INPUT_FIRST
        INPUT_FIRST -> INPUT_SECOND
        INPUT_SECOND -> OUTPUT_FIRST
        OUTPUT_FIRST -> OUTPUT_SECOND
        OUTPUT_SECOND -> OUTPUT
        OUTPUT -> INPUT_OUTPUT
        INPUT_OUTPUT -> NONE
        NONE -> INPUT
    }

    fun next(available: Array<out TransferMode>): TransferMode {
        var current = this
        for (i in values().indices) {
            val possible = current.next()
            if (available.contains(possible)) {
                return possible
            }
            current = possible
        }
        return this
    }

    companion object {
        val DEFAULT = arrayOf(INPUT, OUTPUT, INPUT_OUTPUT, NONE)
        val SOLID_INFUSER = arrayOf(INPUT, OUTPUT, INPUT_OUTPUT, INPUT_FIRST, INPUT_SECOND, NONE)
        val ELECTROLYTIC_SEPARATOR = arrayOf(TransferMode.INPUT, TransferMode.OUTPUT_FIRST, TransferMode.OUTPUT_SECOND, TransferMode.NONE)
    }

}