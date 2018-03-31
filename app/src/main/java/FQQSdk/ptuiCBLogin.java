package FQQSdk;

import java.security.spec.ECField;

public class ptuiCBLogin {
    private String url;
    private String nick;
    private String data;

    public ptuiCBLogin(String data) throws Exception {
        this.data = data;
        if(data==null||data.length()<8)
        {
            throw new Exception("数据错误");
        }
        String[] haha = data.substring(7).split(",");
        url = haha[2];
        nick = haha[5];


    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
