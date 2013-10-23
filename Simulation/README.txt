********************************Parking application***********************
The code attached is an application that simulates a parking lot, where leds
indicate at each slot if it is occupied or vacant. Aditionally it also allows 
counting the number of spots that are occupied or available, to show newcomers
if there are slots available in that parking space.

The idea is to locate a light sensor in each individual parking 
spot to predict if there is or there is not a car. This information can been obtained
by observing the changes of light that it receives. When a car arrives the state
of the slot must change from vacant to occupied, and when it leaves from occupied
to vacant. To have accurate estimation of the light difference that represents a change
of status it was necessary to train a decision tree using an scaled model of a parking lot.
This model can be read from csv files. 

A central application, called Parking Simulator, will keep the sensor network model and the
global status of cars running and slots available. When a sensor receives a change of light 
it changes its state and communicates the value to the central application. The communication 
uses the shortest path initially. If the communication fails, it tries to achieve sending 
the message via other neighbours to find a new possible path. If there is no successful path 
this sensor will show an error.

Due to the low avaibility of real sensors, the applicaton allows you to set up Virtual Sensors
in your network that will work exactly the same as the real ones. 

 *********************Prerequisites  to install the application***********************
To  install the application the next prerequisites are needed:
1. NetBeans  7.3.1
2. Sun SPOTManager Version 5.1.3-20120424
3. Ant 1.8 

The file installation.pdf (taken from the tutorials) with the instructions 
of installation is attached in root folder. 	

**************************How to execute the application*********************
The first step to execute the application is to configure the .csv file with
the locations of the real sensors and the virtual ones. The rules to configure
the file are the following:

     * Each position in the parking is separate by a comma ",".
	 * the symbol "-" represents empty space. In other words it is the parking street.
	 * A virtual sensor has the value of  the minimum  number nodes that has to visit 
	   to achieve the central or sink.
	 * A real sensor has the value of minimal distance to the central or sink together 
	   with the real address of the sensor. The two values are separated with a semicolon.
	 * A distance of 0 means that the specified Sensor is connected to the basestation.

The next lines show some examples of how to configure the file.

#file1.csv. just with virtual sensors
-,-,-,-,-
3,2,1,0,-
3,2,1,0,-
-,-,-,-,-

#file2.csv. just with one real sensor
-,-,-,-,-
3,2,1;0014.4F01.0000.5720,0,- 
3,2,1,0,-
-,-,-,-,- 

This model is possible because the sensors that are deployed in a parking are always 
located in a static way and does not need to be changed frequently.

The files must be located in the maps folder of the Simulation application. There are 
some examples included on this deliverable. A copy of the file must be also located
at the application folder for the MIDlet that runs in the real sensor. This folder is
called RealSensorApplication. Inside this folder the files must be located at 
resources/com/parking/.

After configuring the file the next step is to deploy the application in the real 
sensor. For this task it is necesary to:

* Modify the property basestation in the class Sensor.java with the address of the 
  base station that communicates with the central application and the virtual sensors.
* Modify the property MatrixFile in the file MANIFEST.MF with the name of the file
  that has the parking configuration.
* Compile and install the application in each Sun SPOT connected through the USB port
  of the computer and executing the following:
  - In linux/mac (command line): RealSensor.sh
  - In linux/mac (clickable): RealSensor.command
  - In windows: RealSensor.bat (please run as administrator) 
   
The next step is to move to the Simulation folder that contains the central application
and execute the following:

  * In linux/mac (command line): ParkingSimulator.sh
  * In linux/mac (clickable): ParkingSimulator.command
  * In windows: ParkingSimulator.bat (please run as administrator) 

***************************Light Sensor Calibration***********************
To use light sensors to predict if a car was allocated in a parking spot,
a model was developed using the decision tree J48. To create the 
model real data was collected. For the experiments it was necessary to simulate 
the behaviour of the sensor when a car blocks and unblocks the light. 
This experiments were performed at night, without external light and using
a carton box with a lamp to simulate the light of the parking.

The data was collected in the following format:

current light|previous light|difference|status

With this data the algorithm returns a set of rules that with the current, 
the previous light value and the difference allows the prediction of the current status.
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
Due to that there were not enough sun spots to test the behaviour of the parking
we created a java application that is able to make a simulation where virtual 
and real sensor can interact to increase and decrease the number of spots
available and to find the best path or alternate paths available. 
The main classes that abstract the behaviour of the main components are:

MainFrame: It is the graphic interface that simulates the parking  and shows
the position that will be occupied for virtual and real sensors.

Sensor: It is an abstract class that defines the behaviour of any kind of sensor. A 
general sensor keeps its status, its neighbours, the best option to 
communicate with the central or sink. RealSensor and VirtualSensor implement this
class. 

RealSensor: It  Keeps the properties and behaviour necessary to allow the 
communication between the SunSPOTS, and the central application and virtual sensors. 
It uses the classes realSensor.MessageReceptor and realSensor.MessageSender for the 
communication.

VirtualSensor: It represent a virtual sensor that simulate the behaviour 
of real sensor.

Message: Class that represents the format of the message that is passed
among real and virtual sensors. it keeps the message id, header, content and
the stack that represents the sensors visited in order to have a the path
 to send a reply message.
 
Central: It models the behaviour of the sink were is keep the global state of
the parking.
  
RandomSimulator: This class generates changes of status in the virtual sensor
to simulate and automated simulation. If the random simulation is not used, it
is necessary to change the amount of light of the virtual sensor manually in
 the graphic interface. 
 
********************************Real Sensor*********************************   
The application that runs on SunSPOTs is in a different project called RealSensorApplication.
It has to be deployed in each SunSPOT. RealSensor is the class that implements all the logic 
to send messages.  The other main classes are Receiver and Sender, that communicate with the 
interface RealSensor in the Simulator application.




 

