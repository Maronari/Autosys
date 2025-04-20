"use client"

import type React from "react"

import { useEffect, useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import SensorChart from "@/components/sensor-chart"
import SensorValue from "@/components/sensor-value"
import ControlStatus from "@/components/control-status"
import { AlertCircle, Gauge, ThermometerIcon, Droplets, Wind } from 'lucide-react'
import { 
  useWebSocket, 
  type SensorStatus, 
  type SensorDataItem, 
  type ControlMechanismItem 
} from "@/components/websocket-provider"

// Расширяем тип SensorDataItem для включения иконки
type SensorData = SensorDataItem & {
  icon: React.ReactNode
}

// Используем тип ControlMechanismItem напрямую
type ControlMechanism = ControlMechanismItem

export default function Dashboard() {
  // Состояние для хранения данных датчиков и механизмов
  const [sensors, setSensors] = useState<SensorData[]>([
    {
      id: "temp1",
      name: "Температура",
      value: 24.5,
      unit: "°C",
      history: Array(20)
        .fill(0)
        .map((_, i) => ({
          timestamp: Date.now() - (20 - i) * 60000,
          value: 22 + Math.random() * 5,
        })),
      icon: <ThermometerIcon className="h-5 w-5" />,
      status: "normal",
    },
    {
      id: "pressure1",
      name: "Давление",
      value: 101.3,
      unit: "кПа",
      history: Array(20)
        .fill(0)
        .map((_, i) => ({
          timestamp: Date.now() - (20 - i) * 60000,
          value: 100 + Math.random() * 3,
        })),
      icon: <Gauge className="h-5 w-5" />,
      status: "normal",
    },
    {
      id: "humidity1",
      name: "Влажность",
      value: 45,
      unit: "%",
      history: Array(20)
        .fill(0)
        .map((_, i) => ({
          timestamp: Date.now() - (20 - i) * 60000,
          value: 40 + Math.random() * 15,
        })),
      icon: <Droplets className="h-5 w-5" />,
      status: "warning",
    },
    {
      id: "airflow1",
      name: "Поток воздуха",
      value: 2.4,
      unit: "м/с",
      history: Array(20)
        .fill(0)
        .map((_, i) => ({
          timestamp: Date.now() - (20 - i) * 60000,
          value: 2 + Math.random() * 1.5,
        })),
      icon: <Wind className="h-5 w-5" />,
      status: "normal",
    },
  ])

  const [controlMechanisms, setControlMechanisms] = useState<ControlMechanism[]>([
    { id: "valve1", name: "Клапан 1", status: "active", lastUpdated: "2 мин. назад" },
    { id: "valve2", name: "Клапан 2", status: "inactive", lastUpdated: "5 мин. назад" },
    { id: "pump1", name: "Насос 1", status: "active", lastUpdated: "1 мин. назад" },
    { id: "fan1", name: "Вентилятор 1", status: "error", lastUpdated: "10 мин. назад" },
    { id: "heater1", name: "Нагреватель", status: "inactive", lastUpdated: "15 мин. назад" },
  ])

  // Используем WebSocket соединение
  const { isConnected, lastMessage } = useWebSocket()

  // Обрабатываем полученные сообщения
  useEffect(() => {
    if (lastMessage) {
      // Используем проверки типа для сужения типа lastMessage
      if (lastMessage.type === "sensor-data") {
        const { sensorId, value } = lastMessage.data

        setSensors((prevSensors) =>
          prevSensors.map((sensor) => {
            if (sensor.id === sensorId) {
              // Определяем статус на основе значения
              let status: SensorStatus = "normal"
              if (sensor.id === "temp1" && (value > 28 || value < 18)) {
                status = value > 30 || value < 15 ? "critical" : "warning"
              } else if (sensor.id === "humidity1" && (value > 60 || value < 30)) {
                status = value > 70 || value < 20 ? "critical" : "warning"
              }

              // Обновляем историю
              const newHistory = [
                ...sensor.history.slice(1),
                {
                  timestamp: Date.now(),
                  value: value,
                },
              ]

              return {
                ...sensor,
                value: Number(value.toFixed(1)),
                history: newHistory,
                status,
              }
            }
            return sensor
          }),
        )
      } else if (lastMessage.type === "control-status") {
        const { mechanismId, status, timestamp } = lastMessage.data

        setControlMechanisms((prevMechanisms) =>
          prevMechanisms.map((mechanism) => {
            if (mechanism.id === mechanismId) {
              // Форматируем время последнего обновления
              const lastUpdated = formatTimeSince(timestamp || Date.now())

              return {
                ...mechanism,
                status: status as ControlMechanism["status"],
                lastUpdated,
              }
            }
            return mechanism
          }),
        )
      } else if (lastMessage.type === "initial-data") {
        // Обработка начальных данных при подключении
        if (lastMessage.sensors) {
          // Добавляем иконки к датчикам, полученным с сервера
          const sensorsWithIcons = lastMessage.sensors.map(sensor => {
            let icon: React.ReactNode = <AlertCircle className="h-5 w-5" />
            
            // Определяем иконку на основе ID или имени датчика
            if (sensor.id.includes('temp') || sensor.name.toLowerCase().includes('температура')) {
              icon = <ThermometerIcon className="h-5 w-5" />
            } else if (sensor.id.includes('pressure') || sensor.name.toLowerCase().includes('давление')) {
              icon = <Gauge className="h-5 w-5" />
            } else if (sensor.id.includes('humid') || sensor.name.toLowerCase().includes('влажность')) {
              icon = <Droplets className="h-5 w-5" />
            } else if (sensor.id.includes('air') || sensor.name.toLowerCase().includes('воздух')) {
              icon = <Wind className="h-5 w-5" />
            }
            
            return {
              ...sensor,
              icon
            }
          })
          
          setSensors(sensorsWithIcons)
        }

        if (lastMessage.mechanisms) {
          setControlMechanisms(lastMessage.mechanisms)
        }
      }
    }
  }, [lastMessage])

  // Функция для форматирования времени
  const formatTimeSince = (timestamp: number): string => {
    const seconds = Math.floor((Date.now() - timestamp) / 1000)

    if (seconds < 60) return "только что"
    if (seconds < 3600) return `${Math.floor(seconds / 60)} мин. назад`
    if (seconds < 86400) return `${Math.floor(seconds / 3600)} ч. назад`
    return `${Math.floor(seconds / 86400)} д. назад`
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-2 mb-4">
        <div className={`h-3 w-3 rounded-full ${isConnected ? "bg-green-500" : "bg-red-500"}`} />
        <span className="text-sm font-medium">{isConnected ? "Подключено к серверу" : "Отключено от сервера"}</span>
      </div>
      <div className="flex flex-col space-y-2">
        <h1 className="text-3xl font-bold">Панель мониторинга</h1>
        <p className="text-muted-foreground">
          Мониторинг датчиков и состояния исполнительных механизмов в реальном времени
        </p>
      </div>

      <Tabs defaultValue="overview" className="space-y-4">
        <TabsList>
          <TabsTrigger value="overview">Обзор</TabsTrigger>
          <TabsTrigger value="sensors">Датчики</TabsTrigger>
          <TabsTrigger value="controls">Механизмы</TabsTrigger>
        </TabsList>

        <TabsContent value="overview" className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {sensors.map((sensor) => (
              <SensorValue key={sensor.id} sensor={sensor} />
            ))}
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {sensors.map((sensor) => (
              <Card key={sensor.id}>
                <CardHeader className="pb-2">
                  <CardTitle className="text-lg flex items-center gap-2">
                    {sensor.icon}
                    {sensor.name}
                  </CardTitle>
                  <CardDescription>Исторические данные</CardDescription>
                </CardHeader>
                <CardContent>
                  <SensorChart data={sensor.history} unit={sensor.unit} />
                </CardContent>
              </Card>
            ))}
          </div>

          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <AlertCircle className="h-5 w-5" />
                Состояние исполнительных механизмов
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4">
                {controlMechanisms.map((mechanism) => (
                  <ControlStatus key={mechanism.id} mechanism={mechanism} />
                ))}
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="sensors" className="space-y-4">
          <div className="grid grid-cols-1 gap-4">
            {sensors.map((sensor) => (
              <Card key={sensor.id}>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    {sensor.icon}
                    {sensor.name}
                  </CardTitle>
                  <CardDescription>Подробная информация</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div className="flex flex-col space-y-2">
                      <div className="text-2xl font-bold">
                        {sensor.value} {sensor.unit}
                      </div>
                      <div className="text-sm text-muted-foreground">Текущее значение</div>
                      <div
                        className={`text-sm font-medium ${
                          sensor.status === "normal"
                            ? "text-green-500"
                            : sensor.status === "warning"
                              ? "text-amber-500"
                              : "text-red-500"
                        }`}
                      >
                        {sensor.status === "normal"
                          ? "Нормальное состояние"
                          : sensor.status === "warning"
                            ? "Предупреждение"
                            : "Критическое состояние"}
                      </div>
                    </div>
                    <div className="md:col-span-2">
                      <SensorChart data={sensor.history} unit={sensor.unit} />
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </TabsContent>

        <TabsContent value="controls" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Исполнительные механизмы</CardTitle>
              <CardDescription>Текущее состояние и управление</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {controlMechanisms.map((mechanism) => (
                  <div key={mechanism.id} className="flex items-center justify-between p-4 border rounded-lg">
                    <div className="space-y-1">
                      <h3 className="font-medium">{mechanism.name}</h3>
                      <p className="text-sm text-muted-foreground">Обновлено: {mechanism.lastUpdated}</p>
                    </div>
                    <div className="flex items-center gap-2">
                      <div
                        className={`h-3 w-3 rounded-full ${
                          mechanism.status === "active"
                            ? "bg-green-500"
                            : mechanism.status === "inactive"
                              ? "bg-gray-400"
                              : "bg-red-500"
                        }`}
                      />
                      <span className="text-sm font-medium">
                        {mechanism.status === "active"
                          ? "Активен"
                          : mechanism.status === "inactive"
                            ? "Неактивен"
                            : "Ошибка"}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  )
}
