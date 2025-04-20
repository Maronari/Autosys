import type React from "react"
import { Card, CardContent } from "@/components/ui/card"
import { type SensorStatus } from "@/components/websocket-provider"

type SensorValueProps = {
  sensor: {
    id: string
    name: string
    value: number
    unit: string
    icon: React.ReactNode
    status: SensorStatus
  }
}

export default function SensorValue({ sensor }: SensorValueProps) {
  return (
    <Card>
      <CardContent className="p-4 flex flex-col space-y-2">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            {sensor.icon}
            <span className="font-medium">{sensor.name}</span>
          </div>
          <div
            className={`h-2 w-2 rounded-full ${
              sensor.status === "normal" ? "bg-green-500" : sensor.status === "warning" ? "bg-amber-500" : "bg-red-500"
            }`}
          />
        </div>
        <div className="text-2xl font-bold">
          {sensor.value} <span className="text-sm font-normal">{sensor.unit}</span>
        </div>
        <div
          className={`text-xs font-medium ${
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
      </CardContent>
    </Card>
  )
}
