Segment OMERO data using Fiji run headlessly in notebook
========================================================

**Description:**
----------------

The following examples follow up on the script shown in :doc:`threshold_scripting`.
But, unlike in :doc:`threshold_scripting`, here, the Fiji is incorporated into a docker environment and run in headless mode.
The scripts are embedded into a Jupyter Notebook. Groovy language is used, which is very similar to Java but can be used in Fiji scripting. The scripts show:

-  How to use a macro attached on an OMERO Dataset using a groovy script and Fiji and save ROIs, results and images to OMERO

-  How to crop an image in OMERO using a groovy script and Fiji


**Setup:**
----------
Fiji has been installed in a Docker image.

See also 

- https://github.com/ome/training-notebooks/blob/master/Dockerfile
- https://github.com/ome/training-notebooks/tree/master/Fiji


**Resources:**

-  Data: Samples images from the Image Data Resource (IDR) \ https://idr.openmicroscopy.org/webclient/?show=project-51
-  Notebooks https://github.com/ome/training-notebooks/tree/master/Fiji 
-  Macro: [LINK to macro example on github]

**Step-by-Step**
----------------

#.  Create a Fiji macro, for example by recording it and save it locally. You        can for example use adjusted macro created in :doc:`threshold_manual`.
    That macro is available on [LINK to macro on github].
    Note: Not all commands which can be run and recorded in the Fiji user interface can be also run in the Fiji in headless mode. 

#.  In your browser, go to the server address provided.

#.  Log in using the credentials provided.

#.  Make sure you are selecting your own data. Select the Dataset **A-Fiji-dataset**.

#.  Open the "Attachments" harmonica in the right-hand panel and click on the plus icon. Browse and attach the Fiji macro which you saved locally from [LINK to macro on github] to this Dataset as File Annotation.

#.  Go to https://idr-analysis.openmicroscopy.org/training

#.  Look under Notebooks > Fiji for run_attached_macro.ipynb.

#.  Select the first Step and click on the Run button to execute each step in turn.

#.  For the connection to OMERO, you will be asked to enter your login details when running the first cell under "Description". Note that the connection itself happens only in the next cell, highlighted as "Connect to OMERO".

#.  In the following cell, you will be asked to enter the ID of the Dataset onto which you attached the Macro file above.

#.  The code in the next cell is fetching the attachments on the Dataset and offering a dropdown menu for you to select a suitable attachment (= your attached macro).

#.  The next cell is asking for input about the types of data which should be saved back to OMERO (ROIs, Results or Images). This is of course dependent on the type of results your Macro is producing in Fiji. For this example, lets select ROIs and Results.

#.  The script in the next cell will process all the Images in the specified         Dataset, applying threshold, analyzing particles, i.e. steps which are           captured inside your macro file. Further, it will save ROIs back in OMERO        and create a CSV to be attached to each image in that Dataset in OMERO.

#.  Return to OMERO.web and open an Image from this Dataset in OMERO.iviewer.

#.  Click the *ROIs* tab to see the added ROIs.

#.  In the *Settings* tab, turning channels on/off will also show/hide
    ROIs assigned to those channels.

#.  Open the image in OMERO.figure for a quick publication by going to
    Info tab in iviewer and clicking on OMERO.figure in the Open with
    line.\ |image1|

#.  Click the *ROIs* tab to see the added ROIs. Note that the ROIs have
    been assigned a Channel index to indicate which Channel they were
    derived from.

#.  In the *Settings* tab, turning channels on/off will also show/hide ROIs
    assigned to those channels.

#.  Go to https://idr-analysis.openmicroscopy.org/training

#.  Look under Notebooks > Fiji for crop_image.ipynb.

#.  Run through the notebook, logging in as previously and indicating the Image ID of the image you want to crop.

#.  In OMERO.web, refresh and note that the cropped image has been imported into a newly created Dataset "Cropped image".

.. |image1| image:: images/threshold_script2.png
   :width: 1.89583in
   :height: 0.36458in

