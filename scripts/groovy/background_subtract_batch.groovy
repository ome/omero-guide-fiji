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
 * This Groovy script uses ImageJ to Subtract Background.
 * The images with subtracted background are imported into a new Dataset in OMERO.
 * The dataset is named script_editor_output_from_dataset_ID where ID is the ID of 
 * the specified dataset.
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
#@ Integer(label="Dataset ID", value=2331) dataset_id

import java.io.File
import java.util.ArrayList
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

    StringBuffer options = new StringBuffer()
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

def get_image_ids(gateway, dataset_id) {
    "List all image's ids contained in a Dataset"

    browse = gateway.getFacility(BrowseFacility)
    user = gateway.getLoggedInUser()
    ctx = new SecurityContext(user.getGroupId())
    ids = new ArrayList(1)
    val = new Long(dataset_id)
    ids.add(val)
    images = browse.getImagesForDatasets(ctx, ids)
    j = images.iterator()
    image_ids = new ArrayList()
    while (j.hasNext()) {
        image = j.next()
        image_ids.add(String.valueOf(image.getId()))
    }
    return image_ids
}

def find_dataset(gateway, dataset_id) {
    "Load the Dataset"
    browse = gateway.getFacility(BrowseFacility)
    user = gateway.getLoggedInUser()
    ctx = new SecurityContext(user.getGroupId())
    return browse.findIObject(ctx, "omero.model.Dataset", dataset_id)
}

def upload_image(path, gateway, id) {
    "Upload an image to omero"

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
    candidates = new ImportCandidates(reader, path, error_handler)
    containers = candidates.getContainers()
    containers.each() { c ->
        c.setTarget(dataset)
    }
    reader.setMetadataOptions(new DefaultMetadataOptions(MetadataLevel.ALL))
    return library.importCandidates(config, candidates)

}

// Connect to OMERO
gateway = connect_to_omero()

// Retrieve the images contained in the specified dataset
image_ids = get_image_ids(gateway, dataset_id)

//Create a dataset to store the newly created images will be added
name = "script_editor_output_from_dataset_" + dataset_id
d = new DatasetData()
d.setName(name)
dm = gateway.getFacility(DataManagerFacility)
user = gateway.getLoggedInUser()
ctx = new SecurityContext(user.getGroupId())
d = dm.createDataset(ctx, d, null)

// Loop through each image
image_ids.each() { image_id ->
    println image_id
    open_image_plus(HOST, USERNAME, PASSWORD, group_id, image_id)
    IJ.run("Enhance Contrast...", "saturated=0.3")
    IJ.run("Subtract Background...", "rolling=50 stack")

    // Save modified image as OME-TIFF using Bio-Formats
    imp = IJ.getImage()
    imp = IJ.getImage()
    name = imp.getTitle().replaceAll("\\s","")
    file = File.createTempFile("name", ".ome.tiff")
    path_to_file = file.getAbsolutePath()
    println path_to_file
    options = "save=" + path_to_file + " export compression=Uncompressed"
    IJ.run(imp, "Bio-Formats Exporter", options)
    imp.changes = false
    imp.close()

    // Upload the generated OME-TIFF to OMERO
    println "uploading..."
    str2d = new String[1]
    str2d[0] = path_to_file
    success = upload_image(str2d, gateway, d.getId())
    // delete the local OME-TIFF image
    (new File(path_to_file)).delete()
    println "imported"
}

println "Done"
// Close the connection
gateway.disconnect()
