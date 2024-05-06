package com.example.playlist.DataAccess.Models;

import com.example.playlist.Enums.Visibility;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
@Data
@Document
public class Playlist {
        @Id
        private String id;
        private Integer userId;
        private String playlistName;
        private Visibility visibility;
        private List<SongDetails> songs = new ArrayList<>();
        //to do: add another list for artists

        public void AddSongToPlaylist(SongDetails song)
        {
            if(this.songs == null)
            {
                this.songs= new LinkedList<>();
            }
            this.songs.add(song);
        }
        public Playlist() {}
        public Playlist(Integer userId, String playlistName)
        {
            this.userId = userId;
            this.playlistName = playlistName;
        }

        public void setVisibility(String visibility)
        {
            if (visibility.equals("PPublic") || visibility.equals("PPrivate") || visibility.equals("Friends"))
            {
                this.visibility = Visibility.valueOf(visibility);
            }
            else
            {
                this.visibility = Visibility.Unknown;
            }
        }


}
