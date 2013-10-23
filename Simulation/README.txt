********************************Parking application***********************
The code attached is an application that allows counting the number
of spot that are occupied or available in a parking.

To make it possible the idea is to locate a light sensor in each individual parking 
spot  to predict if there is or not a car. This information can been obtaining
observing  the changes of light that it receives.

When a sensor receives a change of light that represents a state modification, 
it changes its state and communicate the value to the central, using initially 
the closest path,  that can be found with the configuration file given in the 
next section. 

If the communication fail sending the message to another sensor, so it tries to achieve
 the central sending the message  via other neighbours to find a new possible path. 
 If there is not any successful path this sensor would show error.

**************************How execute the application*********************
The first step to execute the application is to configure the .csv file with
the locations of the real sensor and the virtual one. The rules to configure
the file are the following:

     * Each position in the parking is separate by a comma ",".
	 * the symbol "-" represents empty space. In other words it is the parking street.
	 * A virtual sensor has the value of  the minimum  number nodes that has to visit 
	   to achieve the central or sink.
	 * A virtual sensor has the value of minimal distance to the central or sink together 
	   with the address of the sensor. The two values are separate	d with  a semi colon.

The next lines shows some examples to configure the file.

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

This model is possible because the sensors that  are deployed in a parking are always 
located in a static way and does not need to be changed frequently.

*******Prerrequisites

***************************Light Sensor Calibration***********************
To use light sensors to predict if a car was allocated in a parking spot,
it was developed a model using the  decision tree J48. To create the 
model first it was collected real data at night, without external light 
and using a carton box  with a lamp to simulate the light of the parking.
The data was collected in the following format:

current light|previous light|cifference|status

With this data the algorithm returns a set of rules that with the current, 
the previous light value and the difference  allow to predict the current status.
 The following rules are the result obtained and used in the implementation 
(See Class simulation.EvaluateLight):

Difference <= -12
|   Current <= 11: car 
|   Current > 11: no change 
Difference > -12
|   Difference <= 10: no change
|   Difference > 10
|   |   Previous <= 11: no car
|   |   Previous > 11: no change

********************************Simulator*********************************
Due to that there were no enough sun spots to test the behaviour of the parking
we create a java simulator that is able to make a simulation where virtual 
and real sensor can interact to increase and decrease the number of spots
available and to find the best path or the path available 
The main classes  that  abstract the behaviour of the main components are:

MainFrame: It is the graphic interface that simulates the parking  and shows
the position that will be occupied for virtual and real sensors.
Sensor: It is an abstract class that defines the behaviour of any kind of sensor. A 
general sensor keeps its status, its neighbours, the best option to 
communicate with the central or sink. It also has the list of neighbours where it 
attempt to send a message, to use a new path, if the sending to the central  was not successful. 

RealSensor: It  Keeps the properties and behaviour necessary to allow the 
communication between the virtual sensors and the real ones. It use the 
classes realSensor.MessageReceptor and realSensor.MessageSender for the 
communication.

VirtualSensor: It represent a virtual sensor that simulate the behaviour 
of real sensor.

Message: Class that represents the format of the message that is passed
among real and virtual sensors. it keeps the message id, header, content and
the stack that represent the sensors visited before to arrive to the central to then 
know the path to send a reply message.
Central: It model the behaviour of the sink were is keep the global state of
the parking.
  
RandomSimulator: This class generate changes of status in the virtual sensor
to simulate and automated simulation. If the random simulation is not used, it
is necessary to change the amount of light of the virtual sensor manually in
 the graphic interface.
********************************Real Sensor*********************************   
Over each sun spot it is deployed and application that model the real sensor 
the main clases are RealSensor where is implemented all the logic to send messages
to other real sensors and to the virtual ones. The other main classes are Receiver
and Sender.




 

