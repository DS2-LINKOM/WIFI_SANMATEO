package mx.linkom.wifi_sanmateo.adaptadores;

/**
 * Created by one on 19/8/16.
 */
public class ListasClassGridVisita {

    private String title;
    private String title2;
    private String subtitle;


    public ListasClassGridVisita(String title,String title2, String subtitle) {
        this.title = title;
        this.title2 = title2;
        this.subtitle = subtitle;
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

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

}
