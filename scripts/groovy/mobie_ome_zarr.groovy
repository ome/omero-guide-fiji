/*
 * -----------------------------------------------------------------------------
 *  Copyright (C) 2021 University of Dundee. All rights reserved.
 *
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * ------------------------------------------------------------------------------
 */

/*
 * This Groovy script shows how to open a ome.zarr file store in S3 using
 * Mobie and BigDataViewer (bdv).
 */

import bdv.util.BdvFunctions
import de.embl.cba.mobie.n5.zarr.OMEZarrS3Reader
import de.embl.cba.mobie.n5.source.Sources

reader = new OMEZarrS3Reader("https://s3.embassy.ebi.ac.uk/", "us-west-2", "idr")

image = reader.readKey("zarr/v0.2/6001247.zarr")
imageBdvSources = BdvFunctions.show(image)
