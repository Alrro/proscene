# `TerseHandling` Tutorial #

| Requirements: [Processing >= 2.x](https://processing.org/download/), and the [TerseHandling library](http://otrolado.info/tersehandling.zip) (source code found under the "**examples**" section of the library package) |
|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

`TerseHandling` is a framework designed for _sketching_ generic input events, i.e., transforming input events into high-level user-defined actions, mainly implemented using [generic programming](http://en.wikipedia.org/wiki/Generics_in_Java) techniques. It provides its own set of events which supports up to [6-DOFs](http://en.wikipedia.org/wiki/Degrees_of_freedom_(mechanics)), together with a set of **agents** which parses them, i.e., identifies object **grabbers** and the **actions** they should perform, allowing the developer to focus on implementing those actions. A developer workflow using `TerseHandling` would typically involve the following tasks:

  * Choose one among of the provided `TerseHandling` **Agents** (extending it if necessary) and attach to it an event listening mechanism.
  * Reduce (hardware or software) input events into their `TerseHandling` counterparts.
  * Choose a callback mechanism: direct actionless approach (non-generic programming), or high-level action-driven (which extensively uses generic programming). In the latter case:
    * Define **shortcuts** over `TerseHandling` events to trigger different actions using **Profiles**.
    * Implement the actions to be performed by object grabbers.

`TerseHandling` is a full-fledged, stand-alone library which will become the new event backend for the upcoming Proscene-2 series, but can also backed up the event sub-system of any other third party **Java** or **Processing** library.

This tutorial introduces a series of `TerseHandling` examples, implemented as a well commented **Processing** sketches. It requires [Processing >= 2.x](https://processing.org/download/) and the [TerseHandling library](http://otrolado.info/tersehandling.zip) (for Processing third party library installation instructions check [here](http://wiki.processing.org/w/How_to_Install_a_Contributed_Library#Manual_Install)). The zip package includes the fully functional tutorial source code, found under the examples section of the library for you to study them. This tutorial includes the following examples: SimpleCallback, ActionDrivenCallback, TerseEventTuples, SpaceNavigatorAgentFeed, and [TUIOAgent](https://code.google.com/p/proscene/wiki/TUIOAgent). Please use the side bar to navigate among them.

Please remember that your feedback is more than welcome to help us fine tune `TerseHandling` before its first release.