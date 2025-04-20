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

export default function LoginForm() {
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const router = useRouter()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    setLoading(true)

    try {
      // Создаем FormData для отправки данных формы
      const formData = new FormData()
      formData.append("username", username)
      formData.append("password", password)

      // Отправляем запрос на авторизацию
      const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        body: formData,
        // Важно: включаем credentials для сохранения куки
        credentials: "include",
        // Не устанавливаем Content-Type, браузер сделает это автоматически для FormData
      })

      if (response.ok) {
        // Успешная авторизация
        console.log("Авторизация успешна")
        // Перенаправляем на дашборд
        router.push("/dashboard")
      } else {
        // Ошибка авторизации
        const errorData = await response.text()
        setError(errorData || "Ошибка авторизации. Проверьте логин и пароль.")
      }
    } catch (err) {
      console.error("Ошибка при авторизации:", err)
      setError("Ошибка соединения с сервером. Пожалуйста, попробуйте позже.")
    } finally {
      setLoading(false)
    }
  }

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
