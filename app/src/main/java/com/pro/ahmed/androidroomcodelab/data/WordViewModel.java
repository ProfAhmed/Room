package com.pro.ahmed.androidroomcodelab.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.pro.ahmed.androidroomcodelab.data.models.Word;

import java.util.List;

public class WordViewModel extends AndroidViewModel {
    private WordRepository mRepository;
    private LiveData<List<Word>> mAllWords;
    private static Word word;


    public WordViewModel(Application application) {
        super(application);
        mRepository = new WordRepository(application);
        mAllWords = mRepository.getAllWords();
    }

    public static Word getWord() {
        return word;
    }

    public static void setWord(Word word) {
        WordViewModel.word = word;
    }

    public LiveData<List<Word>> getAllWords() {
        return mAllWords;
    }

    public void insert(Word word) {
        mRepository.insert(word);
    }

    public void update(Word word) {
        mRepository.update(word);
    }

    public void delete(Word word) {
        mRepository.delete(word);
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }
}
