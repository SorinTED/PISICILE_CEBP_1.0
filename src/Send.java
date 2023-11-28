public class Send {
    private String[] args;
    public Send(String[] args)
    {
        this.args = args;
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

    public static void send_to(String sender, String[] args)
    {
        if(args.length >= 3)
        {
            if(args[0].equals("login"))
            {
                System.out.println("Logging in ... ");
                User userToLogin = new User(args[1], args[2]);
                Database loginDatabase = new Database();
                User loggedInUser = Database.login(userToLogin);
                try{
                    loginDatabase.sem_login_database.acquire();
                    if(loggedInUser != null)
                        System.out.println("User " + loggedInUser + " Logged in successfully !");
                    else
                        System.out.println("Bad credentials for: " + userToLogin);
                    loginDatabase.sem_login_database.release();
                } catch (Exception exc)
                {
                    System.out.println(exc);
                }

            }
            else if(args[0].equals("send") && Receiver_queue.all_receivers.contains(args[1]))
            {
                System.out.println("Sending message ... ");
                Receiver_queue user_queue = Receiver_queue.find_queue_for(args[1]);
                try {
                    user_queue.sem_receiver_queue.acquire();
                    user_queue.write(user_queue, sender, args);
                    System.out.println(user_queue.receiver_name + ": " + user_queue.message_queue);
                    user_queue.sem_receiver_queue.release();
                } catch (Exception exc)
                {
                    System.out.println(exc);
                }
            }
            else if(args.length >= 6)
            {
                if(args[0].equals("add") && args[1].equals("to")
                        && Topic.all_topics_name.contains(args[2]) && args[3].equals("for")
                        && (isNumeric(args[4].substring(0, args[4].length() - 1)))
                        && (args[4].charAt(args[4].length()-1) == 'h'
                        || args[4].charAt(args[4].length()-1) == 'H'))
                {
                    System.out.println("Adding to topic ... ");
                    Topic topic = Topic.find_topic(args[2]);
                    try {
                        topic.sem_topic_queue.acquire();
                        topic.write(sender, args);
                        System.out.println(topic.topic_name + ": " + topic.topic_queue);
                        topic.sem_topic_queue.release();
                    } catch (Exception exc)
                    {
                        System.out.println(exc);
                    }
                }
                else
                {
                    System.out.println("Error sending the message");
                }
            }
        }
        else
        {
            System.out.println("Error sending the message");
        }
    }

}
