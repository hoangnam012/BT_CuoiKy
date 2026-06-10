package com.example.myapplication.chatdetail

import java.lang.String.format
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs

fun isOver30Minutes(timer1: String, timer2: String): Boolean {
    if (timer1.isBlank() || timer2.isBlank()) return false
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date1 = sdf.parse(timer1)?.time ?: 0L
        val date2 = sdf.parse(timer2)?.time ?: 0L

        abs(date1 - date2) > 30 * 60 * 1000
    } catch (e: Exception) {
        false
    }
}
fun parseFomatt(timer: String): String {
    if(timer.isBlank()) return ""
    return try {
        val sdfIn = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateIn = sdfIn.parse(timer) ?: return ""

        val sdfOut = SimpleDateFormat("EEEE HH:mm", Locale("vi", "VN"))
        return sdfOut.format(dateIn)
    } catch (e: Exception) {
        ""
    }
}