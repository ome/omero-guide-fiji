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
 * This Groovy script uses ImageJ to analyse an Image.
 * The number of generated ROIs is then added
 * to a MapAnnotation (Key/Value pairs). The MapAnnotation is then attached
 * to the Image.
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

import java.util.ArrayList
import java.util.List
import java.io.File
import java.lang.reflect.Array

// OMERO Dependencies
import omero.model.NamedValue
import omero.model.ImageI
import omero.gateway.Gateway
import omero.gateway.LoginCredentials
import omero.gateway.SecurityContext
import omero.gateway.facility.BrowseFacility
import omero.gateway.facility.DataManagerFacility
import omero.gateway.model.ImageData
import omero.gateway.model.MapAnnotationData
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
import ij.plugin.frame.RoiManager
import ij.measure.ResultsTable

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


// Connect to OMERO
println "connecting..."
gateway = connect_to_omero()

println "opening Image..."
// Open the Image using Bio-Formats
open_image_plus(HOST, USERNAME, PASSWORD, group_id, String.valueOf(image_id))

imp = IJ.getImage()
// Analyse the images. This section could be replaced by any other macro
IJ.run("8-bit");
//white might be required depending on the version of Fiji
IJ.run(imp, "Auto Threshold", "method=MaxEntropy stack")
IJ.run(imp, "Analyze Particles...", "size=10-Infinity pixel display clear add stack");
IJ.run("Set Measurements...", "area mean standard modal min centroid center \
        perimeter bounding summarize feret's median\
        stack display redirect=None decimal=3")

rm = RoiManager.getInstance()
rm.runCommand(imp, "Measure")
roi_count = rm.getCount()
println roi_count
// Create the map annotation
println "Creating MapAnnotation..."
List<NamedValue> result = new ArrayList<NamedValue>()
result.add(new NamedValue("Analysis", "Fiji"))
result.add(new NamedValue("ROIs", ""+roi_count))
result.add(new NamedValue("Antibody", "Rabbit"))
MapAnnotationData data = new MapAnnotationData()
data.setContent(result)
data.setDescription("Training Example")
//Use the following namespace if you want the annotation to be editable
//in the webclient and insight
data.setNameSpace(MapAnnotationData.NS_CLIENT_CREATED)

exp = gateway.getLoggedInUser()
group_id = exp.getGroupId()
ctx = new SecurityContext(group_id)
DataManagerFacility fac = gateway.getFacility(DataManagerFacility.class)
fac.attachAnnotation(ctx, data, new ImageData(new ImageI(image_id, false)))


// Close the connection
gateway.disconnect()
println "Done"
