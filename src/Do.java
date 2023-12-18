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

    public static void execute(String sender, String[] args)
    {
        //login
        if(args.length == 3 && args[0].equals("login")) {
            System.out.println("Logging in ... ");
            User userToLogin = new User(args[1], args[2]);
            Database loginDatabase = new Database();
            User loggedInUser = Database.login(userToLogin);
            try {
                loginDatabase.sem_login_database.acquire();
                if (loggedInUser != null)
                    System.out.println("User " + loggedInUser + " Logged in successfully !");
                else
                    System.out.println("Bad credentials for: " + userToLogin);
                loginDatabase.sem_login_database.release();
            } catch (Exception exc) {
                System.out.println(exc);
            }
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
            System.out.println(Receiver_queue.list_of_users());
        }
        //admin see all topics
        else if(args.length == 4 && args[0].equals("admin") && args[1].equals("see") && args[2].equals("all")
                && args[3].equals("topics"))
        {
            System.out.println(Topic.list_of_topics());
        }
        //admin create user
        else if(args.length == 4 && args[0].equals("admin") && args[1].equals("create") && args[2].equals("user"))
        {
            System.out.println("User " + args[3] + " created");
            Receiver_queue user = new Receiver_queue(args[3]);
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
        else if(args.length == 4 && args[0].equals("admin") && args[1].equals("delete") && args[2].equals("user")
                && Receiver_queue.all_receivers.contains(args[3]))
        {
            System.out.println("User " + args[3] + " deleted");
            Receiver_queue queue = Receiver_queue.find_queue_for(args[3]);
            queue.delete_user();
            queue=null;
        }
        //read messages in queue for user from sender
        else if(args.length == 4 && args[0].equals("read") && args[1].equals("messages") && args[2].equals("for")
                && Receiver_queue.all_receivers.contains(args[3]))
        {
            System.out.println("Show messages for " + args[3] + " from " + sender); // add the sender
            Receiver_queue user_queue = Receiver_queue.find_queue_for(args[3]);
            user_queue.read_messages_from_queue(user_queue,args[3],sender,false);
        }
        //read all messages in queue for user
        else if(args.length == 4 && args[0].equals("read") && args[1].equals("messages") && args[2].equals("for")
                && Receiver_queue.all_receivers.contains(args[3]) && sender.equals("all"))
        {
            System.out.println("Show all messages for " + args[3] ); // add the sender
            Receiver_queue user_queue = Receiver_queue.find_queue_for(args[3]);
            user_queue.read_messages_from_queue(user_queue,args[3],sender,false);
        }
        //admin see all messages in queue for use
        else if(args.length == 5 && args[0].equals("admin") && args[1].equals("see") && args[2].equals("messages")
                && Receiver_queue.all_receivers.contains(args[4]))
        {

        }
        //read all messages in topic from author(sender)
        else if(args.length == 2 && args[0].equals("read") && args[1].equals("topics") )
        {

        }
        else
        {
            System.out.println("Command error, maybe a typo or invalid user/topic?");
        }

    }

}
