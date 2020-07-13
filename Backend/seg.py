import os
import sys
import random
import math
# import numpy as np
# import skimage.io
# import matplotlib
# import matplotlib.pyplot as plt
import cv2
# Root directory of the project
ROOT_DIR = os.path.abspath("./Mask_RCNN/")
print(ROOT_DIR)
import warnings
warnings.filterwarnings("ignore")

# Import Mask RCNN
sys.path.append(ROOT_DIR)  # To find local version of the library
from mrcnn import utils
import mrcnn.model as modellib
from mrcnn import visualize
# Import COCO config
sys.path.append(os.path.join(ROOT_DIR, "samples/coco/"))  # To find local version
import coco

# Directory to save logs and trained model
MODEL_DIR = os.path.join(ROOT_DIR, "logs")

# Local path to trained weights file
COCO_MODEL_PATH = os.path.join('', "mask_rcnn_coco.h5")

# Download COCO trained weights from Releases if needed
if not os.path.exists(COCO_MODEL_PATH):
    utils.download_trained_weights(COCO_MODEL_PATH)

# Directory of images to run detection on
IMAGE_DIR = os.path.join(ROOT_DIR, "images")

class InferenceConfig(coco.CocoConfig):
    # Set batch size to 1 since we'll be running inference on
    # one image at a time. Batch size = GPU_COUNT * IMAGES_PER_GPU
    GPU_COUNT = 1
    IMAGES_PER_GPU = 1

config = InferenceConfig()
config.display()

# Create model object in inference mode.
model = modellib.MaskRCNN(mode="inference", model_dir='mask_rcnn_coco.hy', config=config)

# Load weights trained on MS-COCO
model.load_weights('mask_rcnn_coco.h5', by_name=True)

# COCO Class names
class_names = ['BG', 'person', 'bicycle', 'car', 'motorcycle', 'airplane',
               'bus', 'train', 'truck', 'boat', 'traffic light',
               'fire hydrant', 'stop sign', 'parking meter', 'bench', 'bird',
               'cat', 'dog', 'horse', 'sheep', 'cow', 'elephant', 'bear',
               'zebra', 'giraffe', 'backpack', 'umbrella', 'handbag', 'tie',
               'suitcase', 'frisbee', 'skis', 'snowboard', 'sports ball',
               'kite', 'baseball bat', 'baseball glove', 'skateboard',
               'surfboard', 'tennis racket', 'bottle', 'wine glass', 'cup',
               'fork', 'knife', 'spoon', 'bowl', 'banana', 'apple',
               'sandwich', 'orange', 'broccoli', 'carrot', 'hot dog', 'pizza',
               'donut', 'cake', 'chair', 'couch', 'potted plant', 'bed',
               'dining table', 'toilet', 'tv', 'laptop', 'mouse', 'remote',
               'keyboard', 'cell phone', 'microwave', 'oven', 'toaster',
               'sink', 'refrigerator', 'book', 'clock', 'vase', 'scissors',
               'teddy bear', 'hair drier', 'toothbrush']

# HELPER FUNCTIONS
def cvResize(image):
    # image = cv2.resize(image, (720, 457), interpolation = cv2.INTER_AREA)
    image = cv2.resize(image, None, fx=0.3, fy=0.3)
    image = cv2.cvtColor(image, cv2.COLOR_BGR2BGRA)
    print(image.shape)
    # image = cv2.copyMakeBorder(image,100,0,0,0,cv2.BORDER_CONSTANT)
    return image

class ObjectRecognizer():
    def __init__(self, image):  
        self.image = image

    def loadImageInCV(self):
        image = cv2.imread(self.image, cv2.IMREAD_UNCHANGED)
        if image.shape[2] == 4:
            image = cv2.cvtColor(image, cv2.COLOR_BGRA2BGR)
        return image

    def writtenImage(self, pic):
        imageName = "Name.jpg"
        cv2.imwrite(imageName, pic)
        return imageName
    
    def recognizedObjects(self):
        image = self.loadImageInCV()
        # from keras.backend import clear_session
        # ## Code where you train or use a model ##
        # clear_session()
        # Run detection
        results = model.detect([image], verbose=1)
        # Visualize results
        r = results[0]
        visualize.display_instances(image, r['rois'], r['masks'], r['class_ids'], class_names, r['scores'])

        mask = r['masks']
        mask = mask.astype(int)

        objectsDetected = []
        for i in range(mask.shape[2]):
            temp = cv2.imread(self.image)
            # temp = cvResize(temp)
            temp = cv2.cvtColor(temp, cv2.COLOR_BGR2BGRA)
            for j in range(temp.shape[2]):
                temp[:,:,j] = temp[:,:,j] * mask[:,:,i]

            writtenFile = cv2.imwrite("recognized-objects/"+str(i)+"image.png", temp)

            objectsDetected.append(str(i)+"image.png")
            
        return objectsDetected


Image = ObjectRecognizer("Name.jpg")
print(Image.image)
recognizedObjects = Image.recognizedObjects()
print(recognizedObjects)