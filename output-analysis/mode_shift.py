#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Feb  6 23:07:04 2020

@author: BrianStephanie
"""

import pandas as pd
import numpy as np


data = pd.read_csv('/Volumes/GoogleDrive/My Drive/2019/Fall/Calibration/congestion pricing/sub_id.csv')
data = data.set_index('id')
man = data.loc[data.attribute == 'man']
nonman = data.loc[data.attribute == 'nonman']
out = data.loc[data.attribute == 'outside']
index = pd.concat([man,nonman],axis = 1)

score0 = pd.read_csv('/Volumes/GoogleDrive/My Drive/2019/Fall/Calibration/new-param/score_0.csv')
##### update with scenario 4
score1 = pd.read_csv('/Volumes/GoogleDrive/My Drive/2019/Fall/Calibration/new-param/score_4.csv')  

score2 = pd.read_csv('/Volumes/GoogleDrive/My Drive/2019/Fall/Calibration/new-param/score_5.csv')
score0 = score0.set_index('id')
score1 = score1.set_index('id')
score2 = score2.set_index('id')

welfare = pd.DataFrame(0,index = [0,4,5,6], columns = ['man','nonman','outside','whole'])


welfare.iloc[0,0] = np.mean(score0.loc[man.index,'score'])
welfare.iloc[0,1] = np.mean(score0.loc[nonman.index,'score'])
welfare.iloc[0,2] = np.mean(score0.loc[out.index,'score'])
welfare.iloc[0,3] = np.mean(score0.loc[index.index,'score'])


welfare.iloc[1,0] = np.mean(score1.loc[man.index,'score'])
welfare.iloc[1,1] = np.mean(score1.loc[nonman.index,'score'])
welfare.iloc[1,2] = np.mean(score1.loc[out.index,'score'])
welfare.iloc[1,3] = np.mean(score1.loc[index.index,'score'])

welfare.iloc[2,0] = np.mean(score2.loc[man.index,'score'])
welfare.iloc[2,1] = np.mean(score2.loc[nonman.index,'score'])
welfare.iloc[2,2] = np.mean(score2.loc[out.index,'score'])
welfare.iloc[2,3] = np.mean(score2.loc[index.index,'score'])

welfare.to_csv('/Volumes/GoogleDrive/My Drive/2019/Fall/Calibration/new-param/welfare.csv')

data0 = pd.read_csv('/Volumes/GoogleDrive/My Drive/2019/Fall/Calibration/new-param/pricing_0.csv')
data1 = pd.read_csv('/Volumes/GoogleDrive/My Drive/2019/Fall/Calibration/new-param/pricing_4.csv')
data2 = pd.read_csv('/Volumes/GoogleDrive/My Drive/2019/Fall/Calibration/new-param/pricing_5.csv')

data0 = data0.set_index('id')
# data1 = data1.set_index('id')
# data2 = data2.set_index('id')

# temp_data = data1.loc[data1['mode']=='transit_walk',:]
# temp_data = temp_data.loc[temp_data.activity != 'pt interaction',:]
# data1.loc[temp_data.index,'mode'] = 'access_walk'

# temp_data = data2.loc[data2['mode']=='transit_walk',:]
# temp_data = temp_data.loc[temp_data.activity != 'pt interaction',:]
# data2.loc[temp_data.index,'mode'] = 'access_walk'

temp = data0.loc[man.index,:]
temp = temp.loc[temp['mode'] == 'car',:]
temp = temp.reset_index()
temp = temp.set_index(['id','trip_id'])


temp1 = data1.set_index(['id','trip_id'])
temp1 = temp1.loc[temp.index,:]


temp2 = data2.set_index(['id','trip_id'])
temp2 = temp2.loc[temp.index,:]

shift1 = temp1.groupby('mode')['mode'].count()
shift2 = temp2.groupby('mode')['mode'].count()

shift1.to_csv('/Volumes/GoogleDrive/My Drive/2019/Fall/Calibration/new-param/shift1.csv', header = True)
shift2.to_csv('/Volumes/GoogleDrive/My Drive/2019/Fall/Calibration/new-param/shift2.csv', header = True)



