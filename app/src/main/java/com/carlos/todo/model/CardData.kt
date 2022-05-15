package com.carlos.todo.model

import java.util.*

data class CardData (
    var id: Int,
    var title: String,
    var description: String,
    var date: String? = null
)