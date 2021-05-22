package util;

import android.app.Application;

public class LoginAPI extends Application {

    private String userPhone, userCode,userName,imgUrl;

    private static LoginAPI instance;

    public static LoginAPI getInstance() {
        if (instance == null)
            instance = new LoginAPI();
        return instance;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public LoginAPI() {

    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }



    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

}
