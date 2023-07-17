Analyze, save ROIS and measurement
===================================

Description
-----------

The following workflows should work
both with ImageJ and Fiji, after these have been correctly set up with
the OMERO plugin for Fiji/ImageJ.

Using the User Interface of the OMERO plugin, we will show:

-  How to connect to OMERO using the OMERO plugin for ImageJ/Fiji.

-  How to open an image from OMERO.server into Fiji/ImageJ.

-  How to manually save ROIs and measurements as CSV back to the
   original image in OMERO.server.

-  How to import a newly created image from Fiji/ImageJ into OMERO.

Setup
-----

-  Install Fiji on the local machine with the OMERO.insight-ij plugin.
   The installation instructions can be found at :doc:`installation`.

Resources
---------

-  Samples images from the Image Data Resource (IDR) `idr0021 <https://idr.openmicroscopy.org/search/?query=Name:idr0021>`__.


Step-by-step
------------

In this first example we show how to open an OMERO image in Fiji, draw ROIs, measure those ROIs and show how to save the ROIs and the measurement back to OMERO. 

#. Launch Fiji/ImageJ.

#. Go to *Plugins > OMERO > Connect* To OMERO. This will show a login
   screen where you can enter the name of the server to connect to,
   the username and password. The OMERO plugin will allow you to
   browse your data in a similar manner to OMERO.web.

#. In the OMERO login dialog, click the wrench icon\ |image1| and then
   add the server address in the dialog. By default, only "localhost"
   is listed. Click on the *plus* icon to add a new line to the list
   and type into the line the server address.

#. Click Apply.

   .. image:: images/manual2.png

#.  Enter your credentials and click *Login*.

#.  Select the **A-Fiji-dataset** Dataset.

#.  Double-click on a thumbnail or on an Image in the left-hand tree to
    open an Image in ImageJ.

#.  Go to *Analyze > Tools > ROI Manager...*

#.  Draw a shape using for example the Freehand selection tool.

#. In the ROI manager, click the button *Add [t]* to add the shape to the
   ROI Manager.

   .. image:: images/manual3.png

#. Move to another channel, using the *c slider*.

#. Draw other shapes if desired. Click *Add [t]* to add them to the ROI Manager.

#. When done with the drawing, click the button *Measure* in the ROI Manager.

#. A dialog with measurements for each shape pops up.

#. To save the ROI and the measurement back to OMERO, go to *Plugins > OMERO > Save ROIs To OMERO*.

#. In the dialog popping up, under the *Save* section select *ROI* and *Measurements*.

#. The measurements are saved back to OMERO as a CSV file and linked to the Image.

   .. image:: images/manual4.png

#. Go to OMERO.web and log in.

#. Select the image opened in Fiji/ImageJ.

#. Check that there is a new CSV file under the *Attachments* harmonica.

#. Open the image in OMERO.iviewer to the see the ROIs and make sure that you can interact with them.


.. |image1| image:: images/manual1.png
   :width: 0.24105in
   :height: 0.24105in
