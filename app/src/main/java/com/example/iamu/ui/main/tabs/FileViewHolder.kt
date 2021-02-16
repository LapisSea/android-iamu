package com.example.iamu.ui.main.tabs

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.iamu.R
import com.example.iamu.ui.main.mimeType
import java.io.File


class FileViewHolder(view: View, val showExtension: Boolean) : RecyclerView.ViewHolder(view) {

    lateinit var file: File

    private val fileName = itemView.findViewById<TextView>(R.id.file_name)
    private val fileThumbnail = itemView.findViewById<ImageView>(R.id.file_thumbnail)
    private val progressBar = itemView.findViewById<ProgressBar>(R.id.progress_bar)

    fun updateData(file: File, progress: Float?) {
        this.file = file
        fileName.text = if (showExtension) file.name else file.nameWithoutExtension

        if (fileThumbnail != null) {
            val ctx=itemView.context
            val m = file.mimeType()
            if (m != null && (m.startsWith("video/") || m.startsWith("image/"))) {
                fileThumbnail.isVisible=true
                Glide.with(ctx)
                    .load(file)
                    .error({
                        fileThumbnail.isVisible=false
                    })
                    .into(fileThumbnail);
            }else{
                fileThumbnail.isVisible=false
            }
        }
        if(progressBar!=null){
            progressBar.isVisible=progress!=null
            if(progress!=null){
                progressBar.progress=progress.toInt()
            }
        }
    }
}