package com.example.parkify.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun java.util.Date.toDisplayString(): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(this)

fun Double.toCurrency(): String =
    NumberFormat.getCurrencyInstance(Locale("es", "CO")).format(this)

fun String.isValidPlaca(): Boolean =
    this.uppercase().matches(Regex("^[A-Z]{3}[0-9]{2,3}[A-Z0-9]?\$"))