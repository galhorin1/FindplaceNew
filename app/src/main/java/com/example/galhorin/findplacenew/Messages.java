package com.example.galhorin.findplacenew;



/**
 * Created by galhorin on 1/26/2016.
 */
public class Messages {

    String description,nickName;

    public Messages() {
        this.description = "";
        this.nickName = "";
    }
    public Messages(Messages ms) {
        this.description = ms.getDescription();
        this.nickName = ms.getNickname();
    }

    public Messages(String description, String nickname ) {
        this.description = description;
        this.nickName = nickname;
    }

    public String getNickname() {
        return nickName;
    }

    public void setNickname(String nickname) {
        this.nickName = nickname;
    }

    public String getDescription() {

        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
