import matplotlib.pyplot as plt
import math
import datetime

def sma(data, window):
    if len(data) < window:
        return None
    return sum(data[-window:]) / float(window)

def ema(data, window):
    if len(data) < 2 * window:
        raise ValueError("data is too short")
    c = 2.0 / (window + 1)
    current_ema = sma(data[-window * 2:-window], window)
    for value in data[-window:]:
        current_ema = (c * value) + ((1 - c) * current_ema)
    return current_ema

def week2datePair(weekId):
    days = weekId * 7
    start = datetime.date(2010, 1, 1)
    delta1 = datetime.timedelta(days)
    delta2 = datetime.timedelta(days + 7)
    end1 = start + delta1
    end2 = start + delta2
    return str(end1) + ',' + str(end2)
    
def readTS(infilepath):
    ts = [0 for i in range(0, 250)]
    infile = open(infilepath)
    for line in infile:
        line = line.strip()
        weekId = int(line)
        if weekId < 0 or weekId >= 250:
            continue
        ts[weekId] = ts[weekId] + 1
    infile.close()
    return ts

def predict(data, window, topk,basepath,index):
    scoreDic = {}
    scoreL = [0 for i in range(0, len(data))]
    # compute anomaly scores
    for i in range(2 * window, len(data)):
        predictV = ema(data[:i], window)
        if data[i] < 50 or predictV < 50:
            continue
        scoreDic[i] = data[i] * math.log(data[i]) / predictV
        scoreL[i] = data[i] * math.log(data[i]) / predictV
    scoreDicSorted = sorted(scoreDic.items(), key=lambda x:x[1], reverse=True)
    
    # draw figures
    fig = plt.figure()
    ax = fig.add_subplot(111)
    ax.plot(data, "b-", label='Notice Time Series')
    ax.set_xlabel('Date')
    ax.set_ylabel('# of Notices')
    ax.set_xticklabels([week2dateSingle(0),week2dateSingle(50),week2dateSingle(100),week2dateSingle(150),week2dateSingle(200),week2dateSingle(250)])
    ax2 = ax.twinx()
    ax2.plot(scoreL, "r-", label='Anomaly Score Time Series')
    ax2.set_ylabel('Anomaly Score')
    ax.legend(loc=2)
    ax2.legend(loc=1)
    
    # save files
    plt.savefig(basepath+'/individuals/'+str(index)+'.pdf')
    outfile = open(basepath+'individuals/'+str(index)+'AnomalyScore','w')
    for ele in scoreL:
        outfile.write(str(ele)+'\n')
    outfile.close()
    outfile = open(basepath+'individuals/'+str(index)+'Data','w')
    for ele in data:
        outfile.write(str(ele)+'\n')
    outfile.close()
    return scoreDicSorted[:topk]

def week2dateSingle(weekId):
    days = weekId * 7
    start = datetime.date(2010, 1, 1)
    delta1 = datetime.timedelta(days)
    end1 = start + delta1
    return str(end1)

def outputResult(anomalyScores, busidicpath, outputpath):
    outputfile = open(outputpath, 'w')
    busiDic = readBusiDic(busidicpath)
    for i in range(0, len(anomalyScores)):
        if len(anomalyScores[i]) == 0:
            continue
        weekId = anomalyScores[i][0][0]
        dateId = week2datePair(weekId)
        busiName = busiDic[i]
        units = busiName.split('_')
        if len(units[0]) == 0:
            units[0] = '[no name]'
        if len(units) != 2:
            print 'error'
        outputfile.write(units[0] + '\t' + units[1] + '\t' + dateId + '\n')
    outputfile.close()
    
def readBusiDic(busidicpath):
    busiName = {}
    busifile = open(busidicpath)
    for line in busifile:
        line = line.strip()
        units = line.split('\t')
        busiName[int(units[1])] = units[0]
    busifile.close()
    return busiName
    
if __name__ == '__main__':
    window = 10 # length of history time window
    binThresh = 50 # ignore bins that has less than 50 notices
    topk = 1 # return top 1 anomaly
    numB = 332 # number of bins
    basepath = '../' # root path of the files
    anomalyScores = [] # 
    for i in range(0, numB):
        ts = readTS(basepath + 'individuals/' + str(i)) # read time series files
        topkList = predict(ts, window, topk,basepath,i) # predict time series value using EMA
#         print topkList
        anomalyScores.append(topkList)
    outputResult(anomalyScores, basepath + 'filenameDic', basepath + 'spikes')
    
