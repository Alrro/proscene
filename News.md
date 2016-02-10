# 20/07/2013 **Proscene v1.2.0**, "Lirio-de-Mayo" released #

We are pleased to announce the immediate availability of **proscene v1.2.0**, supporting the recent **Processing-2.0.1**. Download it directly through the new Processing PDE "import library" function. You may also download it [here](http://proscene.googlecode.com/files/proscene-1.2.0.zip) and install it manually by yourself.

This release introduces the following features (respect to **Proscene-v1.1.1**):

  * **CAD Camera Profile:** This new camera profile mimics the behavior of that found in some CAD applications. To enable it just register it at the scene, e.g., `scene.registerCameraProfile(new CameraProfile(scene, "CAD_CAM", CameraProfile.Mode.CAD))`. Have a look at the new **CameraHandling.CadCamera** example which also illustrates how to set the camera CAD axis.
  * **iFrame damping actions:** Proscene approach to damping regards it as special case of spinning/translating with _friction_ which is defined with the (new) methods `InteractiveFrame.setSpinningFriction` and `InteractiveFrame.setTossingFriction`. Just bear in mind that damping is disabled by default (which fits current mouse behaviour). Have a look at the new **Basics.MouseSensitivities** example which also illustrates the setting of all variables involved in mouse interaction.
  * The ability to set the scene as _left-handed_ (as within **Processing** by default) or _right handed_, see `scene.setLeftHanded()` and `scene.setRightHanded()`, respectively (check also the **CadCamera** example)

packaged with some of the fantastic [proscene class room sketches](http://www.openprocessing.org/classroom/1158) by Jacques Maire, found under the **Geometry**, **Quaternions**, and **Shapes** categories of the examples.

Be aware of the following API changes that have been made (respect to **Proscene-v1.1.1**):

  * The API used to define keyboard shortcuts and mouse bindings has slightly changed, and now looks even simpler. For instance, `setCameraMouseBinding((Scene.Modifier.SHIFT.ID | Scene.Modifier.ALT.ID | Scene.Button.LEFT.ID)`, Scene.MouseAction.ROTATE) which would have bound SHIFT + ALT + mouse left button to camera rotation, has become: `setCameraMouseBinding((Event.SHIFT | Event.ALT), LEFT, Scene.MouseAction.ROTATE)`. You might be able to guess what all the others look like (please refer to the **CameraProfile** documentation for the details). Note, however, that `Processing.Event` key modifier constants differ from their `Processing.PApplet` counterparts, and that you should **only** use the former ones within **Proscene**.
  * `Frame.applyTransformation(PGraphics3D p3d)` has become `Frame.applyTransformation(Scene scn)`. Equivalently, you may also call the new `Scene.applyTransformation(Frame frame)` method.
  * `Scene.coords()` which was needed by ScreenDrawing has been marked as deprecated. All 2D primitive drawing using screen coordinates should now just be simply enclosed between `beginScreenDrawing()` and `endScreenDrawing()` (please refer to the ScreenDrawing example).

Thanks to Julian Nembrini, one of the main developers of the [Anar library](http://anar.ch/) (which now uses proscene as its default camera), and Gennaro Senatore from the [Expedition Engineering](http://www.expedition.uk.com), <a href='Hidden comment:  and Juan Pablo Bonilla and Julián Durán (for the new *InverseKinematics* example which is found under the *FrameHandling* category), '></a> for their collaborations/contributions regarding this release. To all **Proscene** users thank you for your continuous feedback and for playing around with it!

Users of **Processing 1.5.x** may download [Proscene (v1.1.1)](http://proscene.googlecode.com/files/proscene-1.1.1.zip) which is the last version supporting it.

# 04/06/2013 **Proscene v1.2.0-beta2 (a.k.a. v-1.1.97)** "Casabe wanted" released #

We are pleased to announce the immediate availability of **proscene v1.2.0-beta2**, supporting the recent **Processing-2.0** release. Download it directly through the new Processing PDE "import library" function. You may also download it [here](http://proscene.googlecode.com/files/proscene-1.1.97.zip) and install it manually by yourself.

MouseShortcuts were slightly modified to deal with a Processing [mouse issue](https://github.com/processing/processing/issues/1693) which was preventing proper scene interactivity. Hopefully this is the last 1.2-beta release, so please give it a thorough testing and report any issue you may find to help us fine tune it before the final 1.2 release.

# 14/12/2012 **Proscene v1.2.0-beta1 (a.k.a. v-1.1.96)** "Tumaco Head-_in_" released #

We are pleased to announce the immediate availability of **proscene v1.2.0-beta1**, supporting the recent **Processing-2.0-beta7** release. Download it directly through the new Processing PDE "import library" function. You may also download it [here](http://proscene.googlecode.com/files/proscene-1.1.96.zip) and install it manually by yourself.

This release supports the new [Processing event model](http://code.google.com/p/processing/wiki/LibraryBasics) which completely replaces the [Java awt one](http://docs.oracle.com/javase/6/docs/api/java/awt/event/package-summary.html), previously used. Important notes regarding it:

  1. **API changes**
    * Since modifier keys are now directly implemented in **Processing**, some **Proscene** internal enum classes have been removed: `Scene.Button`, `Scene.Arrow`, and `Scene.Modifier`.
    * For the above reason, the API used to define keyboard shortcuts and mouse bindings has slightly changed, and now looks even simpler. For instance, `setCameraMouseBinding((Scene.Modifier.SHIFT.ID | Scene.Modifier.ALT.ID | Scene.Button.LEFT.ID)`, Scene.MouseAction.ROTATE) which would have bound SHIFT + ALT + mouse left button to camera rotation, has become: `setCameraMouseBinding((Event.SHIFT | Event.ALT), LEFT, Scene.MouseAction.ROTATE)`. You might be able to guess what all the others look like (please refer to the **CameraProfile** documentation for the details). Note, however, that `Processing.Event` key modifier constants differ from their `Processing.PApplet` counterparts, and that you should **only** use the former ones within **Proscene**.
  1. **Known issues**
    * As of **Processing-2.0-beta7** the mouse wheel is no longer supported. However, its [re-adoption is scheduled](http://code.google.com/p/processing/issues/detail?id=1423) for a future release. In the meantime, you can pressed and drag the CENTER mouse button which zooms in (and out) by default.
    * Since the new event model doesn't support [extended modifier masks](http://docs.oracle.com/javase/6/docs/api/java/awt/event/InputEvent.html#getModifiersEx()), there's no longer possible to bind a **proscene** action to a combination of mouse buttons which represents, admittedly, a rare use case. Fortunately, it's still possible to bind actions to any combination of modifier keys + any _single_ button (as in the above example).

Please refer to the **Basics.PointUnderPixel**, and the **CameraHandling.CameraCustomization** examples which illustrate the new mouse and keyboard customization dialect, and let us know any issue or suggestion you may have.

Users of **Processing 1.5.x** may download [Proscene (v1.1.1)](http://proscene.googlecode.com/files/proscene-1.1.1.zip) which is the last version supporting it.

# 6/12/2012 **Proscene v1.2.0-alpha4 (a.k.a. v-1.1.95)** "Ibagué dream" released #


---


**11/12/12 Important notice**: Please bear in mind that this release **will not work** with **Processing-2.0-beta7** due to important [changes introduced to the new event model](https://forum.processing.org/topic/using-2-0b7-p3d-with-contributed-libraries). We hope to fix it soon, once we gather more information about it. Please stay tuned...


---


We are pleased to announce the immediate availability of **proscene v1.2.0-alpha4**, supporting **Processing-2.0-beta6**. Download it directly through the new Processing PDE "import library" function. You may also download it [here](http://proscene.googlecode.com/files/proscene-1.1.95.zip) and install it manually by yourself.

This release introduces the ability to set the scene as _left-handed_ (as within **Processing** by default) or _right handed_, see `scene.setLeftHanded()` and `scene.setRightHanded()`, respectively (check also the **CadCamera** example). This release also includes various more of [Jacques Maire](http://www.alcys.com/) fantastic sketches, some of which may also be found at the [OpenProcessing Proscene Classroom](http://www.openprocessing.org/classroom/1158). These new sketches are found under the **Geometry**, **Quaternions**, and **Shapes** categories of the examples. Finally, thanks to Juan Pablo Bonilla and Julián Durán for the new **InverseKinematics** example which is found under the **FrameHandling** category.

This release completes all of the new features which are expected to be part of the **proscene-v1.2** cycle, and thus it (hopefully) will be the last alpha. As usual, suggestions and bug reports are more than welcome.

Users of **Processing 1.5.x** may download [Proscene (v1.1.1)](http://proscene.googlecode.com/files/proscene-1.1.1.zip) which is the last version supporting it.

# 9/11/2012 Release of **Proscene v1.2.0-alpha3 (a.k.a. v-1.1.94)** #

We are pleased to announce the immediate availability of **proscene v1.2.0-alpha3**, supporting the recent **Processing-2.0-beta6** release. Download it directly through the new Processing PDE "import library" function. You may also download it [here](http://proscene.googlecode.com/files/proscene-1.1.94.zip) and install it manually by yourself.

Thanks to the collaborations/contributions with/from Julian Nembrini, one of the main developers of the [Anar library](http://anar.ch/) (which now uses proscene as its default camera), and Gennaro Senatore from the [Expedition Engineering](http://www.expedition.uk.com), this release introduces two new features:

  1. **CAD Camera Profile:** This new camera profile mimics the behavior of that found in some CAD applications. To enable it just register it at the scene, e.g., `scene.registerCameraProfile(new CameraProfile(scene, "CAD_CAM", CameraProfile.Mode.CAD))`. Have a look at the new **CameraHandling.CadCamera** example which also illustrates how to set the camera CAD axis.
  1. **iFrame damping actions:** Proscene approach to damping regards it as special case of spinning/translating with _friction_ which is defined with the (new) methods `InteractiveFrame.setSpinningFriction` and `InteractiveFrame.setTossingFriction`. Just bear in mind that damping is disabled by default (which fits current mouse behaviour). Have a look at the new **Basics.MouseSensitivities** example which also illustrates the setting of all variables involved in mouse interaction.

Give it a go and don't hesitate to provide us some feedback to help us improve these new features.

Users of **Processing 1.5.x** may download [Proscene (v1.1.1)](http://proscene.googlecode.com/files/proscene-1.1.1.zip) which is the last version supporting it.

# 29/09/2012 Release of **Proscene v1.2.0-alpha2 (a.k.a. v-1.1.93)** #

We are pleased to announce the immediate availability of **proscene v1.2.0-alpha2**, supporting the recent **Processing-2.0-beta3** release. Download it directly through the new Processing PDE "import library" function. You may also download it [here](http://proscene.googlecode.com/files/proscene-1.1.93.zip) and install it manually by yourself (for instance if you get a "could not move library to sketchbook" error message using the above method).

This release is mainly a port of Proscene-1.1.1 to **Processing-2.0** (>=beta3) that completely fixes the old [screen drawing issue](http://code.google.com/p/proscene/issues/detail?id=1&can=1) (please refer to the ScreenDrawing example), and also includes some of the great sketches found at the [proscene class room](http://www.openprocessing.org/classroom/1158) by Jacques Maire. Expect for a series of alpha/beta releases in syncing with those of **Processing-2.0**.

Be aware of the following API changes that have been made:

  * `Frame.applyTransformation(PGraphics3D p3d)` has become `Frame.applyTransformation(Scene scn)`. Equivalently, you may also call the new `Scene.applyTransformation(Frame frame)` method.
  * `Scene.coords()` which was needed by ScreenDrawing has been marked as deprecated. All 2D primitive drawing using screen coordinates should now just be simply enclosed between `beginScreenDrawing()` and `endScreenDrawing()` (please refer to the ScreenDrawing example).

Users of **Processing 1.5.x** may download [Proscene (v1.1.1)](http://proscene.googlecode.com/files/proscene-1.1.1.zip) which is the last version supporting it.

# 10/08/2012 Release of **Proscene v1.2.0-alpha1 (a.k.a. v-1.1.92 or just Belo Horizonte Release)** #

We are pleased to announce the immediate availability of **proscene v1.2.0-alpha1**, supporting the recent **Processing-2.0-alpha8** release. Download it directly through the new Processing PDE "import library" function. You may also download it [here](http://proscene.googlecode.com/files/proscene-1.1.92.zip) and install it manually by yourself (for instance if you get a "could not move library to sketchbook" error message using the above method).

This is the first release supporting **Processing-2.0** (>= alpha8) which follows after the [Belo Horizonte HackLab](http://www.hacklab.art.br/) where some bugs relating the port to **Processing-2.0** have been detected and just fixed afterwards. This release is mainly a port of Proscene-1.1.1 to Processing-2.0 (>= alpha8) that completely fixes the old  [screen drawing issue](http://code.google.com/p/proscene/issues/detail?id=1&can=1) (please refer to the ScreenDrawing example), and also includes some of the great sketches found at the [proscene class room](http://www.openprocessing.org/classroom/1158) by Jacques Maire. Expect for a series of alpha/beta releases in syncing with those of **Processing-2.0**.

Be aware of the following API changes that have been made:

  * `Frame.applyTransformation(PGraphics3D p3d)` has become `Frame.applyTransformation(Scene scn)`. Equivalently, you may also call the new `Scene.applyTransformation(Frame frame)` method.
  * `Scene.coords()` which was needed by ScreenDrawing has been marked as deprecated. All 2D primitive drawing using screen coordinates should now just be simply enclosed between `beginScreenDrawing()` and `endScreenDrawing()` (please refer to the ScreenDrawing example).

Users of **Processing 1.5.x** may download [Proscene (v1.1.1)](http://proscene.googlecode.com/files/proscene-1.1.1.zip) which is the last version supporting it.

# 20/01/2012 Release of **Proscene v1.1.1** #

Following our six months release cycle, we are pleased to announce the immediate availability of proscene v1.1.1 (download it [here](http://proscene.googlecode.com/files/proscene-1.1.1.zip)). This is mostly a bug-fix release. Changelog:

  1. **Fixed** excessive instantiation of timers that were performed in various places throughout the library. Now they're only instantiated when they're needed :)
  1. A **_cache_** for some camera matrix operations that are commonly employed has been implemented. Applications continuously calling `camera().project` (e.g., those using multiple ―perhaps thousands of― mouse grabbers) should immediately perceive the benefit. Following the same pattern, `camera.unproject` has also been optimized, but to enable caching of the involved operations `camera().unprojectCacheIsOptimized` must explicitly be called. The latter is disabled by default, since applications heavily relying on this operation (such as the [PointUnderPixel](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/PointUnderPixel/applet/index.html) example) are not the most common.
  1. The new method `scene.hideCursorOnFirstPerson(boolean hide)` allows the `Scene.MouseAction.LOOK_AROUND` to be performed by just moving the mouse (instead of dragging it). The **ThirdPersonCamera** example illustrates this feature. Note that `scene.hasMouseTracking()` is not affected by this new method. The `scene.cursorIsHiddenOnFirstPerson()` is `false` by default.

This version of **proscene** only works with **Processing-1.5.x** and (hopefully) will be the last version supporting it. Development from now on is heading towards **Processing-2.x**. To all **proscene** users thank you for your continuous feedback and for playing around with it!

# 28/06/2011 Release of **Proscene v1.1.0** #

We are pleased to announce the immediate availability of proscene v1.1.0 (download it [here](http://proscene.googlecode.com/files/proscene-v1.1.0.zip)). Changelog:

  1. Generic suppport for [Human Interface Devices (HID)](http://en.wikipedia.org/wiki/Human_interface_device) has been implemented. Learn how to use it [here](HIDevice.md).
  1. Off-screen rendering mode has seen great improvements and it  now supports mouse tracking and screen drawing. See all the examples using off-screen rendering: [CameraCrane](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/CameraCrane/applet/index.html), [StandardCamera](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/StandardCamera/applet/index.html), [ViewFrustumCulling](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/ViewFrustumCulling/applet/index.html) and [BasicUseOffscreen](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/BasicUseOffscreen/applet/index.html).
  1. Screen drawing (i.e., drawing of 2d primitives on top of a 3d scene) has been completely rewritten based on the new `matrixMode` introduced in **Processing-1.5.x**, which means:
    * This version of **proscene** only works with **Processing-1.5.x**.
    * The [flickering issue](http://code.google.com/p/proscene/issues/detail?id=1) with screen drawing has been fixed.
    * The background is no longer handled by **proscene**. Use the **Processing's** built-in [background()](http://processing.org/reference/background_.html) method instead (as it is expected in the first place).
  1. New exciting examples: [CameraCrane](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/CameraCrane/applet/index.html), [SpaceNavigator](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/SpaceNavigator/applet/index.html) (requires the [3d connexion space navigator](http://www.3dconnexion.com/products/spacenavigator.html)), and [Woobik](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/Woobik/applet/index.html).

Many thanks to [Andres Colubri](http://interfaze.info/) (for his continuous support), [Jim Blackhurst](http://jimblackhurst.com/) (for his awesome [Just Cause 2 - Player Death by impact - Point Cloud Visualisation](http://www.youtube.com/watch?v=hEoxaGkNcrg&feature=player_embedded#at=19)),  Mark C. Mitchell (for his awesome [audio-reactive touch application](http://www.thecreators.tv/) which uses the new **HIDevice** **proscene** class), Camilo Salomón ([Woobik](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/Woobik/applet/index.html) example), Ivan Chinome and Cesar Montañez ([CameraCrane](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.1.0/demos/CameraCrane/applet/index.html) example), Eduardo Moriana and Miguel Parra (first kinect experiments), and to all supporters and enthusiasts which gave us some great feedback :)

# 25/05/2011 Release of **Proscene v-1.1.0-beta1 (a.k.a. v-1.0.7)** #

This is the first (and hopefully last) beta release of the next version of **proscene** targeted at testers that wish to give us some feedback (download it [here](http://proscene.googlecode.com/files/proscene-1.0.7.zip)).

Changelog (respect to the [previous alpha release](News.md)):

  1. Screen drawing (i.e., drawing of 2d primitives on top of a 3d scene) has been completely rewritten based on the new `matrixMode` introduced in **Processing-1.5.x**, which means:
    * This version of **proscene** only works with **Processing-1.5.x**.
    * The [flickering issue](http://code.google.com/p/proscene/issues/detail?id=1) with screen drawing has been fixed.
    * The background is no longer handled by **proscene**. Use the **Processing's** built-in [background()](http://processing.org/reference/background_.html) method instead (as it is expected in the first place).
    * Screen drawing works best in **P3D** since there's an [issue with it and the OPENGL renderer](http://code.google.com/p/processing/issues/detail?id=691). Actually, the screen drawing **OPENGL** implementation is based on a different approach which uses `camera().unprojectedCoordinatesOf` (and it is not computationally optimized) .
  1. Off-screen rendering mode has been further improved and now it supports mouse tracking and screen drawing.

# 20/04/2011 Release of **Proscene v-1.1.0-alpha1 (a.k.a. v-1.0.6)** #

This is the first alpha release of the next version of **proscene** targeted at testers and early enthusiasts that wish to give us some feedback (download it [here](http://proscene.googlecode.com/files/proscene-1.0.6.zip)). Development has been focused in the following arenas:

  * Works with the latest [Processing](http://processing.org/) version (1.5). The current stable version of **proscene (1.0.0)** should do it too.
  * Fixes a bug in the `Camera.sphereIsVisible` test needed to properly implement view-frustum-culling.
  * **Off-screen-rendering** has been greatly improved which means **proscene** examples with multiple viewers running in a single skecth no longer require the **napplet** dependency.
  * Generic suppport for [Human Interface Devices](http://en.wikipedia.org/wiki/Human_interface_device) has been implemented.

The main goal of this release has been to add support for Human Interface Devices (HID) with six (or less) [degrees-of-freedom](http://en.wikipedia.org/wiki/6DOF) to control both the camera and any interactive frame attached to the **scene**. This has been done through the new **HIDevice** class. An **HIDevice** object can be of one of two types: **RELATIVE** or **ABSOLUTE**:

  * A **RELATIVE HIDevice** (default configuration) has a neutral position that the device holds when it is not being manipulated. Examples of such devices are the [3d space navigator](http://en.wikipedia.org/wiki/Space_navigator) and the [joystick](http://en.wikipedia.org/wiki/Joystick).
  * An **ABSOLUTE HIDevice** has no such neutral position. Examples of ABSOLUTE devices are the [wii](http://en.wikipedia.org/wiki/Wii) and the [kinect](http://en.wikipedia.org/wiki/Kinect).

Learn [how to use an HIDevice...](HIDevice.md)

# 14/12/2010 Release of **Proscene v-1.0.0** #

Our Christmas mushroom is out for you to grab it! We are pleased to announce the immediate availability of proscene v-1.0.0. Changelog:

  1. **Keyboard shortcuts and camera profiles customization**. Keyboard shortcuts which define global actions (such as drawing of the world axis), and mouse bindings which define how camera actions are binded to the mouse, are fully customizable. Their default behavior should cover most user needs out there though. See the new example [CameraCustomization](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.0.0/demos/CameraCustomization/applet/index.html).
  1. **New animation framework.** The framework comprises three animation mechanisms to define how your scene evolves over time:
    1. **Overriding the animate() method.** In this case, once you declare a Scene derived class, you should implement _animate()_ which defines how your scene objects evolve over time. See the new example [Animation](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.0.0/demos/Animation/applet/index.html).
    1. **External animation handler registration.** You can also declare an external animation method and then register it at the Scene with _addAnimationHandler(Object, String)_. That method should return `void` and have one single `Scene` parameter. See the new example [AnimationHandler](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.0.0/demos/AnimationHandler/applet/index.html).
    1. **By querying the state of the animatedFrameWasTriggered variable.** During the drawing loop, the variable _animatedFrameWasTriggered_ is set to `true` each time an animated frame is triggered (or it is set to `false` otherwise), which is useful to notify the outside world when an animation event occurs. See the example [Flock](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.0.0/demos/Flock/applet/index.html) which has been ported to the new animation framework using this technique.
  1. **New off-screen rendering mode support**. Off-screen rendering mode allows to map your scene contents to a texture and then you can do whatever you want with it. Check the new example [BasicUseOffscreen](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.0.0/demos/BasicUseOffscreen/applet/index.html).
  1. **New and improved examples** which illustrates many aspects of the library. New examples are: [Animation](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.0.0/demos/Animation/applet/index.html), [AnimationHandler](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.0.0/demos/AnimationHandler/applet/index.html), [BasicUseOffscreen](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.0.0/demos/BasicUseOffscreen/applet/index.html), [CameraCustomization](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.0.0/demos/CameraCustomization/applet/index.html) and [Scramble](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.0.0/demos/Scramble/applet/index.html). Improved examples are: [CameraInterpolation](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.0.0/demos/CameraInterpolation/applet/index.html), [MouseGrabbers](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.0.0/demos/MouseGrabbers/applet/index.html) and [Flock](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-1.0.0/demos/Flock/applet/index.html).

Many thanks to [Andres Colubri](http://codeanticode.wordpress.com) for all his contributions to the project which includes, among many, off-screen rendering mode. Many thanks also to Alejandro Duarte for his cool Scramble demo. Many thanks also to other contributors, testers and followers!

# 18/07/2010 Release of **Proscene v-0.9.0** #

Version 0.9.0 of **Proscene** is out. Changelog:

  * New examples: [Flock](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-0.9.0/demos/Flock/applet/index.html), [StandardCamera](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-0.9.0/demos/StandardCamera/applet/index.html), [ThirdPersonCamera](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-0.9.0/demos/ThirdPersonCamera/applet/index.html) and [ViewFrustumCulling](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-0.9.0/demos/ViewFrustumCulling/applet/index.html).
  * New scene external draw handler registration (see the Scene documentation for details).
  * New analytical computation of the frustum planes equations which enables view frustum culling against the proscene camera.
  * New THIRD\_PERSON camera mode. How does it work?
    * Simply add an _InteractiveAvatarFrame_ (which is an specialization of an _Avatar_) to your scene: call `scene.setInteractiveFrame(myInteractiveAvatarFrame)` (see the example [ThirdPersonCamera](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-0.9.0/demos/ThirdPersonCamera/applet/index.html)) or  `scene.setAvatar(myAvatar)` (see the example [Flock](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-0.9.0/demos/Flock/applet/index.html)).
    * Then just call `scene.setCameraMode(CameraMode.THIRD_PERSON)` and the camera will follow your _InteractiveAvatarFrame_ (which is controlled using the mouse).
  * New registration of the keyboard and mouse handlers which provides tighter integration with processing. It also means that proscene enabled sketches comprise even less code.
  * Code polishing.
  * Developed and fully tested in **Processing-1.2.1** under x86\_64 GNU/Linux (Kubuntu-10.04).

Many thanks to ["adamcsmitty"](http://github.com/acsmith/napplet) and to [Peter Lagger](http://www.lagers.org.uk/) for their suggestions regarding how to handle multiple viewers in proscene. **Note** that the new [StandardCamera](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-0.9.0/demos/StandardCamera/applet/index.html) and [ViewFrustumCulling](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-0.9.0/demos/ViewFrustumCulling/applet/index.html) examples (which handle multiple viewers) need Adam's [napplet library (>=0.3.5)](http://github.com/acsmith/napplet/downloads) to run.

# 24/04/2010 Release of **Proscene v-0.8.0** #

The main changes in this release respect to the previous one (v-0.7.1) are:

  * New **keyframes**' functionality through the KeyFrameInterpolator class.
  * New handy set of examples, including [CameraInterpolation](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-0.9.0/demos/CameraInterpolation/applet/index.html), [FrameInterpolation](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-0.9.0/demos/FrameInterpolation/applet/index.html), [Luxo](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-0.9.0/demos/Luxo/applet/index.html), [PointUnderPixel](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-0.9.0/demos/PointUnderPixel/applet/index.html), and [ScreenDrawing](http://disi.unal.edu.co/grupos/remixlab/local/projects/proscene-0.9.0/demos/ScreenDrawing/applet/index.html).
  * Code polishing and tighter integration with **Processing**, meaning that sketches using **proscene** should run even faster than before.

This version of **Proscene** has been developed and fully tested in **Processing-1.1** under x86\_64 GNU/Linux (Kubuntu-9.10). However, it should properly work under Mac and Windows too (as some users have reported).