package com.hadirahimi.chatgpt.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.recyclerview.widget.LinearLayoutManager
import com.hadirahimi.chatgpt.R
import com.hadirahimi.chatgpt.databinding.ActivityMainBinding
import com.hadirahimi.chatgpt.model.ModelMessage
import com.hadirahimi.chatgpt.utils.BY_GPT
import com.hadirahimi.chatgpt.utils.BY_USER
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity()
{
    private lateinit var binding : ActivityMainBinding
    private val  messageList = mutableListOf<ModelMessage>()
    private lateinit var client : OkHttpClient
    private lateinit var json:MediaType
    private lateinit var adapterMessage : AdapterMessage
    
    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //InitViews
        binding.apply {
            //setup toolbar
            setSupportActionBar(toolbar)
            
            //App name Theme
            appNameTheme()
            
            //setup server
            setupServer()
            
            //setup adapter
            adapterMessage = AdapterMessage()
            
            //setup recyclerview
            binding.chatRecycler.apply {
                val layout = LinearLayoutManager(this@MainActivity,LinearLayoutManager.VERTICAL,false)
                layout.stackFromEnd = true
                layoutManager = layout
                adapter = adapterMessage
                setHasFixedSize(true)
            }
            
            //click listener
            ivSend.setOnClickListener {
                val message = etMessage.text.toString()
                newMessage(message, BY_USER)
                requestToApi(message)
            }
            
            
            
        }
    }
    
    private fun requestToApi(message : String)
    {
        val body = JSONObject()
          try{
                body.put("model","text-davinci-003")
                body.put("prompt",message)
                body.put("max_tokens",2000)
                body.put("temperature",0)
            }catch (e:Exception){
                e.printStackTrace()
            }
        val requestBody = body.toString().toRequestBody(json)
        val request = Request.Builder()
            .url("https://api.openai.com/v1/completions")
            .header("Authorization","Bearer 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")
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object:Callback{
            override fun onFailure(call : Call , e : IOException)
            {
                newMessage("Failed to load response. because : ${e.message}", BY_GPT)
            }
    
            override fun onResponse(call : Call , response : Response)
            {
                if (response.isSuccessful)
                {
                      try{
                        
                          val jsonObject = JSONObject(response.body!!.string())
                          val jsonArray = jsonObject.getJSONArray("choices")
                          val result : String = jsonArray.getJSONObject(0).getString("text")
                          newMessage(result, BY_GPT)
                          
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                }else
                {
                    newMessage("Failed to load response. because : ${response.body}", BY_GPT)
                }
            }
    
        })
        
    }
    
    private fun newMessage(message : String , message_by : String)
    {
        messageList.add(ModelMessage(message,message_by))
        adapterMessage.submitData(messageList)
        binding.chatRecycler.smoothScrollToPosition(adapterMessage.itemCount)
        binding.etMessage.setText("")
    }
    
    private fun setupServer()
    {
        json = "application/json; charset=utf-8".toMediaType()
        client = OkHttpClient()
    }
    
    private fun appNameTheme()
    {
        val spannableString = SpannableString(getString(R.string.app_name))
        val colorSpan = ForegroundColorSpan(Color.parseColor("#74aa9c"))
        spannableString.setSpan(colorSpan,4,7,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvAppName.text = spannableString
    }
}










