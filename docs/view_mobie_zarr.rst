View zarr file using MoBIE and BDV
==================================

Description
-----------

This section shows how to view ome.zarr files in Fiji using `MoBIE <https://github.com/mobie/mobie-viewer-fiji)>`__.

We show:

- how to install the required dependencies
- how to view an ome.zarr file stored in S3 using the scripting editor of Fiji.

Setup
-----

- Install Fiji on a local machine.
- Activate the ``MoBIE-beta`` update site.
- Go to ``Help>Update...``
- In the ImageJ updater dialog, click on ``Manage update sites``.
- Click ``Add update site``.
- Enter for the name ``MoBIE`` (so you can identify it) and for the URL: ``https://sites.imagej.net/MoBIE/``. See `How to install an update site <https://imagej.net/update-sites/following#Introduction>`__.
- Restart Fiji.

Resources
---------

-  Samples images from the Image Data Resource (IDR) `idr0021 <https://idr.openmicroscopy.org/search/?query=Name:idr0062>`__ that have been converted into `https://github.com/ome/ngff <https://github.com/ome/ngff>`__.

-  Script: Groovy script for opening the images in BigDataViewer using MoBIE.
   -  :download:`mobie_ome_zarr.groovy <../scripts/groovy/mobie_ome_zarr.groovy>`.

Step-by-step
------------

#. Launch Fiji.

#. Go to *File > New > Script...*.

#. A dialog pops up. In the *Language* menu, select *Groovy*.

#. Copy, into the text script editor of Fiji, :download:`mobie_ome_zarr.groovy <../scripts/groovy/mobie_ome_zarr.groovy>`.

#. Click *Run*.