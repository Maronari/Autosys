"use client"

import { Card, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { useWebSocket, type ControlMechanismItem } from "@/components/websocket-provider"

type ControlStatusProps = {
  mechanism: ControlMechanismItem
}

export default function ControlStatus({ mechanism }: ControlStatusProps) {
  const { sendMessage, isConnected } = useWebSocket()

  const toggleStatus = () => {
    if (!isConnected) {
      alert("Нет подключения к серверу")
      return
    }

    // Отправляем команду на сервер
    sendMessage({
      type: "control-command",
      data: {
        mechanismId: mechanism.id,
        command: mechanism.status === "active" ? "deactivate" : "activate",
      },
    })
  }

  return (
    <Card
      className={`border-l-4 ${
        mechanism.status === "active"
          ? "border-l-green-500"
          : mechanism.status === "inactive"
            ? "border-l-gray-400"
            : "border-l-red-500"
      }`}
    >
      <CardContent className="p-4 flex flex-col space-y-2">
        <div className="font-medium">{mechanism.name}</div>
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
          <span className="text-sm">
            {mechanism.status === "active" ? "Активен" : mechanism.status === "inactive" ? "Неактивен" : "Ошибка"}
          </span>
        </div>
        <div className="text-xs text-muted-foreground">Обновлено: {mechanism.lastUpdated}</div>
        {mechanism.status !== "error" && (
          <Button variant="outline" size="sm" onClick={toggleStatus} disabled={!isConnected}>
            {mechanism.status === "active" ? "Выключить" : "Включить"}
          </Button>
        )}
      </CardContent>
    </Card>
  )
}
