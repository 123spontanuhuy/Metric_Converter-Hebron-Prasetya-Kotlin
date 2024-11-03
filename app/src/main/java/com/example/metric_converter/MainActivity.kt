package com.example.metric_converter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    // Deklarasi variabel XML ke dalam kode Kotlin
    private lateinit var spMetrics: Spinner
    private lateinit var spOriginal: Spinner
    private lateinit var spConvert: Spinner
    private lateinit var inputValue: EditText
    private lateinit var resultText: TextView

    // Fungsi untuk menghitung hasil konversi
    private fun calculateResult(input: Double, index: Int, index2: Int, index3: Int) {
        var result = input
        var index4: Int

        if (index in 1..4) { // Untuk panjang, massa, waktu, arus listrik
            index4 = index2 - index3
            if (index4 > 0) {
                for (x in 1..index4)
                    result *= 10
            } else if (index4 < 0) {
                index4 = abs(index4)
                for (x in 1..index4)
                    result /= 10
            }
            resultText.text = "$result"
        } else if (index == 5) { // Untuk suhu
            result = when {
                index2 == 0 && index3 == 1 -> (input * 9 / 5) + 32 // Celcius -> Fahrenheit
                index2 == 0 && index3 == 2 -> input + 273.15 // Celcius -> Kelvin
                index2 == 1 && index3 == 0 -> (input - 32) * 5 / 9 // Fahrenheit -> Celcius
                index2 == 1 && index3 == 2 -> ((input - 32) * 5 / 9) + 273.15 // Fahrenheit -> Kelvin
                index2 == 2 && index3 == 0 -> input - 273.15 // Kelvin -> Celcius
                index2 == 2 && index3 == 1 -> ((input - 273.15) * 9 / 5) + 32 // Kelvin -> Fahrenheit
                else -> input
            }
            resultText.text = "$result"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Menghubungkan komponen XML dengan variabel Kotlin
        inputValue = findViewById(R.id.getInputValue)
        resultText = findViewById(R.id.resultText)

        // Daftar satuan untuk setiap metrik
        val lengths = listOf("Milimeter", "Centimeter", "Desimeter", "Meter", "Dekameter", "Hektometer", "Kilometer")
        val mass = listOf("Miligram", "Centigram", "Desigram", "Gram", "Dekagram", "Hektogram", "Kilogram")
        val time = listOf("Milisekon", "Centisekon", "Desisekon", "Sekon", "Dekasekon", "Hektosekon", "Kilosekon")
        val electricCurrent = listOf("Miliampere", "Centiampere", "Desiampere", "Ampere", "Dekaampere", "Hektoampere", "Kiloampere")
        val temperature = listOf("Celcius", "Fahrenheit", "Kelvin")

        // Mengatur adapter untuk spinner satuan
        val adapterLengths = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, lengths)
        val adapterMass = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, mass)
        val adapterTime = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, time)
        val adapterElectricCurrent = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, electricCurrent)
        val adapterTemperature = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, temperature)

        var indexMetric = 0
        var indexMetric2 = 0
        var indexMetric3 = 0

        var getInputNum = 0.0

        // Menghubungkan spinner ke variabel Kotlin
        spOriginal = findViewById(R.id.spOriginal)
        spOriginal.isEnabled = false

        spConvert = findViewById(R.id.spConvert)
        spConvert.isEnabled = false

        spMetrics = findViewById(R.id.spMetrics)

        // Mengatur pilihan metrik
        spMetrics.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                spOriginal.isEnabled = position != 0
                spConvert.isEnabled = position != 0

                when (position) {
                    1 -> { // Panjang
                        spOriginal.adapter = adapterLengths
                        spConvert.adapter = adapterLengths
                    }
                    2 -> { // Massa
                        spOriginal.adapter = adapterMass
                        spConvert.adapter = adapterMass
                    }
                    3 -> { // Waktu
                        spOriginal.adapter = adapterTime
                        spConvert.adapter = adapterTime
                    }
                    4 -> { // Arus listrik
                        spOriginal.adapter = adapterElectricCurrent
                        spConvert.adapter = adapterElectricCurrent
                    }
                    5 -> { // Suhu
                        spOriginal.adapter = adapterTemperature
                        spConvert.adapter = adapterTemperature
                    }
                }
                indexMetric = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Tidak ada yang dipilih
            }
        }

        // Ketika satuan asal dipilih
        spOriginal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                indexMetric2 = position
                if (getInputNum != 0.0 && indexMetric3 != 0) {
                    calculateResult(getInputNum, indexMetric, indexMetric2, indexMetric3)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Tidak ada yang dipilih
            }
        }

        // Ketika satuan tujuan dipilih
        spConvert.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                indexMetric3 = position
                if (getInputNum != 0.0 && indexMetric2 != 0) {
                    calculateResult(getInputNum, indexMetric, indexMetric2, indexMetric3)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Tidak ada yang dipilih
            }
        }

        // Mendengarkan perubahan teks pada input nilai
        inputValue.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val getString: String = s?.toString() ?: ""
                getInputNum = if (getString.isNotEmpty()) {
                    try {
                        getString.toDouble()
                    } catch (e: NumberFormatException) {
                        0.0
                    }
                } else {
                    0.0
                }

                if (getInputNum != 0.0) {
                    calculateResult(getInputNum, indexMetric, indexMetric2, indexMetric3)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Sebelum teks berubah
            }

            override fun afterTextChanged(s: Editable?) {
                // Setelah teks berubah
            }
        })
    }
}
