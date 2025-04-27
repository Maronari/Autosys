-- Заполнение таблицы opc_node_group
INSERT INTO opc_node_group (group_id, group_name)
VALUES
(1, 'Температурные датчики'),
(2, 'Датчики давления'),
(3, 'Влажность');

-- Заполнение таблицы sensors
INSERT INTO sensors (sensor_id, sensor_opcua_endpoint, sensor_status)
VALUES
(1, 'opc.tcp://localhost:4801/freeopcua/server', 1),  -- 1 = активен
(2, 'opc.tcp://localhost:4802/freeopcua/server', 1),  -- 0 = неактивен
(3, 'opc.tcp://localhost:4803/freeopcua/server', 1);

-- Заполнение таблицы sensor_params
INSERT INTO sensor_params (param_id, sensor_id, node_id, group_id, param_name, param_max_value, param_min_value)
VALUES
(1, 1, 'ns=2;i=6', 1, 'Температура', 84, 70),
(2, 2, 'ns=2;i=6', 2, 'Давление', 10, 0),
(3, 3, 'ns=2;i=6', 3, 'Поток', 4, 0);

-- Заполнение таблицы opc_node_group
INSERT INTO user_role (role_id, name, description, privilege)
VALUES
(1, 'ADMIN', 'admin', true),
(2, 'USER', 'user', false),
(3, 'MANAGER', 'manager', false);

INSERT INTO "user" (user_id, role_id, name, login, password)
VALUES
(1, 1, 'admin', 'admin', 'password'),
(2, 2, 'user', 'user', 'password'),
(3, 2, 'manager', 'manager', 'password');