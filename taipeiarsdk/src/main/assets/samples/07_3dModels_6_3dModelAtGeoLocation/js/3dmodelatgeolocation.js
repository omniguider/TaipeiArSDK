var World = {
    loaded: false,
    rotating: false,
    trackableVisible: false,
    resourcesLoaded: false,
    modelLoaded: false,
    dragged: false,
    moving: true,
    snapped: false,
    interactionContainer: 'snapContainer',
    previousDragValue: {
        x: 0,
        y: 0
    },
    previousScaleValue: 0,
    previousScaleValueButtons: 0,
    previousRotationValue: 0,
    previousTranslateValueRotate: {
        x: 0,
        y: 0
    },
    previousTranslateValueSnap: {
        x: 0,
        y: 0
    },
    defaultScale: 0,
    targetNameA: '',
    targetNameB: '',
    targetNameC: '',

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
            var location = new AR.RelativeLocation(null,    //location
                getRandomArbitrary(-20, 20),    //northing
                getRandomArbitrary(-20, 20),    //easting
                getRandomArbitrary(1, 6));  //altitudeDelta

            /* Next the model object is loaded. */
            var scaleValue = parseFloat(1);
            var modelEarth = new AR.Model("assets/earth.wt3", {
                onError: World.onError,
                scale: {
                    x: scaleValue,
                    y: scaleValue,
                    z: scaleValue
                },
                translate: {
                    x: 0.0,
                    y: 0.0,
                    z: -0.3
                },
                rotate: {
                  roll: 0.0,
                  tilt: parseFloat(0),
                  heading: 0.0,
                },
                onScaleChanged: function(scale) {
                    var s = scaleValue * scale;
                    this.scale = {x: s, y: s, z: s};
                },
                onScaleEnded: function(scale) {
                    scaleValue = this.scale.x;
                },
                onDragChanged: function(x, y) {
                            var movement = {
                                x: 0,
                                y: 0
                            };
                            /* Calculate the touch movement between this event and the last one */
                            movement.x = World.previousDragValue.x - x;
                            movement.y = World.previousDragValue.y - y;
                         if (World.rotating) {
                            /*
                                Rotate the car model accordingly to the calculated movement values and the current
                                orientation of the model.
                            */
                            this.rotate.z += (Math.cos(this.rotate.y * Math.PI / 180) * movement.x *
                                -1 + Math.sin(this.rotate.y * Math.PI / 180) * movement.y) * 180;
                            this.rotate.x += (Math.cos(this.rotate.y * Math.PI / 180) * movement.y +
                                Math.sin(this.rotate.y * Math.PI / 180) * movement.x) * -180;
                        } else if (World.moving) {
                            this.translate.x -= movement.x;
                            this.translate.z += movement.y;
                        }
                            World.previousDragValue.x = x;
                            World.previousDragValue.y = y;
                            console.log("model, onDragChanged:" + x + "," + y + " model is rotating:" + World.rotating + " model is moving:" + World.moving);
                        World.dragged = true;
                    },
                    onDragEnded: function( /*x, y*/ ) {
                            World.previousDragValue.x = 0;
                            World.previousDragValue.y = 0;
                    },
                  onRotationChanged: function(angleInDegrees) {
                                        this.rotate.z = World.previousRotationValue - angleInDegrees;
                                    },
                                    onRotationEnded: function( /*angleInDegrees*/ ) {
                                        World.previousRotationValue = this.rotate.z
                                    },
            });

//            var image = new AR.ImageResource("assets/bird.png");
//            var video = new AR.VideoDrawable("assets/flower.mp4", 1.8, {
//                isTransparent:true,
//                scale: {
//                    x: scaleValue,
//                    y: scaleValue,
//                    z: scaleValue
//                },
//                rotate: {
//                    x: 0,
//                    y: getRandomArbitrary(0, 180)
//                },
//                onScaleChanged: function(scale) {
//                    var s = scaleValue * scale;
//                    this.scale = {x: s, y: s, z: s};
//                },
//                onScaleEnded: function(scale) {
//                    scaleValue = this.scale.x;
//                },
//            });
//            video.play(-1);

//            var myAnimation = new AR.ModelAnimation(modelEarth, "Animation_00");
//            myAnimation.start(500);

            /* Putting it all together the location and 3D model is added to an AR.GeoObject. */
            this.geoObject = new AR.GeoObject(location, {
                drawables: {
                    cam: [modelEarth],
                }
            });
        }

//       var model = new AR.Model("assets/diplodocus.wt3", {
//           onLoaded: this.loadingStep,
//               scale: {
//                   x: 0.4,
//                   y: 0.4,
//                   z: 0.4
//               },
//               rotate: {
//                   x: 45,
//                   y: 270,
//                   z: 270
//               },
//               onClick: function() {
//                   console.log("model, onClick:" + World.myAnimation.isRunning());
//                   if (!World.dragged) {
//                       if (World.myAnimation.isRunning()) {
//                           World.myAnimation.pause();
//                       } else {
//                           World.myAnimation.resume();
//                       }
//                   } else {
//                       World.dragged = false;
//                   }
//               },
//               onScaleChanged: function(scale) {
//                   var s = scaleValue * scale;
//                   this.scale = {x: s, y: s, z: s};
//               },
//               onScaleEnded: function(scale) {
//                   scaleValue = this.scale.x;
//               },
//               onError: World.onError
//       });
//
//        if (World.myAnimation == null){
//              this.myAnimation = new AR.ModelAnimation(model, "Animation_00", {
//                  onStart: function() {
//                      console.log("onStart");
//                  },
//                  onFinish: function() {
//                      console.log("onFinish");
//                  }
//              });
//          }
//        // a Positionable having the circle attached as its drawable
//        var positionable = new AR.Positionable("myPositionable", {
//            drawables : {
//                cam : model
//            }
//        });
    },

    onError: function onErrorFn(error) {
        alert(error);
    }
};

function getRandomArbitrary(min, max) {
    return Math.random() * (max - min) + min;
}

World.init();