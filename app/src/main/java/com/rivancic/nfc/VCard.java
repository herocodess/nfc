package com.rivancic.nfc;

/**
 * Class that is responsible for creating vCard format from the provided data.
 * https://en.wikipedia.org/wiki/VCard
 * Created by rivancic on 22/11/15.
 */
public class VCard {

    private static final String NAME = "N";
    private static final String BEGIN_VCARD = "BEGIN:VCARD";
    private static final String VERSION = "VERSION:4.0";
    private static final String END_VCARD = "END:VCARD";
    private static final String URL = "URL";
    private static final String PHOTO = "PHOTO";
    private static final String TITLE = "TITLE";
    private static final String EMAIL = "EMAIL";

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String lastName = "";
    private String firstName = "";
    private String email = "";
    private String url = "";
    private String title = "";

    /**
     * @return vCard String representation.
     */
    public String builVCardNfcMessage() {

        String result = "";
        String beginVcard = BEGIN_VCARD + "\n";
        String versionVcard = VERSION + "\n";
        String nameVcard = String.format(NAME + ":%s;%s\n", lastName, firstName);
        String emailVcard = String.format(EMAIL + ":%s\n", email);
        String titleVcard = String.format(TITLE + ":%s\n", title);
        String base64ImageData = "";
        String photoVcard = String.format(PHOTO + ":data:image/jpeg;base64,%s\n", base64ImageData);
        String urlVcard = String.format(URL + ":%s\n", url);
        String endVcard = END_VCARD;

        result = beginVcard +
                versionVcard +
                nameVcard +
                emailVcard +
                titleVcard +
                urlVcard +
                endVcard;

        return result;
    }
}
