package com.chrg.mediaplayer

class SongsInfo {

    var SongName:String?=null
    var AuthorName:String?=null
    var SongURL:String?=null
    constructor(SongName:String, AuthorName:String, SongURL:String){
        this.SongName = SongName
        this.AuthorName = AuthorName
        this.SongURL = SongURL
    }

}