import sys

from sqlalchemy import Column, ForeignKey, Integer, String
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import relationship
from sqlalchemy import create_engine

Base = declarative_base()


class User(Base):
    __tablename__ = 'user'
    id = Column(Integer, primary_key=True)
    email = Column(String(250), nullable=False)
    picture = Column(String(250))
    username = Column(String(250))

    @property
    def serialize(self):
        return {
            'id': self.id,
            'email': self.email,
            'picture': self.picture,
            'username': self.username
        }


class Picture(Base):
    __tablename__ = 'picture'
    name = Column(String, nullable=False, primary_key=True)
    height = Column(Integer)
    width = Column(Integer)

    @property
    def serialize(self):
        return {
            'name': self.name,
            'height': self.height,
            'width': self.width
        }

engine = create_engine('sqlite:///arcam.db')

Base.metadata.create_all(engine)
