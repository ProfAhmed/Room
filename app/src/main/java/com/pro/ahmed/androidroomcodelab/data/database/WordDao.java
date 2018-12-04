package com.pro.ahmed.androidroomcodelab.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.pro.ahmed.androidroomcodelab.data.models.Word;

import java.util.List;

@Dao
public interface WordDao {

    @Insert
    void insert(Word word);

    @Update
    void update(Word word);

    @Delete
    void delete(Word word);

    @Query("Delete From word_table ")
    void deleteAll();

    @Query("Select * From word_table Order By word ASC")
    LiveData<List<Word>> getAllWords();
}
