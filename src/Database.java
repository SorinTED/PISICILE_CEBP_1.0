import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class Database{
    public static LinkedList<User> database = new LinkedList<>();;
    public static Semaphore sem_database_wr = new Semaphore(1);
    public static Semaphore sem_database_rd = new Semaphore(5);
    public static LinkedList<User> authUsers = new LinkedList<>();;
    public static Semaphore sem_authUsers_wr = new Semaphore(1);
    public static Semaphore sem_authUsers_rd = new Semaphore(5);

    public static boolean concurrency_enabled_for_write = true;
    public static Event_Bot_Thread event_bot = new Event_Bot_Thread("Database",2000);

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
            System.out.println(e);
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
        boolean notifications = false;
        User userToLogin = findUser(user, notifications);
        if(userToLogin != null) {
            if (!userToLogin.isLoggedIn()) {
                try {
                    sem_authUsers_wr.acquire();
                    if (!isAuthenticated(userToLogin)) {
                        userToLogin.setLoggedIn(true);
                        authUsers.add(userToLogin);
                    }
                    sem_authUsers_wr.release();
                } catch (Exception e) {
                    System.out.println(e);
                }
                return true;
            }
            else {
                System.out.println("User " + user + " is already logged in!");
                return false;
            }
        }
        return false;
    }

    public static void logout(User user)
    {
        try{
            sem_authUsers_wr.acquire();
            if(isAuthenticated(user))
            {
                user.setLoggedIn(false);
                authUsers.remove(user);
            }
            sem_authUsers_wr.release();
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    public static boolean isAdmin(User user)
    {
        return user.getRole().equals("ADMIN");
    }

    public static User findUser(User user, boolean notifications)
    {
        if(concurrency_enabled_for_write) {
            try {
                sem_database_rd.acquire();
                if (database.contains(user)) {
                    int index = database.indexOf(user);
                    sem_database_rd.release();
                    return database.get(index);
                } else {
                    if (notifications) {
                        System.out.println("User " + user + " not found in DB");
                    }
                    sem_database_rd.release();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            return null;
        }
        else
        {
            if(database.contains(user)) {
                int index = database.indexOf(user);
                return database.get(index);
            }
            return null;
        }
    }

    public static User findUserByUsername(String username, boolean notifications)
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
            if(notifications) {
                System.out.println("Username '" + username + "' not found in DB");
            }
            sem_database_rd.release();
        }catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public static User findUserBySender(String sender, boolean verbose)
    {
        boolean notifications = false;
        String username = sender.substring(1, sender.length() - 1);
        return findUserByUsername(username, notifications);
    }

    public static boolean addUser(User user)
    {
        boolean notifications = false;
        if(concurrency_enabled_for_write) {
            try {
                if (findUser(user, notifications) != null) {
                    System.out.println("User " + user + " already signed up!");
                    return false;
                }

                sem_database_wr.acquire();
                database.add(user);
                System.out.println("User " + user + " created!");
                sem_database_wr.release();

            } catch (Exception e) {
                System.out.println(e);
            }
        }
        else
        {
            if (findUser(user, notifications) != null) {
                System.out.println("User " + user + " already signed up!");
                return false;
            }
            database.add(user);
            System.out.println("User " + user + " created!");
        }
        return true;
    }

    public static boolean deleteUser(String username)
    {
        boolean notifications = true;
        User user = findUserByUsername(username, notifications);
        if(user == null) {
            System.out.println("User not found in DB!");
            return false;
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
        return true;
    }

    public static void seeUsers()
    {
        for(User user : database) {
            System.out.println(user);
        }
    }
}
