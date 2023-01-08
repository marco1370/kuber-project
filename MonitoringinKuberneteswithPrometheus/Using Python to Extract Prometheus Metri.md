Using Python to Extract Prometheus Metrics
Introduction
This lab guides the student through the use of a Python program to interface with the Prometheus API endpoint. The program will use PromQL examples to pull CPU and memory metrics and output them in a Comma Separated Value (CSV) file that may then be used for a Machine Learning program. This lab only covers the extract step and the Machine Learning part is covered in a subsequent lab.

Solution
Gain access to the master node with a terminal emulator
To use SSH to access the master node, enter:

 ssh cloud_user@[Master's Public IP Address]
You will be prompted for the cloud_user password that is available on the lab startup page.

Review the promql.py program in GitHub
To review the promql.py program, navigate to the following GitHub address:

 https://github.com/linuxacademy/content-aiops-essentials/blob/master/promql.py
 
Start the promql.py program on the master node
From your terminal session on the master node, enter the following command to start the Python program:

 python3 promql.py > promql.out 2> promql.err &
Note: Be sure you use the ampersand after the command so it will run in background on your server.

As the promql.py program is running, stress the cluster
To stress the cluster, you may deploy the stress-test deployment with the following command:

 kubectl create -f stress-test.yaml
 
Run the Prometheus dashboard as you vary cluster load
Navigate in your browser to the Prometheus dashboard:

 http://[Master Node IP]:9090
While the Python program is gathering metrics, use the following command to vary the load by changing the number of replicas:

 kubectl scale deployment.v1.apps/stress-test --replicas=10
You may then increase the number of replicas to 20, 30, 40, and so on.

 kubectl scale deployment.v1.apps/stress-test --replicas=[number here]
If you want to use other PromQL in the Prometheus dashboard, here are the two examples we use in the Python promql.py program:

 100 - avg(irate(node_cpu_seconds_total{job="node",mode="idle"}[5m])) by (instance) * 100
And:

 (node_memory_MemTotal_bytes - (node_memory_MemFree_bytes + node_memory_Cached_bytes + node_memory_Buffers_bytes)) / node_memory_MemTotal_bytes * 100
 
Examine the Python program output
You may look at the promql.py output with any of the following commands:

 tail promql.out
Or:

 more promql.out
Or:

 tail -f promql.out
 