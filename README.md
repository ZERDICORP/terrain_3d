# terrain_3d 🏞️

#### Procedural terrain generation based on Perlin Noise in 3D!

## What is it? :no_mouth:
Well.. I've been playing around with 3D graphics rendering recently.  
And, also, I studied Perlin Noise.  
Don't feel it yet?  
I couldn't resist trying to combine the two :yum: :sweat_drops:

## How it works? :hushed:
> There is not much to talk about here, everything is extremely simple.  
> I can literally do it in 3 steps.

#### Step #1 - Create a heightmap with Perlin Noise
Yes, we just take the perlin noise and find the noise values for all points in the 2d map (the resulting values are the **heightmap**).  
If you want to learn a little more about perlin noise: https://github.com/ZERDICORP/perlin_noise_2d

#### Step #2 - Convert heightmap to 3D point array
Knowing the height values, we can already generate points in 3D space.  
One of the axes should indicate the depth of the terrain (in my case it is the x-axis).  
We apply height values to it.

#### Step #3 - Connect all points with polygons
Yes, this is the last step to create the base terrain.  

Then you can add all sorts of cool things (color depending on the height values, the plane of the water surface, various objects on the map and much, much more).

## How can I try it? :rabbit2:
> I am using IntellijIDEA, btw
#### 1. File to start the program: `src/main/java/just/curiosity/terrain_3d/Main`
#### 2. Interaction with the renderer: https://github.com/ZERDICORP/renderer_3d#interaction-point_right-raised_hands
> To generate a new terrain, press the "R" key

## Screenshot :heart_eyes_cat:
![image](https://user-images.githubusercontent.com/56264511/167263946-6ae42d66-d757-428e-89ad-00a9115325a4.png)
