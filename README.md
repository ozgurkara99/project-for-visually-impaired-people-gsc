# Project For Visually Impaired People GSC
Google's Solutions Challenge
To build our solution, we included Tensorflow Object Detection API and used a depth estimation model based on "monodepth2". Apart from that, we developed our Android application to fetch data from computer and give an audio output to the visually impaired people. To build our solution, we used Flask to set up communication between the app and host. We used Android Studio to design and develop our Android application.

## Models Links
[Models](https://drive.google.com/open?id=1Q28giepDWvGJzW1IDZaEVDbYSUW-z7Aw)
## Hierarchy
main.py  
layers.py  
flask_web.py  
templates  
-home.html  
networks  
-__init__.py  
-depth_decoder.py  
-pose_cnn.py  
-pose_decoder.py  
-resnet_encoder.py   
output  
-output.txt    
Models  
-Depth-Estimation-Models  
--mono+stereo_1024x320  
---depth.pth  
---encoder.pth  
---pose.pth  
---pose_encoder.pth    
-Object-Detection-Models  
--ssdlite_mobilenet_v2_coco_2018_05_09  
---saved_model  
----mscoco_label_map.pbtxt  
----saved_model.pb  
## Installation
1- Download the repo to your server.  
2- Download models which are given above.  
3- Download the dependencies  
`pip install numpy opencv-python tensorflow pillow matplotlib torch torchvision tensorflow-object-detection-api`
4- To run program write this command:  
`python main.py ip_adress_of_camera_source` 
ip_adress_of_camera_source is the IP Webcam server's ip adress that is written in the program and in this form: http://192.168.xxxxx:xxxx   
5- Deniz burayı yaz
## References
[Monodepth-2](https://github.com/nianticlabs/monodepth2)   
[Mannequin Challenge](https://github.com/google/mannequinchallenge)  
[Tensorflow Object Detection API](https://github.com/tensorflow/models/tree/master/research/object_detection)
