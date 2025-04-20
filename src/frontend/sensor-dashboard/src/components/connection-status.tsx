"use client"

import { useWebSocket } from "@/components/websocket-provider"
import { AlertCircle, CheckCircle2, WifiOff } from "lucide-react"
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert"

export default function ConnectionStatus() {
  const { isConnected, connectionAttempts } = useWebSocket()

  if (isConnected) {
    return (
      <Alert variant="default" className="bg-green-50 border-green-200 mb-4">
        <CheckCircle2 className="h-4 w-4 text-green-600" />
        <AlertTitle>Подключено</AlertTitle>
        <AlertDescription>Соединение с сервером установлено</AlertDescription>
      </Alert>
    )
  }

  if (connectionAttempts > 0) {
    return (
      <Alert variant="default" className="bg-amber-50 border-amber-200 mb-4">
        <AlertCircle className="h-4 w-4 text-amber-600" />
        <AlertTitle>Переподключение...</AlertTitle>
        <AlertDescription>Попытка подключения к серверу ({connectionAttempts}/5)</AlertDescription>
      </Alert>
    )
  }

  return (
    <Alert variant="destructive" className="mb-4">
      <WifiOff className="h-4 w-4" />
      <AlertTitle>Нет соединения</AlertTitle>
      <AlertDescription>Не удалось подключиться к серверу. Проверьте соединение.</AlertDescription>
    </Alert>
  )
}
