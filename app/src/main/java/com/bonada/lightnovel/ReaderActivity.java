package com.bonada.lightnovel;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;

public class ReaderActivity extends Activity {
    private Book book; private String text; private ScrollView scroll; private TextView content; private Handler handler=new Handler(Looper.getMainLooper());
    private float font=18f; private int bg=0; private SharedPreferences prefs;
    private Runnable saver=()->saveProgress();
    @Override public void onCreate(Bundle b){super.onCreate(b);String id=getIntent().getStringExtra("id");for(Book x:Library.load(this))if(x.id.equals(id))book=x;if(book==null){finish();return;}
        prefs=getSharedPreferences("reader",MODE_PRIVATE);font=prefs.getFloat(book.id+"_font",18f);bg=prefs.getInt(book.id+"_bg",0);try{text=TxtParser.read(new File(getFilesDir(),book.fileName));}catch(Exception e){finish();return;}build();restore();}
    private TextView tv(String s,float sp,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(sp);t.setTextColor(color);t.setPadding(22,16,22,28);return t;}
    private void build(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(background());
        LinearLayout bar=new LinearLayout(this);bar.setGravity(Gravity.CENTER_VERTICAL);bar.setPadding(4,0,4,0);bar.setBackgroundColor(background());
        Button back=btn("‹");back.setOnClickListener(v->{saveProgress();finish();});bar.addView(back,new LinearLayout.LayoutParams(48,54));
        TextView title=tv(book.title,18,fg());title.setGravity(Gravity.CENTER_VERTICAL);title.setSingleLine(true);bar.addView(title,new LinearLayout.LayoutParams(0,54,1));
        Button toc=btn("目录");toc.setOnClickListener(v->showToc());bar.addView(toc,new LinearLayout.LayoutParams(70,54));
        Button set=btn("Aa");set.setOnClickListener(v->showSettings());bar.addView(set,new LinearLayout.LayoutParams(60,54));root.addView(bar);
        scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundColor(background());content=tv(text,font,fg());content.setTextIsSelectable(true);scroll.addView(content,new ScrollView.LayoutParams(-1,-2));root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);
        scroll.getViewTreeObserver().addOnScrollChangedListener(()->{handler.removeCallbacks(saver);handler.postDelayed(saver,500);});
    }
    private Button btn(String s){Button b=new Button(this);b.setText(s);b.setTextSize(15);b.setMinWidth(0);b.setMinHeight(0);b.setPadding(0,0,0,0);return b;}
    private int background(){return bg==0?Color.rgb(250,250,250):bg==1?Color.rgb(246,239,218):Color.rgb(30,30,30);}
    private int fg(){return bg==2?Color.rgb(220,220,220):Color.rgb(45,45,45);}
    private void restore(){int pos=prefs.getInt(book.id+"_pos",0);scroll.post(()->{int max=Math.max(0,content.getHeight()-scroll.getHeight());scroll.scrollTo(0,Math.min(pos,max));});}
    private void saveProgress(){prefs.edit().putInt(book.id+"_pos",scroll.getScrollY()).putFloat(book.id+"_font",font).putInt(book.id+"_bg",bg).apply();}
    @Override protected void onPause(){super.onPause();saveProgress();}
    private void showToc(){
        ListView lv=new ListView(this);ArrayAdapter<String>a=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1){@Override public View getView(int p,View v,android.view.ViewGroup parent){TextView t=(TextView)super.getView(p,v,parent);t.setTextSize(16);t.setPadding(28,18,20,18);return t;}};
        for(Book.Chapter c:book.chapters)a.add(c.title);lv.setAdapter(a);Dialog d=new Dialog(this);d.setTitle("目录 · "+book.chapters.size()+"章");d.setContentView(lv);Window w=d.getWindow();if(w!=null)w.setLayout(-1,-1);lv.setOnItemClickListener((p,v,pos,id)->{Book.Chapter c=book.chapters.get(pos);int y=offsetToY(c.start);scroll.scrollTo(0,y);saveProgress();d.dismiss();});d.show();if(d.getWindow()!=null)d.getWindow().setLayout((int)(getResources().getDisplayMetrics().widthPixels*.94),(int)(getResources().getDisplayMetrics().heightPixels*.82));
    }
    private int offsetToY(int charOffset){if(charOffset<=0)return 0;String sub=text.substring(0,Math.min(charOffset,text.length()));TextView probe=new TextView(this);probe.setText(sub);probe.setTextSize(font);probe.setPadding(22,16,22,28);probe.measure(View.MeasureSpec.makeMeasureSpec(content.getWidth(),View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED));return probe.getMeasuredHeight();}
    private void showSettings(){
        LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(28,18,28,10);TextView size=tv("字体大小："+Math.round(font)+"sp",18,fg());box.addView(size);LinearLayout buttons=new LinearLayout(this);Button minus=btn("A−");Button plus=btn("A＋");buttons.addView(minus,new LinearLayout.LayoutParams(0,52,1));buttons.addView(plus,new LinearLayout.LayoutParams(0,52,1));box.addView(buttons);TextView bgTitle=tv("阅读背景",17,fg());box.addView(bgTitle);LinearLayout colors=new LinearLayout(this);String[] names={"白色","米色","深色"};for(int i=0;i<3;i++){final int x=i;Button q=btn(names[i]);colors.addView(q,new LinearLayout.LayoutParams(0,52,1));q.setOnClickListener(v->{bg=x;applyStyle();});}box.addView(colors);
        Dialog d=new Dialog(this);d.setTitle("阅读设置");d.setContentView(box);minus.setOnClickListener(v->{font=Math.max(14,font-1);size.setText("字体大小："+Math.round(font)+"sp");applyStyle();});plus.setOnClickListener(v->{font=Math.min(30,font+1);size.setText("字体大小："+Math.round(font)+"sp");applyStyle();});d.show();if(d.getWindow()!=null)d.getWindow().setLayout((int)(getResources().getDisplayMetrics().widthPixels*.92),-2);
    }
    private void applyStyle(){int y=scroll.getScrollY();content.setTextSize(font);content.setTextColor(fg());content.setBackgroundColor(background());scroll.setBackgroundColor(background());if(scroll.getChildCount()>0)scroll.getChildAt(0).setBackgroundColor(background());scroll.post(()->scroll.scrollTo(0,y));saveProgress();}
}
