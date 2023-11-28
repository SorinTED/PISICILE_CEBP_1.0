import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class Database {
    public static LinkedList<User> database = new LinkedList<>();
    public Semaphore sem_login_database = new Semaphore(1);

    public static User login(User user)
    {
        for(User userInDB : database)
            if(userInDB.equals(user))
                return userInDB;
        return null;
    }

    public void addUser(User user)
    {
        database.add(user);
    }
}
