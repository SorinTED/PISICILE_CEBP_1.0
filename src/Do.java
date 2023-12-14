import javax.xml.crypto.Data;

public class Do {
    private String[] args;
    public Do(String[] args)
    {
        this.args=args;
    }
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static User execute_login(String sender, String[] args){
        //login
        if(args.length == 3 && args[0].equals("login")) {
            System.out.println("Logging in ... ");
            String username = args[1];
            String password = args[2];
            User userToLogin = new User(username, password);
            if(Database.findUser(userToLogin) != null) {
                if(Database.login(userToLogin)) {
                    System.out.println("User " + userToLogin + " Logged in successfully !");
                    return userToLogin;
                }
                else{
                    System.out.println("Bad credentials for: '" + username + "'");
                }
            }

        }
        return null;
    }

    public static void execute(String sender, String[] args)
    {
        //logout
        if(args.length == 1 && args[0].equals("logout"))
        {
            String username = sender.substring(1, sender.length() - 1);
            User user = Database.findUserByUsername(username);
            Database.logout(user);
            System.out.println("User " + user + " Logged Out");
        }

        //send to user message
        else if(args.length >= 3 && args[0].equals("send") && Receiver_queue.all_receivers.contains(args[1]))
        {
            Receiver_queue user_queue = Receiver_queue.find_queue_for(args[1]);
            if(user_queue.space_left_in_queue())
                user_queue.write(user_queue, sender, args);
            else
                System.out.println("Queue for messages to " + args[1] + " is full");
        }
        //post to topic message
        else if(args.length >= 6 && args[0].equals("add") && args[1].equals("to")
                && Topic.all_topics_name.contains(args[2]) && args[3].equals("for")
                && (isNumeric(args[4].substring(0, args[4].length() - 1)))
                && (args[4].charAt(args[4].length()-1) == 'h'
                || args[4].charAt(args[4].length()-1) == 'H'))
        {
            //System.out.println("Adding to topic ... ");
            Topic topic = Topic.find_topic(args[2]);
            if (topic.space_left_in_queue())
                topic.write(sender, args);
            else
                System.out.println("Queue for topic " + topic.topic_name + " is full");
        }
        //admin set server timeout
        else if(args.length == 5 && args[0].equals("admin") && args[1].equals("set")
                && args[2].equals("server") && args[3].equals("timeout")
                && isNumeric(args[4].substring(0, args[4].length() - 1))
                && (args[4].charAt(args[4].length()-1) == 'h'
                || args[4].charAt(args[4].length()-1) == 'H'))
        {
            System.out.println("Setting server timeout to " + Double.valueOf(args[4].substring(0, args[4].length() - 1)));
            Topic.setServer_timeout(Double.valueOf(args[4].substring(0, args[4].length() - 1)));
        }
        //admin set max posts to topic
        else if(args.length == 6 && args[0].equals("admin") && args[1].equals("set")
                && Topic.all_topics_name.contains(args[2]) && args[3].equals("max")
                && (isNumeric(args[4])) && args[5].equals("posts"))
        {
            Topic topic = Topic.find_topic(args[2]);
            System.out.println("Setting max posts to topic " + topic.topic_name + " to " + Integer.valueOf(args[4]));
            topic.setMax_posts(Integer.valueOf(args[4]));
        }
        //admin set max messages for user queue
        else if(args.length == 6 && args[0].equals("admin") && args[1].equals("set")
                && Receiver_queue.all_receivers.contains(args[2]) && args[3].equals("max")
                && (isNumeric(args[4])) && args[5].equals("messages"))
        {
            Receiver_queue queue = Receiver_queue.find_queue_for(args[2]);
            System.out.println("Setting max messages to " + queue.receiver_name + " to " + Integer.valueOf(args[4]));
            queue.setMax_messages(Integer.valueOf(args[4]));
        }
        //admin empty topic
        else if(args.length == 4 && args[0].equals("admin") && args[1].equals("empty")
                && args[2].equals("topic") && Topic.all_topics_name.contains(args[3]))
        {
            System.out.println("Emptying topic " + args[3]);
            Topic topic = Topic.find_topic(args[3]);
            topic.empty_topic();
        }
        //admin empty user messages
        else if(args.length == 5 && args[0].equals("admin") && args[1].equals("empty") && args[2].equals("user")
                && Receiver_queue.all_receivers.contains(args[3]) && args[4].equals("messages"))
        {
            System.out.println("Emptying user " + args[3] + " messages");
            Receiver_queue queue = Receiver_queue.find_queue_for(args[3]);
            queue.empty_queue();
        }
        //admin see all users
        else if(args.length == 4 && args[0].equals("admin") && args[1].equals("see") && args[2].equals("all")
                && args[3].equals("users"))
        {
            Database.seeUsers();
            System.out.println(Receiver_queue.list_of_users());
        }
        //admin see all topics
        else if(args.length == 4 && args[0].equals("admin") && args[1].equals("see") && args[2].equals("all")
                && args[3].equals("topics"))
        {
            System.out.println(Topic.list_of_topics());
        }
        //admin create user
        else if(args.length == 5 && args[0].equals("admin") && args[1].equals("create") && args[2].equals("user")
                && args[3].length()!=0 && args[4].length()!=0)
        {
            String username = args[3];
            String password = args[4];
            User userAdded = new User(username, password);
            Database.addUser(userAdded);
            Receiver_queue user = new Receiver_queue(username);
            System.out.println("User '" + username + "' created");
        }
        //admin create topic
        else if(args.length == 4 && args[0].equals("admin") && args[1].equals("create") && args[2].equals("topic"))
        {
            System.out.println("Topic " + args[3] + " created");
            Topic topic = new Topic(args[3]);
        }
        //admin delete topic
        else if(args.length == 4 && args[0].equals("admin") && args[1].equals("delete") && args[2].equals("topic")
                && Topic.all_topics_name.contains(args[3]))
        {
            System.out.println("Topic " + args[3] + " deleted");
            Topic topic = Topic.find_topic(args[3]);
            topic.delete_topic();
            topic=null;
        }
        //admin delete user
        else if(args.length == 4 && args[0].equals("admin") && args[1].equals("delete") && args[2].equals("user"))
        {
            String username = args[3];
            Database.deleteUser(username);
            Database.seeUsers();
            Receiver_queue queue = Receiver_queue.find_queue_for(username);
            queue.delete_user();
            queue = null;
            System.out.println("User " + username + " deleted");
        }
        else
        {
            System.out.println("Command error, maybe a typo or invalid user/topic?");
        }
    }

}
