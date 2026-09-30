package com.bonada.lightnovel;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;

public final class TxtParser {
    private TxtParser() {}
    public static String read(File f) throws IOException {
        byte[] b=readAll(f); int off=0; Charset cs=StandardCharsets.UTF_8;
        if(b.length>=3 && (b[0]&255)==0xEF && (b[1]&255)==0xBB && (b[2]&255)==0xBF) off=3;
        else if(b.length>=2 && (b[0]&255)==0xFF && (b[1]&255)==0xFE){cs=Charset.forName("UTF-16LE");off=2;}
        else if(b.length>=2 && (b[0]&255)==0xFE && (b[1]&255)==0xFF){cs=Charset.forName("UTF-16BE");off=2;}
        String s=new String(b,off,b.length-off,cs);
        int bad=0; for(int i=0;i<s.length();i++) if(s.charAt(i)=='\uFFFD') bad++;
        if(bad>3) s=new String(b,off,b.length-off,Charset.forName("GB18030"));
        return s.replace("\r\n","\n").replace('\r','\n');
    }
    private static byte[] readAll(File f)throws IOException{
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        try(InputStream in=new BufferedInputStream(new FileInputStream(f))){byte[] buf=new byte[8192];int n;while((n=in.read(buf))!=-1)out.write(buf,0,n);}
        return out.toByteArray();
    }
    private static final Pattern CH=Pattern.compile("^\\s*(?:(第\\s*[0-9零一二三四五六七八九十百千万两〇]+\\s*[章节卷部篇回集]|(?:序章|楔子|引子|番外|尾声|后记|终章|正文)|Chapter\\s+\\d+|CHAPTER\\s+\\d+).*)\\s*$",Pattern.CASE_INSENSITIVE);
    public static ArrayList<Book.Chapter> chapters(String text){
        ArrayList<Book.Chapter> list=new ArrayList<>();
        String[] lines=text.split("\\n",-1); int pos=0; String title=null,start=null; int startPos=0;
        for(String line:lines){
            Matcher m=CH.matcher(line.trim());
            if(m.matches() && line.trim().length()<=80){
                if(title!=null) list.add(new Book.Chapter(title,startPos,pos));
                title=line.trim(); startPos=pos;
            }
            pos += line.length()+1;
        }
        if(title!=null) list.add(new Book.Chapter(title,startPos,text.length()));
        if(list.isEmpty()) list.add(new Book.Chapter("全文",0,text.length()));
        return list;
    }
}
