import { NextResponse } from "next/server"
import type { NextRequest } from "next/server"

// Этот middleware будет запускаться для всех запросов
export function middleware(request: NextRequest) {
    // Клонируем заголовки для модификации
    const requestHeaders = new Headers(request.headers)

    // Добавляем заголовок для передачи куки в WebSocket
    requestHeaders.set("Cookie", request.cookies.toString())

    // Возвращаем ответ с модифицированными заголовками
    return NextResponse.next({
        request: {
            headers: requestHeaders,
        },
    })
}

// Указываем, для каких путей должен запускаться middleware
export const config = {
    matcher: ["/ws/:path*", "/api/:path*"],
}
