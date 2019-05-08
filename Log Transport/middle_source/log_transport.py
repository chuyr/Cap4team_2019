import subprocess, os, sys
import re
import pymysql as sql
import threading
from datetime import datetime

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

def tasklist():
    wmic = subprocess.check_output('wmic path win32_process get caption,creationdate', shell=True) #process 시작시간 저장
    wmic = wmic.decode('euc-kr')
    threading.Timer(600, tasklist).start()
    print(datetime.now())

    return wmic

conn = sql.connect(host='localhost', user='root', passwd='toor', db='test', charset='utf8')       #db 연결
curs = conn.cursor()        #conn으로부터 cursor 생성

arrinfo = getMacAddress()
print("Host : " + arrinfo['host']
    + "\nMAC 1 : " + arrinfo[0]
    + "\nMAC 2 : " + arrinfo[1]
    + "\nMAC 3 : " + arrinfo[2])


wmic = tasklist()

if re.findall(r'\w+.exe\b', wmic):      #.exe로 끝나는 모든 string을 저장, set() = 중복제거
    process_name = re.findall(r'\w+.exe', wmic)
    start_proc = re.findall(r'2019\w\w\w\w+\w\w\w\w\w\w', wmic)        #2019년도로 시작하는 문자열 검색

sql = """select * from test order by start_proc desc limit 1"""
last_proc = curs.execute(sql)       #DB에 저장된 데이터 중 가장 최근 시작한 프로세스

for i in range (0, len(process_name)):       #DB에 저장될 컨텐츠
    if int(start_proc[i]) < int(last_proc):      #start_proc에 저장된 데이터 중 last_proc 보다 작을 경우, 다음 인덱스로 넘어감
        i+=1
    sql1 = """insert into test(mac, proc_name, start_proc)
            values (%s, %s, %s)"""
    curs.execute(sql1, (arrinfo[2], process_name[i], start_proc[i]))
    conn.commit()

conn.close()        #conn 닫기
