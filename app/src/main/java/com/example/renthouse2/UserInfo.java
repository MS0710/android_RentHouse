package com.example.renthouse2;

public class UserInfo {
    private String account;
    private String password;
    private String name;
    private String picture;
    public UserInfo(String _account,String _password,String _name,String _picture){
        this.account = _account;
        this.password = _password;
        this.name = _name;
        this.picture = _picture;
    }

    public String getName() {
        return name;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public String getPicture() {
        return picture;
    }
}
