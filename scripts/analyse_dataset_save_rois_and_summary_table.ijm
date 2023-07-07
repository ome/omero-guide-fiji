//@ String(label="Username", value="trainer-1") USERNAME
//@ String(label="Password", style='password', persist=false) PASSWORD
//@ String(label="Host", value='wss://workshop.openmicroscopy.org/omero-ws') HOST
//@ Integer(label="Port", value=443) PORT
//@ Integer(label="Dataset ID", value=24656) dataset_id

run("OMERO Extensions");

connected = Ext.connectToOMERO(HOST, PORT, USERNAME, PASSWORD);


if (connected != "true") {
	exit("Not connected");
}


setBatchMode(true);
images = Ext.list("images", "dataset", dataset_id);
if (images.length == 0) {
	exit("No images found");
}
image_ids = split(images, ",");

table_name = "Summary_from_Fiji"; 
for (i = 0; i < image_ids.length; i++) {
    id = image_ids[i];
    Ext.getImage(id);
    
    //Analyse the data
    roiManager("reset");
    run("8-bit");
    //white might be required depending on the version of Fiji
    run("Auto Threshold", "method=MaxEntropy stack white");
    run("Analyze Particles...", "size=10-Infinity pixel display clear add stack");
    run("Set Measurements...", "area mean standard modal min centroid center perimeter bounding summarize feret's median stack display redirect=None decimal=3");
    roiManager("Measure");
    
    nROIs = Ext.saveROIs(id, "");
    print("Image " + id + ": " + nROIs + " ROI(s) saved.");
    
    print("creating summary results for image ID " + id);
    Ext.addToTable(table_name, "Results", id);
    
    roiManager("reset");
    close("Results");
    close();
}

results_file = getDir("temp") + dataset_id + "_merged_results.csv";
Ext.saveTableAsFile(table_name, results_file, ",");
file_id = Ext.addFile("Dataset", dataset_id, results_file);
File.delete(results_file);

setBatchMode(false);

Ext.disconnect();
print("processing done");
