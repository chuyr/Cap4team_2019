import subprocess, os, sys
import re

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

arrinfo = getMacAddress()
print("Host : " + arrinfo['host']
    + "\nMAC 1 : " + arrinfo[0]
    + "\nMAC 2 : " + arrinfo[1]
    + "\nMAC 3 : " + arrinfo[2])

wmic = tasklist()

if re.findall(r'\w+.exe\b', wmic):      #.exe로 끝나는 모든 string을 저장, set() = 중복제거
    process_name = re.findall(r'\w+.exe', wmic)
    start_proc = re.findall(r'2019\w\w\w\w+\w\w\w\w\w\w', wmic)        #2019년도로 시작하는 문자열 검색

for i in range (0, len(process_name)):       #DB에 저장될 컨텐츠
    print("Index : " , i ,
        "MAC : " , arrinfo[2] ,
        "proc_name : " ,process_name[i] ,
        "start_proc : " ,start_proc[i])
