How to install the OMERO plugin for Fiji/ImageJ
===============================================

Description:
------------

Fiji is a free open-source image processing package based on
ImageJ, \ https://imagej.net/Fiji\ . The following workflow should how to
install the OMERO plugin for Fiji and ImageJ.

The OMERO plugin does not have yet and update site.

**Setup step-by-step:**
-----------------------

We assume that you have already Fiji/ImageJ installed locally.

In this section, we will cover the steps required to install the
OMERO.imagej plugin for Fiji. If you wish to install it for ImageJ,
an additional step is needed.

We first describe how the common installation steps for ImageJ and Fiji.
We then describe how to install the *Bio-Formats Package* for ImageJ.

Installing the OMERO.imagej plugin also adds the dependencies
required to connect to OMERO using the Script Editor of Fiji.

Installation of the OMERO.imagej plugin for Fiji and ImageJ, the
common steps:

-  Find the Plugins folder of your Fiji application and check if it contains any old omero_ij-5.x.x-all.jar file(s) or OMERO.imagej-5.x.x folder(s). Remove any such jar files or folders from the Plugins folder.

-  Download from \ https://www.openmicroscopy.org/omero/downloads \
   the latest 5.x.x version of ImageJ/Fiji plugin for OMERO

.. image:: images/setup1.png

-  For recent plugin versions (5.5.7 and higher): Note where you the downloaded the omero_ij-5.x.x-all.jar file. Copy that file into the *plugins* folder of Fiji.

-  For plugin versions lower than 5.5.7: Extract the downloaded .zip archive. Remember where you extracted it to.

-  For plugin versions lower than 5.5.7: Copy the extracted folder and paste it to the *plugins* folder of Fiji.

-  **Note:** For plugin versions lower than 5.5.7: Some Windows unzip apps create a double folder enclosing the plugin. If that is the case, copy the inner OMERO.imagej-5.x.x folder into *Fiji.app > plugins* folder.

-  Now, restart Fiji. If you are using ImageJ, follow with the additional step below.

**Installation of Bio-Formats Package, ImageJ only:**

-  Download the latest version of the *Bio-Formats Package* from:
   https://www.openmicroscopy.org/bio-formats/downloads

.. image:: images/setup2.png

-  Move the downloaded file into the *ImageJ > plugins* folder.

-  Restart ImageJ.
