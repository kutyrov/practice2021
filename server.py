import asyncio
import pymysql.cursors


def process_data(reqv, people):  # обрабатываем запрос
    resp = 'error\n\n'
    try:
        command, args, _ = reqv.split('\n', 2)
    except ValueError:
        return resp
    args = args.split(' ')
    if command == 'check' and len(args) == 6:
        try:
            name = str(args[0] + ' ' + args[1] + ' ' + args[2])
            birthday = str(args[3])
            passport_id = int(args[4])
            key = args[5]
        except ValueError:
            return resp
        if passport_id in people:
            if people[passport_id][0] == name and people[passport_id][1] == birthday:
                resp = 'ok\nyou exist\n\n'
                # resp = blind_signature(key)
                return resp
    return resp


def blind_signature(key):  # функция для подписи ключа пользователя
    return key


def get_data_sql(f_host, f_user, f_db):
    # подключаемся к БД и считываем всех пользователей
    try:
        con = pymysql.connect(host=f_host, user=f_user, db=f_db)
    except pymysql.err.OperationalError:
        return None
    people = {}

    with con:

        cur = con.cursor()
        cur.execute("SELECT * FROM data")

        rows = cur.fetchall()

        for row in rows:
            #print("{0} {1} {2}".format(row[0], row[1], row[2]))
            people[int(row[2])] = (str(row[0]), str(row[1]))

        return people


class ClientServerProtocol(asyncio.Protocol):
    def __init__(self):
        self.people = global_people

    def connection_made(self, transport):
        self.transport = transport

    def data_received(self, data):
        # print("data received ", data)  # данные пришли
        # обработали, сформировали ответ
        resp = process_data(data.decode(), self.people)
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
        print('shut this beautiful server up')
        pass

    server.close()
    loop.run_until_complete(server.wait_closed())
    loop.close()


if __name__ == "__main__":
    global_people = get_data_sql(
        f_host='localhost', f_user='root', f_db='voters')

    if global_people is None:
        print("Error! Can't get data. Maybe DB is unreachable")
        exit()
    else:
        print('got people\'s data')
    run_server('127.0.0.1', 8888)
