# User guide for Fiji and OMERO
[![Binder](https://mybinder.org/badge_logo.svg)](https://mybinder.org/v2/gh/ome/omero-guide-fiji/master?filepath=notebooks)
[![Documentation Status](https://readthedocs.org/projects/omero-guide-fiji/badge/?version=latest)](https://omero-guides.readthedocs.io/en/latest/fiji/docs/index.html)
[![Actions Status](https://github.com/ome/omero-guide-fiji/workflows/repo2docker/badge.svg)](https://github.com/ome/omero-guide-fiji/actions)

The documentation is deployed at [How to use Fiji](https://omero-guides.readthedocs.io/en/latest/fiji/docs/index.html).

This guide demonstrates how to analyze data stored in OMERO in Fiji.
This can be done using the User Interface or via the scripting facility in Fiji.


To run the notebooks, you can either [run on mybinder.org](https://mybinder.org/v2/gh/ome/omero-guide-fiji/master?filepath=notebooks) or build locally with [repo2docker](https://repo2docker.readthedocs.io/).

To build locally:

 * Install [Docker](https://www.docker.com/) if required
 * Create a virtual environment and install repo2docker from PyPI.
 * Clone this repository
 * Run  ``repo2docker``. 
 * Depending on the permissions, you might have to run the command as an admin

```
pip install jupyter-repo2docker
git clone https://github.com/ome/omero-guide-fiji.git
cd omero-guide-fiji
repo2docker .
```

This a Sphinx based documentation. 
If you are unfamiliar with Sphinx, we recommend that you first read 
[Getting Started with Sphinx](https://docs.readthedocs.io/en/stable/intro/getting-started-with-sphinx.html).
