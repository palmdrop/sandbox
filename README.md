<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/othneildrew/Best-README-Template">
    <img src="images/logo.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">Sandbox</h3>

  <p align="center">
    A personal generative playground. Finally on github after multiple requests.

  </p>
</p>



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
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

[![Product Name Screen Shot][product-screenshot]](https://example.com)

This is a long-lived super-project containing a collection of generative techniques I've been working on recently. The structure may be unconventional, and the documentation (comments and other) lacking, but please remember that this has for a long time just been hidden away in a private repository for the sake of personal exploration of generative art and procedural generation, among other things. 

Most of my work is posted on reddit or on my instagram (athough a blog is in the works, link will be up soon), but this project is one of the first steps of actually sharing the code. Since this is just a sandbox, there's no interface for using the application. Instead, the project contains many "main" files for trying out different concepts, and most main files are assoicated with multiple "sketches" that run related ideas. For now, the source code has to be edited in order to change the settings. A short usage guide can be found under <a href="#usage">usage</a>.

I realize this is more of a library now than anything else, although scattered and probably incoherent. Explore at your own peril, but if you find anything (a class, a module, whatever) you like, feel free to use it in your own projects.

Regarding the project structure:
* TODO DESCRIBE DIRECTORIES!

### Built With

No big frameworks used, however [processing](https://processing.org/) is required. The project is not formated as a processing sketch. Instead, the processing core is imported as a library. This was done to ensure that I could use a regular IDE (like intellij IDEA) for organizing the code. More on installation/prerequisites under<a href="#getting-started">getting started</a>.

<!-- GETTING STARTED -->
## Getting Started

I suggest setting up the project in a popular IDE for easy use, since there's no installation script (and as mentioned, no user interface). I'll go through how to set the project up using Intellij IDEA, but other IDE's shouldn't be much different. 

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
3. Setup a java project (for example, using a popular IDE like Intellij IDEA or Eclipse)
    * *Here follows an example setup using Intellij IDEA:* 
    a) Open the `sandbox` repository you've just cloned as a project.
    b) Under `Project structure -> Project` pick java 11 as the Project SDK (Also make sure the language level is at least 11).
    c) Under `Project structure -> SDKs` choose `11` (for Java 11) and then click `classpath`. Press `+` and navigate to where you installed processing. Pick `processing-3.5.x/core/library/core.jar`. 
4. Run desired main class (all can be found under directly under `src`).

<!-- USAGE EXAMPLES -->
## Usage

There's many `main` files in the project. These are all for exploring different concepts. Try running one of them. A window will be spawned and after it finishes loading, you'll hopefully see something exiting on the screen. 



Use this space to show useful examples of how a project can be used. Additional screenshots, code examples and demos work well in this space. You may also link to more resources.

_For more examples, please refer to the [Documentation](https://example.com)_



<!-- ROADMAP -->
## Roadmap

See the [open issues](https://github.com/othneildrew/Best-README-Template/issues) for a list of proposed features (and known issues).



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request



<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE` for more information.



<!-- CONTACT -->
## Contact

Your Name - [@your_twitter](https://twitter.com/your_username) - email@example.com

Project Link: [https://github.com/your_username/repo_name](https://github.com/your_username/repo_name)


<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/othneildrew/Best-README-Template.svg?style=for-the-badge
[contributors-url]: https://github.com/othneildrew/Best-README-Template/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/othneildrew/Best-README-Template.svg?style=for-the-badge
[forks-url]: https://github.com/othneildrew/Best-README-Template/network/members
[stars-shield]: https://img.shields.io/github/stars/othneildrew/Best-README-Template.svg?style=for-the-badge
[stars-url]: https://github.com/othneildrew/Best-README-Template/stargazers
[issues-shield]: https://img.shields.io/github/issues/othneildrew/Best-README-Template.svg?style=for-the-badge
[issues-url]: https://github.com/othneildrew/Best-README-Template/issues
[license-shield]: https://img.shields.io/github/license/othneildrew/Best-README-Template.svg?style=for-the-badge
[license-url]: https://github.com/othneildrew/Best-README-Template/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/othneildrew
[product-screenshot]: images/screenshot.png
o
