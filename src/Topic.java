import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class Topic {
    public String topic_name;
    public static LinkedList all_topics_name = new LinkedList<>();
    private static LinkedList all_topics = new LinkedList<>();
    public LinkedList message_queue = new LinkedList();
    public Semaphore sem_receiver_queue = new Semaphore(1);

    public Topic(String topic_name)
    {
        this.topic_name = topic_name;
        all_topics_name.add(topic_name);
        all_topics.add(this);
    }
    public static Topic find_topic(String topic_name)
    {
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
    }
    public void write(String author ,String[] args)
    {
        LinkedList content = new LinkedList();
        content.add(author);
        String message = "";
        for(int i=2; i<args.length; i += 1)
        {
            message+= args[i] + " ";
        }
        content.add(message);
        Date timestamp = new Date();
        content.add(timestamp);
        this.message_queue.add(content);
    }
}
