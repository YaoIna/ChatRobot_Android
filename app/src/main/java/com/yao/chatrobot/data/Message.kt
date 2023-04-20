package com.yao.chatrobot.data

data class Message(val message: String, val role: Role)

enum class Role {
    User, Robot
}