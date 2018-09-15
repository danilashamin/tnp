package ru.landmark.tnp.Structurs;

public class IntervalTime
{
    public String startTime;
    public String endTime;

    public float price;

    public boolean pn;
    public boolean vt;
    public boolean sr;
    public boolean ct;
    public boolean pt;
    public boolean sub;
    public boolean vosk;

    public boolean isCreate;

    public IntervalTime(String startTime, String endTime, float price,
                        boolean pn, boolean vt, boolean sr, boolean ct, boolean pt, boolean sub, boolean vosk, boolean isCreate)
    {
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.pn = pn;
        this.vt = vt;
        this.sr = sr;
        this.ct = ct;
        this.pt = pt;
        this.sub = sub;
        this.vosk = vosk;
        this.isCreate = isCreate;
    }
}
