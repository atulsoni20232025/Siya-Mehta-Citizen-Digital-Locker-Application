package com.example.digitallocker

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class DocumentAdapter(private val list: ArrayList<Document>) :
    RecyclerView.Adapter<DocumentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileName: TextView = itemView.findViewById(R.id.fileName)
        val shareBtn: Button = itemView.findViewById(R.id.shareBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_document, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val doc = list[position]

        // Show icon
        if (doc.fileType == "pdf") {
            holder.fileName.text = "📄 " + doc.fileName
        } else {
            holder.fileName.text = "🖼️ " + doc.fileName
        }

        // OPEN FILE
        holder.itemView.setOnClickListener {

            val intent = Intent(Intent.ACTION_VIEW)
            val uri = Uri.parse(doc.fileUrl)

            if (doc.fileType == "pdf") {
                intent.setDataAndType(uri, "application/pdf")
            } else {
                intent.setDataAndType(uri, "image/*")
            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            try {
                holder.itemView.context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(holder.itemView.context, "No app found", Toast.LENGTH_SHORT).show()
            }
        }

        // SHARE
        holder.shareBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, doc.fileUrl)

            holder.itemView.context.startActivity(
                Intent.createChooser(intent, "Share via")
            )
        }
    }
}