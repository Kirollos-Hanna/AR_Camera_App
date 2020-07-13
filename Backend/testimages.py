import cv2

from seg import ObjectRecognizer

phoneImg = cv2.imread('20200120_223037.jpg', cv2.IMREAD_UNCHANGED)
phoneImg = cv2.resize(phoneImg, None, fx=0.4, fy=0.4)
phoneImg = cv2.copyMakeBorder(phoneImg,100,100,0,0,cv2.BORDER_CONSTANT)

properImg = cv2.imread('12283150_12d37e6389_z.jpg', cv2.IMREAD_UNCHANGED)
# EXAMPLE: image = cv2.copyMakeBorder( src, top, bottom, left, right, borderType)
constant= cv2.copyMakeBorder(properImg,100,10,10,10,cv2.BORDER_CONSTANT)

# cv2.imshow("Phone Image", phoneImg)
# cv2.imshow("Proper Image", properImg)
# cv2.imshow("Sample Image", sampleImg)
# cv2.imshow("Constant Image", constant)
# cv2.waitKey(0)
print("recognized-objects/"+str(1)+'12283150_12d37e6389_z.jpg')
def func():
    Image = ObjectRecognizer("Name.jpg")
    print(Image.image)
    recognizedObjects = Image.recognizedObjects()
    print(recognizedObjects)

func()
# Image = ObjectRecognizer("Name.jpg")
# print(Image.image)
# recognizedObjects = Image.recognizedObjects()
# print(recognizedObjects)