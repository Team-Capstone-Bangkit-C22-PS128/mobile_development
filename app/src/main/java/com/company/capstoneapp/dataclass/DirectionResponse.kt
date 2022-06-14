package com.company.capstoneapp.dataclass

import com.google.gson.annotations.SerializedName

data class DirectionResponse(

	@field:SerializedName("routes")
	val routes: List<RoutesItem>,

	@field:SerializedName("geocoded_waypoints")
	val geocodedWaypoints: List<GeocodedWaypointsItem>,

	@field:SerializedName("status")
	val status: String
)

data class LegsItem(

	@field:SerializedName("duration")
	val duration: Duration,

	@field:SerializedName("start_location")
	val startLocation: StartLocation,

	@field:SerializedName("distance")
	val distance: Distance,

	@field:SerializedName("start_address")
	val startAddress: String,

	@field:SerializedName("end_location")
	val endLocation: EndLocation,

	@field:SerializedName("end_address")
	val endAddress: String,

	@field:SerializedName("via_waypoint")
	val viaWaypoint: List<Any>,

	@field:SerializedName("steps")
	val steps: List<StepsItem>,

	@field:SerializedName("traffic_speed_entry")
	val trafficSpeedEntry: List<Any>
)

data class StartLocation(

	@field:SerializedName("lng")
	val lng: Double,

	@field:SerializedName("lat")
	val lat: Double
)

data class EndLocation(

	@field:SerializedName("lng")
	val lng: Double,

	@field:SerializedName("lat")
	val lat: Double
)

data class StepsItem(

	@field:SerializedName("duration")
	val duration: Duration,

	@field:SerializedName("start_location")
	val startLocation: StartLocation,

	@field:SerializedName("distance")
	val distance: Distance,

	@field:SerializedName("travel_mode")
	val travelMode: String,

	@field:SerializedName("html_instructions")
	val htmlInstructions: String,

	@field:SerializedName("end_location")
	val endLocation: EndLocation,

	@field:SerializedName("maneuver")
	val maneuver: String,

	@field:SerializedName("polyline")
	val polyline: Polyline
)

data class Southwest(

	@field:SerializedName("lng")
	val lng: Double,

	@field:SerializedName("lat")
	val lat: Double
)

data class RoutesItem(

	@field:SerializedName("summary")
	val summary: String,

	@field:SerializedName("copyrights")
	val copyrights: String,

	@field:SerializedName("legs")
	val legs: List<LegsItem>,

	@field:SerializedName("warnings")
	val warnings: List<Any>,

	@field:SerializedName("bounds")
	val bounds: Bounds,

	@field:SerializedName("overview_polyline")
	val overviewPolyline: OverviewPolyline,

	@field:SerializedName("waypoint_order")
	val waypointOrder: List<Any>
)

data class Northeast(

	@field:SerializedName("lng")
	val lng: Double,

	@field:SerializedName("lat")
	val lat: Double
)

data class OverviewPolyline(

	@field:SerializedName("points")
	val points: String
)

data class Bounds(

	@field:SerializedName("southwest")
	val southwest: Southwest,

	@field:SerializedName("northeast")
	val northeast: Northeast
)

data class GeocodedWaypointsItem(

	@field:SerializedName("types")
	val types: List<String>,

	@field:SerializedName("geocoder_status")
	val geocoderStatus: String,

	@field:SerializedName("place_id")
	val placeId: String
)

data class Distance(

	@field:SerializedName("text")
	val text: String,

	@field:SerializedName("value")
	val value: Int
)

data class Duration(

	@field:SerializedName("text")
	val text: String,

	@field:SerializedName("value")
	val value: Int
)

data class Polyline(

	@field:SerializedName("points")
	val points: String
)
