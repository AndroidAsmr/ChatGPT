package com.hadirahimi.chatgpt.ui

import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hadirahimi.chatgpt.databinding.ItemChatBinding
import com.hadirahimi.chatgpt.model.ModelMessage
import com.hadirahimi.chatgpt.utils.BY_GPT
import com.hadirahimi.chatgpt.utils.BY_USER

class AdapterMessage : RecyclerView.Adapter<AdapterMessage.MyViewHolder>()
{
    private lateinit var binding : ItemChatBinding
    private var messageList = mutableListOf<ModelMessage>()
    
    inner class MyViewHolder : RecyclerView.ViewHolder(binding.root)
    {
        fun setData(message:ModelMessage)
        {
            binding.apply {
                when(message.messageBy)
                {
                    BY_USER ->{
                        layoutUser.visibility = View.VISIBLE
                        layoutGpt.visibility = View.GONE
                        userMessage.text = message.message
                    }
                    BY_GPT ->{
                        layoutUser.visibility = View.GONE
                        layoutGpt.visibility = View.VISIBLE
                        gptResponse.text = message.message
                    }
                }
            }
        }
    }
    
    override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : MyViewHolder
    {
        binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder()
    }
    
    override fun getItemCount() : Int = messageList.size
    
    override fun onBindViewHolder(holder : MyViewHolder , position : Int)
    {
        holder.setData(messageList[position])
    }
    
    override fun getItemViewType(position : Int) : Int
    {
        return position
    }
    class MessageDiffUtils(private val oldList : List<ModelMessage>,private val newList: List<ModelMessage>):DiffUtil.Callback()
    {
        override fun getOldListSize() : Int
        {
            return oldList.size
        }
    
        override fun getNewListSize() : Int
        {
            return newList.size
        }
    
        override fun areItemsTheSame(oldItemPosition : Int , newItemPosition : Int) : Boolean
        {
            return oldList[oldItemPosition] === newList[newItemPosition]
        }
    
        override fun areContentsTheSame(oldItemPosition : Int , newItemPosition : Int) : Boolean
        {
            return oldList[oldItemPosition].message === newList[newItemPosition].message
        }
    }
    fun submitData(list:List<ModelMessage>)
    {
        val messageDiffUtil = MessageDiffUtils(messageList,list)
        val diffUtils = DiffUtil.calculateDiff(messageDiffUtil)
        messageList.clear()
        messageList.addAll(list)
        diffUtils.dispatchUpdatesTo(this)
    }
}











