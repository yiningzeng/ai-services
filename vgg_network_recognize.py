# -*- coding: utf-8 -*-

""" Very Deep Convolutional Networks for Large-Scale Visual Recognition.

Applying VGG 16-layers convolutional network to Oxford's 17 Category Flower
Dataset classification task.

References:
    Very Deep Convolutional Networks for Large-Scale Image Recognition.
    K. Simonyan, A. Zisserman. arXiv technical report, 2014.

Links:
    http://arxiv.org/pdf/1409.1556

"""

from __future__ import division, print_function, absolute_import

from PIL import Image
import argparse
import numpy as np

import json
from urlparse import parse_qs
from wsgiref.simple_server import make_server

import pickle
from tflearn.data_utils import image_dirs_to_samples

import tflearn
from tflearn.layers.core import input_data, dropout, fully_connected
from tflearn.layers.conv import conv_2d, max_pool_2d
from tflearn.layers.estimator import regression

import tflearn.datasets.oxflower17 as oxflower17

global model

# 将Image加载后转换成float32格式的tensor
def pil_to_nparray(pil_image):
    pil_image.load()
    return np.asarray(pil_image, dtype="float32")

# 定义函数，参数是函数的两个参数，都是python本身定义的，默认就行了。
def application(environ, start_response):
	start_response('200 OK', [('Content-Type', 'text/html')])
    # environ是当前请求的所有数据，包括Header和URL，body，这里只涉及到get
    # 获取当前get请求的所有数据，返回是string类型
	params = parse_qs(environ['QUERY_STRING'])
    # 获取get中key为name的值
	name = params.get('file', [''])[0]
	print('收到file=',name)

	imgs = []
	img = Image.open(name)
	img = img.resize((224, 224),Image.ANTIALIAS)
	imgs.append(pil_to_nparray(img))
	predict = model.predict(imgs)
	return predict


if __name__ == "__main__":
	
	print('开始执行')
	# Building 'VGG Network'
	network = input_data(shape=[None, 224, 224, 3])

	network = conv_2d(network, 64, 3, activation='relu')
	network = conv_2d(network, 64, 3, activation='relu')
	network = max_pool_2d(network, 2, strides=2)

	network = conv_2d(network, 128, 3, activation='relu')
	network = conv_2d(network, 128, 3, activation='relu')
	network = max_pool_2d(network, 2, strides=2)

	network = conv_2d(network, 256, 3, activation='relu')
	network = conv_2d(network, 256, 3, activation='relu')
	network = conv_2d(network, 256, 3, activation='relu')
	network = max_pool_2d(network, 2, strides=2)

	network = conv_2d(network, 512, 3, activation='relu')
	network = conv_2d(network, 512, 3, activation='relu')
	network = conv_2d(network, 512, 3, activation='relu')
	network = max_pool_2d(network, 2, strides=2)

	network = conv_2d(network, 512, 3, activation='relu')
	network = conv_2d(network, 512, 3, activation='relu')
	network = conv_2d(network, 512, 3, activation='relu')
	network = max_pool_2d(network, 2, strides=2)

	network = fully_connected(network, 4096, activation='relu')
	network = dropout(network, 0.5)
	network = fully_connected(network, 4096, activation='relu')
	network = dropout(network, 0.5)
	network = fully_connected(network, 6, activation='softmax')

	network = regression(network, optimizer='rmsprop',
						 loss='categorical_crossentropy',
						 learning_rate=0.0001)

	# Load trained model
	model = tflearn.DNN(network)
	model.load('model_vgg-1500')
	port = 8081
	httpd = make_server("0.0.0.0", port, application)
	print("serving http on port {0}...".format(str(port)))
	httpd.serve_forever()

