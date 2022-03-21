// information about server communication. This sample webservice is provided by Wikitude and returns random dummy places near given location
//var ServerInformation = {
//	POIDATA_SERVER: "https://example.wikitude.com/GetSamplePois/",
//	POIDATA_SERVER_ARG_LAT: "lat",
//	POIDATA_SERVER_ARG_LON: "lon",
//	POIDATA_SERVER_ARG_NR_POIS: "nrPois"
//};

// implementation of AR-Experience (aka "World")
var World = {
	// you may request new data from server periodically, however: in this sample data is only requested once
	isRequestingData: false,

	// true once data was fetched
	initiallyLoadedData: false,

	// different POI-Marker assets
	markerDrawable_idle: null,
	markerDrawable_selected: null,
	markerDrawable_directionIndicator: null,

	// list of AR.GeoObjects that are currently shown in the scene / World
	markerList: [],

	// The last selected marker
	currentMarker: null,
	addMarker: null,
	showPlaceNr: 0,
    model: null,
	image: null,

	locationUpdateCounter: 0,
	updatePlacemarkDistancesEveryXLocationUpdates: 50,

	POIData: [],

	// called to inject new POI data
	loadPoisFromJsonData: function loadPoisFromJsonDataFn(poiData) {
	    console.info('loadPoisFromJsonData');
        World.POIData = poiData;
        console.info('name: ' + poiData[0].name);

        World.refreshMarkerView();
	},

     refreshMarkerView: function refreshMarkerViewFn()
     {
        console.info('refreshMarkerView');
         /* Destroys all existing AR-Objects (markers & radar). */
         AR.context.destroyAll();

		// show radar & set click-listener
		PoiRadar.show();
		$('#radarContainer').unbind('click');
//		$("#radarContainer").click(PoiRadar.clickedRadar);

		// empty list of visible markers
		World.markerList = [];

		// start loading marker assets
		World.markerDrawable_idle = new AR.ImageResource("assets/ar_bg_unselected.png");
		World.markerDrawable_selected = new AR.ImageResource("assets/ar_bg_selected.png");
		World.markerDrawable_directionIndicator = new AR.ImageResource("assets/indi.png");
		World.markerDrawable_icon_ar = new AR.ImageResource("assets/ar.png");
		World.markerDrawable_icon_art = new AR.ImageResource("assets/art.png");
		World.markerDrawable_icon_exhibition = new AR.ImageResource("assets/exhibition.png");
		World.markerDrawable_icon_monument = new AR.ImageResource("assets/monument.png");
		World.markerDrawable_icon_sport = new AR.ImageResource("assets/sport.png");

		// loop through POI-information and create an AR.GeoObject (=Marker) per POI
//		for (var currentPlaceNr = 0; currentPlaceNr < poiData.length; currentPlaceNr++) {
//
//		    var location1 = new AR.GeoLocation(parseFloat(poiData[currentPlaceNr].latitude), parseFloat(poiData[currentPlaceNr].longitude));
//		    var distance1 = location1.distanceToUser();
//
//            console.info(distance1);
//
//
//		    if (distance1 > 50)
//            {
//               //more than 50 m away from User
//               altitude1 =  21*World.showPlaceNr;
//            }
//            if (distance1 > 100)
//            {
//               altitude1 =  40*World.showPlaceNr;
//            }
//            if (distance1 > 250)
//            {
//               altitude1 =  90*World.showPlaceNr;
//            }
//            if (distance1 > 500)
//            {
//               altitude1 =  120*World.showPlaceNr;
//            }
//            if (distance1 > 1000)
//            {
//               altitude1 =  130*World.showPlaceNr;
//            }
//            if (distance1 > 1500)
//            {
//               altitude1 =  180*World.showPlaceNr;
//            }
//            if (distance1 > 2000)
//            {
//               altitude1 =  220*World.showPlaceNr;
//            }
//            if (distance1 > 5000)
//            {
//                continue;
//            }
//            World.showPlaceNr++;
//            altitude1 = altitude1/8;
//
//            console.info('name: ' + poiData[currentPlaceNr].name);
//            console.info('sat height: ' + altitude1);
//
//			var singlePoi = {
//				"id": poiData[currentPlaceNr].id,
//				"latitude": parseFloat(poiData[currentPlaceNr].latitude),
//				"longitude": parseFloat(poiData[currentPlaceNr].longitude),
//				"altitude": altitude1,
//				"title": poiData[currentPlaceNr].name + distance1.toString(),
//				"description": poiData[currentPlaceNr].description,
//				"selected": poiData[currentPlaceNr].selected
//			};
//
//            World.addMarker = new Marker(singlePoi);
//			World.markerList.push(World.addMarker);
//
//			if(poiData[currentPlaceNr].selected == "true")
//			{
//			     if (World.currentMarker) {
//			        if (World.currentMarker.poiData.id == World.addMarker.poiData.id) {
//			            return;
//                    }
//                        World.currentMarker.setDeselected(World.currentMarker);
//                    }
//                    World.addMarker.setSelected(World.addMarker);
//                    World.currentMarker = World.addMarker;
//			}
//		}

//		"altitude": 33 + (currentPlaceNr * 400)
//      "altitude": parseFloat(poiData[currentPlaceNr].altitude),

         // loop through POI-information and create an AR.GeoObject (=Marker) per POI
         World.showPlaceNr = 0;
         for (var index = 0; index < World.POIData.length && World.markerList.length < 10 ; index++)
         {
             var location = new AR.GeoLocation(parseFloat(World.POIData[index].latitude),
                                               parseFloat(World.POIData[index].longitude));
             var distance = location.distanceToUser();

             //只顯示1公里內POI
//             if (distance > 1000 && World.POIData[index].selected == "false")
//             {
//                 continue;
//             }

             //設定高度
//             var altitude = 5*index + 5;
            var altitude;
            if (distance > 0)
            {
               altitude =  10*World.showPlaceNr +500;
            }
            if (distance > 50)
            {
               altitude =  30*World.showPlaceNr +550;
            }
            if (distance > 100)
            {
               altitude =  60*World.showPlaceNr +600;
            }
            if (distance > 250)
            {
               altitude =  90*World.showPlaceNr +800;
            }
            if (distance > 500)
            {
               altitude =  300*World.showPlaceNr +900;
            }
            World.showPlaceNr++;
            altitude = altitude/8;
            console.info('POIData name '+World.POIData[index].name);
            console.info('POIData altitude '+altitude);

             //距離文字
             var distanceStr = (distance > 999) ?
             (distance / 1000).toFixed(2) + " km" :
             (Math.round(distance)).toString() + " m"

             //POI資訊
             var singlePoi =
             {
                 "id": World.POIData[index].id,
                 "latitude": parseFloat(World.POIData[index].latitude),
                 "longitude": parseFloat(World.POIData[index].longitude),
                 "altitude": altitude,
                 "title": World.POIData[index].name,
                 "description": distanceStr,
                 "category": World.POIData[index].category
             };

              World.addMarker = new Marker(singlePoi);

              //marker尺寸
              World.addMarker.markerDrawable_idle.height = 1.8;
              World.addMarker.markerDrawable_selected.height = 1.8;

              //選擇或未選顏色-紅/綠
              if (World.POIData[index].selected == "true")
              {
                  World.addMarker.setSelected(World.addMarker);
                  World.currentMarker = World.addMarker;
              }
              else
              {
                  World.addMarker.setDeselected(World.addMarker);
                  World.markerList.push(World.addMarker);
              }
         }
		// updates distance information of all placemarks
		World.updateDistanceToUserValues();
		World.updateStatusMessage(index + ' places loaded');
	},

	// sets/updates distances of all makers so they are available way faster than calling (time-consuming) distanceToUser() method all the time
	updateDistanceToUserValues: function updateDistanceToUserValuesFn() {
		for (var i = 0; i < World.markerList.length; i++) {
			World.markerList[i].distanceToUser = World.markerList[i].markerObject.locations[0].distanceToUser();
			console.info('distanceToUser: ' + World.markerList[i].distanceToUser);
		}
	},

	// updates status message shown in small "i"-button aligned bottom center
	updateStatusMessage: function updateStatusMessageFn(message, isWarning) {

		var themeToUse = isWarning ? "e" : "c";
		var iconToUse = isWarning ? "alert" : "info";

		$("#status-message").html(message);
		$("#popupInfoButton").buttonMarkup({
			theme: themeToUse
		});
		$("#popupInfoButton").buttonMarkup({
			icon: iconToUse
		});
	},

	// location updates, fired every time you call architectView.setLocation() in native environment
	locationChanged: function locationChangedFn(lat, lon, alt, acc) {

		// request data if not already present
//		if (!World.initiallyLoadedData) {
//			World.requestDataFromServer(lat, lon);
//			World.initiallyLoadedData = true;
//		} else if (World.locationUpdateCounter === 0) {
//			// update placemark distance information frequently, you max also update distances only every 10m with some more effort
//			World.updateDistanceToUserValues();
//		}
//
//		// helper used to update placemark information every now and then (e.g. every 10 location upadtes fired)
//		World.locationUpdateCounter = (++World.locationUpdateCounter % World.updatePlacemarkDistancesEveryXLocationUpdates);
//        if (World.POIData.length == 0){
//            return;
//        }
//        console.info('World.initiallyLoadedData'+World.initiallyLoadedData);
//        console.info('World.locationUpdateCounter'+World.locationUpdateCounter);
//         if (!World.initiallyLoadedData)
//         {
//             World.refreshMarkerView();
//             World.initiallyLoadedData = true;
//         }
//         else if (World.locationUpdateCounter == 0)
//         {
//            //Update placemark distance information frequently, you max also update distances only every 10m with some more effort.
////            World.updateDistanceToUserValues();
//
//            World.refreshMarkerView();
//         }
//
//        /* Helper used to update placemark information every now and then (e.g. every 10 location upadtes fired). */
//        World.locationUpdateCounter = (++World.locationUpdateCounter % World.updatePlacemarkDistancesEveryXLocationUpdates);
	},

	createModelAtLocation: function createModelAtLocationFn(lat, lng, size, altitude, rotateY, url) {
        console.info('createModelAtLocation');
        var location = new AR.GeoLocation(lat, lng);

        if(World.image){
            if(!World.image.destroyed){
                World.image.destroy();
            }
            World.image = null;
        }

        if(World.model){
            if(!World.model.destroyed){
                World.model.destroy();
            }
            World.model = null;
        }
        World.model = new AR.Model(url, {
            onLoaded: this.worldLoaded,
            onError: World.onError,
            scale: {
                x: size,
                y: size,
                z: size
            },
            rotate: {
                y: rotateY
            },
        });

        var modelAnimation = new AR.ModelAnimation(World.model, "Animation_00");
        modelAnimation.start(500);

        /* Putting it all together the location and 3D model is added to an AR.GeoObject. */
        this.geoObject = new AR.GeoObject(location, {
            drawables: {
                cam: [World.model],
            },
        });
    },

    createImageAtLocation: function createImageAtLocationFn(lat, lng, url) {
        console.info('createImageAtLocation');
        var location = new AR.GeoLocation(lat, lng);

        if(World.image){
            if(!World.image.destroyed){
                World.image.destroy();
            }
            World.image = null;
        }
        if(World.model){
            if(!World.model.destroyed){
                World.model.destroy();
            }
            World.model = null;
        }

        World.image = new AR.ImageResource(url);
        this.imageDrawable = new AR.ImageDrawable(World.image, 1.8, {
            zOrder: 0,
            opacity: 1.0
        });

        /* Putting it all together the location and 3D model is added to an AR.GeoObject. */
        this.geoObject = new AR.GeoObject(location, {
            drawables: {
                cam: [this.imageDrawable]
            }
        });
    },

	// fired when user pressed maker in cam
	onMarkerSelected: function onMarkerSelectedFn(marker) {
		//World.currentMarker = marker;

        var targetId = marker.poiData.id;
        var markerSelectedJSON = {
            action: "showPOIInfo",
            id: targetId,
        };
        AR.platform.sendJSONObject(markerSelectedJSON);

		// deselect previous marker
        if (World.currentMarker) {
            if (World.currentMarker.poiData.id == marker.poiData.id) {
                return;
            }
            World.currentMarker.setDeselected(World.currentMarker);
        }
        // highlight current one
        marker.setSelected(marker);
        World.currentMarker = marker;

        for (var index = 0; index < World.POIData.length ; index++){
            if (World.POIData[index].name == marker.poiData.title){
                World.POIData[index].selected = "true"
            } else{
                World.POIData[index].selected = "false"
            }
        }

//		// update panel values
//		$("#poi-detail-title").html(marker.poiData.title);
//		$("#poi-detail-description").html(marker.poiData.description);
//
//		/* It's ok for AR.Location subclass objects to return a distance of `undefined`. In case such a distance was calculated when all distances were queried in `updateDistanceToUserValues`, we recalcualte this specific distance before we update the UI. */
//		if( undefined == marker.distanceToUser ) {
//			marker.distanceToUser = marker.markerObject.locations[0].distanceToUser();
//		}
//		var distanceToUserValue = (marker.distanceToUser > 999) ? ((marker.distanceToUser / 1000).toFixed(2) + " km") : (Math.round(marker.distanceToUser) + " m");
//
//		$("#poi-detail-distance").html(distanceToUserValue);

		// show panel
//		$("#panel-poidetail").panel("open", 123);
//
//		$( ".ui-panel-dismiss" ).unbind("mousedown");
//
//		$("#panel-poidetail").on("panelbeforeclose", function(event, ui) {
//			World.currentMarker.setDeselected(World.currentMarker);
//		});
	},

	// screen was clicked but no geo-object was hit
	onScreenClick: function onScreenClickFn() {
		// you may handle clicks on empty AR space too
	},

	// returns distance in meters of placemark with maxdistance * 1.1
	getMaxDistance: function getMaxDistanceFn() {

		// sort places by distance so the first entry is the one with the maximum distance
		World.markerList.sort(World.sortByDistanceSortingDescending);

		// use distanceToUser to get max-distance
		var maxDistanceMeters = World.markerList[0].distanceToUser;

		// return maximum distance times some factor >1.0 so ther is some room left and small movements of user don't cause places far away to disappear
		return maxDistanceMeters * 1.1;
	},

	// request POI data
//	requestDataFromServer: function requestDataFromServerFn(lat, lon) {
//
//		// set helper var to avoid requesting places while loading
//		World.isRequestingData = true;
//		World.updateStatusMessage('Requesting places from web-service');
//
//		// server-url to JSON content provider
//		var serverUrl = ServerInformation.POIDATA_SERVER + "?" + ServerInformation.POIDATA_SERVER_ARG_LAT + "=" + lat + "&" + ServerInformation.POIDATA_SERVER_ARG_LON + "=" + lon + "&" + ServerInformation.POIDATA_SERVER_ARG_NR_POIS + "=20";
//
//		var jqxhr = $.getJSON(serverUrl, function(data) {
//			World.loadPoisFromJsonData(data);
//		})
//			.error(function(err) {
//				World.updateStatusMessage("Invalid web-service response.", true);
//				World.isRequestingData = false;
//			})
//			.complete(function() {
//				World.isRequestingData = false;
//			});
//	},

	// helper to sort places by distance
	sortByDistanceSorting: function(a, b) {
		return a.distanceToUser - b.distanceToUser;
	},

	// helper to sort places by distance, descending
	sortByDistanceSortingDescending: function(a, b) {
		return b.distanceToUser - a.distanceToUser;
	}

};


/* forward locationChanges to custom function */
AR.context.onLocationChanged = World.locationChanged;

/* forward clicks in empty area to World */
AR.context.onScreenClick = World.onScreenClick;