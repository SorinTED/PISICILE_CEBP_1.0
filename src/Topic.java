import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class Topic {
    public String topic_name;
    private static double server_timeout = -1; //= 0.0013; ~ 5 sec

    // all topics names
    public static LinkedList all_topics_name = new LinkedList<>();
    // all topics addresses
    public static LinkedList all_topics = new LinkedList<>();
    // for this topic a list of messages like [[author, content, timestamp], ... ]
    public LinkedList topic_queue = new LinkedList();

    public Semaphore sem_topic_queue = new Semaphore(1);
    public static Semaphore sem_linked_lists = new Semaphore(1);

    public static Event_Bot_Thread event_bot = new Event_Bot_Thread(1000);

    public Topic(String topic_name)
    {
        try {
            this.topic_name = topic_name;
            all_topics_name.add(topic_name);
            all_topics.add(this);
        }catch (Exception exc)
        {
            System.out.println(exc);
        }
    }
    public static void setServer_timeout(double timeout)
    {
        server_timeout = timeout;
    }
    public static Topic find_topic(String topic_name)
    {
        try {
            if(all_topics_name.contains(topic_name))
            {
                int index = all_topics_name.indexOf(topic_name);
                return (Topic) all_topics.get(index);
            }
            else
            {
                System.out.println("Topic not found!");
                return null;
            }
        }catch (Exception exc)
        {
            System.out.println(exc);
        }
        return null;
    }
    public void write(String author ,String[] args)
    {
        LinkedList content = new LinkedList();
        content.add(author);
        String message = "";
        for(int i=5; i<=args.length - 1; i += 1)
        {
            message+= args[i] + " ";
        }
        content.add(message);
        Date timestamp = new Date();
        content.add(timestamp);
        content.add(args[4].substring(0, args[4].length() - 1));
        this.topic_queue.add(content);
    }
    public static double get_post_lifetime(LinkedList post)
    {
        // in hours
        double now = new Date().getTime()/1000/60.0/60.0;
        double timestamp = ((Date)(post.get(2))).getTime()/1000/60.0/60.0;
        return now - timestamp;
    }
    public static double get_post_timeout(LinkedList post)
    {
        // in hours
        return Double.valueOf(post.get(3).toString());
    }
    // Event 1
    public static void delete_posts_after()
    {
        try {
            sem_linked_lists.acquire();
            for(Object element : Topic.all_topics)
            {
                Topic topic = (Topic)(element);
                topic.sem_topic_queue.acquire();

                Iterator i = topic.topic_queue.iterator();

                while(i.hasNext())
                {
                    LinkedList post = (LinkedList) i.next();
                    double post_lifetime = get_post_lifetime(post);
                    double post_timeout = get_post_timeout(post);
                    if(post_lifetime>=server_timeout && server_timeout > 0 || post_lifetime>=post_timeout)
                    {
                        System.out.println("Removed");
                        System.out.println(post_timeout);
                        i.remove();
                    }
                }

                topic.sem_topic_queue.release();
            }
            sem_linked_lists.release();
        }catch (Exception exc){
            System.out.println(exc);
        }
    }
}
