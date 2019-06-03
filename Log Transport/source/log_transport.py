import subprocess, os, sys
import re
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

def tasklist():
    wmic = subprocess.check_output('wmic path win32_process get caption,creationdate', shell=True) #process 시작시간 저장
    wmic = wmic.decode('euc-kr')

    return wmic

conn = pymysql.connect(host='192.168.1.102', user='root', passwd='1234', db='mysql', charset='utf8')       #db 연결(host='192.168.43.89', user='root', passwd='1234', db='mysql')
curs = conn.cursor()        #conn으로부터 cursor 생성

arrinfo = getMacAddress()
# print("\nMAC 1 : " + arrinfo[0]
#     + "\nMAC 2 : " + arrinfo[1]
#     + "\nMAC 3 : " + arrinfo[2])

now = datetime.datetime.now()       #현재시간
gap = datetime.timedelta(days=1)
tomorrow = now + gap     #현재시간부터 1일 후



for second in range(0, 86400):
    wmic = tasklist()

    if re.findall(r'\w+.exe\b', wmic):      #.exe로 끝나는 모든 string을 저장, set() = 중복제거
        process_name = re.findall(r'\w+.exe', wmic)
        start_proc = re.findall(r'2019\w\w\w\w+\w\w\w\w\w\w', wmic)        #2019년도로 시작하는 문자열 검색

    curs.execute("select * from proc_log")        #table_name = proc_log
    msg = curs.fetchone()
    # print(msg)
    if msg != None:     #테이블에 데이터가 있으면
        curs.execute("select start_proc from proc_log order by start_proc desc limit 1")       #table name = proc_log, DB에 저장된 데이터 중 가장 최근 시작한 프로세스
        last_proc = curs.fetchone()
        print("last_proc : ", int(last_proc[0]))
        for i in range (0, len(process_name)):       #DB에 저장될 컨텐츠
            if int(start_proc[i]) < int(last_proc[0]):      #start_proc에 저장된 데이터 중 last_proc 보다 작을 경우, 다음 인덱스로 넘어감, start_proc + index
                i += 1
            elif int(start_proc[i]) > int(last_proc[0]):
                sql = """insert into proc_log(mac, proc_name, start_proc) values (%s, %s, %s)"""  #table name = proc_log
                curs.execute(sql, (arrinfo[2], process_name[i], start_proc[i]))
                conn.commit()

    # sql2 = """select id from test order by id desc limit 1"""       #최근 index 값
    # curs.execute(sql2)
    # index = curs.fetchone()

    if msg == None:     # 테이블에 데이터가 없을 경우
        for i in range (0, len(process_name)):       #DB에 저장될 컨텐츠
            sql1 = """insert into proc_log(mac, proc_name, start_proc) values (%s, %s, %s)"""  #table name = proc_log
            curs.execute(sql1, (arrinfo[2], process_name[i], start_proc[i]))
            conn.commit()

    print(datetime.datetime.now())
    time.sleep(20)      #20초동안 지연
    second += 20

conn.close()        #conn 닫기
