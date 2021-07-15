import asyncio
import pymysql.cursors


def process_data(data):  # обрабатываем запрос
    command, args = data.split(' ', 1)
    if command == 'check':
        resp = 'ok\nyou exist\n\n'
    else:
        resp = 'error\nwrong command\n\n'
    return resp


def blind_signature(key):  # функция для подписи ключа пользователя
    return key


class ClientServerProtocol(asyncio.Protocol):
    def connection_made(self, transport):
        self.transport = transport

    def data_received(self, data):
        print("data received ", data)  # данные пришли
        resp = process_data(data.decode())  # обработали, сформировали ответ
        self.transport.write(resp.encode())  # отправили


def run_server(host, port):
    print("i am alive and ready for work!")
    loop = asyncio.get_event_loop()
    coro = loop.create_server(
        ClientServerProtocol,
        host, port
    )

    server = loop.run_until_complete(coro)

    try:
        loop.run_forever()
    except KeyboardInterrupt:
        print('shut this beatiful server up')
        pass

    server.close()
    loop.run_until_complete(server.wait_closed())
    loop.close()


#run_server('127.0.0.1', 8888)

'''
# подключаемся к БД и считываем всех пользователей
con = pymysql.connect(host='localhost', user='root', db='voters')

with con:

    cur = con.cursor()
    cur.execute("SELECT * FROM data")

    rows = cur.fetchall()

    for row in rows:
        print("{0} {1} {2}".format(row[0], row[1], row[2]))
'''
