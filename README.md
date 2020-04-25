# project-for-visually-impaired-people-gsc
Google's Solutions Challenge
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
