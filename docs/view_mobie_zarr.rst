View zarr file using MoBIE and BDV
==================================

Description
-----------

This section shows how to view ome.zarr files in Fiji using `MoBIE <https://github.com/mobie/mobie-viewer-fiji>`__.

We show:

- how to install the required dependencies
- how to view an ome.zarr file stored in S3 using the User Interface
- how to view an ome.zarr file stored in S3 using the scripting editor of Fiji.

Setup
-----

- Install Fiji on a local machine.
- Go to ``Help>Update...``
- In the ImageJ updater dialog, click on ``Manage update sites``.
- Click ``Add update site``.
- Enter for the name ``MoBIE`` (so you can identify it) and for the URL: ``https://sites.imagej.net/MoBIE/``. See `How to install an update site <https://imagej.net/update-sites/following>`__.
- Restart Fiji.

Resources
---------

- Samples images from the Image Data Resource (IDR) that have been converted into `https://github.com/ome/ngff <https://github.com/ome/ngff>`__. A list of available files can be found `here <https://blog.openmicroscopy.org/>`__.

- Script: Groovy script for opening the images in BigDataViewer using MoBIE.
   -  :download:`mobie_ome_zarr.groovy <../scripts/groovy/mobie_ome_zarr.groovy>`.

- Watch an introductory `video <https://www.youtube.com/watch?v=KposKXm7xeg>`__.

Step-by-step
------------

**Opening an ome.zarr file from the User Interface**
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

#. Launch Fiji.

#. Go to *Plugins > BigDataViewer > OME ZARR > Open OME ZARR from S3...*.

#. A dialog pops up.

#. In the text field, enter the desired URL e.g.

 ``https://s3.embassy.ebi.ac.uk/idr/zarr/v0.1/9836832.zarr``

#. Click the *OK* button.

#. When the image is displayed in the BigDataViewer, select the dialog and press *P* to display the rendering controls.

#. Modify the settings as you see fit.

**Opening an ome.zarr file using a Groovy script**
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

#. Launch Fiji.

#. Go to *File > New > Script...*.

#. A dialog pops up. In the *Language* menu, select *Groovy*.

#. Copy the content of :download:`mobie_ome_zarr.groovy <../scripts/groovy/mobie_ome_zarr.groovy>` and paste it into the text script editor of Fiji.

#. Click *Run*.

#. When the image is displayed in the BigDataViewer, select the dialog and press *P* to display the rendering controls.

#. Modify the settings as you see fit.
