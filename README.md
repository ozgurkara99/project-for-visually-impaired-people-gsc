<img align="left" width="75" height="70" src="https://github.com/ozgurkara99/project-for-visually-impaired-people-gsc/blob/master/torch.png" alt="A Torch in Darkness project app icon">

# A Torch in Darkness

For video:

<a href="http://www.youtube.com/watch?feature=player_embedded&v=tk5gTQsqOwM
" target="_blank"><img src="http://img.youtube.com/vi/tk5gTQsqOwM/0.jpg" 
alt="IMAGE ALT TEXT HERE" width="240" height="180" border="10" /></a>

Project For Visually Impaired People GSC

Google's Solutions Challenge
- To build our solution, we included Tensorflow Object Detection API and used a depth estimation model based on "monodepth2". Apart from that, we developed our Android application to fetch data from computer and give an audio output to the visually impaired people. To build our solution, we used Flask to set up communication between the app and host. We used Android Studio to design and develop our Android application.

## Models Links
[Models](https://drive.google.com/open?id=1Q28giepDWvGJzW1IDZaEVDbYSUW-z7Aw)

## Installation
1- Clone the repo.

2- Download models which are given above and add this folder to the same directory with main.py .

3- Download the dependencies.  
`pip install numpy opencv-python tensorflow pillow==6.1 imutils matplotlib pytorch=0.4.1 torchvision=0.2.1 tensorflow-object-detection-api`  

4- To run program write this command:  
`python main.py ip_adress_of_camera_source`
ip_adress_of_camera_source is the IP Webcam server's ip adress that is written in the program and in this form: http://192.168.xxxxx:xxxx

5- Install [IP Webcam](https://play.google.com/store/apps/details?id=com.pas.webcam&hl=tr) app from Google Play Store on your Android Device 

6- Download .apk file from [here](https://github.com/ozgurkara99/project-for-visually-impaired-people-gsc/blob/master/android_app/app/release/app-release.apk)

7- Install the app on your Android device.

8- Connect your Android device to the same network with your computer.

9- Run IP Webcam and click start server at the end of the application page.

10- Run flask_web.py:
`python flask_web.py`

11- Get local IP from your computer and try to reach same content from your Android device

12- If you are successful, you should copy the address from your Android web browser.

13- Run our app and click “Address” button.

14- Enter the copied address and click “OK”.
`eg. http://192.168.xxxxx:xxxx`

15- Click “Start” button. You should see details about the camera preview at the end of the page and hear the stereo based sound output from your Android device.

16- To be able to hear stereo based sound output, you should use a headphone.

## References
[Monodepth-2](https://github.com/nianticlabs/monodepth2)   
[Mannequin Challenge](https://github.com/google/mannequinchallenge)  
[Tensorflow Object Detection API](https://github.com/tensorflow/models/tree/master/research/object_detection)

Torch icon made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
