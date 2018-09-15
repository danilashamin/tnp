package ru.landmark.tnp.Structurs;

/**
 * Created by gameb on 28.03.2017.
 */

public class TariffMain
{
    public String name;
    public int freeCarts;
    public int busyCarts;

    public TariffMain (String name, int freeCarts, int busyCarts)
    {
        this.name = name;
        this.freeCarts = freeCarts;
        this.busyCarts = busyCarts;
    }
}
