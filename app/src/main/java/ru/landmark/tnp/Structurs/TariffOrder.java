package ru.landmark.tnp.Structurs;

public class TariffOrder
{
    public String nameTariff;
    public int countCart;
    public boolean isCreate;

    public TariffOrder(String nameTariff, int countCart, boolean isCreate)
    {
        this.nameTariff = nameTariff;
        this.countCart = countCart;
        this.isCreate = isCreate;
    }
}
