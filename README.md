# Knots-and-Graphs
#
# This project is a series of tools to be used by the Knots and Graphs math research group at OSU, ran by Sergey Chmutov.
# The tools so far completed or in development are:
# - A program to calculate the Arrow polynomial of a virtual knot (equivalent to the Jones polynomial of a classical knot).
# - A program to recognize a knot from an image of that knot, which can be used as input for the Arrow polynommial program and others to come.
#
# Future tools include more programs to calculate different invariants of knots, and possibly a tool to draw and manipulate knots. 
#
# For curious readers, this is how the Recognizer works:
# We define the number of "protrusions" of a point to be exactly what it sounds like. A point in the middle of a strand (like as pictured below)
# will have two protrusions: ----------^----------
# The strand reaches out in two directions from the point ^, so the point ^ has two protrusions. 
# Now, for an endpoint, like so: $------------------ 
# The strand reaches out in only one direction from the point $, so the point $ has one protrusion.
#
# Now, the recognizer calculates the number of protrusions of each point by running the following process on each points:
# Call the point in consideration p. Draw a circle with small radius centered at p. The initial radius should be small enough that
# most of the circle is contained within the knot. Expand the radius of this circle until most of the circle is not contained in the knot,
# then count the number of intersections the circle has with the knot. This is the number of protrusions of p.
#
# After this, the recognition is pretty simple. The program clusters all points with 1 protrusion into 2 * numCrossings clusters, where 
# numCrossings is the number of crossings of the knot. This is because each crossing has two endpoints associated with it, namely, the parts
# of the strand that are cut off to symbolize a strand going under another strand. From there, the clusters are paired up, the midpoint of each
# cluster pair is found, and each one of those midpoints is the center of a crossing. Since the knot representation requires that a crossing
# be represented as the 4 arcs associated with the knot in counter-clockwise order (more information can be found in the comments of
# ClassicalCrossing.java), for each crossing c, we have to traverse a circle centered at the center of c (found earlier as the midpoint of
# a pair of clusters) in a counter-clockwise fashion, and note each arc we come across. Then we have the 4 arc labels needed to represent each
# crossing. Once the arcs have been given an orientation, the writhe of each crossing is easy to calculate based on which endpoints are connected
# to which other endpoints. The 4 arc labels + writhe of each crossing is a sufficient representation for each crossing, and the knot is 
# represented as the collection of such crossings. For more information on this representation, read a wikipedia/wolfram alpha page on the 
# planar diagram representation of a knot. Note that our representation actually a modification of the planar diagram representation, since
# we save the writhe and the planar diagram doesn't, but this information is necessary to have a full, distinctive representation for each knot.