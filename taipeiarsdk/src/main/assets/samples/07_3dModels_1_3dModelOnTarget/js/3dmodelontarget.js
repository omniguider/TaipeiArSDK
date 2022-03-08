var World = {
	loaded: false,
	rotating: true,
	trackableVisible: false,
    resourcesLoaded: false,
    modelLoaded: false,
    dragged: false,
    moving: false,
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
		this.createOverlays();
	},

	createOverlays: function createOverlaysFn() {
	    console.info('is been called');

//        document.getElementById('Camera').style.visibility = 'hidden';

        AR.context.destroyAll();

        World.targetNameA = '';
        World.targetNameB = '';
        World.targetNameC = '';

	    console.info(pattenUrl);
		/*
			First an AR.ImageTracker needs to be created in order to start the recognition engine. It is initialized with a AR.TargetCollectionResource specific to the target collection that should be used. Optional parameters are passed as object in the last argument. In this case a callback function for the onTargetsLoaded trigger is set. Once the tracker loaded all its target images, the function worldLoaded() is called.

			Important: If you replace the tracker file with your own, make sure to change the target name accordingly.
			Use a specific target name to respond only to a certain target or use a wildcard to respond to any or a certain group of targets.
		*/
		this.targetCollectionResource = new AR.TargetCollectionResource(pattenUrl, {
			onLoaded: function () {
				World.resourcesLoaded = true;
				//this.loadingStep;
			},
            onError: function(errorMessage) {
            	alert(errorMessage);
            }
		});

		this.tracker = new AR.ImageTracker(this.targetCollectionResource, {
			//onTargetsLoaded: this.loadingStep,
            onError: function(errorMessage) {
            	alert(errorMessage);
            }
		});


		/*
			Similar to 2D content the 3D model is added to the drawables.cam property of an AR.ImageTrackable.
			"*" means all target in the tracker file.

		*/
		var trackable = new AR.ImageTrackable(this.tracker, "*", {
//			drawables: {
//				cam: [this.modelCar]
//			},
			onImageRecognized:function (target) {

                console.info(target.name);
                myTarget = target.name;

                World.targetNameA = myTarget;
                World.onPoiDetailMoreButtonClicked(target.name);

                return false;
			}
		});
	},

	createNewOverlays: function createNewOverlaysFn() {

    		this.targetCollectionResource = new AR.TargetCollectionResource(pattenUrl, {
    			onLoaded: function () {
    				World.resourcesLoaded = true;
    				//this.loadingStep;
    			},
                onError: function(errorMessage) {
                	alert(errorMessage);
                }
    		});

    		this.tracker = new AR.ImageTracker(this.targetCollectionResource, {
                onError: function(errorMessage) {
                	alert(errorMessage);
                }
    		});


    		var trackable = new AR.ImageTrackable(this.tracker, "*", {
    			onImageRecognized:function (target) {

                    console.info(target.name);
                    myTarget = target.name;

                    if (myTarget == World.targetNameA || myTarget == World.targetNameB ||myTarget == World.targetNameC){
                        return;
                    }else {
                        if (World.targetNameA == '')
                            World.targetNameA = myTarget;
                        else if (World.targetNameB == '')
                            World.targetNameB = myTarget;
                        else if (World.targetNameC == '')
                            World.targetNameC = myTarget;

                        World.onPoiDetailMoreButtonClicked(target.name);
                    }

                    return false;
    			}
    		});
    	},


    createOverlays3D: function createOverlays3DFn(uri, angle, view_size) {

            console.info('doing 3D overlay');
            console.info(uri);
    		/*
    			First an AR.ImageTracker needs to be created in order to start the recognition engine. It is initialized with a AR.TargetCollectionResource specific to the target collection that should be used. Optional parameters are passed as object in the last argument. In this case a callback function for the onTargetsLoaded trigger is set. Once the tracker loaded all its target images, the function worldLoaded() is called.

    			Important: If you replace the tracker file with your own, make sure to change the target name accordingly.
    			Use a specific target name to respond only to a certain target or use a wildcard to respond to any or a certain group of targets.
    		*/
    		this.targetCollectionResource = new AR.TargetCollectionResource(pattenUrl, {
    			onLoaded: function () {
    				World.resourcesLoaded = true;
    				//this.loadingStep;
    			},
                onError: function(errorMessage) {
                	alert(errorMessage);
                }
    		});

    		this.tracker = new AR.ImageTracker(this.targetCollectionResource, {
    			//onTargetsLoaded: this.loadingStep,
                onError: function(errorMessage) {
                	alert(errorMessage);
                }
    		});

    		/*
    			3D content within Wikitude can only be loaded from Wikitude 3D Format files (.wt3). This is a compressed binary format for describing 3D content which is optimized for fast loading and handling of 3D content on a mobile device. You still can use 3D models from your favorite 3D modeling tools (Autodesk® Maya® or Blender) but you'll need to convert them into the wt3 file format. The Wikitude 3D Encoder desktop application (Windows and Mac) encodes your 3D source file. You can download it from our website. The Encoder can handle Autodesk® FBX® files (.fbx) and the open standard Collada (.dae) file formats for encoding to .wt3.

    			Create an AR.Model and pass the URL to the actual .wt3 file of the model. Additional options allow for scaling, rotating and positioning the model in the scene.

    			A function is attached to the onLoaded trigger to receive a notification once the 3D model is fully loaded. Depending on the size of the model and where it is stored (locally or remotely) it might take some time to completely load and it is recommended to inform the user about the loading time.
    		*/
            var scaleValue = parseFloat(view_size);
            var rotationValues = 0.0;
//            if (World.model != null) {
//                World.model.enabled = false;
//                World.resetModel();
//            } else {
                this.model = new AR.Model(uri, {
                    //onLoaded: this.loadingStep,
                    onError: World.onError,
                    scale: {
    //    				x: 0.045,
    //    				y: 0.045,
    //    				z: 0.045
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
                        tilt: parseFloat(angle),
    //                    tilt: 270.0,
                        heading: 0.0,
                    },
    //    			onRotationChanged: function(angleInDegrees) {
    //                    this.rotate.z = rotationValues - angleInDegrees;
    //                },
    //                onRotationEnded: function(angleInDegrees) {
    //                    rotationValues = this.rotate.z
    //                },
                    onClick: function() {
                        console.log("model, onClick:" + World.myAnimation.isRunning());
                        //to fix the bug that the onClick event will triggered even it a drag event
                        //we don't stop animation if the event is drag event
                        if (!World.dragged) {
                            if (World.myAnimation.isRunning()) {
                                World.myAnimation.pause();
                            } else {
                                World.myAnimation.resume();
                            }
                        } else {
                            World.dragged = false;
                        }
                    },
                    onScaleChanged: function(scale) {
                        var s = scaleValue * scale;
                        this.scale = {x: s, y: s, z: s};
                    },
                    onScaleEnded: function(scale) {
                        scaleValue = this.scale.x;
                    },

                    onDragChanged: function(x, y) {
    //                    if (World.snapped) {
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
    //                    }
                        World.dragged = true;
                    },
                    onDragEnded: function( /*x, y*/ ) {
    //                    if (World.snapped) {
                            World.previousDragValue.x = 0;
                            World.previousDragValue.y = 0;
    //                    }
                    },
                    onRotationChanged: function(angleInDegrees) {
                        this.rotate.z = previousRotationValue - angleInDegrees;
                    },
                    onRotationEnded: function( /*angleInDegrees*/ ) {
                        previousRotationValue = this.rotate.z
                    },
                    onLoaded: function() {
                        console.log("model, onLoaded");
                        World.modelIsLoaded();
                        if (!World.recognized) return;
                        World.model.enabled = true;
                        if (!World.trackable.snapToScreen.enabled) {
                            World.trackable.snapToScreen.enabled = true;
                            AR.platform.sendJSONObject({
                                action: "hidePatternHint"
                            });
                            AR.platform.sendJSONObject({
                                action: "showToolButtons"
                            });
                            World.myAnimation.start(500);
                        }
                    }
                });
//            }

            var myAnimation = new AR.ModelAnimation(this.model, "Animation_00");
//            //myAnimation.onFinish = myAnimation.start;
            myAnimation.start(500);
////            this.appearingAnimation = this.createAppearingAnimation(this.model, 0.045);
//            this.appearingAnimation = this.createAppearingAnimation(this.model, parseFloat(view_size));

//            this.rotationAnimation = new AR.PropertyAnimation(this.model, "rotate.z", -25, 335, 10000);

//            var imgRotate = new AR.ImageResource("assets/btn_rotate_g.png", {
//                onError: World.onError
//            });

//            this.buttonRotate = new AR.ImageDrawable(imgRotate, 0.2, {
//                translate: {
//                    x: 0.35,
//                    y: 0.45
//                },
//                onClick: World.toggleAnimateModel
//            });

            var imgSnap = new AR.ImageResource("assets/btn_move_w.png", {
                onError: World.onError
            });

            this.buttonSnap = new AR.ImageDrawable(imgSnap, 0.2, {
                translate: {
                    x: -0.35,
                    y: -0.45
                },
                onClick: World.toggleSnapping
            });

            if (World.myAnimation == null){
                this.myAnimation = new AR.ModelAnimation(this.model, "Animation_00", {
                    onStart: function() {
                        console.log("onStart");
                    },
                    onFinish: function() {
                        console.log("onFinish");
                    }
                });
            }

    		/*
    			Similar to 2D content the 3D model is added to the drawables.cam property of an AR.ImageTrackable.
    		*/
//    		if (World.trackable != null) {
//                if (World.trackable.snapToScreen.enabled)
//                    World.trackable.snapToScreen.enabled = false;
//            } else {
                this.trackable = new AR.ImageTrackable(this.tracker, myTarget, {
                    drawables: {
                        cam: [World.model]
                    },
                    snapToScreen: {
                        snapContainer: document.getElementById('snapContainer')
                    },
    //    			onImageRecognized: function onImageRecognizedFn(){
    ////    			    document.getElementById("overlayPicker").className = "overlayPicker";
    //                    console.info('onImageRecognized3D');
    //                    World.appear();
    //
    //    			},
                    onImageRecognized: function onImageRecognizedFn(target) {
                        console.log(target.name + " " + World.targetId + " " + World.masterkey + " " +
                            (target.name !== World.targetId) + " " + (target.name !== World.masterkey));
    //                    if (target.name !== World.targetId && target.name !== World.masterkey)
    //                        return;
                        World.recognized = true;
                         console.info("World.model.isLoaded()"+World.model.isLoaded());
                        if (!World.model.isLoaded())
                            return;
                        World.model.enabled = true;
                        console.info("World.trackable.snapToScreen.enabled"+World.trackable.snapToScreen.enabled);
                        if (!World.trackable.snapToScreen.enabled) {
//
//                            console.info(target.name);
//                            World.trackable.snapToScreen.enabled = true;
//                            //World.toggleSnapping();
//                            AR.platform.sendJSONObject({
//                                action: "hidePatternHint"
//                            });
//                            AR.platform.sendJSONObject({
//                                action: "showToolButtons"
//                            });
//                            if (World.isArMission)
//                                AR.platform.sendJSONObject({
//                                    action: "showMissionARRecognized"
//                                });
//                            if (World.myAnimation.isRunning())
//                                World.myAnimation.stop();
//                            World.myAnimation = new AR.ModelAnimation(World.model, "Animation_00");
//                            World.myAnimation.start();
//                            var watchDog = function() {
//                                console.log("WatchDog:" + World.myAnimation.isRunning());
//                                if (!World.myAnimation.isRunning()) {
//                                    World.myAnimation.resume();
//                                }
//                            };
//                            setTimeout(watchDog, 100);
                        }
                    },
                    onImageLost: function onImageLostFn() {

                        World.createNewOverlays();
    //    			    document.getElementById("overlayPicker").className = "overlayPickerInactive";
//                        World.disappear();
//
//                        AR.platform.sendJSONObject({
//                            action: "showPatternHint"
//                        });
//                        AR.platform.sendJSONObject({
//                            action: "hideToolButtons"
//                        });
//                        console.info('restarted!!!!3D');
//                        World.createOverlays();

    //                    setTimeout(function () {
    //                       World.createOverlays();
    //                    }, 3000);


//                        return false;
                    }
                });
//    	}
    },

    createOverlaysImg: function createOverlaysImgFn(uri) {

                console.info('doing image overlay');

            /*
                Use AR.ClientTracker instead of targetCollectionResource
                To load file from webservice.
             */
            this.tracker = new AR.ClientTracker(pattenUrl, {
                onTargetsLoaded: this.worldLoaded,
                onError: function(errorMessage) {
                	alert(errorMessage);
                }
            });

    		/*
    			The next step is to create the augmentation. In this example an image resource is created and passed to the AR.ImageDrawable. A drawable is a visual component that can be connected to an IR target (AR.ImageTrackable) or a geolocated object (AR.GeoObject). The AR.ImageDrawable is initialized by the image and its size. Optional parameters allow for position it relative to the recognized target.
    		*/
    		/* Create overlay for page one */
    		var imgOne = new AR.ImageResource(uri,{
                onLoaded: function(){
                    World.modelIsLoaded;
                    AR.platform.sendJSONObject({
                        action: "hidePatternHint"
                    });
                    AR.platform.sendJSONObject({
                        action: "showRescanButton"
                    });
                }

            });
    		var overlayOne = new AR.ImageDrawable(imgOne, 1, {
    			translate: {
    				x:-0.15
    			}

    		});

    		/*
    			The last line combines everything by creating an AR.ImageTrackable with the previously created tracker, the name of the image target and the drawable that should augment the recognized image.
    			Please note that in this case the target name is a wildcard. Wildcards can be used to respond to any target defined in the target collection. If you want to respond to a certain target only for a particular AR.ImageTrackable simply provide the target name as specified in the target collection.
    		*/
    		var pageOne = new AR.ImageTrackable(this.tracker, myTarget, {
    			drawables: {
    				cam: overlayOne
    			},
    			onImageLost: function onImageLostFn() {
                    console.info('restarted!!!!');


                    World.createOverlays();

                    return false;
                },
    			//onImageRecognized: this.removeLoadingBar,
                onError: function(errorMessage) {
                	alert(errorMessage);
                }
    		});
    },

    createOverlaysVideo: function createOverlaysVideoFn(uri, videoTransparent, coverImage) {

            console.info('doing video overlay'+videoTransparent);
            var isTransparent_b;
            if(videoTransparent == 'Y')
                isTransparent_b = true;
            else
                isTransparent_b = false;

            var isCoverImage_b;
                if(coverImage == 'Y')
                    isCoverImage_b = false;
                else
                    isCoverImage_b = true;

            document.getElementById('loader').style.visibility = 'visible';
            this.tracker = new AR.ClientTracker(pattenUrl, {
                onTargetsLoaded: this.worldLoaded,
                onError: function(errorMessage) {
                	alert(errorMessage);
                }
            });

    		/*
    			Besides images, text and HTML content you are able to display videos in augmented reality. With the help of AR.VideoDrawables you can add a video on top of any image recognition target (AR.ImageTrackable) or have it displayed at any geo location (AR.GeoObject). Like any other drawable you can position, scale, rotate and change the opacity of the video drawable.

    			The video we use for this example is "video.mp4". As with all resources the video can be loaded locally from the application bundle or remotely from any server. In this example the video file is already bundled with the application.

    			The URL and the size are required when creating a new AR.VideoDrawable. Optionally the offsetX and offsetY parameters are set to position the video on the target. The values for the offsets are in SDUs. If you want to know more about SDUs look up the code reference.
    		*/
    		var video = new AR.VideoDrawable(uri, 1.0, {
//    			translate: {
//    				y: -0.3
//    			}
                isTransparent: isTransparent_b,
//                onLoaded: World.modelIsLoaded,
                onLoaded: function() {
                    console.log("model, onLoaded");
                    World.modelIsLoaded();

//                    World.pageOne.snapToScreen.enabled = isCoverImage_b;
                    if (!World.pageOne.snapToScreen.enabled) {
                        World.pageOne.snapToScreen.enabled = false;
                        AR.platform.sendJSONObject({
                            action: "hidePatternHint"
                        });
                        AR.platform.sendJSONObject({
                            action: "showRescanButton"
                        });
                    }
                }
    		});

    		/*
    			Adding the video to the image target is straight forward and similar like adding any other drawable to an image target.

    			Note that this time we use "*" as target name. That means that the AR.ImageTrackable will respond to any target that is defined in the target collection. You can use wildcards to specify more complex name matchings. E.g. 'target_?' to reference 'target_1' through 'target_9' or 'target*' for any targets names that start with 'target'.

    			To start the video immediately after the target is recognized we call play inside the onImageRecognized trigger. Supplying -1 to play tells the Wikitude SDK to loop the video infinitely. Choose any positive number to re-play it multiple times.

    			Once the video has been started for the first time (indicated by this.hasVideoStarted), we call pause every time the target is lost and resume every time the tracker is found again to continue the video where it left off.
    		*/
    		this.pageOne = new AR.ImageTrackable(this.tracker, myTarget, {
    			drawables: {
    				cam: [video]
    			},
    			snapToScreen: {
    			    enabledOnExitFieldOfVision: isCoverImage_b,
                    snapContainer: document.getElementById('snapContainer')
                },
    			onImageRecognized: function onImageRecognizedFn() {
    			    setTimeout(function () {
    			        document.getElementById('loader').style.visibility = 'hidden';
    			        }, 2000);
    				if (this.hasVideoStarted) {
    					video.resume();
    				}
    				else {
    					this.hasVideoStarted = true;
    					video.play(-1);
    				}
    				//World.removeLoadingBar();
    			},
    			onImageLost: function onImageLostFn() {
//    				video.pause();
//    				console.info('restarted!!!!');
//                    World.createOverlays();

//                    setTimeout(function () {
//                       World.createOverlays();
//                    }, 3000);

    				return false;
    			},
                onError: function(errorMessage) {
                	alert(errorMessage);
                }
    		});
    },

    createOverlaysText: function createOverlaysTextFn(uri) {

                console.info('doing text image overlay');

            /*
                Use AR.ClientTracker instead of targetCollectionResource
                To load file from webservice.
             */
            this.tracker = new AR.ClientTracker(pattenUrl, {
                onTargetsLoaded: this.worldLoaded,
                onError: function(errorMessage) {
                    alert(errorMessage);
                }
            });

            /*
                The next step is to create the augmentation. In this example an image resource is created and passed to the AR.ImageDrawable. A drawable is a visual component that can be connected to an IR target (AR.ImageTrackable) or a geolocated object (AR.GeoObject). The AR.ImageDrawable is initialized by the image and its size. Optional parameters allow for position it relative to the recognized target.
            */
            /* Create overlay for page one */
            var imgOne = new AR.ImageResource(uri);
            var overlayOne = new AR.ImageDrawable(imgOne, 1, {
                translate: {
                    x:-0.15
                }

            });

            World.modelIsLoaded();

            AR.platform.sendJSONObject({
                action: "hidePatternHint"
            });
            AR.platform.sendJSONObject({
                action: "showRescanButton"
            });
            /*
                The last line combines everything by creating an AR.ImageTrackable with the previously created tracker, the name of the image target and the drawable that should augment the recognized image.
                Please note that in this case the target name is a wildcard. Wildcards can be used to respond to any target defined in the target collection. If you want to respond to a certain target only for a particular AR.ImageTrackable simply provide the target name as specified in the target collection.
            */
            var pageOne = new AR.ImageTrackable(this.tracker, myTarget, {
                drawables: {
                    cam: overlayOne
                },
                onImageLost: function onImageLostFn() {
                    console.info('restarted!!!!');


                    World.createOverlays();

                      AR.platform.sendJSONObject({
                            action: "showPatternHint"
                        });
                        AR.platform.sendJSONObject({
                            action: "hideRescanButton"
                        });

                    return false;
                },
                //onImageRecognized: this.removeLoadingBar,
                onError: function(errorMessage) {
                    alert(errorMessage);
                }
            });
    },


	createAppearingAnimation: function createAppearingAnimationFn(model, scale) {
    		/*
    			The animation scales up the 3D model once the target is inside the field of vision. Creating an animation on a single property of an object is done using an AR.PropertyAnimation. Since the car model needs to be scaled up on all three axis, three animations are needed. These animations are grouped together utilizing an AR.AnimationGroup that allows them to play them in parallel.

    			Each AR.PropertyAnimation targets one of the three axis and scales the model from 0 to the value passed in the scale variable. An easing curve is used to create a more dynamic effect of the animation.
    		*/
    		var sx = new AR.PropertyAnimation(model, "scale.x", 0, scale, 1500, {
    			type: AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC
    		});
    		var sy = new AR.PropertyAnimation(model, "scale.y", 0, scale, 1500, {
    			type: AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC
    		});
    		var sz = new AR.PropertyAnimation(model, "scale.z", 0, scale, 1500, {
    			type: AR.CONST.EASING_CURVE_TYPE.EASE_OUT_ELASTIC
    		});

    		return new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [sx, sy, sz]);
    },

//    appear: function appearFn() {
//        console.info('appear_called');
//
//        World.loaded = true;
//
//        World.trackableVisible = true;
//        if ( World.loaded ) {
//            // Resets the properties to the initial values.
//            World.resetModel();
//            World.appearingAnimation.start();
//        }
//    },

//    disappear: function disappearFn() {
//
//        console.info('disappear_called');
//
//        World.trackableVisible = false;
//    },

    resetModel: function resetModelFn() {

//    		World.snapped = false
//    		World.rotationAnimation.stop();
//            World.rotating = false;
            World.model.rotate.x = 0;
            World.model.rotate.y = 0;
            World.model.rotate.z = 0;
            World.model.translate.x = 0;
            World.model.translate.y = 0;
            World.model.translate.z = -0.3;
    },

    startAnimation: function startAnimationFn() {

        console.info('startAnimation_called');

        if (World.myAnimation == null)
            World.myAnimation = new AR.ModelAnimation(this.model, "Animation_00");
        if (World.myAnimation.isRunning()) {

        } else {
            World.myAnimation.start(500);
        }
//        var watchDog = function() {
//            console.log("WatchDog:" + World.myAnimation.isRunning());
//            if (!World.myAnimation.isRunning()) {
//                World.myAnimation.resume();
//            }
//        };
//        setTimeout(watchDog, 100);

    },

//    toggleAnimateModel: function toggleAnimateModelFn() {
//            if (!World.rotationAnimation.isRunning()) {
//                if (!World.rotating) {
//                    /* Starting an animation with .start(-1) will loop it indefinitely. */
//                    World.rotationAnimation.start(-1);
//                    World.rotating = true;
//                } else {
//                    /* Resumes the rotation animation */
//                    World.rotationAnimation.resume();
//                }
//            } else {
//                /* Pauses the rotation animation */
//                World.rotationAnimation.pause();
//            }
//
//            return false;
//        },

	 onPoiDetailMoreButtonClicked: function onPoiDetailMoreButtonClickedFn(targetName) {
                console.info('myTarget!');
	            console.info(myTarget);

                var targetId = targetName;

        		var markerSelectedJSON = {
                    action: "return_target_id",
                    id: targetId,
                };
        		/*
        			The sendJSONObject method can be used to send data from javascript to the native code.
        		*/
        		AR.platform.sendJSONObject(markerSelectedJSON);
     },

      callback3DModel: function callback3DModelFn(data, angle, isRightTarget, view_size){
           var str = data;
           var imageVisibility = String(isRightTarget);
           console.info('starting add 3D model');

//           document.getElementById('Camera').style.visibility = 'visible';
           if(imageVisibility == "true"){
               document.getElementById('Image').style.visibility = 'hidden';
           }
           else{
               document.getElementById('Image').style.visibility = 'visible';
           }

           World.createOverlays3D(str, angle, view_size);

      },

      callbackImage: function callbackImageFn(data, isRightTarget){
         var str = data;
         var imageVisibility = String(isRightTarget);
         console.info('starting add image');

//         document.getElementById('Camera').style.visibility = 'visible';
         if(imageVisibility == "true"){
         document.getElementById('Image').style.visibility = 'hidden';
         }
         else{
         document.getElementById('Image').style.visibility = 'visible';
         }

         World.createOverlaysImg(str);

       },

      callbackVideo: function callbackVideoFn(data, isRightTarget, isTransparent, isCoverImage){
           var str = data;
           var imageVisibility = String(isRightTarget);
           var videoTransparent = isTransparent;
           var coverImage = isCoverImage;
           console.info('start add video resource'+isTransparent);

//           document.getElementById('Camera').style.visibility = 'visible';
           if(imageVisibility == "true"){
           document.getElementById('Image').style.visibility = 'hidden';
           }
           else{
           document.getElementById('Image').style.visibility = 'visible';
           }

           World.createOverlaysVideo(str, videoTransparent, coverImage);

      },

      callbackText: function callbackTextFn(data, isRightTarget){
            var str = data;
            var imageVisibility = String(isRightTarget);
            console.info('start add text resource');

//            document.getElementById('Camera').style.visibility = 'visible';
            if(imageVisibility == "true"){
            document.getElementById('Image').style.visibility = 'hidden';
            }
            else{
            document.getElementById('Image').style.visibility = 'visible';
            }

            World.createOverlaysText(str);

       },

      captureScreen: function captureScreenFn() {

            AR.platform.sendJSONObject({
                action: "capture_screen"
            });

      },

      callPattenRecognizeStart: function callPattenRecognizeStartFn(data, Img){

        pattenUrl = data;
        var imageUrl = String(Img);
        var image = document.getElementById('Image');
//        var camera = document.getElementById('Camera');

        World.createOverlays();

         AR.platform.sendJSONObject({
            action: "showPatternHint"
        });

      },

      switchCam: function switchCamFn() {
        if (AR.hardware.camera.position === AR.CONST.CAMERA_POSITION.FRONT) {
            AR.hardware.camera.position = AR.CONST.CAMERA_POSITION.BACK;
        } else {
            AR.hardware.camera.position = AR.CONST.CAMERA_POSITION.FRONT;
        }
        console.log("switchCam:" + AR.hardware.camera.position);
    },
    /* Switch to front camera. */
    switchCamToFront: function switchCamToFrontFn() {
        console.log("switchCamToFront");
        if (AR.hardware.camera.position === AR.CONST.CAMERA_POSITION.BACK)
            AR.hardware.camera.position = AR.CONST.CAMERA_POSITION.FRONT;
    },
    /* Switch to back camera. */
    switchCamToBack: function switchCamToBackFn() {
        console.log("switchCamToBack");
        if (AR.hardware.camera.position === AR.CONST.CAMERA_POSITION.FRONT)
            AR.hardware.camera.position = AR.CONST.CAMERA_POSITION.BACK;
    },
    /* Switch mode of moving or rotate 3D model. */
    modeChange: function modeChangeFn() {
        World.moving = !World.moving;
        World.rotating = !World.rotating;
        console.log("modeChangeFn");
    },

    modelIsLoaded: function modelIsLoadedFn() {
        console.log("modelIsLoaded");
        World.modelLoaded = true;
        World.detectHideLoading();
        document.getElementById('Image').style.visibility ='hidden';
    },

    detectHideLoading: function detectHideLoadingFn() {
//        console.log("detectHideLoading," +
//            " wtcLoaded:" + World.wtcLoaded +
//            " modelIsLoaded:" + World.modelLoaded);
//        if (World.modelLoaded)
//            AR.platform.sendJSONObject({
//                action: "everyIsReady"
//            });
    },

    onError: function onErrorFn(error) {
        alert(error);
    }

};

World.init();
