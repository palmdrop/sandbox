<!-- PROJECT LOGO -->
<br />
<p align="center">

  <h1 align="center">Sandbox</h1>

  <p align="center">
    A personal generative playground. Finally on github after multiple requests.

  </p>
</p>

![Object 1](/output/example-texture-oo1.jpg "Digital object one")

<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->
## About The Project

This is a long-lived project containing a collection of generative techniques that I've been working on recently. The purpose of the project is for me to explore generative art and procedural generation, among other things. The structure may be unconventional and the documentation (comments and other) lacking, but please keep in mind that this has for a long time just been hidden away in a private repository. After several requests I've decided to make the code public. 

Since this is just a sandbox, there's no interface for using the application. Instead, the project contains many "main" files each dedicated to a specific concept, and each main file is associated with multiple "sketches" that run related ideas.For now, the source code has to be edited in order to change the settings. A short usage guide can be found under <a href="#usage">usage</a>.

I realize this is more of a library now than anything else, although scattered and probably incoherent. Explore at your own peril, but if you find anything (a class, a module, whatever) you like, feel free to use it in your own projects.

Samples from my experiments are published on my [instagram](https://www.instagram.com/palmdrop/). Occasionally, I also make [blog posts](https://palmdrop.github.io/) where I describe some part of my process.

Here's a few sample images:

![Bridge](/output/example-texture-surface1.png)
![Mess](/output/example-mess1.png)
![Text](/output/example-text1.png)
![Mix](/output/example-imagemix1.png)

More images can be found under `output`.

### Built With

No big frameworks used, however [processing](https://processing.org/) is required. However, the project is not formated as a processing sketch. Instead, the processing core is imported as a library. This was done to ensure that I could use a regular IDE (like IntelliJ IDEA) for organizing the code. More about installation/prerequisites under <a href="#getting-started">getting started</a>.

<!-- GETTING STARTED -->
## Getting Started

I suggest setting up the project in a popular IDE for easy use, since there's no installation script (and as mentioned, no user interface). I'll go through how to set the project using IntelliJ IDEA, but other IDE's shouldn't be much different. 

### Prerequisites

* [JDK 11](https://openjdk.java.net/projects/jdk/11/) (or newer)
* [Processing 3.5.X](https://processing.org/download/) (or newer)

### Installation

1. Install JDK 11 or newer.
2. Install processing 3.5.4 or newer.
3. Clone this repository
   ```sh
   git clone https://github.com/palmdrop/sandbox.git
   ```
3. Setup a java project (below follows some steps for setting up an IntelliJ IDEA project, but any IDE might be used)
    * Open the `sandbox` repository you've just cloned as a project.
    * Under `Project structure -> Project` pick Java 11 as the Project SDK (also make sure the language level is at least 11).
    * Under `Project structure -> SDKs` choose `11` (for Java 11) and then click `classpath`. Press `+` and navigate to where you installed Processing. Pick `processing-3.5.X/core/library/core.jar`. 
4. Run desired main class (all can be found directly under `src`).

<!-- USAGE EXAMPLES -->
## Usage

There's many `main` classes in the project, all dedicated to exploring different concepts. Try running one of them. A window will be spawned and after it finishes loading, you'll hopefully see something exiting on the screen. 

Here's a short description of the purpose of each `main` class:

* `ColorMain`: for generating color palettes and playing around with color spaces.
* `ImageMain`: for mixing images together and analyzing image data.
* `NebulaMain`: for creating organic shapes and landscapes.
* `OrganicMain`: for algorithms simulating plant growth.
* `ChaoticOrganicMain`: a more chaotic take on `OrganicMain`.
* `TextMain`: for analyzing font letters and using them to visualize images or heightmaps. 
* `Texturemain`: similar to `NebulaMain`, but specifically constructed to design interesting textures using noise and domain warping. 
* `VisionMain`: for analyzing any type of raw data and visualizing it in an (hopefully) unconventional way.

Most main classes instantiates a `Sketch` class which is responsible for envisioning a particular idea, experiment or concept. Each sketch is in turn an instantiation of a `Drawer` which takes a `PGraphics` object (native to Processing). This object is drawn to, and then the main class shows the result to the screen. I suggest studying the [Processing documentation](https://processing.org/reference/) to find out more about how this works.

The sketches can be found in the `sketch` package. The names of the the subpackages correspond to the different `main` files. Usually, it's possible to comment out the currently used sketch and instantiate another one from the corresponding package. 

To change the settings of a particular sketch, you'll have to edit the values and setups contained within the sketch itself. Unfortunately, I will not add any detailed instructions for how to use each one. If you're actually interested in running any of them, you'll have to do some experimentation on your own. As mentioned, this project was initially only intended for personal use and nothing else. Maybe in the future I'll create a usable interface for some of the sketches. 

<!-- CONTRIBUTING -->
## Contributing

I do not expect any form of contributing to this project. However, if you do find an especially interesting configuration, or happen to use some of this code for a project of your own, feel free to share. 

<!-- CONTACT -->
## Contact and social media

* [Email](mailto:anton@exlex.se) (the most reliable way of reaching me)
* [Instagram](https://www.instagram.com/palmdrop/) (where I showcase a lot of my work)
* [Blog](palmdrop.github.io) (where I occasionally write posts about my techniques)
