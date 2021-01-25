<!-- PROJECT LOGO -->
<br />
<p align="center">

  <h1 align="center">Sandbox</h1>

  <p align="center">
    A personal generative playground. Finally on github after multiple requests.

  </p>
</p>

![Object 1](/output/example-texture-blob2.jpg "Digital object one")

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
    <li><a href="#usage">Usage</a></i>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#contact">Contact and social media</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->
## About The Project

This is a long-lived project containing a collection of generative techniques that I've been working on recently. Its purpose is to help me explore code-driven art and procedural generation, as well as related topics. The structure of the project may be unconventional and the documentation lacking, but please keep in mind that all this has been hidden away in a private repository for a long time, intended for personal use only. However, after several requests, I've decided to make the code public (I've done some work on cleaning up the code, but here and there you might just find strange reminders and messages that I wrote to myself). 

Since this is just a sandbox project (i.e, my personal playground), there's no user interface. Instead, the project contains many "main" files, each dedicated to a specific concept. Each main file is associated with multiple "sketches" that run related ideas. For now, the source code has to be edited in order to change the settings. A short usage guide can be found under <a href="#usage">usage</a>.

I realize this is more of a library now than anything else, although a scattered and incoherent one. Explore at your own peril. If you find anything (a class, a module, whatever) you like, feel free to use it in your own projects.

Samples from my experiments are published on my [instagram](https://www.instagram.com/palmdrop/). Occasionally, I also make [blog posts](https://palmdrop.github.io/) describing some parts of my process.

Here are a few sample images:

![Bridge](/output/example-texture-surface1.png)
![Mess](/output/example-mess1.png)
![Text](/output/example-text1.png)
![Mix](/output/example-imagemix1.png)

More can be found under `output`.

### Built With

The project is built with the aid of [Processing.](https://processing.org/) However, the code is not formatted as a processing sketch. Instead, the processing core is imported as a library to ensure that I could use a regular IDE (like IntelliJ IDEA) for organizing the code. More about installation/prerequisites under <a href="#getting-started">getting started</a>.

(A parenthesis about using Processing with any IDE: [this](https://happycoding.io/tutorials/java/processing-in-java) is a great blog post explaining how it can be done.)

<!-- GETTING STARTED -->
## Getting Started

I suggest setting up the project in a popular IDE for easy use since there's no installation script (and as mentioned, no user interface). I'll go through how to run the project using IntelliJ IDEA, but the process shouldn't be very different from other IDE's.

### Prerequisites

* [JDK 11](https://openjdk.java.net/projects/jdk/11/) (or newer)
* [Processing 3.5.4](https://processing.org/download/) (or newer)

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

There are many `main` classes in the project, all dedicated to exploring different concepts. Try running one of them. A window will be spawned and after it finishes loading, you'll hopefully see something exciting on the screen. 

Here's a short description of the purpose of each `main` class:

* `ColorMain`: for generating color palettes and playing around with color spaces.
* `ImageMain`: for mixing images together and analyzing image data.
* `NebulaMain`: for creating organic shapes and landscapes.
* `OrganicMain`: for algorithms simulating plant growth.
* `ChaoticOrganicMain`: a more chaotic take on `OrganicMain`.
* `TextMain`: for analyzing font letters and using them to visualize images or heightmaps. 
* `Texturemain`: similar to `NebulaMain`, but specifically designed to produce interesting textures using noise and domain warping. 
* `VisionMain`: for analyzing any type of raw data and visualizing it in a (hopefully) unconventional way.

Most main classes instantiate a `Sketch` class which is responsible for envisioning a particular idea, experiment, or concept. Each sketch is in turn an instantiation of a `Drawer` which takes a `PGraphics` object (native to Processing). This object is drawn to, and then the main class shows the result to the screen. I suggest studying the [Processing documentation](https://processing.org/reference/) to find out more about how this works.

The sketches can be found in the `sketch` package. The names of the subpackages correspond to the different `main` files. Usually, it's possible to comment out the currently used sketch and instantiate another one from the corresponding package. 

To change the settings of a particular sketch, you'll have to edit the values and instantiated objects contained within the sketch itself. Unfortunately, I will not add any detailed instructions for how to use each one. If you're interested in running any of them, you'll have to do some experimentation on your own. As mentioned, this project was initially intended for personal use and nothing else. Maybe in the future, I'll create a user interface for some of the sketches. 

General source data used by some of the sketches can be found under `sourceData`. Source images specifically can be found under `sourceImages`. You may have to change the paths used in certain sketches since I have excluded many of the source files from this repository due to privacy and copyright conerns. 

<!-- CONTRIBUTING -->
## Contributing

I do not expect any contributions to this project. However, if you do find an especially interesting configuration, or happen to use some of this code for a project of your own, feel free to share. 

<!-- CONTACT -->
## Contact and social media
:mailbox_with_mail: [Email](mailto:anton@exlex.se) (the most reliable way of reaching me)

:camera: [Instagram](https://www.instagram.com/palmdrop/) (where I showcase a lot of my work)

:computer: [Blog](https://palmdrop.github.io/) (where I occasionally write posts about my process)

