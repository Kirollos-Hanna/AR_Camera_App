import os
import sys
import random
import math
import numpy as np
import skimage.io
import matplotlib
import matplotlib.pyplot as plt
import cv2
# Root directory of the project
ROOT_DIR = os.path.abspath("../")

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

# Load a random image from the images folder
image = cv2.imread('20200120_223048.jpg', cv2.IMREAD_UNCHANGED)
image = cv2.resize(image, (720, 457), interpolation = cv2.INTER_AREA)
# image = cv2.resize(image, None, fx=0.4, fy=0.4)
img = cv2.cvtColor(image, cv2.COLOR_BGR2BGRA)
print(img.shape)
cv2.imshow("Image", img)
cv2.waitKey(0)

# original image
plt.figure(figsize=(12,10))

# Run detection
results = model.detect([image], verbose=1)

# Visualize results
r = results[0]
visualize.display_instances(image, r['rois'], r['masks'], r['class_ids'], class_names, r['scores'])

mask = r['masks']
mask = mask.astype(int)
# mask.shape

objectsDetected = []
for i in range(mask.shape[2]):
    temp = cv2.imread('20200120_223048.jpg')
    temp = cv2.resize(temp, (720, 457), interpolation = cv2.INTER_AREA)
    # temp = cv2.resize(temp, None, fx=0.4, fy=0.4)
    temp = cv2.cvtColor(temp, cv2.COLOR_BGR2BGRA)
    for j in range(temp.shape[2]):
        temp[:,:,j] = temp[:,:,j] * mask[:,:,i]

    objectsDetected.append(temp)
    
    plt.figure(figsize=(8,8))
    cv2.imshow("Image", temp)
    cv2.waitKey(0)


# opencv loads the image in BGR, convert it to RGB
# img = cv2.cvtColor(objectsDetected[1],
#                    cv2.COLOR_BGR2RGB)
# lower_white = np.array([220, 220, 220], dtype=np.uint8)
# upper_white = np.array([255, 255, 255], dtype=np.uint8)
# mask = cv2.inRange(img, lower_white, upper_white)  # could also use threshold
# mask = cv2.morphologyEx(mask, cv2.MORPH_OPEN, cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (3, 3)))  # "erase" the small white points in the resulting mask
# mask = cv2.bitwise_not(mask)  # invert mask

# # img[mask == 255] = (255, 255, 255)
# # load background (could be an image too)
# bk = np.full(img.shape, 255, dtype=np.uint8)  # white bk

# # get masked foreground
# fg_masked = cv2.bitwise_and(img, img, mask=mask)

# # get masked background, mask must be inverted 
# mask = cv2.bitwise_not(mask)
# bk_masked = cv2.bitwise_and(bk, bk, mask=mask)

# # combine masked foreground and masked background 
# final = cv2.bitwise_or(fg_masked, bk_masked)
# mask = cv2.bitwise_not(mask)  # revert mask to original

# cv2.imshow("Image", img)
# cv2.waitKey(0)


# imageClone = image.copy()
# img_height, img_width = 300, 300
# n_channels = 4
# transparent_img = np.zeros((image.shape[0], image.shape[1], n_channels), dtype=np.uint8)
# cv2.imshow("Image", transparent_img)
# cv2.waitKey(0)

img = objectsDetected[0].reshape(objectsDetected[1].shape[0], objectsDetected[1].shape[1], 4)
print(img[0,0])
print("shape")
print(img.shape)

# for i in range(len(img)):
#     for j in range(len(img[i])):
#         img[i,j] = np.trim_zeros(img[i,j])
# print(img)
# img = np.trim_zeros(img)

# print("shape")
# print(img.shape)
# cv2.imshow("Image", img)

# Resize img

# cv2.imwrite("./transparent_img2.png", img)
# cv2.waitKey(0)