gpx-editor
==========

Simple Java program to move and delete waypoints from a GPX file.

Why gpx-editor?
===============

I use My Tracks on Android to track runs I do, and then usually upload them to Endomondo afterwards (or use the Endomondo Android app directly). Unfortunately my phone often occasionally gets a few wrong GPS points, which screws up all the stats like average pace, fastest mile, etc.
I searched around a bit for a simple tool to correct a GPX file before uploading, but couln't find something simple that works on Linux. Given that GPX is a simple XML based file format I coded up a simple tool.

You can run the tool by having a working version of Java Web Start and running [http://mathijs81.github.com/gpx-editor/gpx-editor.jnlp](http://mathijs81.github.com/gpx-editor/gpx-editor.jnlp).

Commands
========

The scrollwheel zooms in and out, dragging moves the map and when you're on a waypoint you can drag the waypoint around. You can also delete waypoints by clicking them with the right mouse button.
When you're done, click the "Save" button at the bottom.
