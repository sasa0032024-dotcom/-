package com.bonada.lightnovel;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.net.Uri;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;

public class MainActivity extends Activity {
    private ArrayList<Book> books; private LinearLayout list;
    private static final int PICK=1001;
    @Override public void onCreate(Bundle b){super.onCreate(b); build();}
    private void build(){
        books=Library.load(this);
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(Color.WHITE);
        TextView bar=txt("轻阅",20,Color.BLACK); bar.setGravity(Gravity.CENTER_VERTICAL); bar.setPadding(20,0,12,0); root.addView(bar,new LinearLayout.LayoutParams(-1,60));
        Button add=new Button(this); add.setText("＋ 导入 TXT"); add.setOnClickListener(v->pick()); root.addView(add,new LinearLayout.LayoutParams(-1,52));
        ScrollView sv=new ScrollView(this); list=new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL); sv.addView(list); root.addView(sv,new LinearLayout.LayoutParams(-1,0,1)); setContentView(root); refresh();
    }
    private TextView txt(String s,float sp,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(sp);t.setTextColor(color);return t;}
    private void refresh(){list.removeAllViews(); if(books.isEmpty()){TextView e=txt("还没有小说\n点击上方按钮导入本地 TXT",18,Color.GRAY);e.setGravity(Gravity.CENTER);e.setPadding(30,120,30,30);list.addView(e);return;}
        for(Book book:books){ LinearLayout row=new LinearLayout(this);row.setPadding(18,18,12,18);row.setGravity(Gravity.CENTER_VERTICAL);row.setOrientation(LinearLayout.HORIZONTAL);
            TextView t=txt(book.title+"\n"+book.chapters.size()+" 章",18,Color.DKGRAY);row.addView(t,new LinearLayout.LayoutParams(0,76,1));
            Button del=new Button(this);del.setText("删除");del.setOnClickListener(v->{new AlertDialog.Builder(this).setTitle("删除小说？").setMessage(book.title).setPositiveButton("删除",(d,w)->{new File(getFilesDir(),book.fileName).delete();books.remove(book);Library.save(this,books);refresh();}).setNegativeButton("取消",null).show();});row.addView(del,new LinearLayout.LayoutParams(90,52));
            row.setOnClickListener(v->open(book));list.addView(row); View line=new View(this);line.setBackgroundColor(0xFFE5E5E5);list.addView(line,new LinearLayout.LayoutParams(-1,1)); }
    }
    private void pick(){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("text/plain");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,PICK);}
    @Override protected void onActivityResult(int r,int c,Intent d){super.onActivityResult(r,c,d);if(r!=PICK||c!=RESULT_OK||d==null)return;Uri u=d.getData();if(u==null)return;
        try{getContentResolver().takePersistableUriPermission(u,Intent.FLAG_GRANT_READ_URI_PERMISSION); }catch(Exception ignored){}
        try(InputStream in=getContentResolver().openInputStream(u)){String name=String.valueOf(u.getLastPathSegment()); if(name.contains("/"))name=name.substring(name.lastIndexOf('/')+1); if(!name.toLowerCase().endsWith(".txt"))name+=".txt"; String id=Long.toHexString(System.currentTimeMillis());File f=new File(getFilesDir(),id+".txt");try(OutputStream out=new FileOutputStream(f)){byte[] buf=new byte[8192];int n;while((n=in.read(buf))!=-1)out.write(buf,0,n);}String text=TxtParser.read(f);Book b=new Book(id, name.replaceFirst("(?i)\\.txt$",""),f.getName());b.chapters=TxtParser.chapters(text);books.add(0,b);Library.save(this,books);refresh();open(b);}catch(Exception e){new AlertDialog.Builder(this).setTitle("导入失败").setMessage(e.toString()).setPositiveButton("确定",null).show();}}
    private void open(Book b){startActivity(new Intent(this,ReaderActivity.class).putExtra("id",b.id));}
}
