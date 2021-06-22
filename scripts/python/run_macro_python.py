#!/usr/bin/env python
# -*- coding: utf-8 -*-

# -----------------------------------------------------------------------------
#   Copyright (C) 2020 University of Dundee. All rights reserved.

#   Redistribution and use in source and binary forms, with or without modification, 
#   are permitted provided that the following conditions are met:
# 
#   Redistributions of source code must retain the above copyright notice,
#   this list of conditions and the following disclaimer.
#   Redistributions in binary form must reproduce the above copyright notice, 
#   this list of conditions and the following disclaimer in the documentation
#   and/or other materials provided with the distribution.
#   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
#   ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
#   OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
#   IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
#   INCIDENTAL, SPECIAL, EXEMPLARY OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
#   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
#   HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
#   (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
#   OF THE POSSIBILITY OF SUCH DAMAGE.

# This script shows how to use the OMERO Python API and ImageJ Python.
# We run a simple ImageJ1 macro.

# Imports
import imagej
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
    from jnius import autoclass
    autoclass('ij.WindowManager')
    ij.ui().show('Image', ij.py.to_java(plane))
    macro = """run("8-bit")"""
    ij.py.run_macro(macro)


def main():
    try:
        hostname = input("Host [wss://idr.openmicroscopy.org/omero-ws]: \
                         ") or "wss://idr.openmicroscopy.org/omero-ws"
        username = "public"
        password = "public"
        image_id = int(input("Image ID [1884807]: ") or 1884807)
        print("initializing fiji...")
        ij = start_fiji()
        print("connecting to IDR...")
        conn = connect(hostname, username, password)
        print("running the macro...")
        analyse(ij, conn, image_id)
    finally:
        if conn:
            disconnect(conn)


if __name__ == "__main__":
    main()
