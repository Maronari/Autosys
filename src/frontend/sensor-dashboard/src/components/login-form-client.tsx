"use client"

import type React from "react"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert"
import { AlertCircle } from "lucide-react"
import { useAuth } from "@/components/auth-provider"

export default function LoginFormClient() {
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [localError, setLocalError] = useState<string | null>(null)
  const router = useRouter()
  const { login, loading, error: authError } = useAuth()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLocalError(null)

    if (!username || !password) {
      setLocalError("Пожалуйста, введите имя пользователя и пароль")
      return
    }

    try {
      const success = await login(username, password)
      if (success) {
        router.push("/dashboard")
      }
    } catch (err) {
      console.error("Ошибка при авториз��ции:", err)
      setLocalError("Ошибка соединения с сервером. Пожалуйста, попробуйте позже.")
    }
  }

  const error = localError || authError

  return (
    <Card className="w-full max-w-md mx-auto">
      <CardHeader>
        <CardTitle>Вход в систему</CardTitle>
        <CardDescription>Введите ваши учетные данные для доступа к панели мониторинга</CardDescription>
      </CardHeader>
      <CardContent>
        {error && (
          <Alert variant="destructive" className="mb-4">
            <AlertCircle className="h-4 w-4" />
            <AlertTitle>Ошибка</AlertTitle>
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="username">Имя пользователя</Label>
            <Input
              id="username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              autoComplete="username"
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="password">Пароль</Label>
            <Input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              autoComplete="current-password"
            />
          </div>
          <Button type="submit" className="w-full" disabled={loading}>
            {loading ? "Вход..." : "Войти"}
          </Button>
        </form>
      </CardContent>
      <CardFooter className="flex justify-center text-sm text-muted-foreground">
        Для доступа к системе необходимо авторизоваться
      </CardFooter>
    </Card>
  )
}
