
********************************Parking application***********************
The code attached is an application that allow to have an account of the number
of spot that are occuped and that are empty in a parking.

To make it possible the idea is located a light sensor in each individual parking 
spot. This sensor is able to predict if there is or not a car depending on the changes
of light that it receives.

When a sensor receives a change of light that represent a state modification 
it changes its state and communicate the value to the central using initially 
the closest path. 

**************************How execute the application*********************
The first stept to execute the application is to configure the csv file with
the location of the real sensor and the virtual one. The rules to configurate
the file are the following:

     * Each position in the parking is separate by a comma ",".
	 * the symbol "-" represents empty spaces o the parking street.
	 * A virtual sensor has the value of the distance to the central o sink.
	 * A virtual sensor has the value of the distance to the central o sink. It 
	   also has the address of the sensor that is separed of the distance using
	   a semi colon.


#file1.csv. just with virtual sensors
-,-,-,-,-
3,2,1,0,-
3,2,1,0,-
-,-,-,-,-

#file2.csv. just with one real sensors
-,-,-,-,-
3,2,1;0014.4F01.0000.5720,0,-
3,2,1,0,-
-,-,-,-,- 

This model is possible because the sensors deployed in a parking are always 
located in a static way and does not need to be changed frequently.


***************************Light Sensor Calibration***********************
To use light sensors to predict if a car was allocated in a parking spot,
it was developed a model using the  decision tree J48. To create the 
model first it was collected real data at night, without external light 
and using a carton box  with a lamp to simulate the light of the parking.
,-
The data was collected in the following format:

current light|previous light|cifference|status

And given this data to the algorithm it give as a result a set of rules
that given the current and the previous light and differences allow
to predict the current status. The following rules are the result obtained
and used in the implementation (See Class simulation.EvaluateLight):

Difference <= -12
|   Current <= 11: car 
|   Current > 11: ok 
Difference > -12
|   Difference <= 10: ok 
|   Difference > 10
|   |   Previous <= 11: nocar 
|   |   Previous > 11: ok 

********************************Simulator*********************************
Due to there were no enough sun spots to test the behaviour of the parking
we create a java simulator that is able to make a simulation where virtual 
and real sensor can interact to increase and decrease the number of spots
avalable and to find the best path or the path available to take the count
to a central sink were the values are deployed. The main classes  that 
abstract the behavior of the main components are:

MainFrame: It is the graphic interface that simulates the parking showing 
the position that will be occupated for virtual and real sensors.

Sensor: Abstract class that define the behaviour of any kind of sensor. A 
general sensor keeps its status, its neighbors, the best option to 
communicate with the central or sink and the list of neighbours where it 
attempt to send a message but it was not succesfull. It is use to try a 
new path to send the message.

RealSensor: Keeps the properties and behaviour necessary to allow the 
communication between the virtual sensors and the real ones. It use the 
classes realSensor.MessageReceptor and realSensor.MessageSender for the 
communication.

VirtualSensor: It represent a virtual sensor that simulate the behaviour 
of real sensor.

Message: Class that represent the format of the message that is passed
among real and virtual sensors. it keeps the message id, header, content and
the stack that represent the sensors visited before to arrive to the central.
This is use to return a reply.

Central:It model the behaviour of the sink were is keep the globa state of
the parking.
  
RandomSimulator: This class generate changes of status in the virtual sensor
to simulate and automated simulation. If the random simulationis not used, it
is necessary to change the amount of light of the virtual sensor manually.  
For this is use the graphic interface.
********************************Real Sensor*********************************   
Over each sun spot it is deployed and application that model the sensor 
beha





 

