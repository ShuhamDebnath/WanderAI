package com.shuham.wanderai

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform