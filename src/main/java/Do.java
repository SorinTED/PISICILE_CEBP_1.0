import java.nio.charset.StandardCharsets;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

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
                    
                    //--------- RabbitMQ playground --------- 
                    ConnectionFactory factory = new ConnectionFactory();
                    factory.setHost("localhost");
                    try (Connection connection = factory.newConnection();
                        Channel channel = connection.createChannel()) {
                        channel.queueDeclare("general", false, false, false, null);
                        String message = args[1] + " logged in";
                        channel.basicPublish("", "general", null, message.getBytes(StandardCharsets.UTF_8));
                        //System.out.println("RabbitMQ: " + " Sent '" + message);

                        //--------------------------
                        // channel.queueDeclare(args[1], false, false, false, null);
                        // System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

                        // DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                        //     String mm = new String(delivery.getBody(), StandardCharsets.UTF_8);
                        //     System.out.println("[" + args[1] + "] Received '" + mm + "'");
                        // };
                        // channel.basicConsume(args[1], true, deliverCallback, consumerTag -> { });

                    } catch (Exception e) {}
                    
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
            User user = Database.findUserBySender(sender);
            Database.logout(user);
            System.out.println("User " + user + " Logged Out");

            //--------- RabbitMQ playground ---------
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {
                channel.queueDeclare("general", false, false, false, null);
                String message = user + " logged off";
                channel.basicPublish("", "general", null, message.getBytes(StandardCharsets.UTF_8));
                //System.out.println("RabbitMQ: " + " Sent '" + message);

            } catch (Exception e) {

            }
        }

        //send to user message
        else if(args.length >= 3 && args[0].equals("send") && Receiver_queue.find_queue_for(args[1]) != null)
        {
            Receiver_queue user_queue = Receiver_queue.find_queue_for(args[1]);
            if(user_queue.space_left_in_queue())
                user_queue.write(user_queue, sender, args);
            else
                System.out.println("Queue for messages to " + args[1] + " is full");
        }
        //post to topic message
        else if(args.length >= 6 && args[0].equals("add") && args[1].equals("to")
                && Topic.find_topic(args[2]) != null && args[3].equals("for")
                && (isNumeric(args[4].substring(0, args[4].length() - 1)))
                && (args[4].charAt(args[4].length()-1) == 'h'
                || args[4].charAt(args[4].length()-1) == 'H'))
        {
            //System.out.println("Adding to topic ... ");
            Topic topic = Topic.find_topic(args[2]);
            if (topic.space_left_in_queue())
                topic.write(topic, sender, args);
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
            User senderUser = Database.findUserBySender(sender);
            if (Database.isAdmin(senderUser))
            {
                System.out.println("Setting server timeout to " + Double.valueOf(args[4].substring(0, args[4].length() - 1)));
                Topic.setServer_timeout(Double.valueOf(args[4].substring(0, args[4].length() - 1)));
            } else {
                System.out.println("You don't have this permission!");
            }
        }
        //admin set max posts to topic
        else if(args.length == 6 && args[0].equals("admin") && args[1].equals("set")
                && Topic.find_topic(args[2]) != null && args[3].equals("max")
                && (isNumeric(args[4])) && args[5].equals("posts"))
        {
            User senderUser = Database.findUserBySender(sender);
            if(Database.isAdmin(senderUser))
            {
                Topic topic = Topic.find_topic(args[2]);
                System.out.println("Setting max posts to topic " + topic.topic_name + " to " + Integer.valueOf(args[4]));
                topic.setMax_posts(Integer.valueOf(args[4]));
            }
            else {
                System.out.println("You don't have this permission!");
            }

        }
        //admin set max messages for user queue
        else if(args.length == 6 && args[0].equals("admin") && args[1].equals("set")
                && Receiver_queue.find_queue_for(args[2]) != null && args[3].equals("max")
                && (isNumeric(args[4])) && args[5].equals("messages"))
        {
            User senderUser = Database.findUserBySender(sender);
            if(Database.isAdmin(senderUser))
            {
                Receiver_queue queue = Receiver_queue.find_queue_for(args[2]);
                System.out.println("Setting max messages to " + queue.receiver_name + " to " + Integer.valueOf(args[4]));
                queue.setMax_messages(Integer.valueOf(args[4]));
            }
            else {
                System.out.println("You don't have this permission!");
            }
        }
        //admin empty topic
        else if(args.length == 4 && args[0].equals("admin") && args[1].equals("empty")
                && args[2].equals("topic") && Topic.find_topic(args[3]) != null)
        {
            User senderUser = Database.findUserBySender(sender);
            if(Database.isAdmin(senderUser))
            {
                System.out.println("Emptying topic " + args[3]);
                Topic topic = Topic.find_topic(args[3]);
                topic.empty_topic();
            }
            else {
                System.out.println("You don't have this permission!");
            }
        }
        //admin empty user messages
        else if(args.length == 5 && args[0].equals("admin") && args[1].equals("empty") && args[2].equals("user")
                && Receiver_queue.find_queue_for(args[3]) != null && args[4].equals("messages"))
        {
            User senderUser = Database.findUserBySender(sender);
            if(Database.isAdmin(senderUser))
            {
                System.out.println("Emptying user " + args[3] + " messages");
                Receiver_queue queue = Receiver_queue.find_queue_for(args[3]);
                queue.empty_queue();
            }
            else {
                System.out.println("You don't have this permission!");
            }
        }
        //admin see all users
        else if(args.length == 4 && args[0].equals("admin") && args[1].equals("see") && args[2].equals("all")
                && args[3].equals("users"))
        {
            User senderUser = Database.findUserBySender(sender);
            if(Database.isAdmin(senderUser))
            {
                Database.seeUsers();
                System.out.println(Receiver_queue.list_of_users());
            }
            else {
                System.out.println("You don't have this permission!");
            }
        }
        //admin see all topics
        else if(args.length == 4 && args[0].equals("admin") && args[1].equals("see") && args[2].equals("all")
                && args[3].equals("topics"))
        {
            User senderUser = Database.findUserBySender(sender);
            if(Database.isAdmin(senderUser))
            {
                System.out.println(Topic.list_of_topics());
            }
            else {
                System.out.println("You don't have this permission!");
            }
        }
        //admin create user
        else if(args.length == 5 && args[0].equals("admin") && args[1].equals("create") && args[2].equals("user")
                && args[3].length()!=0 && args[4].length()!=0)
        {
            User senderUser = Database.findUserBySender(sender);
            if(Database.isAdmin(senderUser))
            {
                String username = args[3];
                String password = args[4];
                User userAdded = new User(username, password);
                Database.addUser(userAdded);
                Receiver_queue user = new Receiver_queue(username);
                System.out.println("User '" + username + "' created");
            }
            else {
                System.out.println("You don't have this permission!");
            }
        }
        //admin create topic
        else if(args.length == 4 && args[0].equals("admin") && args[1].equals("create") && args[2].equals("topic"))
        {
            User senderUser = Database.findUserBySender(sender);
            if(Database.isAdmin(senderUser))
            {
                System.out.println("Topic " + args[3] + " created");
                Topic topic = new Topic(args[3]);
            }
            else {
                System.out.println("You don't have this permission!");
            }
        }
        //admin delete topic
        else if(args.length == 4 && args[0].equals("admin") && args[1].equals("delete") && args[2].equals("topic")
                && Topic.find_topic(args[3]) != null)
        {
            User senderUser = Database.findUserBySender(sender);
            if(Database.isAdmin(senderUser))
            {
                System.out.println("Topic " + args[3] + " deleted");
                Topic topic = Topic.find_topic(args[3]);
                topic.delete_topic();
                topic=null;
            }
            else {
                System.out.println("You don't have this permission!");
            }
        }
        //admin delete user
        else if(args.length == 4 && args[0].equals("admin") && args[1].equals("delete") && args[2].equals("user"))
        {
            User senderUser = Database.findUserBySender(sender);
            if(Database.isAdmin(senderUser))
            {
                String username = args[3];
                Database.deleteUser(username);
                Database.seeUsers();
                Receiver_queue queue = Receiver_queue.find_queue_for(username);
                if (queue != null)
                {
                    queue.delete_user();
                }
                System.out.println("User " + username + " deleted");
            }
            else {
                System.out.println("You don't have this permission!");
            }
        }
        else
        {
            System.out.println("Command error, maybe a typo or invalid user/topic?");
        }
    }

}
