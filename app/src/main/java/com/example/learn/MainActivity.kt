package com.example.learn

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateLanguage.HINDI
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.*

class MainActivity : AppCompatActivity(){
    lateinit var mic:ImageView
    lateinit var textOutput:EditText
    lateinit var translateBtn:Button
    lateinit var translatedText:TextView
    lateinit var englishToHindi:Translator
    lateinit var pDialog:SweetAlertDialog
    //lateinit var speaker:ImageView
    //lateinit var tts:TextToSpeech
    private val REQUEST_CODE_SPEECH_INPUT = 1
    var originalText:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //initializing variables with respective ids
        mic=findViewById(R.id.micIV)
        mic.background=resources.getDrawable(R.drawable.mic_bg)
        textOutput=findViewById(R.id.TextOutput)
        translateBtn=findViewById(R.id.translateBtn)
        translatedText=findViewById(R.id.translatedtText)
//        speaker=findViewById(R.id.speaker)
//
//        speaker!!.isEnabled=false
//        tts= TextToSpeech(this,this)
//        speaker!!.setOnClickListener{
//            speakOut()
//        }

        mic.setOnClickListener{
            val intent=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

            //passing language model and model free form intent
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            //passing our language as default
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )
            //specifying a prompt message as we speak to text
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speech to Text")

            //calling a start activity for result method and passing our result code
            try{
                startActivityForResult(intent,REQUEST_CODE_SPEECH_INPUT)
            }catch (e:Exception){
                Toast.makeText(this,"",Toast.LENGTH_LONG).show()
            }

        }
        //setupProgressDialog()
        translateBtn.setOnClickListener{
            originalText=textOutput.text.toString()
            prepareTranslationModel()
        }

    }



    private fun setupProgressDialog() {
        pDialog =
            SweetAlertDialog(this@MainActivity, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Loading"
        pDialog.setCancelable(false)
        pDialog.show()
    }

    private fun prepareTranslationModel() {
        val options:TranslatorOptions=TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.HINDI)
            .build()

        englishToHindi=Translation.getClient(options)
        //first download model if user does not have it
        //pDialog.titleText="model downloading..."
        //pDialog.show()
        englishToHindi.downloadModelIfNeeded().addOnSuccessListener {
            //pDialog.dismissWithAnimation()
            translateText()
        }.addOnFailureListener{
            translatedText.text="Error ${it.message}"
        }
    }

    private fun translateText() {
        //pDialog.titleText="Translate text"
        //pDialog.show()
        englishToHindi.translate(originalText).addOnSuccessListener {
            //pDialog.dismissWithAnimation()
            translatedText.text=it
        }.addOnFailureListener{
            translatedText.text="Error ${it.message}"
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQUEST_CODE_SPEECH_INPUT){
            if(resultCode== RESULT_OK && data!=null){
                val res:ArrayList<String> = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String> /* = java.util.ArrayList<kotlin.String> */
                //setting data to our outer text view
                textOutput.setText(
                    Objects.requireNonNull(res)[0]
                )
            }
        }
    }


//    override fun onInit(status: Int) {
//        if(status==TextToSpeech.SUCCESS){
//            val result=tts!!.setLanguage(Locale.UK)
//            if(result==TextToSpeech.LANG_MISSING_DATA||result==TextToSpeech.LANG_NOT_SUPPORTED){
//                Log.e("TTS","Language not supported!")
//
//            }else{
//                speaker!!.isEnabled=true
//            }
//        }
//    }

//    private fun speakOut() {
//        val text = translatedText!!.text.toString()
//        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
//    }
//    public override fun onDestroy() {
//        // Shutdown TTS when
//        // activity is destroyed
//        if (tts != null) {
//            tts!!.stop()
//            tts!!.shutdown()
//        }
//        super.onDestroy()
//    }

}

