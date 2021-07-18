import asyncio
import pymysql.cursors


def process_data(reqv, people):  # обрабатываем запрос
    # print(reqv)
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
            # print(key)
        except ValueError:
            return resp
        # как вариант можно не хранить всех пользователей в global data а каждый раз делать запрос к серверу по данным
        if passport_id in people:
            if people[passport_id][0] == name and people[passport_id][1] == birthday:
                resp = 'ok\nyou exist\n\n'
                #resp = 'ok\n'+blind_signature(key).decode()+'\n\n'
                # print(blind_signature(key))
                #new_key = blind_signature(key)

                # sign[len(sign)/2:] + sign[:len(sign)/2
                return resp
    return resp


def blind_signature(key):  # функция для подписи ключа пользователя
    # это пример для 34.10-2012 нужно заменить на 34.10-2001
    # или же можно и не менять не зря же гост заменили ¯\_(ツ)_/¯
    from pygost.gost3410 import CURVES
    curve = CURVES["id-tc26-gost-3410-12-512-paramSetA"]
    from os import urandom
    prv_raw = urandom(64)
    from pygost.gost3410 import prv_unmarshal
    prv = prv_unmarshal(prv_raw)
    from pygost.gost3410 import public_key
    pub = public_key(curve, prv)
    from pygost.gost3410 import pub_marshal
    from pygost.utils import hexenc
    # print "Public key is:", hexenc(pub_marshal(pub))
    from pygost import gost34112012512
    data_for_signing = key.encode()  # единственная измененная строка
    dgst = gost34112012512.new(data_for_signing).digest()[::-1]
    from pygost.gost3410 import sign
    signature = sign(curve, prv, dgst)
    return signature


def get_data_sql(f_host, f_user, f_db):
    # подключаемся к БД и считываем всех пользователей
    # возможно стоит добавить графу пароль для проверки, отправившего запрос
    # людей, прочитавших этот коммент прошу сообщить мне об этом :)
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
