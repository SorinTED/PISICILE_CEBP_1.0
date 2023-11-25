public class Send {
    private String[] args;
    public Send(String[] args)
    {
        this.args=args;
    }
    public static void send_to(String sender, String[] args)
    {
        if(args.length >= 3 && args[0].equals("send") && Receiver_queue.all_receivers.contains(args[1]))
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
        else if(args.length >= 3 && args[0].equals("add") && Topic.all_topics_name.contains(args[1]))
        {
            System.out.println("Adding to topic ... ");
            Topic topic = Topic.find_topic(args[1]);
            try {
                topic.sem_receiver_queue.acquire();
                topic.write(sender, args);
                System.out.println(topic.topic_name + ": " + topic.message_queue);
                topic.sem_receiver_queue.release();
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
