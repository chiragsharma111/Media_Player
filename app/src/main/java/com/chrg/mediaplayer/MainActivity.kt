package com.chrg.mediaplayer

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.song_ticket.view.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    var listSongs = ArrayList<SongsInfo>()
    var adapter:MySongsAdapter?=null
    var mp:MediaPlayer?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LoadURLOnline()
        checkPermission()

        var myTracking = mySongTrack()
        myTracking.start()
    }

    fun LoadURLOnline() {
        listSongs.add(SongsInfo("You Belong to Me", "Taylor Swift", "https://www.chrisbuhneing.com/music/Country%20Western/Taylor%20Swift/You%20belong%20with%20me.mp3"))
        listSongs.add(SongsInfo("This ain't Techno", "David Guetta", "http://dl.navasong.ir/Media/Music3/David%20Guetta%20-%20This%20Ain%27t%20Techno%20%282019%29.mp3"))
        listSongs.add(SongsInfo("ME!", "Taylor Swift", "http://dl.navasong.ir/Media/Music3/Taylor%20Swift%20-%20ME%21%20%28feat.%20Brendon%20Urie%20of%20Panic%21%20At%20The%20Disco%29%20%282019%29%20320.mp3"))
        listSongs.add(SongsInfo("Beautiful People", "Ed Sheeran", "http://dl.navasong.ir/Media/Music3/Ed%20Sheeran%20-%20Beautiful%20People%20%28Acoustic%29%20%282019%29.mp3"))
        listSongs.add(SongsInfo("Rescue Me", "OneRepublic", "http://dl.navasong.ir/Media/Music3/OneRepublic%20-%20Rescue%20Me%20%282019%29%20.mp3"))
        listSongs.add(SongsInfo("SeÃ±orita", "Shawn Mendes", "http://dl.navasong.ir/Media/Music3/Shawn%20Mendes%20-%20Se%C3%B1orita%20%282019%29.mp3"))
        listSongs.add(SongsInfo("Attention", "Charlie Puth", "https://themamaship.com/music/Catalog/Attention-Charlie%20Puth.mp3"))
        listSongs.add(SongsInfo("Demons", "Imagine Dragons", "http://sv2.mybia2music.com/s2/Music/1391/11%20Bahman/123669/Imagine%20Dragons%20-%20Night%20Visions/004-imagine_dragons-demons.mp3"))
        listSongs.add(SongsInfo("The River", "Imagine Dragons", "http://sv2.mybia2music.com/s2/Music/1391/11%20Bahman/123669/Imagine%20Dragons%20-%20Night%20Visions/014-imagine_dragons-the_river.mp3"))

    }

    inner class MySongsAdapter: BaseAdapter {
        var MySongList = ArrayList<SongsInfo>()
        constructor(MySongList: ArrayList<SongsInfo>): super() {
            this.MySongList=MySongList
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val myView = layoutInflater.inflate(R.layout.song_ticket, null)
            val song = this.MySongList[p0]
            myView.tvSongName.text = song.SongName
            myView.tvAuthorName.text = song.AuthorName
            myView.buPlay.setOnClickListener( View.OnClickListener {
                // TODO: Play song from URL

                if (myView.buPlay.text.equals("STOP")){
                    mp!!.stop()
                    myView.buPlay.text = "PLAY"
                }
                else {
                    mp = MediaPlayer()
                    try {
                        mp!!.setDataSource(song.SongURL)
                        mp!!.prepare()
                        mp!!.start()
                        myView.buPlay.text = "STOP"
                        sbProgress.max = mp!!.duration
                    } catch (ex: Exception) {}
                }
            })
            return myView
        }

        override fun getItem(p0: Int): Any {
            return this.MySongList[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return this.MySongList.size
        }
    }

    inner class mySongTrack(): Thread() {

        override fun run() {
            while (true){
                try {
                    Thread.sleep(1000)
                } catch (ex: Exception) {}

                runOnUiThread{
                    if (mp!=null){
                        sbProgress.progress = mp!!.currentPosition
                    }
                }
            }
        }

    }
    private var REQUEST_CODE_ASK_PERMISSION = 123
    fun checkPermission() {
        if(Build.VERSION.SDK_INT>=23) {
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_ASK_PERMISSION )
                return
            }
        }
        loadSong()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        // why should the requestCode be equal to READIMAGE
        when(requestCode){
            REQUEST_CODE_ASK_PERMISSION -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    loadSong()
                }
                else {
                    Toast.makeText(applicationContext, "Cannot access images", Toast.LENGTH_LONG).show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
    fun loadSong() {
        val allSongURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val cursor = contentResolver.query(allSongURI, null, selection, null, null)
        if(cursor!=null) {
            if(cursor!!.moveToFirst()) {
                do{
                    val songURL = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val AuthorName = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val SongName = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                    listSongs.add(SongsInfo(SongName, AuthorName, songURL))

                }while (cursor!!.moveToNext())
            }
            cursor.close()
            adapter = MySongsAdapter(listSongs)
            lvListSongs.adapter = adapter
        }


    }
}
