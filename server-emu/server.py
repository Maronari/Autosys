import asyncio
import copy
import logging
from datetime import datetime
import time
import random

import os
from dotenv import load_dotenv

from asyncua import ua, uamethod, Server


_logger = logging.getLogger(__name__)

load_dotenv()


class SubHandler:
    """
    Subscription Handler. To receive events from server for a subscription
    """

    def datachange_notification(self, node, val, data):
        _logger.warning("Python: New data change event %s %s", node, val)

    def event_notification(self, event):
        _logger.warning("Python: New event %s", event)

async def main():
    server = Server()
    await server.init()
    # server.disable_clock()  #for debuging
    server.set_endpoint("opc.tcp://0.0.0.0:4840/freeopcua/server/")
    server.set_server_name("FreeOpcUa Sensor Server")
    # set all possible endpoint policies for clients to connect through
    server.set_security_policy(
        [
            ua.SecurityPolicyType.NoSecurity,
            ua.SecurityPolicyType.Basic256Sha256_SignAndEncrypt,
            ua.SecurityPolicyType.Basic256Sha256_Sign,
        ]
    )

    # setup our own namespace
    uri = "http://examples.freeopcua.github.io"
    idx = await server.register_namespace(uri)

    # create a new node type we can instantiate in our address space
    dev = await server.nodes.base_object_type.add_object_type(idx, "Sensor")
    await (await dev.add_variable(idx, "sensor1", 1.0)).set_modelling_rule(True)
    await (await dev.add_property(idx, "device_id", "0340")).set_modelling_rule(True)
    ctrl = await dev.add_object(idx, "controller")
    await ctrl.set_modelling_rule(True)
    await (await ctrl.add_property(idx, "state", "Idle")).set_modelling_rule(True)

    # populating our address space

    # Add Node
    node_name = os.getenv('NODE_NAME')
    node_value = float(os.getenv('NODE_VALUE'))
    is_read_only = bool(os.getenv('IS_READ_ONLY'))

    main_node = await server.nodes.objects.add_variable(idx, node_name, node_value)
    if is_read_only:
        await main_node.set_read_only()
    else:
        await main_node.set_writable()

    # import some nodes from xml
    # await server.import_xml("custom_nodes.xml")

    # creating a default event object
    myevgen = await server.get_event_generator()
    myevgen.event.Severity = 300

    # starting!
    async with server:
        print("Available loggers are: ", logging.Logger.manager.loggerDict.keys())
        # trigger event, all subscribed clients will receive it
        if is_read_only:
            while True:
                await asyncio.sleep(0.1)
                random_value = random.uniform(node_value*0.95, node_value*1.05)
                await main_node.write_value(random_value)


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    asyncio.run(main())