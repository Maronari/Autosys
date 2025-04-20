"use client"

import { createContext, useContext, useEffect, useState, type ReactNode } from "react"
import SockJS from "sockjs-client"

// Определяем базовые типы для датчиков и механизмов
export type SensorStatus = "normal" | "warning" | "critical"

export type SensorHistoryPoint = {
  timestamp: number
  value: number
}

export type SensorDataItem = {
  id: string
  name: string
  value: number
  unit: string
  history: SensorHistoryPoint[]
  status: SensorStatus
}

export type MechanismStatus = "active" | "inactive" | "error"

export type ControlMechanismItem = {
  id: string
  name: string
  status: MechanismStatus
  lastUpdated: string
}

// Определяем типы для сообщений с дискриминантным полем type
interface BaseMessage {
  type: string
}

interface SensorDataMessage extends BaseMessage {
  type: "sensor-data"
  data: {
    sensorId: string
    value: number
  }
}

interface ControlStatusMessage extends BaseMessage {
  type: "control-status"
  data: {
    mechanismId: string
    status: string
    timestamp?: number
  }
}

interface ControlCommandMessage extends BaseMessage {
  type: "control-command"
  data: {
    mechanismId: string
    command: string
  }
}

interface InitialDataMessage extends BaseMessage {
  type: "initial-data"
  sensors?: SensorDataItem[]
  mechanisms?: ControlMechanismItem[]
}

// Объединенный тип для всех возможных сообщений
type WebSocketMessage = SensorDataMessage | ControlStatusMessage | ControlCommandMessage | InitialDataMessage | null

// Определяем типы для WebSocket контекста
type WebSocketContextType = {
  isConnected: boolean
  lastMessage: WebSocketMessage
  sendMessage: (message: any) => void
  connectionAttempts: number
}

// Создаем контекст
const WebSocketContext = createContext<WebSocketContextType | undefined>(undefined)

// Хук для использования WebSocket контекста
export const useWebSocket = () => {
  const context = useContext(WebSocketContext)
  if (context === undefined) {
    throw new Error("useWebSocket must be used within a WebSocketProvider")
  }
  return context
}

type WebSocketProviderProps = {
  url?: string
  children: ReactNode
}

export function WebSocketProvider({ url = "http://localhost:8080/ws", children }: WebSocketProviderProps) {
  const [socket, setSocket] = useState<WebSocket | null>(null)
  const [isConnected, setIsConnected] = useState(false)
  const [lastMessage, setLastMessage] = useState<WebSocketMessage>(null)
  const [connectionAttempts, setConnectionAttempts] = useState(0)

  useEffect(() => {
    const ws: WebSocket | null = null
    let sockjs: any = null
    let reconnectTimeout: NodeJS.Timeout | null = null
    let reconnectAttempts = 0
    const maxReconnectAttempts = 5
    const baseReconnectDelay = 3000 // 3 seconds

    const connect = () => {
      // Очищаем предыдущий таймаут, если он существует
      if (reconnectTimeout) {
        clearTimeout(reconnectTimeout)
        reconnectTimeout = null
      }

      try {
        // Создаем SockJS соединение
        sockjs = new SockJS(url)

        // SockJS имеет тот же API, что и WebSocket
        sockjs.onopen = () => {
          console.log("WebSocket connected")
          setIsConnected(true)
          reconnectAttempts = 0 // Сбрасываем счетчик попыток при успешном подключении
          setConnectionAttempts(0)
        }

        sockjs.onclose = (event: CloseEvent) => {
          console.log(`WebSocket disconnected: ${event.code} ${event.reason}`)
          setIsConnected(false)

          // Пытаемся переподключиться с экспоненциальной задержкой
          if (reconnectAttempts < maxReconnectAttempts) {
            const delay = baseReconnectDelay * Math.pow(1.5, reconnectAttempts)
            console.log(
              `Attempting to reconnect in ${delay}ms (attempt ${reconnectAttempts + 1}/${maxReconnectAttempts})`,
            )

            reconnectTimeout = setTimeout(() => {
              reconnectAttempts++
              setConnectionAttempts(reconnectAttempts)
              connect()
            }, delay)
          } else {
            console.error(`Failed to reconnect after ${maxReconnectAttempts} attempts`)
          }
        }

        sockjs.onerror = (error: Event) => {
          console.error("WebSocket error:", error)
        }

        sockjs.onmessage = (event: MessageEvent) => {
          try {
            const data = JSON.parse(event.data) as WebSocketMessage
            console.log("Received message:", data)
            setLastMessage(data)
          } catch (error) {
            console.error("Error parsing WebSocket message:", error)
          }
        }

        setSocket(sockjs)
      } catch (error) {
        console.error("Error creating WebSocket:", error)

        // Пытаемся переподключиться при ошибке создания
        if (reconnectAttempts < maxReconnectAttempts) {
          const delay = baseReconnectDelay * Math.pow(1.5, reconnectAttempts)
          reconnectTimeout = setTimeout(() => {
            reconnectAttempts++
            setConnectionAttempts(reconnectAttempts)
            connect()
          }, delay)
        }
      }
    }

    // Инициируем подключение
    connect()

    // Закрываем соединение при размонтировании компонента
    return () => {
      if (sockjs) {
        sockjs.close()
      }

      if (reconnectTimeout) {
        clearTimeout(reconnectTimeout)
      }
    }
  }, [url])

  // Функция для отправки сообщений
  const sendMessage = (message: any) => {
    if (socket && isConnected) {
      socket.send(JSON.stringify(message))
    } else {
      console.error("Cannot send message, WebSocket is not connected")
    }
  }

  const value = {
    isConnected,
    lastMessage,
    sendMessage,
    connectionAttempts,
  }

  return <WebSocketContext.Provider value={value}>{children}</WebSocketContext.Provider>
}
