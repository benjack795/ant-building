# ant-building
Final Year Undergraduate Project at Sussex University
Modelling Nest-Building Behaviour in Leptothorax and its Response to Environmental Stimuli 
Java based system using the Box2D physics engine.

Leptothorax ants build nests by moving debris around their brood in flat rock crevices. 
In 1992, Franks et al. created a static collective behaviour model, correlating with real ant data. 
I recreated the original model in a modern physics engine, and investigated its robustness by adding in negative weather-based environmental effects.

In order to run this project, compile the LeptoSimu folder as a project using the IntelliJ Java IDE.
Settings can then be adjusted from the menu interface before pressing START.

Adjustable parameters include:
-Number of building ants
-Number of immobile central brood ants to build around
-Number of boulders in the environment to pick up and deposit
-Dropping time (builder ants drop boulders a given time period after colliding with the brood)
-Wind directon
-Wind force (10% chance wind is applied as a global force to all objects in the simulation each second)
-Puddle Frequency (how often puddle objects that block ants are spawned)
-Max puddle width 
-Excavation mode (models extreme area filled with debris)
-Active brood (models moving central brood ants)

Simulation can be paused with P.
