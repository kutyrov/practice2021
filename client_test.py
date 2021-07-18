import socket
import time
import re


class ClientError(Exception):
    pass


class Client():

    def __init__(self, host, port, timeout=None):

        self.sock = socket.create_connection((host, port), timeout)
        self.request = ""
        self.response = ""
        self.response_status = True

    def put(self, data):
        self.request = data
        self.sock.sendall(self.request.encode("utf8"))
        # получим ответ
        self.response = self.sock.recv(1024).decode()
        print(self.response)

    def __del__(self):
        self.sock.close()


# тестирование

client = Client("127.0.0.1", 8888, timeout=15)
# while True:
try:
    #data = input()
    key = ''
    with open("public.txt") as file_handler:
        for line in file_handler:
            key += line
    # print(key)
    data = 'check\nПетров Игорь Вячеславович 1987-11-24 2536284637 ' + key + '\n\n'
    client.put(data)
except KeyboardInterrupt:
    print('закрываем клиент')
    # break
