package com.example.renthouse2;

public class UploadHouse {
    private String picture;
    private String picture2;
    private String picture3;
    private String title;
    private String tag1;
    private String tag2;
    private String tag3;
    private String note;
    private String price;
    private String pattern;
    private String meters;
    private String floor;
    private String type;
    private String introduce;
    private String uploadUser;
    public UploadHouse(String _picture,String _picture2,String _picture3,String _title,String _tag1,String _tag2,String _tag3,String _note,
                  String _price,String _pattern,String _meters,String _floor,String _type,
                       String _introduce, String _uploadUser){
        this.picture = _picture;
        this.picture2 = _picture2;
        this.picture3 = _picture3;
        this.title = _title;
        this.tag1 = _tag1;
        this.tag2 = _tag2;
        this.tag3 = _tag3;
        this.note = _note;
        this.price = _price;
        this.pattern = _pattern;
        this.meters = _meters;
        this.floor = _floor;
        this.type = _type;
        this.introduce = _introduce;
        this.uploadUser = _uploadUser;
    }

    public String getPicture() {
        return picture;
    }

    public String getPicture2() {
        return picture2;
    }

    public String getPicture3() {
        return picture3;
    }

    public String getTitle() {
        return title;
    }

    public String getTag1() {
        return tag1;
    }

    public String getTag2() {
        return tag2;
    }

    public String getTag3() {
        return tag3;
    }

    public String getNote() {
        return note;
    }

    public String getPrice() {
        return price;
    }

    public String getPattern() {
        return pattern;
    }

    public String getFloor() {
        return floor;
    }

    public String getIntroduce() {
        return introduce;
    }

    public String getMeters() {
        return meters;
    }

    public String getType() {
        return type;
    }

    public String getUploadUser() {
        return uploadUser;
    }
}
