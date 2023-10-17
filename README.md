# Smart Home Simulation 
### SS22 - group 10

## Description

### #2 - UDP-Sockets

The Gateway class gets the addresses and ports for each container running an instance of the Sensor class
as arguments.
It then initializes multiple instances of the SensorHandler class, allowing to both send and receive messages.
Afterwards a Timer is started that pulls the sensor data and, if successful, prints it to the console.

### #3 - TCP-Sockets and HTTP

The Server class was introduced. The Gateway class does not print
the data to the console anymore, but sends it to the server using HTTP.
To achieve that, a TCP socket connection is established to the server
and whenever the gateway extracts the message from a sensor, it pushes
it to the server. The server employs ClientHandler threads, which read the http 
message, evaluate it, and send an appropriate response.

### #4 - RPC

### #5 - MoM

### #6 - Redundancy

## Running the simulation

1. start docker</br>
2. open a terminal</br>
3. navigate to the root of this project</br>
4. run...
~~~
./init.bat -run
~~~
...to start the simulation normally.
~~~
./init.bat -stop
~~~
...to stop the simulation.
~~~
./init.bat -test
~~~
...to run the tests.
~~~
./init.bat -reset
~~~
...to do a hard-reset.
