# PISICILE_CEBP_1.0

Hello, our application will represent an messaging app similar to reddit or discord, representing an platform for users to message one to one or one to many

### Features Implemented:

#### Messages
- Simple messages that a user can send to another user using this app.
- Once the messages are read, they will disappear.

#### Topics
- Contain a certain number of posts.
- Each post has a timeout, and once the post timeout is reached, they will disappear.
- The server also has a timeout for all posts, also in this case if the timeout is reached, they will disappear.

#### Admin
- This adds an original superuser with capabilities to administrate the system from the terminal using comprehensive special commands.
- He can also behave as a normal user with abilities to write and read messages/posts.
- He exists as soon as the app is launched

### Problems:

1. **Thread-safety**
    - **Issue:** This was the biggest cause of problems we had :)
    - **Solution:** We learnt and used from the java utils library technology such as Semaphores for read and write operations (both static and for single instance),Reentrant Locks for a specific resource (also both static and for single instance) and thread safe operations

   
    Fun Fact: Did you know that an "append" operation in LinkedList using a "for" loop will throw an exception even if an error does not occur? Why? This is considered a write operation in a non thread-safe way, and it uses fail-fast polices. Solution? Use iterators!

2. **Event-based Functions**
    - **Issue:** Develop a way to wait for an event, act when it happens, and do this a loop
    - **Solution:** 3 special thread bots (one in the DB, one for messages and one for topics), pooling for multiple events, waiting for specific events to happen and act

3. **Empty Queues and Topics**
    - **Issue:** Have a safe way to act (or not) in case of an empty data structure
    - **Solution:** Use at the lowest level function to verify, print or discard in case of an empty data structure

4. **Feature related issues**
    - **Issue:** Develop the necessary features required and the one we proposed ourselves we wanted to add
    - **Solution:** Work hard and be creative, tried to use simple operations and keep a clear design as we could
   
5. **RabbitMQ and other technologies**
    - **Issue:** Setting up RabbitMQ was quite challenging
    - **Solution:** Document ourselves using the official documentation and keep trying


   ARCHITECTURE:
     Centralized;
     One thread per user;
     Entry point is a LogIn page;
     
