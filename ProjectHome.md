# A. News #

## 26/10/2013 Proscene v2.0.0-alpha1, released. New repo and blog sites ##

Today we are happy to announce the beginning of the **Proscene2** cycle, together with a [new repo](https://github.com/remixlab/proscene) kindly hosted at github and a [new blog](http://otrolado.info/). **Proscene1** maintenance releases will still see the light here. Automatic download **Proscene** is (and will only be) available for stable releases.

Read [more news...](News.md).

# B. Description #

**Proscene** (pronounced similar as the Czech word **"prosím"** which means **"please"**) is a java library package which provides classes to ease the creation of interactive 3D scenes in [Processing](http://processing.org/).

**Proscene** has been pretty much inspired in the [Qt's](http://qtsoftware.com/) [OpenGL](http://www.opengl.org/) [C++](http://en.wikipedia.org/wiki/C++) [libqglviewer](http://www.libqglviewer.com/) library from where it borrows the concept of an **interactive frame**, i.e., a coordinate system that can be controlled with the mouse. **Proscene** aims at broadening this idea by allowing the user to easily setup an [HID controlled scene](HIDevice.md). **Proscene** has a very similar functionality and API reference to that found in libqglviewer.

**Proscene** provides seemless integration with **Processing**: its API has been designed to fit that of **Processing** and its implementation has been optimized to work along side with it. One of **Proscene** main implementation goals was to keep it independent of the underlying **Processing** 3D renderer. It has been **tested** with the OPENGL, P3D and GLGRAPHICS renderers and can properly work with any of them.

**Proscene v-1.1.1** (current release) works under Linux, Mac OSX and Windows using **Processing-1.5.1** with no other special dependencies or requirements.

**Proscene** support is led by the active and great Processing community at its [forum](http://forum.processing.org/search/proscene) where you can reach us. If you think you have found a bug, you may open a ticket [here](http://code.google.com/p/proscene/issues/list) at the project website.

# C. Key features #

  * **Tested** under Linux, Mac OSX and Windows, and properly works with the P3D, OPENGL and GLGRAPHICS Processing renderers. No special dependencies or requirements needed (apart of course from [Processing-1.5.1](http://processing.org/)).
  * API design that provides seemless integration with **Processing** (e.g., providing flexible animation and drawing mechanisms), and allows extensibility of its key features, such as full [camera and keyboard customization](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/CameraCustomization/applet/index.html).
  * Default interactivity to your **Processing** scenes through the mouse and keyboard that simply does what you expect.
  * Generic suppport for [Human Interface Devices](http://en.wikipedia.org/wiki/Human_interface_device).
  * Arcball, walkthrough and third person camera modes.
  * Hierarchical coordinate systems (frames), with functions to convert between them.
  * Coordinate systems can easily be moved with the mouse.
  * Keyframes.
  * Object picking.
  * Keyboard shortcuts and camera profiles customization.
  * Animation framework.
  * Screen drawing (i.e., drawing of 2d primitives on top of a 3d scene).
  * Off-screen rendering mode support.
  * Handy set of complete documented examples that illustrates the use of the package, including: [BasicUse](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/BasicUse/applet/index.html), [AlternativeUse](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/AlternativeUse/applet/index.html), [Animation](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/Animation/applet/index.html), [AnimationHandler](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/AnimationHandler/applet/index.html) (requires the [GLGraphics](http://glgraphics.sourceforge.net/) library), [BasicUseOffscreen](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/BasicUseOffscreen/applet/index.html) (requires the [GLGraphics](http://glgraphics.sourceforge.net/) library), [CameraCustomization](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/CameraCustomization/applet/index.html), [CameraInterpolation](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/CameraInterpolation/applet/index.html), [CajasOrientadas](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/CajasOrientadas/applet/index.html), [CameraCrane](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/CameraCrane/applet/index.html) (requires the [saito objloader library](http://code.google.com/p/saitoobjloader/)), [ConstrainedCamera](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/ConstrainedCamera/applet/index.html), [ConstrainedFrame](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/ConstrainedFrame/applet/index.html), [Flock](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/Flock/applet/index.html), [FrameInterpolation](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/FrameInterpolation/applet/index.html), [FrameInteraction](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/FrameInteraction/applet/index.html), [Luxo](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/Luxo/applet/index.html), [Moire](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/Moire/applet/index.html), [PointUnderPixel](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/PointUnderPixel/applet/index.html) (requires OpenGL support on your platform), [MouseGrabbers](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/MouseGrabbers/applet/index.html), [Scramble](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/Scramble/applet/index.html), [ScreenDrawing](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/ScreenDrawing/applet/index.html), [SpaceNavigator](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/SpaceNavigator/applet/index.html) (requires a [space navigator](http://www.3dconnexion.com/products/spacenavigator.html) and the [procontroll library](http://creativecomputing.cc/p5libs/procontroll/)), [StandardCamera](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/StandardCamera/applet/index.html), [ThirdPersonCamera](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/ThirdPersonCamera/applet/index.html),  [ViewFrustumCulling](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/ViewFrustumCulling/applet/index.html), and [Woobik](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/Woobik/applet/index.html). Note that running these examples online requires a Java-enabled browser.
  * A complete [reference documentation](http://www.disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/reference/index.html).
  * Active support and continuous discussions led by the [Processing community](http://forum.processing.org/search/proscene).

# D. Projects using **proscene** known to us #

  * [Anar library](http://anar.ch/) by LaBelle and Nembrini.
  * [OpenProcessing Proscene Classroom](http://www.openprocessing.org/classroom/1158) by [Jacques Maire](http://www.thematica.fr/matica3D/).
  * [PushMePullMe 3D, Interactive real time physics](http://www.expeditionworkshed.org/index.php?mid=2&cid=21&sid=212) by [Gennaro Senatore](http://engd-usar.cege.ucl.ac.uk/profilepreview/view/id/35) in collaboration with [expeditionworkshed](http://www.expeditionworkshed.org/index.php?mid=2&cid=21&sid=212&pid=502).
  * [Just Cause 2 - Player Death by impact - Point Cloud Visualisation](http://www.youtube.com/watch?v=hEoxaGkNcrg&feature=player_embedded#at=19) by [Jim Blackhurst](http://jimblackhurst.com/).
  * [Audio-reactive touch application](http://www.thecreators.tv/) by Constanza Casas, Mark C. Mitchell and Pieter Steyaert.
  * [Generative Artwork for Montblanc](http://www.onformative.com/work/montblanc-artworks/) by [Onformative Studio](http://www.onformative.com/).
  * [Making of Ruins](http://www.flickr.com/photos/luigidealoisio/6005821445/in/photostream) by [Luigi De Aloisio](http://www.flickr.com/photos/luigidealoisio/).
  * [Panoramica 2010](http://vimeo.com/9562290) by Diego Alberti.
  * [Fluidos en RA](http://www.youtube.com/watch?v=GoW1MdDv68Y&feature=player_embedded#at=15) by Diego Alberti.

If your project is using **proscene** don't be shy and let us know ;) It will be great to add yours here!

# E. Origin of the name #

**Proscene** not only means a **"pro-scene"**, but it is a two-phoneme word pronounced similar as the Czech word **"prosím"** (which means **"please"**), obtained by removing the middle phoneme (**"ce"**) of the word **pro-ce-ssing**. Thus, the name **"Proscene"** suggests the main goal of the package, which is to help you _shorten_ the creation of interactive 3D scenes in **Processing**.

# F. Installation Procedure #

The installation procedure follows the standard [Processing contributed library installation procedure](http://wiki.processing.org/w/How_to_Install_a_Contributed_Library) and it is quite straightforward. Just unzip the proscene-x.y.z.zip file and put the extracted proscene folder into the libraries folder of your processing sketches (or directly into your processing's libraries folder). Reference and examples are included in the proscene folder.

# G. Usage #

All library features requires a `Scene` object (which is the main package class) to be instantiated (usually within your sketch setup method). There are three ways to do that:
  1. **Direct instantiation**. In this case you should instantiate your own Scene object at the `PApplet.setup()` function.
  1. **Inheritance**. In this case, once you declare a `Scene` derived class, you should implement `proscenium()` which defines the objects in your scene. Just make sure to define the `PApplet.draw()` method, even if it's empty.
  1. **External draw handler registration**. You can even declare an external drawing method and then register it at the Scene with `addDrawHandler(Object, String)`. That method should return `void` and have one single `Scene` parameter. This strategy may be useful when you have the same drawing code shared among multiple viewers.

See the examples **BasicUse**, **AlternativeUse**, and **StandardCamera** for an illustration of these techniques. To get start using the library and learn its main features, have a look at the complete set of well documented examples that come along with it. Other uses are also covered in the example set and include (but are not limited to): drawing mechanisms, animation framework, and camera and keyboard customization. Advanced users may take full advantage of the fully documented [API reference](http://www.disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/reference/index.html) (which is also included in the package file).

# H. Acknowledgements #

Many thanks to Gilles Debunne for releasing his awesome libqglviewer as free software. Many thanks to [Andres Colubri](http://codeanticode.wordpress.com) for redistributing previous releases of **Proscene** (such as v-0.7.0) along side his fantastic [GLGraphics](http://users.design.ucla.edu/~acolubri/processing/glgraphics/home/index.html) ([v-0.9.3.3-3](http://sourceforge.net/projects/glgraphics/files/glgraphics/0.9.3.3/glgraphics-0.9.3.3.zip/download)) **Processing** library. **Proscene** actually started as a **GLGraphics** component and thanks to Andres contributions it became a stand-alone library (read the whole story [here](http://codeanticode.wordpress.com/2010/03/04/glgraphics-0-9-3-3/)).

<a href='Hidden comment: 
=I. Author, core developer and maintainer=

[http://disi.unal.edu.co/profesores/pierre/ Jean Pierre Charalambos], [http://www.unal.edu.co National University of Colombia]
'></a>