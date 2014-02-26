package com.salvadordalvik.something.widget;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.common.io.CharStreams;
import com.salvadordalvik.fastlibrary.FastDialogFragment;
import com.salvadordalvik.fastlibrary.util.FastUtils;
import com.salvadordalvik.something.R;
import com.salvadordalvik.something.util.SomePreferences;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by matthewshepard on 2/1/14.
 */
public class PreferencesDialogFragment extends FastDialogFragment implements View.OnClickListener {
    private String[] themes;// = {"default", "dark", "yospos", "amberpos", "fyad"};
    private int[] themeColors;// = {Color.WHITE, Color.GRAY, Color.rgb(87, 255, 87), Color.rgb(232, 188, 68), Color.rgb(255, 174, 255)};
    private String[] friendlyThemeNames;
    private LinearLayout primaryThemes;

    public PreferencesDialogFragment() {
        super(R.layout.preference_dialog, R.string.preference_dialog_title);
    }

    private void loadThemeList(){
        try {
            String[] themeList = getResources().getAssets().list("css");
            themes = new String[themeList.length];
            themeColors = new int[themeList.length];
            friendlyThemeNames = new String[themeList.length];
            for(int ix=0;ix<themeList.length;ix++){
                themes[ix] = themeList[ix].replace(".css", "");
                loadThemeIconColor(themeList[ix], ix);
                Log.e("theme", themeList[ix] + " - " + themes[ix]+" - "+friendlyThemeNames[ix]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final static Pattern cssHeaderPattern = Pattern.compile("/\\*\\s*([\\w\\s]*)\\s*([#0-9a-fA-F]+)\\s*\\*/");

    private void loadThemeIconColor(String themeName, int position){
        try {
            InputStreamReader in = new InputStreamReader(getResources().getAssets().open("css/"+themeName));
            String cssFile = CharStreams.toString(in);
            Matcher match = cssHeaderPattern.matcher(cssFile);
            if(match.find()){
                friendlyThemeNames[position] = match.group(1).trim();
                themeColors[position] = Color.parseColor(match.group(2).trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
            friendlyThemeNames[position] = "Unknown";
            themeColors[position] = Color.WHITE;
        }
    }

    @Override
    public void viewCreated(View frag, Bundle savedInstanceState) {
        loadThemeList();

        primaryThemes = (LinearLayout) frag.findViewById(R.id.preference_theme_container);
        for(int ix=0;ix<themes.length;ix++){
            ImageView theme = new ImageView(getActivity());
            GradientDrawable selectedColor = (GradientDrawable) getResources().getDrawable(R.drawable.preference_theme_icon_checked);
            selectedColor.mutate();
            selectedColor.setColor(themeColors[ix]);
            theme.setImageDrawable(selectedColor);
            theme.setTag(ix);
            theme.setOnClickListener(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(16, 0, 16, 0);
            primaryThemes.addView(theme, params);
        }

        updateThemeIcons();
    }

    @Override
    public void onClick(View v) {
        selectTheme(themes[(Integer) v.getTag()]);
    }

    @Override
    public void refreshData(boolean pullToRefresh) {}

    private void selectTheme(String theme){
        SomePreferences.setTheme(theme);
        updateThemeIcons();
    }

    private void updateThemeIcons() {
        int count = primaryThemes.getChildCount();
        for(int ix=0; ix<count; ix++){
            View child = primaryThemes.getChildAt(ix);
            if(child instanceof ImageView){
                int themeId = (Integer) child.getTag();
                ImageView theme = (ImageView) child;
                if(themes[themeId].equals(SomePreferences.selectedTheme)){
                    GradientDrawable selectedColor = (GradientDrawable) getResources().getDrawable(R.drawable.preference_theme_icon_checked);
                    selectedColor.mutate();
                    selectedColor.setColor(themeColors[themeId]);
                    theme.setImageDrawable(selectedColor);
                }else{
                    GradientDrawable selectedColor = (GradientDrawable) getResources().getDrawable(R.drawable.preference_theme_icon);
                    selectedColor.mutate();
                    selectedColor.setColor(themeColors[themeId]);
                    theme.setImageDrawable(selectedColor);
                }
            }
        }
    }
}
