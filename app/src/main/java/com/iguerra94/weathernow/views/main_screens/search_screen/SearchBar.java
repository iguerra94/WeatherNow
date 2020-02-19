package com.iguerra94.weathernow.views.main_screens.search_screen;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.views.toolbar.TransformingToolbar;

/**
 * A Toolbar with an EditText used for searching
 * <p>In a real life application you would hook up your TextWatcher to this method to track what the user is searching for</p>
 */
public class SearchBar extends TransformingToolbar {

    private EditText editText;

    public SearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(context.getResources().getColor(android.R.color.white));
        setNavigationIcon(R.drawable.ic_back);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        inflate(getContext(), R.layout.merge_search, this);
        editText = findViewById(R.id.toolbar_search_edittext);
    }

    @Override
    public void showContent() {
        super.showContent();
        editText.requestFocus();
    }

    public void clearText() {
        editText.setText(null);
    }

}