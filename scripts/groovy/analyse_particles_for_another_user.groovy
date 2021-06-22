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
 * This Groovy script uses ImageJ to analyse particles, the generated ROIs are
 * saved to OMERO.
 * In this script, the analysis can be done on behalf of another user by a person
 * with more privileges e.g. analyst.
 * More details about restricted privileges can be found at
 * https://docs.openmicroscopy.org/latest/omero/sysadmins/restricted-admins.html
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
#@ Integer(label="Dataset ID", value=2331) dataset_id
#@ Integer(label="Target User's name") target_user

 import java.util.ArrayList


// OMERO Dependencies
import omero.gateway.Gateway
import omero.gateway.LoginCredentials
import omero.gateway.SecurityContext
import omero.gateway.facility.BrowseFacility
import omero.gateway.facility.AdminFacility
import omero.gateway.facility.ROIFacility
import omero.gateway.facility.TablesFacility
import omero.log.SimpleLogger

import org.openmicroscopy.shoola.util.roi.io.ROIReader

import loci.formats.FormatTools
import loci.formats.ImageTools
import loci.common.DataTools

import ij.IJ
import ij.ImagePlus
import ij.ImageStack
import ij.process.ByteProcessor
import ij.process.ShortProcessor
import ij.plugin.frame.RoiManager
import ij.measure.ResultsTable


group_id = -1

// If you want to do analysis for someone else,


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

def get_image_ids(gateway, ctx, dataset_id) {
    "List all image's ids contained in a Dataset"

    browse = gateway.getFacility(BrowseFacility)
    ctx = switch_security_context(ctx, target_user)

    ids = new ArrayList(1)
    ids.add(new Long(dataset_id))
    images = browse.getImagesForDatasets(ctx, ids)

    j = images.iterator()
    image_ids = new ArrayList()
    while (j.hasNext()) {
        image_ids.add(j.next().getId())
    }
    return image_ids
}

def switch_security_context(ctx, target_user) {
    "Switch security context"
    if (!target_user?.trim()) {
        target_user = USERNAME
    }
    service = gateway.getFacility(AdminFacility)
    user = service.lookupExperimenter(ctx, target_user)
    ctx = new SecurityContext(user.getGroupId())
    ctx.setExperimenter(user)
    return ctx
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



def save_rois_to_omero(ctx, image_id, imp) {
    // Save ROI's back to OMERO
    reader = new ROIReader()
    roi_list = reader.readImageJROIFromSources(image_id, imp)
    roi_facility = gateway.getFacility(ROIFacility)
    result = roi_facility.saveROIs(ctx, image_id, exp_id, roi_list)

    roivec = new ArrayList()

    j = result.iterator()
    while (j.hasNext()) {
        roidata = j.next()
        roi_id = roidata.getId()

        i = roidata.getIterator()
        while (i.hasNext()) {
            roi = i.next()
            shape = roi[0]
            t = shape.getZ()
            z = shape.getT()
            c = shape.getC()
            shape_id = shape.getId()
            roivec.add([roi_id, shape_id, z, c, t])
        }
    }
    return roivec
}

// Prototype analysis example
gateway = connect_to_omero()
exp = gateway.getLoggedInUser()
group_id = exp.getGroupId()
ctx = new SecurityContext(group_id)
exp_id = exp.getId()

// get all images_ids in an omero dataset
ids = get_image_ids(gateway, ctx, dataset_id)
// if target_user is null or blank
// Switch context to target user and open omeroImage as ImagePlus object
ctx = switch_security_context(ctx, target_user)

ids.each() { id1 ->
    // if target_user is null or blank
    // Switch context to target user and open omeroImage as ImagePlus object
    open_image_plus(HOST, USERNAME, PASSWORD, group_id, String.valueOf(id1))
    imp = IJ.getImage()
    // Some analysis which creates ROI's and Results Table
    IJ.run("8-bit")
    //white might be required depending on the version of Fiji
    IJ.run(imp, "Auto Threshold", "method=MaxEntropy stack")
    IJ.run(imp, "Analyze Particles...", "size=10-Infinity pixel display clear add stack");
    IJ.run("Set Measurements...", "area mean standard modal min centroid center \
            perimeter bounding fit shape feret's integrated median skewness \
            kurtosis area_fraction stack display redirect=None decimal=3")

    rm = RoiManager.getInstance()
    rm.runCommand(imp, "Measure")
    rt = ResultsTable.getResultsTable()
    roivec = save_rois_to_omero(ctx, id1, imp)
    // Close the various components
    IJ.selectWindow("Results")
    IJ.run("Close")
    IJ.selectWindow("ROI Manager")
    IJ.run("Close")
    imp.changes = false     // Prevent "Save Changes?" dialog
    imp.close()
}
// Close the connection
gateway.disconnect()
println "processing done"
