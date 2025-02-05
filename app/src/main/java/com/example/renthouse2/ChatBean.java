package com.example.renthouse2;

import android.graphics.drawable.Drawable;

public class ChatBean {
    String content;
    Drawable drawable;//可以透過這個來修改頭像
    int type;//用於標識告知Adpater應該建立的是哪一類的ItenView
    private String name;
    private String time;

    public ChatBean(String content, Drawable drawable, int type,String _name,String _time) {
        this.content = content;
        this.drawable = drawable;
        this.type = type;
        this.name = _name;
        this.time = _time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }
}
