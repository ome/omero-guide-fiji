#!/usr/bin/env python
# -*- coding: utf-8 -*-

# -----------------------------------------------------------------------------
#   Copyright (C) 2020 University of Dundee. All rights reserved.

#   This program is free software; you can redistribute it and/or modify
#   it under the terms of the GNU General Public License as published by
#   the Free Software Foundation; either version 2 of the License, or
#   (at your option) any later version.
#   This program is distributed in the hope that it will be useful,
#   but WITHOUT ANY WARRANTY; without even the implied warranty of
#   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#   GNU General Public License for more details.

#   You should have received a copy of the GNU General Public License along
#   with this program; if not, write to the Free Software Foundation, Inc.,
#   51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

# This script shows how to use the OMERO Python API and ImageJ Python.
# We run a simple ImageJ1 macro.

# Imports
import imagej
from jnius import autoclass
from omero.gateway import BlitzGateway

# Step 1 - Load-Fiji
def start_fiji():
    ij = imagej.init('/srv/conda/vnc/Fiji.app', headless=False)
    ij.getVersion()
    return ij


# Step 2 - Connect/Disconnect
def connect(hostname, username, password):
    """
    Connect to an OMERO server
    :param hostname: Host name
    :param username: User
    :param password: Password
    :return: Connected BlitzGateway
    """
    conn = BlitzGateway(username, password,
                        host=hostname, secure=True)
    conn.connect()
    conn.c.enableKeepAlive(60)
    return conn


def disconnect(conn):
    """
    Disconnect from an OMERO server
    :param conn: The BlitzGateway
    """
    conn.close()

# Step 4 - Load plane as 2D numpy array
def load_plane(image):
    """
    Load a 2D-plane as a numpy array
    :param image: The image
    """
    pixels = image.getPrimaryPixels()
    return pixels.getPlane(0, 0, 0)


# Step 5 - Wrap it all up
def analyse(ij, conn, image_id):
    # Step 3 - Load image
    image = conn.getObject("Image", image_id)
    # -
    plane = load_plane(image)
    WindowManager = autoclass('ij.WindowManager')
    ij.ui().show('Image', ij.py.to_java(plane))
    macro = """run("8-bit")"""
    ij.py.run_macro(macro)


def main():
    try:
        hostname = input("Host [wss://idr.openmicroscopy.org/omero-ws]: \
                         ") or "wss://idr.openmicroscopy.org/omero-ws"
        username = "public"
        password = "public"
        image_id = int(input("Image ID [28662]: ") or 28662)
        ij = start_fiji()
        conn = connect(hostname, username, password)
        analyse(ij, conn, image_id)
    finally:
        if conn:
            disconnect(conn)


if __name__ == "__main__":
    main()
