package com.example.renthouse2;

public class ChatMsg {
    private String userName;//使用者名稱
    private String otherSide;//對方名稱
    private String message;//訊息內容
    private String time;

    public ChatMsg(String _userName,String _otherSide,String _message,String _time){
        this.userName = _userName;
        this.otherSide = _otherSide;
        this.message = _message;
        this.time = _time;
    }

    public String getMessage() {
        return message;
    }

    public String getOtherSide() {
        return otherSide;
    }

    public String getUserName() {
        return userName;
    }

    public String getTime() {
        return time;
    }
}
