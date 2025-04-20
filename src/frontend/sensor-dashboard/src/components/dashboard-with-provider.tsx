"use client"

import Dashboard from "@/components/dashboard"
import { WebSocketProvider } from "@/components/websocket-provider"
import ConnectionStatus from "@/components/connection-status"

export function DashboardWithProvider() {
  return (
    <WebSocketProvider url="http://localhost:8080/ws">
      <ConnectionStatus />
      <Dashboard />
    </WebSocketProvider>
  )
}
