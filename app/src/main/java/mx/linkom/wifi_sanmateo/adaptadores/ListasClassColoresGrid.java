package mx.linkom.wifi_sanmateo.adaptadores;

/**
 * Created by one on 19/8/16.
 */
public class ListasClassColoresGrid {

    private String title;
    private String title2;
    private String subtitle;
    private String colorCode;


    public ListasClassColoresGrid(String title, String title2, String subtitle, String colorCode) {
        this.title = title;
        this.title2 = title2;
        this.subtitle = subtitle;
        this.colorCode = colorCode;

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title) {
        this.title2 = title;
    }


    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

}
