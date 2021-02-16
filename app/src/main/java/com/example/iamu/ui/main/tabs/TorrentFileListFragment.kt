package com.example.iamu.ui.main.tabs

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iamu.R
import com.example.iamu.ui.main.torrent.TorrentManagement
import com.example.iamu.ui.main.torrent.TorrentEntry
import com.example.iamu.ui.main.torrent.service.TorrentDownloadState
import com.example.iamu.ui.main.torrent.service.TorrentServiceController
import com.example.iamu.ui.main.torrent.service.downloader.TorrentDownloadListener
import java.io.File

private fun entries(context: Context) = TorrentManagement.listTorrentFiles(context)

class FilesAdapter(
    private val flf: TorrentFileListFragment,
    private var dataSet: Array<File>
) :
    RecyclerView.Adapter<FileViewHolder>() {

    fun refresh() {
        dataSet = entries(flf.requireContext())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.text_row_item, viewGroup, false)
        val fileActions = view.findViewById<Button>(R.id.file_actions)
        val vh = FileViewHolder(view, false)
        fileActions!!.setOnClickListener {
            val menu = PopupMenu(flf.requireActivity(), it)

            val t = TorrentEntry(view.context, vh.file)
            menu.menuInflater.inflate(if (t.media.exists()) R.menu.file_exist_popup else R.menu.file_ghost_popup, menu.menu)

            menu.setOnMenuItemClickListener {
                val result = when (it.itemId) {
                    R.id.option_down_media -> {
                        t.media.mkdirs()
                        flf.controller.enqueue(t)
                        true
                    }
                    R.id.option_del_media -> {
                        t.media.deleteRecursively()
                        true
                    }
                    R.id.option_del_everything -> {
                        t.media.deleteRecursively()
                        t.torrentFile.delete()
                        true
                    }
                    R.id.option_del_torrent -> {
                        t.torrentFile.delete()
                        true
                    }
                    else -> false

                }
                if (result) {
                    dataSet = entries(view.context)
                    notifyDataSetChanged()
                }
                result

            }
            menu.show()
        }
        return vh
    }

    override fun onBindViewHolder(viewHolder: FileViewHolder, position: Int) {
        viewHolder.updateData(dataSet[position],flf.downloadsState[dataSet[position].path])
    }

    override fun getItemCount() = dataSet.size


}

class TorrentFileListFragment : Fragment() {
    lateinit var adp: FilesAdapter

    val downloadsState= mutableMapOf<String, Float>()
    val controller: TorrentServiceController by lazy { TorrentServiceController(requireActivity()) }

    private lateinit var torrentService: TorrentServiceController

    private val listenerTorrent: TorrentDownloadListener = object : TorrentDownloadListener {
        override fun onDownloadStart(torrentFile: String) {
            downloadsState[torrentFile] = 0F
            adp.notifyDataSetChanged()
        }

        override fun onDownloadProgress(torrentFile: String, progress: Float) {
            downloadsState[torrentFile] = progress
            if(progress.toInt()==100) downloadsState.remove(torrentFile)
            adp.notifyDataSetChanged()
        }

        override fun onDownloadEnd(torrentFile: String, torrentDownloadState: TorrentDownloadState) {
            downloadsState.remove(torrentFile)
            adp.notifyDataSetChanged()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        torrentService= TorrentServiceController(requireContext())
        adp = FilesAdapter(this, entries(requireContext()))
        val view = inflater.inflate(R.layout.fragment_torrent_file_list, container, false)

        val listView: RecyclerView = view.findViewById(R.id.file_list)
        listView.layoutManager = LinearLayoutManager(context)
        listView.adapter = adp
        return view
    }

    override fun onResume() {
        super.onResume()
        adp.refresh()
        torrentService.registerDownloadListener(listenerTorrent)
    }

    override fun onPause() {
        super.onPause()
        torrentService.unregisterDownloadListener(listenerTorrent)
    }
}