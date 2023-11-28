import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class Topic {
    public String topic_name;
    private static double server_timeout = -1; //= 0.0013; ~ 5 sec
    private int max_posts = 100;

    // all topics names
    public static LinkedList all_topics_name = new LinkedList<>();
    // all topics addresses
    public static LinkedList all_topics = new LinkedList<>();
    // for this topic a list of messages like [[author, content, timestamp], ... ]
    public LinkedList topic_queue = new LinkedList();

    public Semaphore sem_topic_queue = new Semaphore(1);
    public static Semaphore sem_linked_lists = new Semaphore(1);

    public static Event_Bot_Thread event_bot = new Event_Bot_Thread("Topic",1000);

    public void setMax_posts(int max_posts) { this.max_posts = max_posts; }
    public static void setServer_timeout(double timeout)
    {
        server_timeout = timeout;
    }

    public Topic(String topic_name)
    {
        try {
            sem_linked_lists.acquire();
            this.topic_name = topic_name;
            all_topics_name.add(topic_name);
            all_topics.add(this);
            sem_linked_lists.release();
        }catch (Exception exc)
        {
            System.out.println(exc);
        }
    }
    public static Topic find_topic(String topic_name)
    {
        try {
            sem_linked_lists.acquire();
            if(all_topics_name.contains(topic_name))
            {
                int index = all_topics_name.indexOf(topic_name);
                sem_linked_lists.release();
                return (Topic) all_topics.get(index);
            }
            else
            {
                System.out.println("Topic not found!");
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
        if(this.max_posts - this.topic_queue.size()>0)
                return true;
        else
                return false;
    }
    public void write(String author ,String[] args)
    {
        try {
            this.sem_topic_queue.acquire();

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

            System.out.println(this.topic_name + ": " + this.topic_queue);

            this.sem_topic_queue.release();

        }catch (Exception exc)
        {
            System.out.println(exc);
        }
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
                        System.out.println("Removed: " + post);
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
    //Event 2
    public static void verify_number_of_posts()
    {
        try {
            sem_linked_lists.acquire();
            for(Object element : Topic.all_topics)
            {
                Topic topic = (Topic)(element);
                topic.sem_topic_queue.acquire();

                if(!topic.space_left_in_queue())
                    System.out.println("The queue for topic " + topic.topic_name + " is full");

                topic.sem_topic_queue.release();
            }
            sem_linked_lists.release();
        }catch (Exception exc){
            System.out.println(exc);
        }
    }
}
