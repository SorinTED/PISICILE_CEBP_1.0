import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class Receiver_queue {
    public String receiver_name;
    private int max_messages = 20;
    public static LinkedList all_receivers = new LinkedList<>();
    private static LinkedList all_queues = new LinkedList<>();
    public LinkedList message_queue = new LinkedList();
    public Semaphore sem_receiver_queue = new Semaphore(1);
    public static Semaphore sem_linked_lists = new Semaphore(1);
    public static Event_Bot_Thread event_bot = new Event_Bot_Thread("Messages",1000);

    public Receiver_queue(String receiver_name)
    {
        try {
            sem_linked_lists.acquire();

            this.receiver_name = receiver_name;
            all_receivers.add(receiver_name);
            all_queues.add(this);

            sem_linked_lists.release();
        }catch (Exception exc)
        {
            System.out.println(exc);
        }
    }
    public static Receiver_queue find_queue_for(String user)
    {
        try {
            sem_linked_lists.acquire();

            if(all_receivers.contains(user))
            {
                int index = all_receivers.indexOf(user);
                sem_linked_lists.release();
                return (Receiver_queue) all_queues.get(index);
            }
            else
            {
                System.out.println("User not found!");
                sem_linked_lists.release();
                return null;
            }
        }catch (Exception exc)
        {
            System.out.println(exc);
        }
        return null;
    }
    public boolean space_left_in_queue()
    {
        if(this.max_messages - this.message_queue.size()>0)
            return true;
        else
            return false;
    }
    public void write(Receiver_queue user_queue, String sender ,String[] args)
    {
        try {
            user_queue.sem_receiver_queue.acquire();

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

            System.out.println(user_queue.receiver_name + ": " + user_queue.message_queue);

            user_queue.sem_receiver_queue.release();
        }catch (Exception exc)
        {
            System.out.println(exc);
        }
    }
    //Event 1
    public static void verify_number_of_messages()
    {
        try {
            sem_linked_lists.acquire();
            for(Object element : Receiver_queue.all_queues)
            {
                Receiver_queue receiver_queue = (Receiver_queue)(element);
                receiver_queue.sem_receiver_queue.acquire();

                if(!receiver_queue.space_left_in_queue())
                    System.out.println("The queue for topic " + receiver_queue.receiver_name + " is full");

                receiver_queue.sem_receiver_queue.release();
            }
            sem_linked_lists.release();
        }catch (Exception exc){
            System.out.println(exc);
        }
    }

}
