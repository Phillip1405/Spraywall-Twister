package com.example.spraywalltwister

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.*
import kotlin.math.exp
import kotlin.math.roundToInt
import kotlin.random.Random
import com.fasterxml.jackson.module.kotlin.*
import java.lang.StringBuilder


class MainActivity : AppCompatActivity() {

    var totalColorWeight: Double = 0.0
    var boulderColors: MutableList<BoulderColors> = mutableListOf()
    var bodyParts: MutableList<String> = mutableListOf()
    val fileName = "boulderColors.json"


    var idOldCurr: Int = 0
    var idOld1: Int = 0
    var idOld2: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setColors()
        setWeights()
        val bNewChallenge: Button = findViewById(R.id.b_new_challenge)
        bNewChallenge.setOnClickListener{
            getNewChallenge()
            saveColorsToConfig()
        }

        startNewRound()



    }

    private fun getNewChallenge(){
        setNextColor()
    }

    private fun startNewRound(){
        setNextColor()
        setNextColor()
        setNextColor()
        setNextColor()
    }

    private fun setNextColor(){
        val id = getNextColorId()
        val tCurrChallenge: TextView = findViewById(R.id.t_curr_challenge)
        val tOld1: TextView = findViewById(R.id.t_prev_challenge_one)
        val tOld2: TextView = findViewById(R.id.t_prev_challenge_two)
        val tOld3: TextView = findViewById(R.id.t_prev_challenge_three)

        // Set Colors
        setColorForTextView(tCurrChallenge, id)
        setColorForTextView(tOld1, idOldCurr)
        setColorForTextView(tOld2,idOld1)
        setColorForTextView(tOld3, idOld2)

        // Set Text
        setTextForTextView(tCurrChallenge, id)
        setTextForTextView(tOld1, idOldCurr)
        setTextForTextView(tOld2,idOld1)
        setTextForTextView(tOld3, idOld2)

        tCurrChallenge.text = boulderColors[id].colorName
        tOld1.text = boulderColors[idOldCurr].colorName
        tOld2.text = boulderColors[idOld1].colorName
        tOld3.text = boulderColors[idOld2].colorName



        idOld2 = idOld1
        idOld1 = idOldCurr
        idOldCurr = id
    }

    private fun setTextForTextView(textView: TextView, boulderColorsId: Int){
        textView.text = boulderColors[boulderColorsId].colorName
        textView.setTextColor(boulderColors[boulderColorsId].fontColor)
    }

    private fun setColorForTextView(textView: TextView, boulderColorsId: Int){

        val gd = GradientDrawable()
        gd.setColor(boulderColors[boulderColorsId].colorCode)
        gd.cornerRadius = 5.0f
        gd.setStroke(5, Color.parseColor("#ffffff"))
        textView.background = gd
    }


    private fun getNextColorId(): Int{
        var rnd: Double = Random.nextInt(1,totalColorWeight.roundToInt()).toDouble()
        for (i in 0 until boulderColors.size) {
            rnd -= boulderColors[i].currentWeight
            if (rnd <= 0) {
                return i
            }
        }
        return boulderColors.size - 1
    }

    private fun setColors(){
        if(!loadColorsFromConfig()){
            setDefaultBoulderColors()
        }

    }



    private fun loadColorsFromConfig(): Boolean{
        try {
            val inputStream = openFileInput (fileName)
            if ( inputStream != null ) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var receiveString: String?
                val stringBuilder = StringBuilder()

                while (bufferedReader.readLine().also { receiveString = it } != null) {
                    stringBuilder.append(receiveString)
                }

                val mapper = jacksonObjectMapper()
                boulderColors = mapper.readValue(stringBuilder.toString())

                inputStream.close()
            }
        } catch (e: IOException){
            println("cant read file")
            return false
        } catch (e: FileNotFoundException){
            println("file not found")
            return false
        }
        return true
    }

    private fun saveColorsToConfig(){


        try {
            val fou = openFileOutput(fileName, MODE_PRIVATE)
            val outputStreamWriter = OutputStreamWriter(fou)

            val mapper = jacksonObjectMapper()
            val jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(boulderColors)
            println(jsonString)
            mapper.writerWithDefaultPrettyPrinter().writeValue(outputStreamWriter, boulderColors)

            outputStreamWriter.close()
        } catch (e: IOException) {
            println("File write failed: $e")
        }


    }



    private fun setDefaultBoulderColors(){
        boulderColors.add(BoulderColors("Schwarz", "#000000", "#ffffff", 90.0,0.0))
        boulderColors.add(BoulderColors("Grün", "#2bc42b", "#000000", 80.0,0.0))
        boulderColors.add(BoulderColors("Gelb", "#ebe834", "#000000", 70.0,0.0))
        boulderColors.add(BoulderColors("Blau", "#2b54c4", "#ffffff", 60.0,0.0))
        boulderColors.add(BoulderColors("Rot", "#c4332b", "#000000", 50.0,0.0))
        boulderColors.add(BoulderColors("Weiß", "#ffffff", "#000000", 40.0,0.0))
        boulderColors.add(BoulderColors("Lila", "#a816ab", "#000000", 30.0,0.0))
    }

    fun setBodyParts(){
        val sBody: Spinner = findViewById(R.id.s_body)
        val tCurrChallenge: TextView = findViewById(R.id.t_curr_challenge)

        tCurrChallenge.text = sBody.selectedItem.toString()
        when(sBody.selectedItem.toString()){
            "hands" -> {
                bodyParts =  mutableListOf("Left Hand", "Right Hand")
            }
            "hands_and_feet" ->{
                bodyParts =  mutableListOf("Left Hand", "Right Hand", "Left Foot", "Right Foot");
            }
            "color_only" ->{
                bodyParts =  mutableListOf();
            }
        }
    }

    private fun setWeights(){
        totalColorWeight = 0.0;
        when("todo"){

            "easy" ->{
                for (i in 0 until boulderColors.size) {
                    // Easy: 0.4*e ^ (-(0.01*(x-100))^2)
                    boulderColors[i].currentWeight = 0.4 * exp(-Math.pow(0.01*(boulderColors[i].weight-100), 2.0));
                    totalColorWeight += boulderColors[i].currentWeight;
                }
            }
           "hard" ->{
               for (i in 0 until boulderColors.size) {
                   // hard: 0.4*e ^ (-(0.02*(x-50))^2)
                   boulderColors[i].currentWeight = 0.4 * exp(-Math.pow(0.02*(boulderColors[i].weight-50), 2.0));
                    totalColorWeight += boulderColors[i].currentWeight;
                }
           }
            "fuckyou" ->{
                for (i in 0 until boulderColors.size) {
                    // fuckyou: 0.4*e ^ (-(0.01*(x))^2)
                    boulderColors[i].currentWeight =  0.4 * exp(-Math.pow(0.01*boulderColors[i].weight, 2.0))
                    totalColorWeight += boulderColors[i].currentWeight;
                }
            }
           else ->{
                for (i in 0 until boulderColors.size) {
                    boulderColors[i].currentWeight = boulderColors[i].weight;
                    totalColorWeight += boulderColors[i].currentWeight;
                }
           }
        }
    }

}