package com.fyp.communicationmodule;

public class GIF {
    private String engCaption, malayCaption, gifPicture;

    public GIF() {
    }

    public GIF(String engCaption, String malayCaption, String gifPicture) {
        this.engCaption = engCaption;
        this.malayCaption = malayCaption;
        this.gifPicture = gifPicture;
    }

    public String getEngCaption() {
        return engCaption;
    }

    public void setEngCaption(String engCaption) {
        this.engCaption = engCaption;
    }

    public String getMalayCaption() {
        return malayCaption;
    }

    public void setMalayCaption(String malayCaption) {
        this.malayCaption = malayCaption;
    }

    public String getGifPicture() {
        return gifPicture;
    }

    public void setGifPicture(String gifPicture) {
        this.gifPicture = gifPicture;
    }
}
