package com.example.paymeback.ui.screens

import android.annotation.SuppressLint
import java.text.DecimalFormat
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
val dateFormatter = SimpleDateFormat("dd/MM/yyyy")

@SuppressLint("SimpleDateFormat")
val timestampFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

val decimalFormat = DecimalFormat("0.00")