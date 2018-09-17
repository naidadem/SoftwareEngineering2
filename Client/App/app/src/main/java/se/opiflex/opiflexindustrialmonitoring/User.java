package se.opiflex.opiflexindustrialmonitoring;

/* This class is not currently used, login is handled
 * by the Firebase server and LoginActivity */

public class User
{
    private int companyId;
    private int permissionLevel;
    private String username;
    private String password;

    public User(int companyId, int permissionLevel, String username, String password)
    {
        this.companyId = companyId;
        this.permissionLevel = permissionLevel;
        this.username = username;
        this.password = password;
    }

    public void login(String username, String password)
    {

    }
}
