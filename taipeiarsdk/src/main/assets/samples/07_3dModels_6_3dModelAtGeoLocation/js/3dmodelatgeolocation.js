var World = {

    init: function initFn() {
        this.createModelAtLocation();
    },

    createModelAtLocation: function createModelAtLocationFn() {
        /*
            First a location where the model should be displayed will be defined. This location will be relative to
            the user.
        */
        for (var index = 0; index < 20 ; index++)
        {
//            var geoLoc = new AR.GeoLocation(25.0809392, 121.5646568);
            var location = new AR.RelativeLocation(null,
                getRandomArbitrary(-20, 20),
                getRandomArbitrary(-20, 20),
                getRandomArbitrary(1, 8));

            /* Next the model object is loaded. */
            var scaleValue = parseFloat(1);
            var modelEarth = new AR.Model("assets/earth.wt3", {
                onError: World.onError,
                scale: {
                    x: scaleValue,
                    y: scaleValue,
                    z: scaleValue
                },
                rotate: {
                    x: 180,
                    y: 180
                },
                onScaleChanged: function(scale) {
                    var s = scaleValue * scale;
                    this.scale = {x: s, y: s, z: s};
                },
                onScaleEnded: function(scale) {
                    scaleValue = this.scale.x;
                },
            });

            /* Putting it all together the location and 3D model is added to an AR.GeoObject. */
            this.geoObject = new AR.GeoObject(location, {
                drawables: {
                    cam: [modelEarth],
                }
            });
        }
    },

    onError: function onErrorFn(error) {
        alert(error);
    }
};

function getRandomArbitrary(min, max) {
    return Math.random() * (max - min) + min;
}

World.init();