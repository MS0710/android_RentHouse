package com.example.renthouse2;

public class ChatRoom {

    private String name;
    private String msg;
    public ChatRoom(String _name,String _msg){
        this.name = _name;
        this.msg = _msg;
    }

    public String getName() {
        return name;
    }

    public String getMsg() {
        return msg;
    }
}
