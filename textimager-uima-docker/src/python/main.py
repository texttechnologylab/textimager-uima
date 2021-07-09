import docker
import time
import sys
from docker.types import DeviceRequest

client = docker.from_env()
containers = client.containers.list()
print("Inside docker container now, starting REPL now")

while True:
    command = input("$> ")
    cmdargs = command.split(" ")
    
    if command == "quit":
        break
    elif command == "ls container":
        for x in client.containers.list():
            print("Container name:",x.name," and status:",x.status)
    elif command == "ls images":
        for x in client.images.list():
            if x.tags != []:
                print("Container tag:",x.tags)
    elif cmdargs[0] == "status":
        if len(cmdargs) < 2:
            print("Missing argument for status please try again!")
            continue

        try:
            cont = client.containers.get(cmdargs[1])
            print("Container name:",cont.name," and status:",cont.status)
        except:
            print("No such container '{}'.".format(cmdargs[1]))
    elif cmdargs[0] == "log":
        if len(cmdargs) < 2:
            print("Missing argument for run please try again!")
            continue

        try:
            cont = client.containers.get(cmdargs[1])
            print(cont.logs(timestamps=True).decode("utf-8"))
        except:
            print("Could not find container")
    elif cmdargs[0] == "kill":
        if len(cmdargs) < 2:
            print("Missing argument for run please try again!")
            continue

        try:
            cont = client.containers.get(cmdargs[1])
            cont.stop()
            print("Stopped container {}".format(cmdargs[1]))
        except:
            print("Could not find container")
    elif cmdargs[0] == "run":
        if len(cmdargs) < 2:
            print("Missing argument for run please try again!")
            continue

        start_with_name = input("Container name: ")

        with_gpu = input("With gpu access (yes/no): ")
        if with_gpu == "yes":
            device_requests = device_requests=[DeviceRequest(driver="nvidia",capabilities=[["gpu","utility","compute"]])]
        else:
            device_requests = []

        ports = {}
        while True:
            port_mappings = input("Port mapping container (enter to skip): ")
            if port_mappings == "":
                break
            port_mappings_host = input("Port mapping host (enter to skip): ")
            if port_mappings_host == "":
                break

            ports[port_mappings+"/tcp"] = int(port_mappings_host)
        env = {}
        while True:
            environment_variables = input("Environment pair (key=value,enter to skip: ")
            if environment_variables == "":
                break
            res = environment_variables.find("=")
            if res == -1:
                env[environment_variables] = ""
            else:
                env[environment_variables[0:res]] = environment_variables[res+1:]
        try:
            cont = client.containers.run(client.images.get(cmdargs[1]),None,remove=True,detach=True,environment=env,ports=ports,device_requests=device_requests,name=start_with_name)
            print("Polling logs to check status")
            not_done=True
            start = time.time()
            while not_done:
                stream = cont.logs()
                for x in stream.splitlines():
                    if x.decode("utf-8") == "PyDockerNotify: Container startup success":
                        not_done=False
                elapsed = time.time()-start
                if elapsed > 20:
                    break
            if not_done == True:
                print("Could not find magic string in output, probably container hangs...")
            else:
                print("Got acknowledge from container, startup successful")
        except:
            print("An error occured could not start container")
    else:
        print("Could not recognize command.")

