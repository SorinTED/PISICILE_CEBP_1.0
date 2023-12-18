import javax.xml.crypto.Data;
import java.util.Scanner;

/*
 *                                                  IMPORTANT
 * login <Username> <password>
 *      ex: login Radu Radu69
 *
 *  send message syntax: "send <username> <content>"
 *      ex: send Sorin Hello my dear friend
 *
 *  add post to topic: "add to <topic_name> for <post_timeout>h <content>"
 *                                          or
 *                     "add to <topic_name> for <post_timeout>H <content>"
 *              <post_timeout> - is a value (double or integer) which represents
 *                                  after how many hours will be the post deleted (number+h/H)
 *      ex: add to Moto for 3h I am very excited about my new motorcycle
 *
 * logout
 *
 *
 *                                                  >ADMIN<
 *
 *  admin set server timeout <timeout>h
 *              <post_timeout> - is a value (double or integer) which represents
 *                                  after how many hours will be the post deleted (number+h/H)
 *       ex: set server timeout 4h
 *
 *  admin set <topic> max <number_max_posts> posts
 *       ex: set Moto max 30 posts
 *
 *  admin set <topic> max <number_max_posts> messages
 *       ex: set Moto max 30 posts
 *
 *  admin empty topic <topic>
 *      ex: admin empty moto
 *
 *  admin empty user <user> messages
 *      ex: admin delete Sorin messages
 *
 *  admin delete user <user>
 *      ex: admin delete Sorin
 *
 *  admin delete topic <topic>
 *      ex: admin delete Moto
 *
 *  admin create topic <topic>
 *      ex: admin create topic Moto
 *
 *  admin create user <user> <password>
 *      ex: admin create user Sorin 1234
 *
 *  admin see all topics
 *
 *  admin see all users
 *
 */

public class Main {

    public static void main(String[] args) {
        boolean bots_on = false;
            boolean login_on = false;
            boolean message_on = false;
            boolean topic_on = false;

        boolean my_terminal_on = true;

        boolean database = true;

        //send demos variables
        boolean send_demo = false;
            String thing_to_demo = "Topics";
            int demo_type = 5;
        boolean send_demo_database = true;


        send_demo(send_demo,thing_to_demo,demo_type);

        send_demo_database(send_demo_database, my_terminal_on, database);

        bots(bots_on,topic_on,message_on,login_on);
    }

    private static void send_demo(boolean start, String thing_to_demo, int demo_type) {
        boolean send_demo = true;

        if(!start)
            return;

        Receiver_queue.verbose = true;
        Topic.verbose = true;

        default_values(true);

        System.out.println();
        System.out.println();
        System.out.println();


        if (send_demo) {
            //time delays
            int time_1h = 3600 * 1000;
            int time_1sec = 1 * 1000;
            int time_5sec = 5 * 1000;

            String time_string_1sec = "0.0003h";
            String time_string_5sec = "0.0013h";
            String time_string_10sec = "0.0026h";
            String time_string_1h = "1h";


            //messages demos
            if(thing_to_demo.equals("Messages"))
            {
                //simple send 1 user
                if (demo_type == 0) {
                    Bot_Thread_Send bot1 = new Bot_Thread_Send("Jack", "send Sorin Hello", time_5sec);

                    bot1.start();
                }
                //simple send multiple users
                else if (demo_type == 1)
                {
                    Bot_Thread_Send bot1 = new Bot_Thread_Send("Jack", "send Sorin Hello", time_5sec);
                    Bot_Thread_Send bot2 = new Bot_Thread_Send("Jim", "send Sorin Hi", time_5sec - 1000);
                    Bot_Thread_Send bot3 = new Bot_Thread_Send("John", "send Sorin Yello", time_5sec - 2000);

                    bot1.start();
                    bot2.start();
                    bot3.start();
                }
                //send with concurrency problems unsolved on write
                else if (demo_type == 2)
                {
                    Receiver_queue Sorin_queue = Receiver_queue.find_queue_for("Sorin");
                    Sorin_queue.setMax_messages(100);
                    Receiver_queue.concurrency_enabled_for_write = false;

                    Bot_Thread_Send bot1 = new Bot_Thread_Send("Jack", "send Sorin Hello", time_1sec);
                    Bot_Thread_Send bot2 = new Bot_Thread_Send("Jim", "send Sorin Hi", time_1sec);
                    Bot_Thread_Send bot3 = new Bot_Thread_Send("John", "send Sorin Yello", time_1sec);

                    bot1.start();
                    bot2.start();
                    bot3.start();
                }
                //send with concurrency problems solved on write
                else if (demo_type == 3)
                {
                    Receiver_queue Sorin_queue = Receiver_queue.find_queue_for("Sorin");
                    Sorin_queue.setMax_messages(100);
                    Receiver_queue.concurrency_enabled_for_write = true; //by default is also true, this is just to make it explicit

                    Bot_Thread_Send bot1 = new Bot_Thread_Send("Jack", "send Sorin Hello", time_1sec);
                    Bot_Thread_Send bot2 = new Bot_Thread_Send("Jim", "send Sorin Hi", time_1sec);
                    Bot_Thread_Send bot3 = new Bot_Thread_Send("John", "send Sorin Yello", time_1sec);

                    bot1.start();
                    bot2.start();
                    bot3.start();
                }
                //demo event function full inbox & full queue when sending a message
                else if (demo_type == 4)
                {
                    Receiver_queue Sorin_queue = Receiver_queue.find_queue_for("Sorin");
                    Sorin_queue.setMax_messages(10);
                    Receiver_queue.concurrency_enabled_for_write = false;
                    Receiver_queue.verbose = false;

                    Bot_Thread_Send bot1 = new Bot_Thread_Send("Jack", "send Sorin Hello", time_1sec);
                    Bot_Thread_Send bot2 = new Bot_Thread_Send("Jim", "send Sorin Hi", time_1sec * 2);
                    Bot_Thread_Send bot3 = new Bot_Thread_Send("John", "send Sorin Yello", time_1sec * 3);

                    bot1.start();
                    bot2.start();
                    bot3.start();
                }
                else
                    System.out.println("Invalid demo_type parameter");
            }


            //topic demos
            else if (thing_to_demo.equals("Topics"))
            {
                //simple post 1 topic
                if (demo_type == 0) {
                    Bot_Thread_Send bot1 = new Bot_Thread_Send("Jack", "add to Moto for " + time_string_1h + " Nice motorcycle", time_5sec);

                    bot1.start();
                }
                //simple post multiple posts to topic
                else if (demo_type == 1)
                {
                    Bot_Thread_Send bot1 = new Bot_Thread_Send("Jack", "add to Moto for " + time_string_1h + " Nice motorcycle", time_5sec);
                    Bot_Thread_Send bot2 = new Bot_Thread_Send("Jim", "add to Moto for " + time_string_1h + " Love that model", time_5sec - 1000);
                    Bot_Thread_Send bot3 = new Bot_Thread_Send("John", "add to Moto for " + time_string_1h + " I love motorcycles", time_5sec - 2000);

                    bot1.start();
                    bot2.start();
                    bot3.start();
                }
                //post with server timeout (event server timeout)
                else if (demo_type == 2)
                {
                    Topic Moto_topic = Topic.find_topic("Moto");
                    Moto_topic.setMax_posts(100);
                    Topic.setServer_timeout(0.0026);

                    Bot_Thread_Send bot1 = new Bot_Thread_Send("Jack", "add to Moto for " + time_string_1h + " Nice motorcycle", time_5sec);
                    Bot_Thread_Send bot2 = new Bot_Thread_Send("Jim", "add to Moto for " + time_string_1h + " Love that model", time_5sec - 1000);
                    Bot_Thread_Send bot3 = new Bot_Thread_Send("John", "add to Moto for " + time_string_1h + " I love motorcycles", time_5sec - 2000);

                    bot1.start();
                    bot2.start();
                    bot3.start();
                }
                //post timeout (event post timeout)
                else if (demo_type == 3)
                {
                    Topic Moto_topic = Topic.find_topic("Moto");
                    Moto_topic.setMax_posts(100);

                    Bot_Thread_Send bot1 = new Bot_Thread_Send("Jack", "add to Moto for " + time_string_5sec + " Nice motorcycle", time_5sec);
                    Bot_Thread_Send bot2 = new Bot_Thread_Send("Jim", "add to Moto for " + time_string_5sec + " Love that model", time_5sec - 1000);
                    Bot_Thread_Send bot3 = new Bot_Thread_Send("John", "add to Moto for " + time_string_5sec + " I love motorcycles", time_5sec - 2000);

                    bot1.start();
                    bot2.start();
                    bot3.start();
                }
                //demo max number of posts reached (event full topic) + sending a message to a full topic
                else if (demo_type == 4)
                {
                    Topic Moto_topic = Topic.find_topic("Moto");
                    Moto_topic.setMax_posts(10);
                    Topic.verbose = false;

                    Bot_Thread_Send bot1 = new Bot_Thread_Send("Jack", "add to Moto for " + time_string_1h + " Nice motorcycle", time_1sec);
                    Bot_Thread_Send bot2 = new Bot_Thread_Send("Jim", "add to Moto for " + time_string_1h + " Love that model", time_1sec * 2);
                    Bot_Thread_Send bot3 = new Bot_Thread_Send("John", "add to Moto for " + time_string_1h + " I love motorcycles", time_1sec * 3);

                    bot1.start();
                    bot2.start();
                    bot3.start();
                }
                //post with concurrency problems unsolved on write
                else if (demo_type == 5)
                {
                    Topic Moto_topic = Topic.find_topic("Moto");
                    Moto_topic.setMax_posts(100);
                    Topic.concurrency_enabled_for_write = false;

                    Bot_Thread_Send bot1 = new Bot_Thread_Send("Jack", "add to Moto for " + time_string_1h + " Nice motorcycle", time_1sec);
                    Bot_Thread_Send bot2 = new Bot_Thread_Send("Jim", "add to Moto for " + time_string_1h + " Love that model", time_1sec);
                    Bot_Thread_Send bot3 = new Bot_Thread_Send("John", "add to Moto for " + time_string_1h + " I love motorcycles", time_1sec);

                    bot1.start();
                    bot2.start();
                    bot3.start();
                }
                //post with concurrency problems solved on write
                else if (demo_type == 6)
                {
                    Topic Moto_topic = Topic.find_topic("Moto");
                    Moto_topic.setMax_posts(100);
                    Topic.concurrency_enabled_for_write = true;

                    Bot_Thread_Send bot1 = new Bot_Thread_Send("Jack", "add to Moto for " + time_string_1h + " Nice motorcycle", time_1sec);
                    Bot_Thread_Send bot2 = new Bot_Thread_Send("Jim", "add to Moto for " + time_string_1h + " Love that model", time_1sec);
                    Bot_Thread_Send bot3 = new Bot_Thread_Send("John", "add to Moto for " + time_string_1h + " I love motorcycles", time_1sec);

                    bot1.start();
                    bot2.start();
                    bot3.start();
                }
                else
                    System.out.println("Invalid demo_type parameter");
            }
            else
                System.out.println("Invalid thing to demo");
        }
    }

    private static void database(boolean database)
    {
        if(database)
        {
            User user1 = new User("Radu", "Radu");
            User user2 = new User("Vlad", "Vlad");
            User user3 = new User("Sorin", "Sorin");
            Database.addUser(user1);
            Database.addUser(user2);
            Database.addUser(user3);
            Database.login(user1);
            Database.login(user2);
        }
    }

    private static void default_values(boolean defaults)
    {
        if(defaults)
        {
            User user1 = new User("Radu", "Radu69");
            User user2 = new User("Sorin", "Sorin112");
            User user3 = new User("Pasquale", "Pasquale123");
            Database.addUser(user1);
            Database.addUser(user2);
            Database.addUser(user3);

            Receiver_queue Sorin_messages = new Receiver_queue("Sorin");
            Receiver_queue Radu_messages = new Receiver_queue("Radu");

            Topic moto = new Topic("Moto");
            Topic university = new Topic("University");
            Topic job = new Topic("Job");
        }
    }

    private static void start_terminal(boolean my_terminal_on)
    {
        if(my_terminal_on)
        {
            Scanner scanner = new Scanner(System.in);
            String input;
            String[] input_args;
            User user = null;

            do {
                if(Database.isAuthenticated(user))
                {
                    System.out.print("Enter text (press Enter to exit): ");
                    input = scanner.nextLine();
                    //System.out.println("You entered: " + input);
                    input_args = input.split(" ");
                    Do.execute(user.toString(), input_args);
                }
                else
                {
                    do {
                        System.out.print("Please login:");
                        input = scanner.nextLine();;
                        input_args = input.split(" ");
                        user = Do.execute_login(input, input_args);
                        if( user == null )
                            System.out.println("Bad credentials! Try again");
                    } while (user == null);
                }
            } while (!input.isEmpty());

            scanner.close();
        }
    }

    private static void bots(boolean bots_on, boolean topic_on, boolean message_on, boolean login_on)
    {
        if(bots_on)
        {
            if(topic_on)
            {
                int time_between_topics_messages = 3000;

                Bot_Thread_Send bot1 = new Bot_Thread_Send("Jack","add to Moto for 0.0013h Hello",time_between_topics_messages);
                Bot_Thread_Send bot2 = new Bot_Thread_Send("John","add to Moto for 0.0007h Yello",time_between_topics_messages);
                Bot_Thread_Send bot3 = new Bot_Thread_Send("Jim","add to Moto for 24H Hi",time_between_topics_messages);

                bot1.start();
                bot2.start();
                bot3.start();
            }

            if(message_on)
            {
                int time_between_messages = 1000;

                Bot_Thread_Send bot4 = new Bot_Thread_Send("Jack","send Sorin Hello",time_between_messages);
                Bot_Thread_Send bot5 = new Bot_Thread_Send("John","send Sorin Yello",time_between_messages);
                Bot_Thread_Send bot6 = new Bot_Thread_Send("Jim","send Sorin Hi",time_between_messages);

                bot4.start();
                bot5.start();
                bot6.start();
            }

            if(login_on)
            {
                int time_between_messages = 4000;

                Bot_Thread_Send bot7 = new Bot_Thread_Send("login Radu Radu69", time_between_messages);
                Bot_Thread_Send bot8 = new Bot_Thread_Send("login Sorin Sorin112", time_between_messages);
                Bot_Thread_Send bot9 = new Bot_Thread_Send("login Pasquale Pasquale12", time_between_messages);

                bot7.start();
                bot8.start();
                bot9.start();
            }
        }
    }
}
