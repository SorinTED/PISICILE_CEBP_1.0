public class Send {
    private String[] args;
    public Send(String[] args)
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

    public static void send_to(String sender, String[] args)
    {
        if(args.length >= 3)
        {
            if(args[0].equals("send") && Receiver_queue.all_receivers.contains(args[1]))
            {
                //System.out.println("Sending message ... ");
                Receiver_queue user_queue = Receiver_queue.find_queue_for(args[1]);
                if(user_queue.space_left_in_queue())
                    user_queue.write(user_queue, sender, args);
                else
                    System.out.println("Queue for messages to " + args[1] + " is full");
            }
            else if(args.length >= 6)
            {
                if(args[0].equals("add") && args[1].equals("to")
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
