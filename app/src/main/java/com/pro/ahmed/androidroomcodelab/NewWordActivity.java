package com.pro.ahmed.androidroomcodelab;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pro.ahmed.androidroomcodelab.data.WordViewModel;
import com.pro.ahmed.androidroomcodelab.data.models.Word;

public class NewWordActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.example.android.wordlistsql.REPLY";

    private EditText mEditWordView;
    private WordViewModel mWordViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_word);
        mEditWordView = findViewById(R.id.edit_word);
        mWordViewModel = ViewModelProviders.of(this).get(WordViewModel.class);
        final Button button = findViewById(R.id.button_save);
        final String edit = getIntent().getStringExtra("edit");
        if (edit.equals("edit")) {
            setTitle("Edit");
            mEditWordView.setText(mWordViewModel.getWord().getWord());
            Toast.makeText(this, "Received Id" + mWordViewModel.getWord().getId(), Toast.LENGTH_SHORT).show();
        } else {
            setTitle("Add");
        }
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(mEditWordView.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    String word = mEditWordView.getText().toString();
                    replyIntent.putExtra(EXTRA_REPLY, word);
                    if (edit.equals("edit")) {
                        mWordViewModel.getWord().setWord(word);
                    }
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }
}