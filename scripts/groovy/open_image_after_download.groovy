/*
 * -----------------------------------------------------------------------------
 *  Copyright (C) 2018-2020 University of Dundee. All rights reserved.
 *
 *
 *  Redistribution and use in source and binary forms, with or without modification, 
 *  are permitted provided that the following conditions are met:
 * 
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 *  INCIDENTAL, SPECIAL, EXEMPLARY OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 *  OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ------------------------------------------------------------------------------
 */

/*
 * This Groovy script downloads a file and opens it in ImageJ using Bio-Formats
 * importer.
 * Use this script in the Scripting Dialog of Fiji (File > New > Script).
 * Select Groovy as language in the Scripting Dialog.
 * Error handling is omitted to ease the reading of the script but this
 * should be added if used in production to make sure the services are closed
 * Information can be found at
 * https://docs.openmicroscopy.org/latest/omero5/developers/Java.html
 */
#@ String(label="Username") USERNAME
#@ String(label="Password", style='password') PASSWORD
#@ String(label="Host", value='wss://workshop.openmicroscopy.org/omero-ws') HOST
#@ Integer(label="Image ID", value=2331) image_id


import java.nio.file.Files

// OMERO Dependencies
import omero.gateway.Gateway
import omero.gateway.LoginCredentials
import omero.gateway.SecurityContext
import omero.gateway.facility.TransferFacility
import omero.log.SimpleLogger

import ij.IJ


def connect_to_omero() {
    "Connect to OMERO"

    credentials = new LoginCredentials()
    credentials.getServer().setHostname(HOST)
    credentials.getUser().setUsername(USERNAME.trim())
    credentials.getUser().setPassword(PASSWORD.trim())
    simpleLogger = new SimpleLogger()
    gateway = new Gateway(simpleLogger)
    gateway.connect(credentials)
    return gateway

}

def download_image(gateway, image_id, path) {
    "Download the files composing the image"

    transfer = gateway.getFacility(TransferFacility)
    user = gateway.getLoggedInUser()
    ctx = new SecurityContext(user.getGroupId())
    return transfer.downloadImage(ctx, path, new Long(image_id))

} 

// Connect to OMERO
gateway = connect_to_omero()
// Download the image. This could be composed of several files
tmp_dir = Files.createTempDirectory("OMERO_download")
files = download_image(gateway, image_id, tmp_dir.toString())

files.each() { f ->
    options = "open=" + f.getAbsolutePath()
    options += " autoscale color_mode=Default "
    options += "view=[Standard ImageJ] stack_order=Default"
    IJ.run("Bio-Formats Importer", options)
}

//Delete file in directory then delete it
dir = new File(tmp_dir.toString())
entries = dir.listFiles()
for (i =0; i < entries.length; i++) {
    entries[i].delete()
}
dir.delete()
gateway.disconnect()

println("Done")

