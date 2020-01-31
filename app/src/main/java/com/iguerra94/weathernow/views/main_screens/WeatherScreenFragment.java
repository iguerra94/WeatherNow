package com.iguerra94.weathernow.views.main_screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.google.android.material.appbar.AppBarLayout;
import com.iguerra94.weathernow.R;
import com.iguerra94.weathernow.views.main_screens.search_screen.SearchActivity;
import com.iguerra94.weathernow.views.toolbar.SimpleToolbar;
import com.iguerra94.weathernow.views.transition.FadeOutTransition;
import com.iguerra94.weathernow.views.transition.SimpleTransitionListener;

public class WeatherScreenFragment extends Fragment implements View.OnClickListener {

    private SimpleToolbar weatherScreenToolbar;

    public WeatherScreenFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_weather_screen, container, false);

        AppBarLayout appBarLayout = getActivity().findViewById(R.id.mainToolbarAppBarLayout);
        appBarLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));

        weatherScreenToolbar = getActivity().findViewById(R.id.mainToolbar);
        weatherScreenToolbar.setOnClickListener(this);
        weatherScreenToolbar.setBackground(getActivity().getResources().getDrawable((R.drawable.border_rounded_white_bg)));

        weatherScreenToolbar.setNavigationIcon(R.drawable.ic_sort);

        weatherScreenToolbar.setTitle("Buscar ciudad..");
        weatherScreenToolbar.setTitleTextColor(getActivity().getResources().getColor(R.color.colorGrey));

        setHasOptionsMenu(true);

        return v;
    }

    private void transitionToSearch() {
        // create a transition that navigates to search when complete
        Transition transition = FadeOutTransition.withAction(navigateToSearchWhenDone());

        // let the TransitionManager do the heavy work for us!
        // all we have to do is change the attributes of the toolbar and the TransitionManager animates the changes
        // in this case I am removing the bounds of the toolbar (to hide the blue padding on the screen) and
        // I am hiding the contents of the Toolbar (Navigation icon, Title and Option Items)
        TransitionManager.beginDelayedTransition(weatherScreenToolbar, transition);
        weatherScreenToolbar.hideContent();
    }

    private Transition.TransitionListener navigateToSearchWhenDone() {
        return new SimpleTransitionListener() {
            @Override
            public void onTransitionEnd(Transition transition) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);

                // we are handing the enter transitions ourselves
                // this line overrides that
                getActivity().overridePendingTransition(0, 0);

                // by this point of execution we have animated the 'expansion' of the Toolbar and hidden its contents.
                // We are half way there. Continue to the SearchActivity to finish the animation
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();

        // when you are back from the SearchActivity animate the 'shrinking' of the Toolbar and
        // fade its contents back in
        fadeToolbarIn();

        // in case we are not coming here from the SearchActivity the Toolbar would have been already visible
        // so the above method has no effect
    }

    private void fadeToolbarIn() {
        weatherScreenToolbar.showContent();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            transitionToSearch();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mainToolbar) {
            transitionToSearch();
        }
    }
}