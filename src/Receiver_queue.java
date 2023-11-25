import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class Receiver_queue {
    public String receiver_name;
    public static LinkedList all_receivers = new LinkedList<>();
    private static LinkedList all_queues = new LinkedList<>();
    public LinkedList message_queue = new LinkedList();
    public Semaphore sem_receiver_queue = new Semaphore(1);

    public Receiver_queue(String receiver_name)
    {
        this.receiver_name = receiver_name;
        all_receivers.add(receiver_name);
        all_queues.add(this);
    }
    public static Receiver_queue find_queue_for(String user)
    {
        if(all_receivers.contains(user))
        {
            int index = all_receivers.indexOf(user);
            return (Receiver_queue) all_queues.get(index);
        }
        else
        {
            System.out.println("User not found!");
            return null;
        }
    }
    public void write(Receiver_queue user_queue, String sender ,String[] args)
    {
        LinkedList content = new LinkedList();
        content.add(sender);
        String message = "";
        for(int i=2; i<args.length; i += 1)
        {
            message+= args[i] + " ";
        }
        content.add(message);
        Date timestamp = new Date();
        content.add(timestamp);
        user_queue.message_queue.add(content);
    }
}
