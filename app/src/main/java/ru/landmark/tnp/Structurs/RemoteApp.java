package ru.landmark.tnp.Structurs;

public class RemoteApp
{
    public String Name;
    public String Id;
    public int idBD;

    public RemoteApp(int idBD,String Name, String Id)
    {
        this.idBD = idBD;
        this.Name = Name;
        this.Id = Id;
    }
}
