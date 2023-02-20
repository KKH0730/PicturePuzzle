package com.seno.game.data

interface Mapper<in M, out E> {
    fun fromRemote(model: M): E
}

interface MapperType2<in M, in N, out E> {
    fun fromRemote(param1: M, param2: N): E
}