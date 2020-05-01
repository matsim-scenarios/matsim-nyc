import xml.etree.ElementTree as ElementTree
import pandas as pd
from datetime import datetime

class XmlListConfig(list):
    def __init__(self, aList):
        for element in aList:
            if element:
                # treat like dict
                if len(element) == 1 or element[0].tag != element[1].tag:
                    self.append(XmlDictConfig(element))
                # treat like list
                elif element[0].tag == element[1].tag:
                    self.append(XmlListConfig(element))
            elif element.text:
                text = element.text.strip()
                if text:
                    self.append(text)


class XmlDictConfig(dict):
    def __init__(self, parent_element):
        if parent_element.items():
            self.updateShim(dict(parent_element.items()))
        for element in parent_element:
            if len(element):
                aDict = XmlDictConfig(element)
                #   if element.items():
                #   aDict.updateShim(dict(element.items()))
                self.updateShim({element.tag: aDict})
            elif element.items():  # items() is specialy for attribtes
                elementattrib = element.items()
                #if element.text:
                    #elementattrib.append((element.tag, element.text))  # add tag:text if there exist
                self.updateShim({element.tag: dict(elementattrib)})
            else:
                self.updateShim({element.tag: element.text})

    def updateShim(self, aDict):
        for key in aDict.keys():  # keys() includes tag and attributes
            if key in self:
                value = self.pop(key)
                if type(value) is not list:
                    listOfDicts = []
                    listOfDicts.append(value)
                    listOfDicts.append(aDict[key])
                    self.update({key: listOfDicts})
                else:
                    value.append(aDict[key])
                    self.update({key: value})
            else:
                self.update({key: aDict[key]})

def strtotime(time):
    ftr = [3600,60,1]
    return sum([a*b for a,b in zip(ftr, map(int,time.split(':')))])

"""
/*
 * File              : MATSim_Output_xml_to_dataframe.py
 * Author            : Mina Lee(ml6543)
 * Date              : 05.27.2019
 */
"""

def xml_to_dataframe(path,itr):
    
    

    print(itr)
    # tree = ElementTree.parse('I:\\My Drive\\2019\\Fall\\Calibration\\congestion pricing\\test.xml')
    tree = ElementTree.parse('I:\\My Drive\\2019\\Fall\\Calibration\\congestion pricing\\' + path + '\\BUILT.100.experienced_plans_' + str(itr) + '.xml')
    root = tree.getroot()
    xmldict = XmlDictConfig(root)
     
    print('data loaded',datetime.now())
    
    # initiate activity related list object
    id_person = []
    score = []
    id_data_act = []
    id_trip = []
    activity_data = []
    start_time = []
    end_time = []
    
    # initiate mode related list object
    id_data_mode = []
    mode_data = []
    route_data = []
    dep_time=[]
    trav_time=[]
    dist_data = []
    
    for type_tag in root.findall('person'):
        count = 0
        for scr in type_tag.findall('plan'):
            score.append(scr.get('score'))
            id_person.append(type_tag.get('id'))
        for act in type_tag.findall('plan/activity'):
            id_data_act.append(type_tag.get('id'))  # getting id
            activity_data.append(act.get('type'))  # getting work, home
            start_time.append(act.get('start_time'))
            end_time.append(act.get('end_time'))
            if act.get('type') != 'pt interaction':
                count = count + 1
                id_trip.append(count)
            else:
                id_trip.append(count)
            
            
    # for type_tag in root.findall('person'):
        if len(type_tag.findall('plan/leg')) == 0:
            id_data_mode.append(type_tag.get('id'))
            mode_data.append(None)
            route_data.append(None)
            dep_time.append(None)
            trav_time.append(None)
            dist_data.append(None)
     
        for i, mode in enumerate(type_tag.findall('plan/leg')):
            id_data_mode.append(type_tag.get('id'))
            mode_data.append(mode.get('mode'))
            dep_time.append(mode.get('dep_time'))
            trav_time.append(mode.get('trav_time'))
            
            
            route = mode.find('route')
            dist_data.append(route.get('distance'))        
            
            if i == len(type_tag.findall('plan/leg')) - 1:
                id_data_mode.append(type_tag.get('id'))
                mode_data.append(None)
                route_data.append(None)
                dep_time.append(None)
                trav_time.append(None)
                dist_data.append(None)
                
    
            route = mode.find('route').text
            if type(route) is str:
                if route[:2] != 'PT':
                    route = None
            try:
                routelist=route.split('===')
                routelist=routelist[2]
                routelist=routelist.split('_')
                route_data.append(routelist[0])
            except:
                route_data.append(route)
    
    actList = list(zip(id_data_act, activity_data, start_time, end_time,id_trip))
    modeList = list(zip(mode_data,dep_time,trav_time,route_data,dist_data))
    scoreList = list(zip(id_person,score))
    
    # actList = list(zip(id_data_act, activity_data, start_time, end_time))
    # modeList = list(zip(mode_data))
    df_act = pd.DataFrame(actList, columns=['id', 'activity', 'start_time', 'end_time','trip_id'])
    df_mode = pd.DataFrame(modeList, columns=['mode', 'dep_time', 'trav_time', 'route', 'distance'])
    df_score = pd.DataFrame(scoreList, columns=['id','score'])
    # df_mode.loc[-1] = [None,None,None,None,None]  # adding a row
    # df_mode.index = df_mode.index + 1  # shifting index
    df_mode = df_mode.sort_index()  # sorting by indexdf_merged = pd.concat([df_act, df_mode], axis=1)
            
        
    df_merged = pd.concat([df_act, df_mode], axis=1)
    # df_merged = df_merged[pd.notnull(df_merged['mode'])]
    backup = df_merged
    
    print('Transform finished!',datetime.now())
    # for i in range(len(df_merged)):
    #     print(i)
    #     # if df_merged['mode'][i] == 'transit_walk':
    #     #     if df_merged.id[i] == df_merged.id[i-1]:
    #     #         if df_merged['mode'][i-1] != 'pt':
    #     #             df_merged['mode'][i] = 'walk'
    #     #     else:
    #     #         df_merged['mode'][i] = 'walk'
    #     if df_merged['mode'][i] == 'transit_walk':
    #         if df_merged.activity[i] != 'pt interaction':
    #             df_merged['mode'][i] = 'walk'
    
    temp = df_merged.loc[df_merged['mode']=='transit_walk',:]
    temp = temp.loc[temp.activity != 'pt interaction',:]
    df_merged.loc[temp.index,'mode'] = 'walk'
    
    
    df_merged.to_csv('I:\\My Drive\\2019\\Fall\\Calibration\\congestion pricing\\' + path + '\\pricing_' + str(itr) + '.csv')
    mode_share = df_merged.groupby(df_merged['mode'])['mode'].count()
    # mode_share.to_csv('I:\\My Drive\\2019\\Fall\\Calibration\\congestion pricing\\test\\modeshare_' + str(itr) + '.csv', header = True)
    df_score.to_csv('I:\\My Drive\\2019\\Fall\\Calibration\\congestion pricing\\' + path + '\\score_' + str(itr) + '.csv')
    print('output',datetime.now())
    return

# for i in range(1,6):
#     xml_to_dataframe(i)

#df_pt=df_merged[df_merged['route'].notnull()]
#df_pt=df_pt[df_pt['route'].str.contains("NYU")]
#
#df_pt.to_csv("pt_only.csv")
    

# xml_to_dataframe(0)
# xml_to_dataframe(4)
# xml_to_dataframe(5)

for i in range(4,6):
    xml_to_dataframe('new-param',i)

# xml_to_dataframe('test','4_new')
