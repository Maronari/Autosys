import { cookies } from "next/headers"
import { redirect } from "next/navigation"
import ClientWrapper from "@/components/client-wrapper"

export default async function DashboardPage() {
  // Проверяем наличие куки авторизации на сервере
  const cookieStore = await cookies()
  const isAuthenticated = cookieStore.has("JSESSIONID") || cookieStore.has("SESSION")

  // Если пользователь не авторизован, перенаправляем на страницу входа
  if (!isAuthenticated) {
    redirect("/")
  }

  return (
    <main className="min-h-screen p-4 md:p-8">
      <ClientWrapper />
    </main>
  )
}
