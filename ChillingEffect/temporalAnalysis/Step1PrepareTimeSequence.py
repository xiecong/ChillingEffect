import datetime
import math

def getWeekId(dateTime):
    dateV = dateTime.split('T')[0]
    if len(dateV) != 10:
#         print 'date error'
#         print dateTime
        return -1
    ele = dateV.split('-')
    assert len(ele) == 3, 'date format error'
    end = datetime.date(int(ele[0]), int(ele[1]), int(ele[2]))
    start = datetime.date(2010, 1, 1)
    return int(math.floor((end - start).days / 7))

def readData(inputPath):
    senderDic = {}
    recipientDic = {}
    principleDic = {}
    senderDatesDic = {}
    recipientDatesDic = {}
    principleDatesDic = {}
    infile = open(inputPath)
    for line in infile:
        line = line.strip()
        notice = line.split('\t')  # [id, sendDate, senderName, recipientName, principalName, type]
        date = notice[1]
        sender = notice[2]
        recipient = notice[3]
        principle = notice[4]
        
        if not senderDic.has_key(sender):
            senderDic[sender] = 0
        if not senderDatesDic.has_key(sender):
            senderDatesDic[sender] = []
        senderDic[sender] = senderDic[sender] + 1
        senderDatesDic[sender].append(getWeekId(date))
        
        if not recipientDic.has_key(recipient):
            recipientDic[recipient] = 0
        if not recipientDatesDic.has_key(recipient):
            recipientDatesDic[recipient] = []
        recipientDic[recipient] = recipientDic[recipient] + 1
        recipientDatesDic[recipient].append(getWeekId(date))
        
        if not principleDic.has_key(principle):
            principleDic[principle] = 0
        if not principleDatesDic.has_key(principle):
            principleDatesDic[principle] = []
        principleDic[principle] = principleDic[principle] + 1
        principleDatesDic[principle].append(getWeekId(date))
    return senderDic, recipientDic, principleDic, senderDatesDic, recipientDatesDic, principleDatesDic

def writeIndividual(outputpath, weeksList):
    weeksList = sorted(weeksList, key=lambda x: x)
    outfile = open(outputpath, 'w')
    for week in weeksList:
        outfile.write(str(week) + '\n')
    outfile.close()
    
if __name__ == '__main__':
    threshold = 1 # only consider entities that have notice more than 1000
    basepath = '../'
    inputPath = basepath + 'sourceData/chillingMetadataSample'
    senderDic, recipientDic, principleDic, senderDatesDic, recipientDatesDic, principleDatesDic = readData(inputPath)
    
    senderL = sorted(senderDic.items(), key=lambda x: x[1], reverse=True)
    recipientL = sorted(recipientDic.items(), key=lambda x: x[1], reverse=True)
    principleL = sorted(principleDic.items(), key=lambda x: x[1], reverse=True)
    
    filenameDic = {}
    filenameID = 0
    topIndividualFile = open(basepath + 'topIndividuals', 'w')
    for i in range(0, len(senderL)):
        name = senderL[i][0]
        count = senderL[i][1]
        if count < threshold:
            break
        filename = name + '_sender'
        filenameDic[filename] = filenameID
        filenameID = filenameID + 1
        topIndividualFile.write(name + '\tsender\n')
        writeIndividual(basepath + 'individuals/' + str(filenameDic[filename]), senderDatesDic[name])
    for i in range(0, len(recipientL)):
        name = recipientL[i][0]
        count = recipientL[i][1]
        if count < threshold:
            break
        filename = name + '_recipient'
        filenameDic[filename] = filenameID
        filenameID = filenameID + 1
        topIndividualFile.write(name + '\trecipient\n')
        writeIndividual(basepath + 'individuals/' + str(filenameDic[filename]), recipientDatesDic[name])
    for i in range(0, len(principleL)):
        name = principleL[i][0]
        count = principleL[i][1]
        if count < threshold:
            break
        filename = name + '_principle'
        filenameDic[filename] = filenameID
        filenameID = filenameID + 1
        topIndividualFile.write(name + '\tprinciple\n')
        writeIndividual(basepath + 'individuals/' + str(filenameDic[filename]), principleDatesDic[name])
    topIndividualFile.close()
    
    outfile = open(basepath + 'filenameDic', 'w')
    for filename in filenameDic.keys():
        outfile.write(filename + '\t' + str(filenameDic[filename]) + '\n')
    outfile.close()
