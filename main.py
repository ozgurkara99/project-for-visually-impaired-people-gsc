import numpy as np
import os
import cv2
from imutils.video import VideoStream
import tensorflow as tf
import networks
from PIL import Image
from object_detection.utils import label_map_util
import matplotlib as mpl
import matplotlib.cm as cm
import torch
import torchvision
import sys

tf.gfile = tf.io.gfile
camera_source = 0
if(str(sys.argv[1]) == "0"):
    camera_source = 0
else:
    camera_source = str(sys.argv[1]) + "/video"
default_conf = 0.35
COLORS = np.random.uniform(0, 255, size=(100, 3))

object_detection_model_name = 'ssdlite_mobilenet_v2_coco_2018_05_09'
depth_estimation_model_name = "mono_640x192" 

data_class_name = 'mscoco_label_map.pbtxt'
object_detection_model_path = "Models\\Object-Detection-Models\\" + object_detection_model_name + "\\saved_model"
path_to_labels = os.path.join(object_detection_model_path,data_class_name)
category_index = label_map_util.create_category_index_from_labelmap(path_to_labels, use_display_name=True)

depth_estimation_encoder_path = "Models\\Depth-Estimation-Models\\" + depth_estimation_model_name + "\\encoder.pth"
depth_estimation_decoder_path = "Models\\Depth-Estimation-Models\\" + depth_estimation_model_name + "\\depth.pth"

output_filename = "data/output.txt"

def load_object_detection_model(model_dir):   
    model = tf.saved_model.load(str(model_dir))
    model = model.signatures['serving_default']    
    return model

def run_object_detection_for_single_image(model, image):
    image = np.asarray(image)
    input_tensor = tf.convert_to_tensor(image)
    input_tensor = input_tensor[tf.newaxis,...]   
    output_dict = model(input_tensor)
    num_detections = int(output_dict.pop('num_detections'))
    output_dict = {key:value[0, :num_detections].numpy() for key,value in output_dict.items()}
    output_dict['num_detections'] = num_detections
    output_dict['detection_classes'] = output_dict['detection_classes'].astype(np.int64)
    return output_dict['detection_scores'], output_dict['detection_classes'], output_dict['detection_boxes']

def load_encoder(encoder_path):
    encoder = networks.ResnetEncoder(18, False)
    loaded_dict_enc = torch.load(encoder_path, map_location=device)
    feed_height = loaded_dict_enc['height']
    feed_width = loaded_dict_enc['width']
    filtered_dict_enc = {k: v for k, v in loaded_dict_enc.items() if k in encoder.state_dict()}
    encoder.load_state_dict(filtered_dict_enc)
    encoder.to(device)
    encoder.eval()
    return encoder, feed_height, feed_width

def load_decoder(encoder, depth_decoder_path):
    depth_decoder = networks.DepthDecoder(num_ch_enc=encoder.num_ch_enc, scales=range(4))
    loaded_dict = torch.load(depth_decoder_path, map_location=device)
    depth_decoder.load_state_dict(loaded_dict)
    depth_decoder.to(device)
    depth_decoder.eval()
    return depth_decoder

def convert_to_colormapped(outputs, h, w):
    disp = outputs[("disp", 0)]
    disp_resized = torch.nn.functional.interpolate(disp, (h, w), mode="bilinear", align_corners=False)
    disp_resized_np = disp_resized.squeeze().cpu().numpy()
    vmax = np.percentile(disp_resized_np, 100)
    normalizer = mpl.colors.Normalize(vmin=disp_resized_np.min(), vmax=vmax)
    mapper = cm.ScalarMappable(norm=normalizer, cmap='gray')
    colormapped_im = (mapper.to_rgba(disp_resized_np)[:, :, :3] * 255).astype(np.uint8)     
    colormapped_im = cv2.cvtColor(colormapped_im, cv2.COLOR_RGB2GRAY)
    return colormapped_im

def depth_predict(frame, encoder, depth_decoder, device):
    input_image = Image.fromarray(cv2.cvtColor(frame, cv2.COLOR_BGR2RGB))
    original_width, original_height = input_image.size
    input_image = input_image.resize((feed_width, feed_height), Image.LANCZOS)
    input_image = torchvision.transforms.ToTensor()(input_image).unsqueeze(0)
    input_image = input_image.to(device)
    features = encoder(input_image)
    outputs = depth_decoder(features)
    colormapped_im = convert_to_colormapped(outputs, original_height, original_width)
    return original_width, original_height, colormapped_im

def create_multipliers(width, height, midX):
    right_multiplier = 0
    left_multiplier = 0
    if(midX <= 5/8 * width and midX >= width/2):
        left_multiplier = 2 - (midX/(320))
        right_multiplier = 1
    elif(midX > 5/8 * width):
        left_multiplier = 0
        right_multiplier = 2 - (midX/320)
    elif(midX >= 3/8 * width and midX < width/2):
        left_multiplier = 1
        right_multiplier = (midX/(320))
    elif(midX < 3/8 * width):
        left_multiplier = (midX/320)
        right_multiplier = 0
    return right_multiplier, left_multiplier

def write_to_txt(averages, filename):
    with open(filename, "a") as txt_file:
        if len(averages) == 0:
            print("#" , file=txt_file)
        else:
            averages = np.array(averages)
            maxindex = np.argmax(averages[:,0])
            print("{} {} {} ".format(averages[maxindex,3], averages[maxindex,2], averages[maxindex,1]), file=txt_file)
            
def select_device():
    if torch.cuda.is_available():
        return torch.device("cuda")
    else:
        return torch.device("cpu")

device = select_device()
#device = torch.device("cpu")
# LOADING PRETRAINED MODEL
print("Loading detection model")
detection_model = load_object_detection_model(object_detection_model_path)
print("Loading pretrained encoder")
encoder, feed_height, feed_width = load_encoder(depth_estimation_encoder_path)
print("Loading pretrained decoder")
depth_decoder = load_decoder(encoder, depth_estimation_decoder_path)

with torch.no_grad():
    vs = VideoStream(camera_source).start()
    while True:
        flag, left = 1, 1
        averages = []
        frame = vs.read()
        w, h, colormapped_im = depth_predict(frame, encoder, depth_decoder, device) #depth prediction result  
        conf, classes, boxes = run_object_detection_for_single_image(detection_model, frame) #object detection result
        
        for i,confidence in enumerate(conf):
            if confidence > default_conf:
                (startY, startX, endY, endX) = boxes[i]
                startX, endX, startY, endY = int(startX * w), int(endX * w), int(startY * h), int(endY * h)
                label = "{}: {:.2f}%".format(category_index.get(classes[i]).get('name'),confidence * 100)
                objectarea = colormapped_im[startY:endY,startX:endX]
                
                right, left = create_multipliers(w, h, (startY + endY) /2)
                list_for_one = [np.mean(objectarea), right, left, category_index.get(classes[i]).get('name')]
                averages.append(list_for_one)
                
                cv2.rectangle(frame, (startX, startY), (endX, endY),COLORS[i], 2)
                cv2.putText(frame, label, (startX, (startY - 15 if startY - 15 > 15 else startY + 15)),cv2.FONT_HERSHEY_SIMPLEX, 0.5, COLORS[i], 2)
                cv2.rectangle(colormapped_im, (startX, startY), (endX, endY),COLORS[i], 2)
                cv2.putText(colormapped_im, label, (startX, (startY - 15 if startY - 15 > 15 else startY + 15)),cv2.FONT_HERSHEY_SIMPLEX, 0.5, COLORS[i], 2)

        cv2.imshow("Input", frame)
        cv2.imshow("Output", colormapped_im)
        write_to_txt(averages, output_filename)
        key = cv2.waitKey(1) & 0xFF
        if key == ord("q"):
            break