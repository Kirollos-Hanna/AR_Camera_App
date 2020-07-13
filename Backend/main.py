import os
from datetime import datetime
from flask import Flask, make_response, Response, send_file, request, jsonify
from sqlalchemy import create_engine, desc
from sqlalchemy.orm import sessionmaker
from sqlalchemy.orm.exc import NoResultFound
from entities import Base, User, Picture
from flask_sqlalchemy import SQLAlchemy
import requests
import json
import jsonpickle
import numpy as np
import cv2
import base64

app = Flask(__name__)
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///dummy.db'

PEOPLE_FOLDER = os.path.dirname(os.path.abspath(__file__))
app.config['UPLOAD_FOLDER'] = PEOPLE_FOLDER
# Links the database with flask
# db = SQLAlchemy(app)

# Create a connection to the database
engine = create_engine('sqlite:///arcam.db')
Base.metadata.bind = engine

# Create a session
DBSession = sessionmaker(bind=engine)
session = DBSession()

@app.route('/')
def default1():
    from seg import ObjectRecognizer
    Image = ObjectRecognizer(request.args['imageName'])
    print(Image.image)
    recognizedObjects = Image.recognizedObjects()

    return str(recognizedObjects)

@app.route('/<objectName>')
def getImage(objectName):
    if objectName == "favicon.ico":
        return ""
    return send_file("recognized-objects/{}".format(objectName), mimetype='image/gif')

@app.route('/upload/<picture>', methods=['POST'])
def postImage(picture):
    data = request.get_json()
    imageString = data['ImageString']
    imgdata = base64.b64decode(imageString)
    filename = picture 
    with open(filename, 'wb') as f:
        f.write(imgdata)

    return str(picture)

@app.route('/saveParams', methods=['POST'])
def saveParams():
    data = request.get_json()
    height = data['Height']
    width = data['Width']
    pictureName = data['PictureName']
    
    picture = session.query(Picture).filter_by(name=pictureName).first()
    session.close()
    if picture == None:
        pic = Picture(name=pictureName, height=height, width=width)
        session.add(pic)
        session.commit()
    else:
        picture.height = height
        picture.width = width
        session.add(picture)
        session.commit()
    return str("data received")

@app.route('/getParams')
def getParams():
    picName = request.args['pictureName']
    picture = session.query(Picture).filter_by(name=picName).first()
    session.close()
    if picture == None:
        return str("None")
    return jsonify(Picture=picture.serialize)

if __name__ == '__main__':
    app.debug = True
    app.run(host='0.0.0.0', port=5000)

