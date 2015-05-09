import datetime
import matplotlib.pyplot as plt

def week2dateSingle(weekId):
    days = weekId * 7
    start = datetime.date(2010, 1, 1)
    delta1 = datetime.timedelta(days)
    end1 = start + delta1
    return str(end1)

def getDateId(dateTime):
    dateV = dateTime.split('T')[0]
    if len(dateV) != 10:
#         print 'date error'
#         print dateTime
        return -1
    ele = dateV.split('-')
    assert len(ele) == 3, 'date format error'
    end = datetime.date(int(ele[0]), int(ele[1]), int(ele[2]))
    start = datetime.date(2010, 1, 1)
    return (end - start).days/7

def printplotDmca(dmcaDic, defameDic, outputFigPath):
    dmcaMax = max(dmcaDic.keys())
    dmcaMin = min(dmcaDic.keys())
    maxdateId = dmcaMax
    mindateId = dmcaMin
    print 'Dmca date range: ' + str(maxdateId) + '\t' + str(mindateId)
    dmcaL = []
    for i in range(0, maxdateId):
        if i in dmcaDic.keys():
            dmcaL.append(dmcaDic[i])
        else:
            dmcaL.append(0)
    
    x = [i for i in range(0, maxdateId)]
#     _, ax = plt.subplots(1, 1)
#     plt.figure()
    
    fig = plt.figure()
    ax = fig.add_subplot(111)
    
    plt.plot(x, dmcaL, "r-", label='DMCA')
    ax.set_xticklabels([week2dateSingle(0),week2dateSingle(50),week2dateSingle(100),week2dateSingle(150),week2dateSingle(200),week2dateSingle(250)])
    
    plt.ylabel("count")
    plt.xlabel("weeks")
    plt.title("Temporal Distribution of DMCA Notices")
#     plt.yscale('log')
    plt.grid(True)
    plt.legend(loc=2)
    plt.savefig(outputFigPath)
#     plt.show()

def printplotRemain(courtDic, otherDic, tradeMarkDic, noexistDic, outputFigPath):
    defameMax = max(defameDic.keys())
    defameMin = min(defameDic.keys())
    otherMax = max(otherDic.keys())
    otherMin = min(otherDic.keys())
    courtOderMax = max(courtDic.keys())
    courtOderMin = min(courtDic.keys())
    trademarkMax = max(tradeMarkDic.keys())
    trademarkMin = min(tradeMarkDic.keys())
    maxdateId = max([defameMax, otherMax, courtOderMax, trademarkMax])
    mindateId = min([defameMin, otherMin, courtOderMin, trademarkMin])
    print 'Dmca date range: ' + str(maxdateId) + '\t' + str(mindateId)
    
    defameL = []
    for i in range(0, maxdateId):
        if i in defameDic.keys():
            defameL.append(defameDic[i])
        else:
            defameL.append(0)
    courtL = []
    for i in range(0, maxdateId):
        if i in courtDic.keys():
            courtL.append(courtDic[i])
        else:
            courtL.append(0)
    otherL = []
    for i in range(0, maxdateId):
        if i in otherDic.keys():
            otherL.append(otherDic[i])
        else:
            otherL.append(0)
    tradeMarkL = []
    for i in range(0, maxdateId):
        if i in tradeMarkDic.keys():
            tradeMarkL.append(tradeMarkDic[i])
        else:
            tradeMarkL.append(0)
    x = [i for i in range(0, maxdateId)]
#     _, ax = plt.subplots(1, 1)
#     plt.figure()
    fig = plt.figure()
    ax = fig.add_subplot(111)
    
    plt.plot(x, defameL, "b-", label='Defame')
    plt.plot(x, courtL, "g-", label='Court Order')
    plt.plot(x, tradeMarkL, "c-", label='Trade Mark')
    plt.plot(x, otherL, "y-", label='Other')
#     plt.plot(x, noexistL, "k-x", label='no type info.')
    ax.set_xticklabels([week2dateSingle(0),week2dateSingle(50),week2dateSingle(100),week2dateSingle(150),week2dateSingle(200),week2dateSingle(250)])
    
    plt.xlabel("weeks")
    plt.ylabel("count")
    plt.title("Temporal Distribution of notices(ctd.)")
#     plt.yscale('log')
    plt.grid(True)
    plt.legend(loc=2)
    plt.savefig(outputFigPath)
    
def readData(inputPath, missDateC, missTypeC):
    dmcaDic = {}
    defameDic = {}
    courtDic = {}
    otherDic = {}
    noexistDic = {}
    tradeMarkDic = {}
    infile = open(inputPath)
    for line in infile:
        line = line.strip()
        notice = line.split('\t')  # [id, sendDate, senderName, recipientName, principalName, type]
        type = notice[5]
        dateId = getDateId(notice[1])
        if dateId == -1 or dateId > 250:
#             print notice[1]
            missDateC = missDateC + 1
            continue
        if type == 'Dmca':
            if not dmcaDic.has_key(dateId):
                dmcaDic[dateId] = 0
            dmcaDic[dateId] = dmcaDic[dateId] + 1
        elif type == 'Defamation':
            if not defameDic.has_key(dateId):
                defameDic[dateId] = 0
            defameDic[dateId] = defameDic[dateId] + 1
        elif type == 'CourtOrder':
            if not courtDic.has_key(dateId):
                courtDic[dateId] = 0
            courtDic[dateId] = courtDic[dateId] + 1
        elif type == 'Other':
            if not otherDic.has_key(dateId):
                otherDic[dateId] = 0
            otherDic[dateId] = otherDic[dateId] + 1
        elif type == 'Trademark':
            if not tradeMarkDic.has_key(dateId):
                tradeMarkDic[dateId] = 0
            tradeMarkDic[dateId] = tradeMarkDic[dateId] + 1
        elif type == '-1':
            print type
            missTypeC = missTypeC + 1
            if not noexistDic.has_key(dateId):
                noexistDic[dateId] = 0
            noexistDic[dateId] = noexistDic[dateId] + 1
        else:
            missTypeC = missTypeC + 1
            print 'type error'
            print type
    return dmcaDic, defameDic, courtDic, otherDic, tradeMarkDic, noexistDic, missDateC, missTypeC

if __name__ == '__main__':
    missDateC = 0
    missTypeC = 0
    basepath= '../sourceData/'
    inputPath = basepath+'chillingMetadata'
    outputFigPathDmcadays = basepath+'chillingMetadataDmcainweeks.pdf'
    outputFigPathRemaindays = basepath+'chillingMetadataRemaininweeks.pdf'
    dmcaDic, defameDic, courtDic, otherDic, tradeMarkDic, noexistDic, missDateC, missTypeC = readData(inputPath, missDateC, missTypeC)
    printplotDmca(dmcaDic, defameDic, outputFigPathDmcadays)
    printplotRemain(courtDic, otherDic, tradeMarkDic, noexistDic, outputFigPathRemaindays)