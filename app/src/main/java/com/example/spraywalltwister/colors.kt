package com.example.spraywalltwister

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import kotlin.random.Random
import kotlin.random.Random.Default.nextInt

class BoulderColors(var _colorName: String, var _colorCode: String, var _fontColor: String, val _weight: Double, var _currentWeight: Double) {
    var colorName = _colorName
    var colorCode = Color.parseColor(_colorCode)
    var fontColor = Color.parseColor(_fontColor)
    var weight = _weight
    var currentWeight = _currentWeight



}