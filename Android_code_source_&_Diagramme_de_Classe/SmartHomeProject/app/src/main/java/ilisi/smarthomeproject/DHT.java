package ilisi.smarthomeproject;

/**
 * Created by EL-HARROUGUI on 08/02/2017.
 */

public class DHT {
    private String Temperature;
    private String Humidity;
    private String EtatCapteur;

    public DHT(String EC, String T, String H)
    {
        EtatCapteur = new String(EC);
        Temperature = new String(T);
        Humidity = new String(H);
    }

    public String toString()
    {
        return("SALAM");
    }

    public void setEtatCapteur(String EC)
    {
        EtatCapteur = EC;
    }

    public void setTemperature(String T)
    {
        Temperature = T;
    }

    public void setHumidity(String H)
    {
        Humidity = H;
    }

    public String getEtatCapteur()
    {
        return EtatCapteur;
    }

    public String getTemperature()
    {
        return Temperature;
    }

    public String getHumidity()
    {
        return Humidity;
    }
}
