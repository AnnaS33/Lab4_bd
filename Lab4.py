
import multiprocessing
from time import sleep
import random
import numpy as np
from kazoo.protocol.paths import join
from kazoo.client import KazooClient

class Client(multiprocessing.Process):
    def __init__(self, path: str, id: int):
        super().__init__()
        self.path = f'{path}/{id}'
        self.id = id

    def run(self):
        zk = KazooClient(hosts='127.0.0.1:2181')
        zk.start()

        r=random.randint(0, 1)
        if(r==0):
            value = b'Fixing'
        else:
            value = b'Abort'

        print(f'Client {self.id} request {value.decode()}')
        zk.create(self.path, value, ephemeral=True)


        @zk.DataWatch(self.path)
        def check(data, stat):
            if stat.version != 0:
                print(f'Client {self.id} do {data.decode()}')
        sleep(3)
        zk.stop()
        zk.close()


class Watcher():

    def __init__(self, path: str, num_client: int):
        self.path = f'{path}'
        self.num_client = num_client
        self.watcher = KazooClient(hosts='127.0.0.1:2181')
        self.watcher.start()
        if self.watcher.exists('/lab4'):
            self.watcher.delete('/lab4', recursive=True)
        self.watcher.create('/lab4')
        self.watcher.create(self.path)

    def main(self):

        def check_clients():
            clients = self.watcher.get_children(self.path)
            count_f = 0
            count_a = 0
            for client in clients:
                count_f += int(self.watcher.get(f'{self.path}/{client}')[0] == b'Fixing')
                count_a += int(self.watcher.get(f'{self.path}/{client}')[0] == b'Abort')

            if (count_f > count_a):
                target = b'Fixing'
            else:
                target = b'Abort'

            for client in clients:
                self.watcher.set(f'{self.path}/{client}', target)

        @self.watcher.ChildrenWatch(self.path)
        def watch_clients(clients):
            if len(clients) < self.num_client :
                print('Высказались следующие клиенты', clients)
            elif len(clients) == self.num_client:
                print('Проверяем их решения')
                check_clients()

        for i in range(1,self.num_client+1):
            p = Client(self.path, i)
            p.start()


if __name__ == "__main__":
    path='/lab4/task4'
    num_client=5
    Watcher(path,num_client).main()
