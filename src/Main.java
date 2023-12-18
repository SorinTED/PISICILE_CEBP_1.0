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
 *  admin create user <user>
 *      ex: admin create user Sorin
 *
 *  admin see all topics
 *
 *  admin see all users
 *
 */

public class Main {

    public static void main(String[] args) {
        boolean bots_on = true;
            boolean login_on = false;
            boolean message_on = true;
            boolean topic_on = false;
        boolean my_terminal_on = false;

        boolean defaults = true;

        Database loginDatabase = new Database();

        if(defaults)
        {
            User user1 = new User("Radu", "Radu69");
            User user2 = new User("Sorin", "Sorin112");
            User user3 = new User("Pasquale", "Pasquale123");
            loginDatabase.addUser(user1);
            loginDatabase.addUser(user2);
            loginDatabase.addUser(user3);


            Topic moto = new Topic("Moto");
            Topic university = new Topic("University");
            Topic job = new Topic("Job");


        }

        if(bots_on)
        {
            if(topic_on)
            {
                int time_between_topics_messages = 3000;
                int time_between_topics_messages_receive = 9000;

                Bot_Thread_Send bot1 = new Bot_Thread_Send("Jack","add to Moto for 0.0013h Hello",time_between_topics_messages);
                Bot_Thread_Send bot2 = new Bot_Thread_Send("John","add to Moto for 0.0007h Yello",time_between_topics_messages);
                Bot_Thread_Send bot3 = new Bot_Thread_Send("Jim","add to Moto for 24H Hi",time_between_topics_messages);

                bot1.start();
                bot2.start();
                bot3.start();

                Bot_Thread_Send bot_RT1 = new Bot_Thread_Send("Jack","read topics",time_between_topics_messages_receive);
                Bot_Thread_Send bot_RT2 = new Bot_Thread_Send("John","read topics",time_between_topics_messages_receive);
                Bot_Thread_Send bot_RT3 = new Bot_Thread_Send("Jim","read topics",time_between_topics_messages_receive);
                //admin see all topics

                bot_RT1.start();
                bot_RT2.start();
                bot_RT3.start();
            }

            if(message_on)
            {
                int time_between_messages = 1000;
                int time_between_messages_receive = 3000;

                Receiver_queue Sorin_messages = new Receiver_queue("Sorin");
                Receiver_queue Radu_messages = new Receiver_queue("Radu");

                Bot_Thread_Send bot4 = new Bot_Thread_Send("Jack","send Sorin Hello",time_between_messages);
                Bot_Thread_Send bot5 = new Bot_Thread_Send("John","send Sorin Yello",time_between_messages);
                Bot_Thread_Send bot6 = new Bot_Thread_Send("Jim","send Sorin Hi",time_between_messages);

                bot4.start();
                bot5.start();
                bot6.start();

                Bot_Thread_Send bot_RT4 = new Bot_Thread_Send("all","read messages for Sorin",time_between_messages_receive);
                Bot_Thread_Send bot_RT5 = new Bot_Thread_Send("Jack","read messages for Sorin",time_between_messages_receive);
                Bot_Thread_Send bot_RT6 = new Bot_Thread_Send("John","read messages for Sorin",time_between_messages_receive);
                Bot_Thread_Send bot_RT7 = new Bot_Thread_Send("Jim","read messages for Sorin",time_between_messages_receive);

                //bot_RT4.start();
                bot_RT5.start();
                bot_RT6.start();
                bot_RT7.start();
            }

            if(login_on)
            {
                int time_between_messages = 4000;

                Bot_Thread_Send bot7 = new Bot_Thread_Send("login Radu Radu69",time_between_messages);
                Bot_Thread_Send bot8 = new Bot_Thread_Send("login Sorin Sorin112",time_between_messages);
                Bot_Thread_Send bot9 = new Bot_Thread_Send("login Pasquale Pasquale12",time_between_messages);

                bot7.start();
                bot8.start();
                bot9.start();
            }

        }

        //for me in terminal
        if(my_terminal_on)
        {
            Scanner scanner = new Scanner(System.in);
            String input;
            String[] input_args;

            do {
                System.out.print("Enter text (press Enter to exit): ");
                input = scanner.nextLine();
                //System.out.println("You entered: " + input);
                input_args = input.split(" ");
                Do.execute("Vivaldo Pasquale", input_args);
            } while (!input.isEmpty());

            scanner.close();
        }

    }
}
