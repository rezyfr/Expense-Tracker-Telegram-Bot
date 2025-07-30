package com.expensetracker.util

object MonthIds {
    const val JAN = "23beb6ee83a6817b9a31db441abd8aff"
    const val FEB = "23beb6ee83a6818b882ecc705a06a1ab"
    const val MAR = "23beb6ee83a6816d8c58eb0fb777ed49"
    const val APR = "23beb6ee83a68103989bc9ce39a1af04"
    const val MAY = "23beb6ee83a68139a093ededa06912a2"
    const val JUN = "23beb6ee83a68111bedce0df325afa09"
    const val JUL = "23beb6ee83a681c38330c60dd52c255f"
    const val AUG = "23beb6ee83a681319b54c5749e08519f"
    const val SEP = "23beb6ee83a681528a86ceb56ca741cd"
    const val OCT = "23beb6ee83a681efb546c23084b411e0"
    const val NOV = "23beb6ee83a6818fad16f9a8d9dcdb91"
    const val DEC = "23beb6ee83a68111ad13e9e86471f912"

    fun fromMonth(month: Int): String = when (month) {
        1 -> JAN
        2 -> FEB
        3 -> MAR
        4 -> APR
        5 -> MAY
        6 -> JUN
        7 -> JUL
        8 -> AUG
        9 -> SEP
        10 -> OCT
        11 -> NOV
        12 -> DEC
        else -> throw IllegalArgumentException("Invalid month: $month")
    }
} 