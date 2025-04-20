"use client"

import dynamic from "next/dynamic"
import { Suspense } from "react"
import { Skeleton } from "@/components/ui/skeleton"

// Динамически импортируем компоненты с WebSocket функциональностью
const DashboardWithProvider = dynamic(
  () => import("@/components/dashboard-with-provider").then((mod) => mod.DashboardWithProvider),
  { ssr: false },
)

export default function ClientWrapper() {
  return (
    <Suspense fallback={<LoadingSkeleton />}>
      <DashboardWithProvider />
    </Suspense>
  )
}

function LoadingSkeleton() {
  return (
    <div className="space-y-6">
      <div className="flex items-center gap-2 mb-4">
        <div className="h-3 w-3 rounded-full bg-gray-300" />
        <Skeleton className="h-5 w-40" />
      </div>

      <div className="flex flex-col space-y-2">
        <Skeleton className="h-10 w-64" />
        <Skeleton className="h-5 w-full max-w-md" />
      </div>

      <div className="space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {Array(4)
            .fill(0)
            .map((_, i) => (
              <Skeleton key={i} className="h-32 w-full rounded-lg" />
            ))}
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {Array(4)
            .fill(0)
            .map((_, i) => (
              <Skeleton key={i} className="h-64 w-full rounded-lg" />
            ))}
        </div>
      </div>
    </div>
  )
}
