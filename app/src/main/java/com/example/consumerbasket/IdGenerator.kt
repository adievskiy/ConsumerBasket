package com.example.consumerbasket

class IdGenerator(private val dbHelper: DBHelper) {
    private val existingIds: MutableSet<String> = mutableSetOf()

    fun addId(): String {
        val newId = generateNewId()
        existingIds.add(newId)
        return newId
    }

    private fun generateNewId(): String {
        var nextId = 1
        while (dbHelper.isIdExists(nextId)) {
            nextId++
        }
        return nextId.toString()
    }
}
