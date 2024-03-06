/*
 * -----------------------------------------------------------------------------
 *  Copyright (C) 2019-2020 University of Dundee. All rights reserved.
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
 * This Groovy script uses ImageJ to crop an image.
 * The cropped image is saved locally as OME-TIFF using Bio-Formats Exporter.
 * The OME-TIFF is then imported to OMERO.
 * Use this script in the Scripting Dialog of Fiji (File > New > Script).
 * Select Groovy as language in the Scripting Dialog.
 * Error handling is omitted to ease the reading of the script but this
 * should be added if used in production to make sure the services are closed
 * Information can be found at
 * https://docs.openmicroscopy.org/latest/omero5/developers/Java.html
 */

#@ String(label="Username") USERNAME
#@ String(label="Password", style='password', persist=false) PASSWORD
#@ String(label="Host", value='wss://workshop.openmicroscopy.org/omero-ws') HOST
#@ Integer(label="Image ID", value=2331) image_id

import java.io.File
import java.lang.reflect.Array

// OMERO Dependencies
import omero.gateway.Gateway
import omero.gateway.LoginCredentials
import omero.gateway.SecurityContext
import omero.gateway.facility.BrowseFacility
import omero.gateway.facility.DataManagerFacility
import omero.gateway.model.DatasetData
import omero.log.SimpleLogger

import ome.formats.importer.ImportConfig
import ome.formats.importer.OMEROWrapper
import ome.formats.importer.ImportLibrary
import ome.formats.importer.ImportCandidates
import ome.formats.importer.cli.ErrorHandler
import ome.formats.importer.cli.LoggingImportMonitor

import loci.formats.in.DefaultMetadataOptions
import loci.formats.in.MetadataLevel

import ij.IJ

group_id = "-1"

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

def get_port(HOST) {
    port = 4064
    // check if websockets is used
    if (HOST.startsWith("ws")) {
        port = 443
    }
    return port
}

def open_image_plus(HOST, USERNAME, PASSWORD, group_id, image_id) {
    "Open the image using the Bio-Formats Importer"

    StringBuilder options = new StringBuilder()
    options.append("location=[OMERO] open=[omero:server=")
    options.append(HOST)
    options.append("\nuser=")
    options.append(USERNAME.trim())
    options.append("\nport=")
    options.append(get_port(HOST))
    options.append("\npass=")
    options.append(PASSWORD.trim())
    options.append("\ngroupID=")
    options.append(group_id)
    options.append("\niid=")
    options.append(image_id)
    options.append("] ")
    options.append("windowless=true view=Hyperstack ")
    IJ.runPlugIn("loci.plugins.LociImporter", options.toString())
}

def find_dataset(gateway, dataset_id) {
    "Load the Dataset"
    browse = gateway.getFacility(BrowseFacility)
    user = gateway.getLoggedInUser()
    ctx = new SecurityContext(user.getGroupId())
    return browse.findIObject(ctx, "omero.model.Dataset", dataset_id)
}

def upload_image(paths, gateway, id) {
    "Upload an image to OMERO"

    user = gateway.getLoggedInUser()
    sessionKey = gateway.getSessionId(user)

    config = new ImportConfig()
    config.debug.set('false')
    config.hostname.set(HOST)
    config.port.set(443)
    config.sessionKey.set(sessionKey)
    dataset = find_dataset(gateway, id)

    store = config.createStore()
    reader = new OMEROWrapper(config)

    library = new ImportLibrary(store, reader)
    error_handler = new ErrorHandler(config)

    library.addObserver(new LoggingImportMonitor())
    candidates = new ImportCandidates(reader, paths, error_handler)
    containers = candidates.getContainers()
    containers.each() { c ->
        c.setTarget(dataset)
    }
    reader.setMetadataOptions(new DefaultMetadataOptions(MetadataLevel.ALL))
    return library.importCandidates(config, candidates)
}


// Connect to OMERO
println "connecting..."
gateway = connect_to_omero()

println "opening Image..."
// Open the Image using Bio-Formats
open_image_plus(HOST, USERNAME, PASSWORD, group_id, String.valueOf(image_id))

// Crop the image
println "cropping..."
IJ.makeRectangle(0, 0, 200, 200)
IJ.run("Crop")

// Save modified image as OME-TIFF using Bio-Formats Exporter
imp = IJ.getImage()
name = imp.getTitle().replaceAll("\\s","")
file = File.createTempFile(name, ".ome.tiff")
path_to_file = file.getAbsolutePath()
println  path_to_file
options = "save=" + path_to_file + " export compression=Uncompressed"
IJ.run(imp, "Bio-Formats Exporter", options)
imp.changes = false
imp.close()

// Create a Dataset
d = new DatasetData()
d.setName("Cropped Image")
dm = gateway.getFacility(DataManagerFacility)
user = gateway.getLoggedInUser()
ctx = new SecurityContext(user.getGroupId())
d = dm.createDataset(ctx, d, null)

// Import the generated OME-TIFF to OMERO
println "importing..."
str2d = new String[1]
str2d[0] = path_to_file
success = upload_image(str2d, gateway, d.getId())
// delete the local OME-TIFF image
(new File(path_to_file)).delete()
println "imported"

// Close the connection
gateway.disconnect()
println "Done"
