package ru.landmark.tnp.Structurs;

public class DopServiceOrder
{
    public int id;
    public String nameTariff;
    public float price;
    public boolean isCreate;

    public DopServiceOrder(int id,String nameTariff, float price, boolean isCreate)
    {
        this.id = id;
        this.nameTariff = nameTariff;
        this.price = price;
        this.isCreate = isCreate;
    }
}
