package me.lake.librestreaming.sample.view;

import android.content.Context;

import androidx.appcompat.widget.AppCompatTextView;

public class AlwaysMarqueeTextView extends AppCompatTextView {
  
    // com.duopin.app.AlwaysMaguequeScrollView  
    public AlwaysMarqueeTextView(Context context) {
  
        super(context);  
    }
  

    @Override  
    public boolean isFocused() {  
  
        return true;  
  
    }  
  

}  