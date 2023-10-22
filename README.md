# PISICILE_CEBP_1.0

Hello, our application will represent an messaging app similar to reddit or discord, representing an platform for users to message one to one or one to many

Problems:

1. Synchronization
    Issue: When multiple applications concurrently access shared resources, race conditions may occur, leading to data inconsistency.
    Solution: Implement synchronization mechanisms like locks, semaphores, or mutexes to control access to shared resources and ensure data consistency.

2. Message Queue Overflow
    Issue: Message queues have limited capacity, and simultaneous message transmissions can lead to queue overflow and potential data loss.
    Solution: Develop a mechanism to gracefully handle queue overflows, which may involve waiting for space, discarding or requeuing messages, and robust error handling.

3. Message Cleanup and Expiration
    Issue: Removing expired messages from queues and topics without conflicts during concurrent message processing can be challenging.
    Solution: Use scheduled tasks or separate threads to periodically clean up expired messages. Ensure proper resource locking to prevent conflicts.

4. Message Filtering and Subscription Management
    Issue: Managing client subscriptions to topics and message types dynamically and concurrently is complex.
    Solution: Maintain a data structure to track client subscriptions, and use appropriate locking mechanisms to efficiently route published messages to interested clients.

5. Resource Allocation
    Issue: Concurrent requests for message queues and topics can result in resource contention, where multiple clients compete for the same resources.
    Solution: Implement a resource allocation manager to oversee resource availability and control client requests, ensuring fair and efficient resource allocation.

   ARCHITECTURE:
     Centralized
     One thread per user
     Entry point is a LogIn page
     
