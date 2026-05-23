# Main Concerns
  * Reliabilty
  * Scalability
  * Maintainability

# Data intensive
    * speed of data 
    * quantity
    * complexity

* store data so that they can find it again later (DB)
* remember the result of an expensive operation, to speed up reads (cache)
* allow users to search data by keyword or filter it in various ways (search index)
* periodically crunch a large amt of accumulated data (batch processing)

# Categories
  DB, Queue, Cache

    * data store used as msg queues -> redis
    * msg queue with db-like durability -> kafka

 # Questions
  * How do u ensure that the data remains correct and complete even when things go wrong internally?
  * How do u provide consistently good performance to clients even when parts of your system are degraded?
  * How do u scale to handle an  increase in load?
  * What does a good api service look like?
  * What happens if we our load doubles?

select tweets.*, users.* FROM tweets 
  JOIN users on tweets.sender_id = users.id 
  JOIN follows on follows.followee_id = users.id 
  WHERE follows.follower_id = current_user


AM = (sum of n values) / n
not a good metric because it doen't tell u how many users experience that delay

------
High percentiles of response times, also known as tail latencies, are important
because they directly affect users’ experience of the service. For example, Amazon
describes response time requirements for internal services in terms of the 99.9th per‐
centile, even though it only affects 1 in 1,000 requests. This is because the customers
with the slowest requests are often those who have the most data on their accounts
because they have made many purchases—that is, they’re the most valuable custom‐
ers. It’s important to keep those customers happy by ensuring the website is fast
for them: Amazon has also observed that a 100 ms increase in response time reduces
sales by 1% , and others report that a 1-second slowdown reduces a customer sat‐
isfaction metric by 16% .
------


