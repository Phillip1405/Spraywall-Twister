package com.example.spraywalltwister

import android.graphics.Color


data class BoulderColors(var colorName: String, var colorCode: Int, val fontColor: Int, val weight: Double, var currentWeight: Double){

    constructor (_colorName: String, _colorCode: String, _fontColor: String, _weight: Double, _currentWeight: Double) : this(_colorName, Color.parseColor(_colorCode), Color.parseColor(_fontColor), _weight, _currentWeight) {}

}

