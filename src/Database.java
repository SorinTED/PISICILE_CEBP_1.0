import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class Database{
    public static LinkedList<User> database = new LinkedList<>();;
    public static Semaphore sem_database_wr = new Semaphore(1);
    public static Semaphore sem_database_rd = new Semaphore(5);
    public static LinkedList<User> authUsers = new LinkedList<>();;
    public static Semaphore sem_authUsers_wr = new Semaphore(1);
    public static Semaphore sem_authUsers_rd = new Semaphore(5);

    static {
        initializeDatabase();
    }
    public static void initializeDatabase()
    {
        User admin = new User("admin","admin","ADMIN");
        try {
            sem_database_wr.acquire();
            database.add(admin);
            sem_database_wr.release();

            sem_authUsers_wr.acquire();
            authUsers.add(admin);
            sem_authUsers_wr.release();
        } catch (Exception e){

        }
    }

    public static boolean isAuthenticated(User user)
    {
        try{
            sem_authUsers_rd.acquire();
            if(authUsers.contains(user)) {
                sem_authUsers_rd.release();
                return true;
            }
            sem_authUsers_rd.release();
        } catch (Exception e){
            System.out.println(e);
        }
        return false;
    }

    public static boolean login(User user)
    {
        if(findUser(user) != null)
        {
            try{
                sem_authUsers_wr.acquire();
                if(!isAuthenticated(user))
                {
                    authUsers.add(user);
                }
                sem_authUsers_wr.release();
            }catch (Exception e) {
                System.out.println(e);
            }
            return true;
        }
        return false;
    }

    public static void logout(User user)
    {
        try{
            sem_authUsers_wr.acquire();
            if(isAuthenticated(user))
            {
                authUsers.remove(user);
            }
            sem_authUsers_wr.release();
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    public static User findUser(User user)
    {
        try{
            sem_database_rd.acquire();
            if(database.contains(user))
            {
                int index = database.indexOf(user);
                sem_database_rd.release();
                return database.get(index);
            }
            else{
                System.out.println("User " + user + " not found in DB");
                sem_database_rd.release();
            }
        }catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public static User findUserByUsername(String username)
    {
        try{
            sem_database_rd.acquire();
            for(User user : database) {
                if (user.getUsername().equals(username)) {
                    int index = database.indexOf(user);
                    sem_database_rd.release();
                    return database.get(index);
                }
            }
            System.out.println("User '" + username + "' not found in DB");
            sem_database_rd.release();
        }catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public static void addUser(User user)
    {
        try {
            if(findUserByUsername(user.getUsername()) != null) {
                System.out.println("User already signed up!");
                return;
            }

            sem_database_wr.acquire();
            database.add(user);
            sem_database_wr.release();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void deleteUser(String username)
    {
        User user = findUserByUsername(username);
        if(user == null) {
            System.out.println("User not found in DB");
            return;
        }
        try{
            sem_database_wr.acquire();
            database.remove(user);
            sem_database_wr.release();

            sem_authUsers_wr.acquire();
            authUsers.remove(user);
            sem_authUsers_wr.release();
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public static void seeUsers()
    {
        for(User user : database) {
            System.out.println(user);
        }
    }
}
