import subprocess, os, socket, sys
import pymysql
import datetime
import time

def getMacAddress():    #ipconfig에서 mac주소를 가져오는 함수
    arrinfo = {}
    isdevice = 0
    mk = 0
    if sys.platform == 'win32':
        for line in os.popen("ipconfig /all"):
            if line.lstrip().startswith('호스트'):
                host = line.split(':')[1].strip()
                arrinfo["host"] = host
            else:
                if line.lstrip().startswith('터널'):
                    isdevice = 0
                if line.lstrip().startswith('이더넷'):
                    isdevice = 1
                if line.lstrip().startswith('무선'):
                    isdevice = 1
                if isdevice == 1:
                    if line.lstrip().startswith('미디어 상태'):
                        desc = line.split(':')[1].strip()
                        if desc == '미디어 연결 끊김':
                            isdevice = 0
                    if line.lstrip().startswith('물리적'):
                        mac = line.split(':')[1].strip() #.replace('-',':')
                        arrinfo[mk] = mac
                        isdevice = 0
                        mk += 1
    return arrinfo

def getIpAddress():     #get ip address function
    return socket.gethostbyname(socket.getfqdn())

conn = pymysql.connect(host='192.168.0.81', user='root', passwd='1234', db='mysql', charset='utf8')       #db 연결(host='192.168.43.89', user='root', passwd='1234', db='mysql')
curs = conn.cursor()        #conn으로부터 cursor 생성

arrinfo = getMacAddress()
get_Ip = getIpAddress()
# print(get_Ip)

curs.execute("select * from mac_ip")        #table_name = mac_ip
msg = curs.fetchone()

if msg != None:
    sql = """select ip from mac_ip where mac=%s"""
    curs.execute(sql, (arrinfo[2]))       #table name = mac_ip, DB에 저장된 데이터 중 현재 mac주소가 같은 ip주소를 현재 가져온 ip주소와 비교
    old_Ip = curs.fetchone()
    print("mac : ", arrinfo[2])
    print("old_Ip : ", old_Ip)
    print("get_Ip : ", get_Ip)

    if old_Ip == None:
        sql2 = """insert into mac_ip(mac, ip) values (%s, %s)"""        #table_name = mac_ip
        curs.execute(sql2, (arrinfo[2], get_Ip))
        conn.commit()
        print("mac, ip 새로 추가")

    if get_Ip != old_Ip:      #현재 얻은 ip주소와 db내의 ip주소가 다를 경우
        sql1 = """update mac_ip set ip=%s where mac=%s"""        # ip주소를 변경
        curs.execute(sql1, (get_Ip, arrinfo[2]))
        conn.commit()

if msg == None:     # 테이블에 데이터가 없을 경우
    sql3 = """insert into mac_ip(mac, ip) values (%s, %s)"""  #table name = mac_ip
    curs.execute(sql3, (arrinfo[2], get_Ip))
    conn.commit()
    print("ip주소가 변경되지 않음")

conn.close()
