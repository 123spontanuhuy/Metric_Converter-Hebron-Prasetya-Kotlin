package com.example.metric_converter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    // Deklarasi variabel XML ke dalam kode Kotlin
    private lateinit var spMetrics: Spinner
    private lateinit var spOriginalUnit: Spinner
    private lateinit var spTargetUnit: Spinner
    private lateinit var inputValue: EditText
    private lateinit var resultText: TextView

    // Fungsi untuk menghitung hasil konversi
    private fun calculateResult(input: Double, metricIndex: Int, originalIndex: Int, targetIndex: Int) {
        var result = input
        var indexDifference: Int

        if (metricIndex in 1..4 || metricIndex in 6..7) { // Untuk panjang, massa, waktu, arus listrik, intensitas cahaya, jumlah zat
            indexDifference = originalIndex - targetIndex
            if (indexDifference > 0) {
                for (x in 1..indexDifference)
                    result *= 10
            } else if (indexDifference < 0) {
                indexDifference = abs(indexDifference)
                for (x in 1..indexDifference)
                    result /= 10
            }
            resultText.text = "$result"
        } else if (metricIndex == 5) { // Untuk suhu
            result = when {
                originalIndex == 0 && targetIndex == 1 -> (input * 9 / 5) + 32 // Celcius -> Fahrenheit
                originalIndex == 0 && targetIndex == 2 -> input + 273.15 // Celcius -> Kelvin
                originalIndex == 1 && targetIndex == 0 -> (input - 32) * 5 / 9 // Fahrenheit -> Celcius
                originalIndex == 1 && targetIndex == 2 -> ((input - 32) * 5 / 9) + 273.15 // Fahrenheit -> Kelvin
                originalIndex == 2 && targetIndex == 0 -> input - 273.15 // Kelvin -> Celcius
                originalIndex == 2 && targetIndex == 1 -> ((input - 273.15) * 9 / 5) + 32 // Kelvin -> Fahrenheit
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
        val lengthUnits = listOf("Milimeter", "Centimeter", "Desimeter", "Meter", "Dekameter", "Hektometer", "Kilometer")
        val massUnits = listOf("Miligram", "Centigram", "Desigram", "Gram", "Dekagram", "Hektogram", "Kilogram")
        val timeUnits = listOf("Milisekon", "Centisekon", "Desisekon", "Sekon", "Dekasekon", "Hektosekon", "Kilosekon")
        val electricCurrentUnits = listOf("Miliampere", "Centiampere", "Desiampere", "Ampere", "Dekaampere", "Hektoampere", "Kiloampere")
        val temperatureUnits = listOf("Celcius", "Fahrenheit", "Kelvin")
        val intensitasCahaya = listOf("Milicandela", "Candela", "Kilocandela") // Satuan intensitas cahaya
        val jumlahZat = listOf("Millimol", "Mol", "Kilomol") // Satuan jumlah zat

        // Mengatur adapter untuk spinner satuan
        val adapterLength = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, lengthUnits)
        val adapterMass = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, massUnits)
        val adapterTime = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, timeUnits)
        val adapterElectricCurrent = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, electricCurrentUnits)
        val adapterTemperature = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, temperatureUnits)
        val adapterIntensitasCahaya = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, intensitasCahaya)
        val adapterJumlahZat = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, jumlahZat)

        var metricIndex = 0
        var originalIndex = 0
        var targetIndex = 0

        var inputNumber = 0.0

        // Menghubungkan spinner ke variabel Kotlin
        spOriginalUnit = findViewById(R.id.spOriginal)
        spOriginalUnit.isEnabled = false

        spTargetUnit = findViewById(R.id.spConvert)
        spTargetUnit.isEnabled = false

        spMetrics = findViewById(R.id.spMetrics)

        // Mengatur pilihan metrik
        spMetrics.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                spOriginalUnit.isEnabled = position != 0
                spTargetUnit.isEnabled = position != 0

                when (position) {
                    1 -> { // Panjang
                        spOriginalUnit.adapter = adapterLength
                        spTargetUnit.adapter = adapterLength
                    }
                    2 -> { // Massa
                        spOriginalUnit.adapter = adapterMass
                        spTargetUnit.adapter = adapterMass
                    }
                    3 -> { // Waktu
                        spOriginalUnit.adapter = adapterTime
                        spTargetUnit.adapter = adapterTime
                    }
                    4 -> { // Arus listrik
                        spOriginalUnit.adapter = adapterElectricCurrent
                        spTargetUnit.adapter = adapterElectricCurrent
                    }
                    5 -> { // Suhu
                        spOriginalUnit.adapter = adapterTemperature
                        spTargetUnit.adapter = adapterTemperature
                    }
                    6 -> { // Intensitas Cahaya
                        spOriginalUnit.adapter = adapterIntensitasCahaya
                        spTargetUnit.adapter = adapterIntensitasCahaya
                    }
                    7 -> { // Jumlah Zat
                        spOriginalUnit.adapter = adapterJumlahZat
                        spTargetUnit.adapter = adapterJumlahZat
                    }
                }
                metricIndex = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Tidak ada yang dipilih
            }
        }

        // Ketika satuan asal dipilih
        spOriginalUnit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                originalIndex = position
                if (inputNumber != 0.0 && targetIndex != 0) {
                    calculateResult(inputNumber, metricIndex, originalIndex, targetIndex)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Tidak ada yang dipilih
            }
        }

        // Ketika satuan tujuan dipilih
        spTargetUnit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                targetIndex = position
                if (inputNumber != 0.0 && originalIndex != 0) {
                    calculateResult(inputNumber, metricIndex, originalIndex, targetIndex)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Tidak ada yang dipilih
            }
        }

        // Mendengarkan perubahan teks pada input nilai
        inputValue.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputString: String = s?.toString() ?: ""
                inputNumber = if (inputString.isNotEmpty()) {
                    try {
                        inputString.toDouble()
                    } catch (e: NumberFormatException) {
                        0.0
                    }
                } else {
                    0.0
                }

                if (inputNumber != 0.0) {
                    calculateResult(inputNumber, metricIndex, originalIndex, targetIndex)
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
