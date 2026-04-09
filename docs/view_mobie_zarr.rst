View zarr file using MoBIE and BDV
==================================

Description
-----------

This section shows how to view ome.zarr files in Fiji using `BigDataViewer <https://imagej.net/plugins/bdv/>`__
and `MoBIE <https://github.com/mobie/mobie-viewer-fiji>`__.

We show:

- how to install the required dependencies
- how to view an ome.zarr file stored in S3 using the User Interface
- how to view an ome.zarr file stored in S3 using the scripting editor of Fiji.

Setup
-----

- Install Fiji on a local machine.
- `BigDataViewer` is already included in Fiji.
- To install `MoBIE`, go to ``Help>Update...``
- In the ImageJ updater dialog, click on ``Manage update sites``.
- The `MoBIE`` should already be listed. Select it and click  ``Close``.
- Click ``Apply changes`` to install it.
- If `MoBIE`` is not listed:
   - Click ``Add update site``.
   - Enter for the name ``MoBIE`` (so you can identify it) and for the URL: ``https://sites.imagej.net/MoBIE/``. See `How to install an update site <https://imagej.net/update-sites/following>`__.
   - Click ``Apply changes`` to install it.
- Restart Fiji.

Resources
---------

- Samples images from the Image Data Resource (IDR) that have been converted into `https://github.com/ome/ngff <https://github.com/ome/ngff>`__. A list of available files can be found `here <https://idr.github.io/ome-ngff-samples/>`__.

- Script: Groovy script for opening the images in BigDataViewer using MoBIE.
   -  :download:`mobie_ome_zarr.groovy <../scripts/groovy/mobie_ome_zarr.groovy>`.

- Watch an introductory `video <https://www.youtube.com/watch?v=KposKXm7xeg>`__.

Step-by-step
------------

**Opening an ome.zarr file in BigDataViewer**
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

#. Launch Fiji.

#. Go to *Plugins > BigDataViewer > HDF5/N5/Zarr/OME-NGFF Viewer*

#. A dialog pops up.

#. In the text field, you can click `Browse` to open local data or enter the desired URL e.g.

 ``https://livingobjects.ebi.ac.uk/idr/zarr/v0.4/idr0062A/6001240.zarr``

#. If you Click the `OK`` button tbis will open the image in `BigDataViewer`. You can also click the `Detect datasets` button to inspect the
   content of the zarr file, such as individual pyramid arrays and labels. You can select multiple items, for example to open
   the `(multiscales)` image under `labels` alongside the parent `(multiscales)` image.

#. When the image is displayed in the `BigDataViewer`, select the dialog and press *P* to display the rendering controls.

#. Modify the settings as you see fit.


**Opening an ome.zarr file in MoBIE**
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

#. Launch Fiji.

#. Go to *Plugins > MoBIE > Open > Open OME-Zarr...*

#. A dialog pops up.

#. In the text field, enter the desired URL e.g.

 ``https://livingobjects.ebi.ac.uk/idr/zarr/v0.4/idr0062A/6001240.zarr``

#. Click the `OK` button.
