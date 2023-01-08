Using the Prometheus HTTP API
Introduction
Tools such as the Prometheus expression browser can provide an easy way to execute queries and interact with Prometheus data. However, Prometheus also provides an HTTP API that can allow you to integrate Prometheus with your own custom tools. In this lesson, you will perform some simple queries using the Prometheus HTTP API, allowing you to gain hands-on experience with using that API.

Solution
Log in to the server using the credentials provided:

ssh cloud_user@<PUBLIC_IP_ADDRESS>
Get the Current Number of Threads
Make an HTTP request to the Prometheus server to retrieve the data and save it to a file:

curl localhost:9090/api/v1/query?query=num_threads > /home/cloud_user/num_threads.txt
View the file to verify the output is there:

cat /home/cloud_user/num_threads.txt
Get the num_threads Data Over the Last Five Minutes
Make an HTTP request to retrieve the data and save it to a file:

start=$(date --date '-5 min' +'%Y-%m-%dT%H:%M:%SZ')
end=$(date +'%Y-%m-%dT%H:%M:%SZ')
curl "localhost:9090/api/v1/query_range?query=num_threads&start=$start&end=$end&step=15s" > /home/cloud_user/num_threads_5_minutes.txt
View the file to verify the output is there:

cat /home/cloud_user/num_threads_5_minutes.txt