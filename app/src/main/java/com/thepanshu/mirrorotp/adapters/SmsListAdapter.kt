package com.thepanshu.mirrorotp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.thepanshu.mirrorotp.R
import com.thepanshu.mirrorotp.models.Sms

class SmsListAdapter(private val smsList: ArrayList<Sms>): RecyclerView.Adapter<SmsListAdapter.SmsViewHolder>() {
    inner class SmsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //        val imageView: ImageView = itemView.findViewById(R.id.product_imageView)
        val senderNameTv: MaterialTextView = itemView.findViewById(R.id.senderNameTv)
        val otpTv: MaterialTextView = itemView.findViewById(R.id.otpTv)
        val bodyTv: MaterialTextView = itemView.findViewById(R.id.bodyTv)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsViewHolder {
        val li = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = li.inflate(R.layout.sms_list_item, parent, false)
        return SmsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SmsViewHolder, position: Int) {
        holder.senderNameTv.text = smsList[position].sender
        holder.bodyTv.text = smsList[position].body
        holder.otpTv.text = smsList[position].otp.toString()
    }

    override fun getItemCount() = smsList.size
}