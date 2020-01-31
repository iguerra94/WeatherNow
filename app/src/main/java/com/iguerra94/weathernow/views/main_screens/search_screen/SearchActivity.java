package com.iguerra94.weathernow.views.main_screens.search_screen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;

import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.views.transition.FadeInTransition;
import com.iguerra94.weathernow.views.transition.FadeOutTransition;
import com.iguerra94.weathernow.views.transition.SimpleTransitionListener;

public class SearchActivity extends AppCompatActivity {

    private SearchBar searchbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(searchbar);

        // make sure to check if this is the first time running the activity
        // we don't want to play the enter animation on configuration changes (i.e. orientation)
        if (isFirstTimeRunning(savedInstanceState)) {
            // Start with an empty looking Toolbar
            // We are going to fade its contents in, as long as the activity finishes rendering
            searchbar.hideContent();

            ViewTreeObserver viewTreeObserver = searchbar.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    searchbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    // after the activity has finished drawing the initial layout, we are going to continue the animation
                    // that we left off from the MainActivity
                    showSearch();
                }

                private void showSearch() {
                    // use the TransitionManager to animate the changes of the Toolbar
                    TransitionManager.beginDelayedTransition(searchbar, FadeInTransition.createTransition());
                    // here we are just changing all children to VISIBLE
                    searchbar.showContent();
                }
            });
        }
    }

    private boolean isFirstTimeRunning(Bundle savedInstanceState) {
        return savedInstanceState == null;
    }

    @Override
    public void finish() {
        // at the same time, start the exit transition
        exitTransitionWithAction(new Runnable() {
            @Override
            public void run() {
                // which finishes the activity (for real) when done
                SearchActivity.super.finish();

                // override the system pending transition as we are handling ourselves
                overridePendingTransition(0, 0);
            }
        });
    }

    private void exitTransitionWithAction(final Runnable endingAction) {
        Transition transition = FadeOutTransition.withAction(new SimpleTransitionListener() {
            @Override
            public void onTransitionEnd(Transition transition) {
                endingAction.run();
            }
        });

        TransitionManager.beginDelayedTransition(searchbar, transition);
        searchbar.hideContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_clear) {
            searchbar.clearText();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}