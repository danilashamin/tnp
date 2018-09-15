package ru.landmark.tnp.Structurs;

public class UsersPersonal
{
    public String NameAndSurname;
    public String Login;
    public boolean IsAdmin;

    public UsersPersonal(String NameAndSurname, String Login, String IsAdmin)
    {
        this.NameAndSurname = NameAndSurname;
        this.Login = Login;
        this.IsAdmin = Boolean.parseBoolean(IsAdmin);
    }

    public UsersPersonal(String NameAndSurname, String Login, boolean IsAdmin)
    {
        this.NameAndSurname = NameAndSurname;
        this.Login = Login;
        this.IsAdmin = IsAdmin;
    }
}
