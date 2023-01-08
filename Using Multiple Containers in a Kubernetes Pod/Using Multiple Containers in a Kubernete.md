Using Multiple Containers in a Kubernetes Pod
Introduction
Multi-container Pods in Kubernetes use additional containers within the same Pod to solve technical challenges. This lab will allow you to get hands-on with multi-container Pods. You will build expertise as you design your own multi-container Pod in order to address a real-world issue.

Solution
Log in to the server using the credentials provided:

ssh cloud_user@<PUBLIC_IP_ADDRESS>
Update the Service to Use the New Port
Edit the Service:

kubectl edit svc hive-svc
Change the Service listening port from 9090 to 9076:

ports:
- port: 9076
  ...
Type ESC followed by :wq to save your changes and to update the Service.

Add an init Container to Delay Startup
Edit the app-gateway Deployment:

kubectl edit deployment app-gateway
Scroll down to the Pod template in the Deployment specification. Add an init container to the Pod template on the same level as the containers field.

Note: When copying and pasting code into Vim from the lab guide, first enter :set paste (and then i to enter insert mode) to avoid adding unnecessary spaces and hashes. To save and quit the file, press Escape followed by :wq. To exit the file without saving, press Escape followed by :q!.

     spec:
       containers:
       - ...
       initContainers:
       - name: startup-delay
         image: busybox:stable
         command: ['sh', '-c', 'sleep 10']
Type ESC followed by :wq to save your changes to update the Deployment.

Check the status of the Deployment's Pod:

kubectl get pods
You should see the app-gateway Pod in the Init phase. Wait a few seconds, and re-run the command. You should see the Pod is now Running, and the old one is Terminating.

Add an Ambassador Container to Make the App Use the New Service Port
Edit the app-gateway Deployment again:

kubectl edit deployment app-gateway
Scroll down to locate the Pod template and specification. Change the main container's command to point to localhost instead of hive-svc:

        command:
          - sh
          - -c
          - while true; do curl localhost:9090; sleep 5; done
Add a new ambassador container that uses the haproxy:2.4 image. Mount the existing configMap to haproxy:

  containers:
  - command:
    - sh
    - -c
    - while true; do curl localhost:9090; sleep 5; done
    image: radial/busyboxplus:curl
    imagePullPolicy: IfNotPresent
    name: busybox
    resources: {}
    terminationMessagePath: /dev/termination-log
    terminationMessagePolicy: File
  - name: ambassador
    image: haproxy:2.4
    volumeMounts:
    - name: haproxy-config
      mountPath: /usr/local/etc/haproxy/
  volumes:
  - name: haproxy-config
    configMap:
      name: haproxy-config
Type ESC followed by :wq to save your changes and to update the Deployment.

Check the Pod status:

kubectl get pods
You should see the app-gateway Pod in the Init phase. Wait a few seconds and re-run the command. The Pod should now be Running.

Copy the full name of the app Pod. It should begin with app-gateway- (the one in the Running status). Use the Pod name to check the main container's logs:

kubectl logs $POD_NAME -c busybox
You should see the output from the Hive API. The application is able to successfully communicate with the Service through the ambassador container.

Conclusion
Congratulations — you've completed this hands-on lab!