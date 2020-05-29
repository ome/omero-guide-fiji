Use ImageJ in Python
====================

Description:
------------

This section shows how to use ImageJ as a Python library to analyze data in OMERO.

We will show in the notebooks:

- How to start ImageJ in Python

- How to load data from OMERO

- How to run ImageJ macro from Python

Setup
-----

In this case, Fiji has been installed locally. It is possible to install Fiji "on the fly",
see `ImageJ Tutorials <https://nbviewer.jupyter.org/github/imagej/tutorials/blob/master/notebooks/ImageJ-Tutorials-and-Demo.ipynb>`_ for more options.

Install omero-py and `pyimagej <https://pypi.org/project/pyimagej/>`_ via Conda:

- Install `Miniconda <https://docs.conda.io/en/latest/miniconda.html>`_ if necessary.

- Create a programming environment using Conda and activate it::

    $ conda create -n fiji_python python=3.6

    $ conda activate fiji_python

- Install omero-py and `pyimagej <https://pypi.org/project/pyimagej/>`_::

    $ conda install -c conda-forge pyimagej

    $ conda install -c ome omero-py


