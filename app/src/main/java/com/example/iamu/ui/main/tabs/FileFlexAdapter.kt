package com.example.iamu.ui.main.tabs

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.iamu.R
import com.example.iamu.ui.main.db.IFileFetcher
import com.example.iamu.ui.main.mimeType
import com.google.android.flexbox.*
import java.io.File


class FileFlexAdapter(val df: FileFlexFragment, var dataSet: Array<File>) :
    RecyclerView.Adapter<FileViewHolder>() {

    fun refresh() {
        dataSet = df.iFileFetcher.listFiles(df.requireContext())
        notifyDataSetChanged()
    }

    fun openMedia(ctx: Context, file: File) {
        df.iFileFetcher.logFile(df.requireContext(),file)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(FileProvider.getUriForFile(ctx, "IAMU.provider", file), file.mimeType())
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            ctx.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(ctx, ctx.getString(R.string.unable_to_view_file), Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FileViewHolder {

        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.file_cell_item, viewGroup, false)
        val vh = FileViewHolder(view, true)

        view.setOnClickListener { openMedia(view.context, vh.file) }

        view.setOnLongClickListener {

            val menu: PopupMenu = PopupMenu(view.context, it)

            menu.menuInflater.inflate(R.menu.media_hold_popup, menu.menu)

            menu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.option_open_file -> {
                        openMedia(view.context, vh.file)
                    }
                    R.id.option_del_file -> {
                        vh.file.delete()
                        refresh()
                    }
                }
                true
            }
            menu.show()
            true
        }

        return vh
    }

    override fun onBindViewHolder(viewHolder: FileViewHolder, position: Int) {
        viewHolder.updateData(dataSet[position], null)
    }

    override fun getItemCount() = dataSet.size
}

open class FileFlexFragment(val iFileFetcher: IFileFetcher) : Fragment() {
    lateinit var adp: FileFlexAdapter



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_file_flex, container, false)

        adp = FileFlexAdapter(this, iFileFetcher.listFiles(requireContext()))
        val refresher = view.findViewById<SwipeRefreshLayout>(R.id.pull_to_refresh)
        refresher.setOnRefreshListener {
            adp.refresh()
            refresher.isRefreshing = false
        }

        val listView: RecyclerView = view.findViewById(R.id.file_flex_list)

        val manager = FlexboxLayoutManager(context)
        manager.flexDirection = FlexDirection.ROW
        manager.flexWrap = FlexWrap.WRAP
        manager.alignItems = AlignItems.STRETCH
        manager.justifyContent = JustifyContent.SPACE_AROUND

        listView.layoutManager = manager

        listView.adapter = adp

        return view
    }

    override fun onResume() {
        super.onResume()
        adp.refresh()
    }
}