Use ImageJ in Python
====================

Description:
------------

This section shows how to use ImageJ as a Python library to analyze data in OMERO.

Using the Python API allows us to easily load the 2D-plane we need to see or analyze.
This is much easier that when using the Java API and Bio-Formats plugin.

We will show in the notebooks:

- How to start ImageJ in Python.

- How to load data from OMERO.

- How to run ImageJ macro from Python.

Setup
-----

Install omero-py and `pyimagej <https://pypi.org/project/pyimagej/>`_ via Conda:

- Install `Miniconda <https://docs.conda.io/en/latest/miniconda.html>`_ if necessary.

- Create a programming environment using Conda and activate it::

    $ conda create -n imagej_python python=3.6

    $ conda activate imagej_python

- Install omero-py and `pyimagej <https://pypi.org/project/pyimagej/>`_::

    $ conda install -c conda-forge pyimagej

    $ conda install -c ome omero-py

Step-by-Step
------------

The script used in this document is :download:`run_macro_python.py <../scripts/python/run_macro_python.py>`.
One of the advantage of this approach is that we can load only the 2D-planes we wish to analyze.

In this example, Fiji has been installed locally. It is possible to install Fiji "on the fly",
see `ImageJ Tutorials <https://nbviewer.jupyter.org/github/imagej/tutorials/blob/master/notebooks/ImageJ-Tutorials-and-Demo.ipynb>`_ for more options.

To run the macro, we use ImageJ1 windows so it requires using ImageJ in GUI mode and requires handling the resulting windows. This is the reason the parameter `headless` is set to `False`.
If you are running the example in the Docker container, 
you will also need to start UI environment if it is not already up.

If you do not use any ImageJ1 features e.g. macro, you do **not** need the UI environment and do **not** need to set the `headless` parameter (default is `True`).

Modules and methods which need to be imported:

.. literalinclude:: ../scripts/python/run_macro_python.py
    :start-after: # Imports
    :end-before: # Step 1

Load Fiji. If you run the script locally, point to your local installation of Fiji or load Fiji "on the fly":

.. literalinclude:: ../scripts/python/run_macro_python.py
    :start-after: # Step 1
    :end-before: # Step 2

Connect to the server. It is also important to close the connection again
to clear up potential resources held on the server. This is done in the 
``disconnect`` method:

.. literalinclude:: ../scripts/python/run_macro_python.py
    :start-after: # Step 2
    :end-before: # Step 4

Load an image from IDR:

.. literalinclude:: ../scripts/python/run_macro_python.py
    :start-after: # Step 3
    :end-before: # -

Load the binary plane as numpy array:

.. literalinclude:: ../scripts/python/run_macro_python.py
    :start-after: # Step 4
    :end-before: # Step 5

To be used in ImageJ, the numpy array will be converted into ImageJ types using the `to_java()` method.
In order the use the methods implemented above in a proper standalone script:
Wrap it all up in an ``analyse`` method and call it from ``main``:

.. literalinclude:: ../scripts/python/run_macro_python.py
    :start-after: # Step 5
